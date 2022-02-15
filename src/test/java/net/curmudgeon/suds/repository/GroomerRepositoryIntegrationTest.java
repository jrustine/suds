package net.curmudgeon.suds.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

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
	public void a_testGroomerSavesAndRetrieves() throws Exception {
		List<WorkSchedule> workSchedule = new ArrayList<>();
		workSchedule.add(new WorkSchedule(DayOfWeek.MONDAY, 8, 17));
		workSchedule.add(new WorkSchedule(DayOfWeek.TUESDAY, 8, 17));
		workSchedule.add(new WorkSchedule(DayOfWeek.THURSDAY, 8, 17));
		workSchedule.add(new WorkSchedule(DayOfWeek.FRIDAY, 8, 17));
		workSchedule.add(new WorkSchedule(DayOfWeek.SATURDAY, 10, 14));
		
		Groomer groomer = new Groomer();
		groomer.setEmployeeNumber(EMPLOYEE1);
		groomer.setFirstName("Fiona");
		groomer.setLastName("Perkins");
		groomer.setHomePhoneNumber("(301) 222-4444");
		groomer.setWorkSchedule(workSchedule);
		
		groomerRepository.saveGroomer(groomer);
		
		Groomer result = groomerRepository.getGroomer(EMPLOYEE1);
		assertNotNull(result);
		assertEquals(result.getLatestVersion(), 1, "latest version of " + groomer.getLatestVersion() + " is not 1");
		assertEquals(result.getWorkSchedule().size(), 5, "worksSchedule size of " + result.getWorkSchedule().size() + " does not equal 5");
		assertEquals(result.getFirstName(), groomer.getFirstName(), "first name " + result.getFirstName() + " is not " + groomer.getFirstName());
		assertEquals(result.getLastName(), groomer.getLastName(), "last name " + result.getLastName() + " is not " + groomer.getLastName());
		assertEquals(result.getHomePhoneNumber(), groomer.getHomePhoneNumber(), "phone " + result.getHomePhoneNumber() + " is not " + groomer.getHomePhoneNumber());		

		// Change phone number and re-save.
		groomer.setHomePhoneNumber("(443) 888-9999");
		groomerRepository.saveGroomer(groomer);

		Groomer result2 = groomerRepository.getGroomer(EMPLOYEE1);
		assertNotNull(result2);
		assertEquals(result2.getLatestVersion(), 2, "latest version of " + groomer.getLatestVersion() + " is not 2");
		assertEquals(result2.getWorkSchedule().size(), 5, "worksSchedule size of " + result2.getWorkSchedule().size() + " does not equal 5");
		assertEquals(result2.getFirstName(), groomer.getFirstName(), "first name " + result2.getFirstName() + " is not " + groomer.getFirstName());
		assertEquals(result2.getLastName(), groomer.getLastName(), "last name " + result2.getLastName() + " is not " + groomer.getLastName());
		assertEquals(result2.getHomePhoneNumber(), groomer.getHomePhoneNumber(), "phone " + result2.getHomePhoneNumber() + " is not " + groomer.getHomePhoneNumber());		
	}
}
