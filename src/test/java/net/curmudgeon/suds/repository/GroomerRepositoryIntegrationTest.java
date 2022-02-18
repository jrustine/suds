package net.curmudgeon.suds.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import net.curmudgeon.suds.SudsApplication;
import net.curmudgeon.suds.entity.Groomer;
import net.curmudgeon.suds.entity.WorkSchedule;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

/*
 * Copyright (C) 2022 Jay Rustine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SudsApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
@TestPropertySource(properties = { 
		"amazon.dynamodb.endpoint=http://localhost:8000/", 
		"amazon.aws.accesskey=accesskey1",
		"amazon.aws.secretkey=secretkey1" })
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class GroomerRepositoryIntegrationTest {
	
	@Autowired
	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	@Autowired
	private GroomerRepository groomerRepository;

	private static final String EMPLOYEE1 = "SUDS001";
	private static final String EMPLOYEE2 = "SUDS002";
	private static final String EMPLOYEE3 = "SUDS003";

	@BeforeAll
	public void setup() throws Exception {
		
		// Create new, empty table.
		DynamoDbTable<Groomer> groomerTable = dynamoDbEnhancedClient.table("Groomer", TableSchema.fromBean(Groomer.class));		
		try {
			groomerTable.deleteTable();
		} catch (ResourceNotFoundException e) {
			// Do nothing, table doesn't exist.
		}
		groomerTable.createTable();
	}
	
	@Test
	public void a_testGroomerSaveAndVersioning() throws Exception {
		List<WorkSchedule> workSchedule = new ArrayList<>();
		workSchedule.add(new WorkSchedule(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule.add(new WorkSchedule(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule.add(new WorkSchedule(DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule.add(new WorkSchedule(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule.add(new WorkSchedule(DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(14, 0)));
		
		Groomer groomer = new Groomer();
		groomer.setEmployeeNumber(EMPLOYEE1);
		groomer.setFirstName("Fiona");
		groomer.setLastName("Perkins");
		groomer.setHomePhoneNumber("(301) 222-4444");
		groomer.setWorkSchedule(workSchedule);
		
		groomerRepository.saveGroomer(groomer);
		
		Groomer result = groomerRepository.getGroomerByEmployeeNumber(EMPLOYEE1);
		assertNotNull(result);
		assertEquals(result.getLatestVersion(), 1, "latest version of " + groomer.getLatestVersion() + " is not 1");
		assertEquals(result.getWorkSchedule().size(), 5, "worksSchedule size of " + result.getWorkSchedule().size() + " does not equal 5");
		assertEquals(result.getFirstName(), groomer.getFirstName(), "first name " + result.getFirstName() + " is not " + groomer.getFirstName());
		assertEquals(result.getLastName(), groomer.getLastName(), "last name " + result.getLastName() + " is not " + groomer.getLastName());
		assertEquals(result.getHomePhoneNumber(), groomer.getHomePhoneNumber(), "phone " + result.getHomePhoneNumber() + " is not " + groomer.getHomePhoneNumber());		

		Groomer result2 = groomerRepository.getGroomer("GROOMER#" + EMPLOYEE1);
		assertNotNull(result2);
		assertEquals(result2.getLatestVersion(), 1, "latest version of " + groomer.getLatestVersion() + " is not 1");
		assertEquals(result2.getWorkSchedule().size(), 5, "worksSchedule size of " + result2.getWorkSchedule().size() + " does not equal 5");
		assertEquals(result2.getFirstName(), groomer.getFirstName(), "first name " + result2.getFirstName() + " is not " + groomer.getFirstName());
		assertEquals(result2.getLastName(), groomer.getLastName(), "last name " + result2.getLastName() + " is not " + groomer.getLastName());
		assertEquals(result2.getHomePhoneNumber(), groomer.getHomePhoneNumber(), "phone " + result2.getHomePhoneNumber() + " is not " + groomer.getHomePhoneNumber());		

		// Change phone number and re-save to create a new version.
		groomer.setHomePhoneNumber("(443) 888-9999");
		groomerRepository.saveGroomer(groomer);

		Groomer result3 = groomerRepository.getGroomerByEmployeeNumber(EMPLOYEE1);
		assertNotNull(result3);
		assertEquals(result3.getLatestVersion(), 2, "latest version of " + groomer.getLatestVersion() + " is not 2");
		assertEquals(result3.getWorkSchedule().size(), 5, "worksSchedule size of " + result3.getWorkSchedule().size() + " does not equal 5");
		assertEquals(result3.getFirstName(), groomer.getFirstName(), "first name " + result3.getFirstName() + " is not " + groomer.getFirstName());
		assertEquals(result3.getLastName(), groomer.getLastName(), "last name " + result3.getLastName() + " is not " + groomer.getLastName());
		assertEquals(result3.getHomePhoneNumber(), groomer.getHomePhoneNumber(), "phone " + result3.getHomePhoneNumber() + " is not " + groomer.getHomePhoneNumber());		

		Groomer result4 = groomerRepository.getGroomer("GROOMER#" + EMPLOYEE1);
		assertNotNull(result4);
		assertEquals(result4.getLatestVersion(), 2, "latest version of " + groomer.getLatestVersion() + " is not 2");
		assertEquals(result4.getWorkSchedule().size(), 5, "worksSchedule size of " + result4.getWorkSchedule().size() + " does not equal 5");
		assertEquals(result4.getFirstName(), groomer.getFirstName(), "first name " + result4.getFirstName() + " is not " + groomer.getFirstName());
		assertEquals(result4.getLastName(), groomer.getLastName(), "last name " + result4.getLastName() + " is not " + groomer.getLastName());
		assertEquals(result4.getHomePhoneNumber(), groomer.getHomePhoneNumber(), "phone " + result4.getHomePhoneNumber() + " is not " + groomer.getHomePhoneNumber());		
	}
	
	@Test
	public void b_testMultipleGroomers() throws Exception {
		List<WorkSchedule> workSchedule = new ArrayList<>();
		workSchedule.add(new WorkSchedule(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule.add(new WorkSchedule(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule.add(new WorkSchedule(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule.add(new WorkSchedule(DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule.add(new WorkSchedule(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		
		Groomer groomer = new Groomer();
		groomer.setEmployeeNumber(EMPLOYEE2);
		groomer.setFirstName("Desdemona");
		groomer.setLastName("Arlington");
		groomer.setHomePhoneNumber("(703) 333-7777");
		groomer.setWorkSchedule(workSchedule);
		
		groomerRepository.saveGroomer(groomer);
		
		// Change name and re-save to create a new version.
		groomer.setLastName("Covington");
		groomerRepository.saveGroomer(groomer);

		List<WorkSchedule> workSchedule2 = new ArrayList<>();
		workSchedule2.add(new WorkSchedule(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule2.add(new WorkSchedule(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		workSchedule2.add(new WorkSchedule(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(17, 0)));
		
		Groomer groomer2 = new Groomer();
		groomer2.setEmployeeNumber(EMPLOYEE3);
		groomer2.setFirstName("Roger");
		groomer2.setLastName("McCheese");
		groomer2.setHomePhoneNumber("(443) 555-6666");
		groomer2.setWorkSchedule(workSchedule2);
		
		groomerRepository.saveGroomer(groomer2);
		
		List<Groomer> groomers = groomerRepository.getAllGroomers();
		assertNotNull(groomers);
		
		// Horrible assumption, but we should have 3 by now.
		assertEquals(groomers.size(), 3, "size of " + groomers.size() + " is not 3");
		
		assertTrue(groomers.stream().anyMatch(item -> EMPLOYEE1.equals(item.getEmployeeNumber())));
		assertTrue(groomers.stream().anyMatch(item -> EMPLOYEE2.equals(item.getEmployeeNumber())));
		assertTrue(groomers.stream().anyMatch(item -> EMPLOYEE3.equals(item.getEmployeeNumber())));
		
		// Verify correct versions.
		assertTrue(groomers.stream().anyMatch(item -> "Covington".equals(item.getLastName())));
		assertTrue(groomers.stream().anyMatch(item -> "4438889999".equals(StringUtils.getDigits(item.getHomePhoneNumber()))));
	}
}
