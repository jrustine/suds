package net.curmudgeon.suds.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
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
import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Schedule;
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
public class ScheduleRepositoryIntegrationTest {

	@Autowired
	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	@Autowired
	private ScheduleRepository scheduleRepository;
	
	private static final String GROOMERID1 = "GROOMER#SUDS001";
	private static final String GROOMERID2 = "GROOMER#SUDS002";
	
	private static final String CUSTOMERID1 = "CUSTOMER#4101231234";
	private static final String CUSTOMERID2 = "CUSTOMER#4439998888";
	
	private static final String PETID1 = "PET#BUDDY";
	private static final String PETID2 = "PET#FLUFFERNUTTER";
	private static final String PETID3 = "PET#SPARKY";
	
	@BeforeAll
	public void setup() throws Exception {
		
		// Create new, empty table.
		DynamoDbTable<Schedule> scheduleTable = dynamoDbEnhancedClient.table("Schedule", TableSchema.fromBean(Schedule.class));		
		try {
			scheduleTable.deleteTable();
		} catch (ResourceNotFoundException e) {
			// Do nothing, table doesn't exist.
		}
		scheduleTable.createTable();
	}
	
	@Test
	public void a_testScheduleSavesAndRetrieves() throws Exception {
		Schedule schedule = new Schedule();
		schedule.setAppointmentTime(LocalDateTime.parse("2022-01-03T09:00:00"));
		schedule.setGroomerId(GROOMERID1);
		schedule.setCustomerId(CUSTOMERID1);
		schedule.setPetId(PETID1);

		scheduleRepository.saveSchedule(schedule);
		
		Schedule schedule2 = new Schedule();
		schedule2.setAppointmentTime(LocalDateTime.parse("2022-01-03T10:00:00"));
		schedule2.setGroomerId(GROOMERID2);
		schedule2.setCustomerId(CUSTOMERID1);
		schedule2.setPetId(PETID2);

		scheduleRepository.saveSchedule(schedule2);
		
		Schedule schedule3 = new Schedule();
		schedule3.setAppointmentTime(LocalDateTime.parse("2022-01-04T13:00:00"));
		schedule3.setGroomerId(GROOMERID1);
		schedule3.setCustomerId(CUSTOMERID2);
		schedule3.setPetId(PETID3);

		scheduleRepository.saveSchedule(schedule3);

		Schedule schedule4 = new Schedule();
		schedule4.setAppointmentTime(LocalDateTime.parse("2022-01-10T09:00:00"));
		schedule4.setGroomerId(GROOMERID2);
		schedule4.setCustomerId(CUSTOMERID1);
		schedule4.setPetId(PETID1);

		scheduleRepository.saveSchedule(schedule4);

		List<Schedule> results = scheduleRepository.getSchedule(
				LocalDateTime.parse("2022-01-03T00:00:00"), 
				LocalDateTime.parse("2022-01-08T23:59:59"));
		
		assertNotNull(results);
		assertEquals(results.size(), 3, "size of " + results.size() + " is not 3");
		assertTrue(results.stream().anyMatch(item -> LocalDateTime.parse("2022-01-03T09:00:00").equals(item.getAppointmentTime())));
		assertTrue(results.stream().anyMatch(item -> LocalDateTime.parse("2022-01-03T10:00:00").equals(item.getAppointmentTime())));
		assertTrue(results.stream().anyMatch(item -> LocalDateTime.parse("2022-01-04T13:00:00").equals(item.getAppointmentTime())));
		
		// We only need the ID to test a groomer retrieve.
		Groomer mockGroomer = new Groomer();
		mockGroomer.setGroomerId(GROOMERID1);
		List<Schedule> results2 = scheduleRepository.getScheduleForGroomer(
				mockGroomer,
				LocalDateTime.parse("2022-01-01T00:00:00"), 
				LocalDateTime.parse("2022-01-31T23:59:59"));


		assertNotNull(results2);
		assertEquals(results2.size(), 2, "size of " + results2.size() + " is not 2");
		assertTrue(results2.stream().anyMatch(item -> LocalDateTime.parse("2022-01-03T09:00:00").equals(item.getAppointmentTime())));
		assertTrue(results2.stream().anyMatch(item -> LocalDateTime.parse("2022-01-04T13:00:00").equals(item.getAppointmentTime())));
		
		// We only need the ID to test a parent retrieve too.
		Parent mockParent = new Parent();
		mockParent.setCustomerId(CUSTOMERID1);
		List<Schedule> results3 = scheduleRepository.getScheduleForParent(
				mockParent,
				LocalDateTime.parse("2022-01-01T00:00:00"), 
				LocalDateTime.parse("2022-01-31T23:59:59"));
		assertNotNull(results3);
		assertEquals(results3.size(), 3, "size of " + results3.size() + " is not 3");
		assertTrue(results3.stream().anyMatch(item -> LocalDateTime.parse("2022-01-03T09:00:00").equals(item.getAppointmentTime())));
		assertTrue(results3.stream().anyMatch(item -> LocalDateTime.parse("2022-01-03T10:00:00").equals(item.getAppointmentTime())));
		assertTrue(results3.stream().anyMatch(item -> LocalDateTime.parse("2022-01-10T09:00:00").equals(item.getAppointmentTime())));
	}
}
