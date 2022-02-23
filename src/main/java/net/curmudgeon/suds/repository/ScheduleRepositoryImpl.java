package net.curmudgeon.suds.repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.curmudgeon.suds.entity.Groomer;
import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Schedule;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
 * 
 * Implementation for the Shedule Repository.
 */
public class ScheduleRepositoryImpl implements ScheduleRepository {

	private DynamoDbTable<Schedule> scheduleTable;
	
	// Constructor creates table object.
	public ScheduleRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
		scheduleTable = dynamoDbEnhancedClient.table("Schedule", TableSchema.fromBean(Schedule.class));
	}

	/**
	 * Save out a Schedule object.
	 * 
	 * @param Schedule
	 */
	@Override
	public void saveSchedule(Schedule schedule) {
		
		// Create DynamoDB partition key using the epoch second of the
		// timestamp. The sort key is the time.
		schedule.setScheduleId("SCHEDULE#"+
				schedule.getAppointmentTime().atZone(ZoneId.systemDefault()).toEpochSecond());
		
		scheduleTable.putItem(schedule);
	}

	/**
	 * Get the schedule entries between the specified times.
	 * 
	 * @param start time
	 * @param end time
	 * @return matching Schedules
	 */
	@Override
	public List<Schedule> getSchedule(LocalDateTime start, LocalDateTime end) {
		
		// Build attributes for the start and end times.
		AttributeValue attrStartTime = AttributeValue.builder().s(start.toString()).build();
		AttributeValue attrEndTime = AttributeValue.builder().s(end.toString()).build();
		
		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":startTime", attrStartTime);
		values.put(":endTime", attrEndTime);
		
		// Build expression.
		Expression scheduleExpression = Expression.builder()
				.expressionValues(values)
				.expression("appointmentTime >= :startTime and appointmentTime <= :endTime")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest scheduleRequest = ScanEnhancedRequest.builder().filterExpression(scheduleExpression).build();
		PageIterable<Schedule> scheduleResults = scheduleTable.scan(scheduleRequest);

		// Convert and return results.
		List<Schedule> results = new ArrayList<Schedule>();
		scheduleResults.items().forEach(results::add);		
		Collections.sort(results);
		return results;
	}

	/**
	 * Get the schedule entries for a specific Groomer.
	 * 
	 * @param Groomer
	 * @param start time
	 * @param end time
	 * @return matching Schedules
	 */
	@Override
	public List<Schedule> getScheduleForGroomer(Groomer groomer, LocalDateTime start, LocalDateTime end) {
		
		// Build attribute for the groomer ID and start/end times.
		AttributeValue attrGroomerId = AttributeValue.builder().s(groomer.getGroomerId()).build();
		AttributeValue attrStartTime = AttributeValue.builder().s(start.toString()).build();
		AttributeValue attrEndTime = AttributeValue.builder().s(end.toString()).build();
		
		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":groomerId", attrGroomerId);
		values.put(":startTime", attrStartTime);
		values.put(":endTime", attrEndTime);
		
		// Build expression.
		Expression scheduleExpression = Expression.builder()
				.expressionValues(values)
				.expression("groomerId = :groomerId and appointmentTime >= :startTime and appointmentTime <= :endTime")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest scheduleRequest = ScanEnhancedRequest.builder().filterExpression(scheduleExpression).build();
		PageIterable<Schedule> scheduleResults = scheduleTable.scan(scheduleRequest);

		// Convert and return results.
		List<Schedule> results = new ArrayList<Schedule>();
		scheduleResults.items().forEach(results::add);
		Collections.sort(results);
		return results;
	}

	/**
	 * Get the schedule entries for a specific Parent.
	 * 
	 * @param Parent
	 * @param start time
	 * @param end time
	 * @return matching Schedules
	 */
	@Override
	public List<Schedule> getScheduleForParent(Parent parent, LocalDateTime start, LocalDateTime end) {
		
		// Build attribute for the customer ID and start/end times.
		AttributeValue attrCustomerId = AttributeValue.builder().s(parent.getCustomerId()).build();
		AttributeValue attrStartTime = AttributeValue.builder().s(start.toString()).build();
		AttributeValue attrEndTime = AttributeValue.builder().s(end.toString()).build();
	
		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":customerId", attrCustomerId);
		values.put(":startTime", attrStartTime);
		values.put(":endTime", attrEndTime);
		
		// Build expression.
		Expression scheduleExpression = Expression.builder()
				.expressionValues(values)
				.expression("customerId = :customerId and appointmentTime >= :startTime and appointmentTime <= :endTime")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest scheduleRequest = ScanEnhancedRequest.builder().filterExpression(scheduleExpression).build();
		PageIterable<Schedule> scheduleResults = scheduleTable.scan(scheduleRequest);

		// Convert and return results.
		List<Schedule> results = new ArrayList<Schedule>();
		scheduleResults.items().forEach(results::add);
		Collections.sort(results);
		return results;
	}
}
