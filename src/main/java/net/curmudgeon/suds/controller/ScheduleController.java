package net.curmudgeon.suds.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.curmudgeon.suds.controller.response.CustomerResponse;
import net.curmudgeon.suds.controller.response.GroomerResponse;
import net.curmudgeon.suds.controller.response.PetResponse;
import net.curmudgeon.suds.controller.response.ScheduleResponse;
import net.curmudgeon.suds.entity.Groomer;
import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Schedule;
import net.curmudgeon.suds.repository.CustomerRepository;
import net.curmudgeon.suds.repository.GroomerRepository;
import net.curmudgeon.suds.repository.ScheduleRepository;

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
 * Controller for Groomer data.
 */
@RestController
@RequestMapping("schedule")
public class ScheduleController {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private GroomerRepository groomerRepository;
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	/**
	 * Retrieves schedule entries between the specified start and end dates (inclusive).
	 * 
	 * @param startDate
	 * @param endDate
	 * @return matching entries
	 */
	@GetMapping(value="/{startDate}/{endDate}", produces="application/json")
	public List<ScheduleResponse> getScheduleByDateRange(
			@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
			@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate) {
		
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59);
		
		List<Schedule> schedules = scheduleRepository.getSchedule(startDateTime, endDateTime);
		List<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();
		schedules.stream().forEach(schedule -> responses.add(populateSchedule(schedule)));

		return responses;
	}

	/**
	 * Retrieves schedule for a specific groomer by employee number.
	 * 
	 * @param employeeNumber
	 * @param startDate
	 * @param endDate
	 * @return matching entries
	 */
	@GetMapping(value="/groomer/{employeeNumber}/{startDate}/{endDate}", produces="application/json")
	public List<ScheduleResponse> getScheduleByGroomer(
			@PathVariable String employeeNumber,
			@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
			@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate) {
		
		Groomer groomer = groomerRepository.getGroomerByEmployeeNumber(employeeNumber);
		
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59);
		
		List<Schedule> schedules = scheduleRepository.getScheduleForGroomer(groomer, startDateTime, endDateTime);
		List<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();
		schedules.stream().forEach(schedule -> responses.add(populateSchedule(schedule)));

		return responses;
	}

	/**
	 * Retrieves schedule for a specific customer by phone number.
	 * 
	 * @param phoneNumber
	 * @param startDate
	 * @param endDate
	 * @return matching entries
	 */
	@GetMapping(value="/customer/{phoneNumber}/{startDate}/{endDate}", produces="application/json")
	public List<ScheduleResponse> getScheduleByCustomer(
			@PathVariable String phoneNumber,
			@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
			@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate) {
		
		Parent parent = customerRepository.getParentByPhoneNumber(phoneNumber);
		
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59);
		
		List<Schedule> schedules = scheduleRepository.getScheduleForParent(parent, startDateTime, endDateTime);
		List<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();
		schedules.stream().forEach(schedule -> responses.add(populateSchedule(schedule)));

		return responses;
	}

	/**
	 * Populate a response object from the Schedule entry.
	 * 
	 * @param schedule
	 * @return populated ScheduleResponse
	 */
	private ScheduleResponse populateSchedule(Schedule schedule) {
		ScheduleResponse scheduleResponse = new ScheduleResponse();
		
		scheduleResponse.setScheduleId(schedule.getScheduleId());
		scheduleResponse.setAppointmentTime(schedule.getAppointmentTime());
		scheduleResponse.setGroomer(new GroomerResponse(groomerRepository.getGroomer(schedule.getGroomerId())));
		scheduleResponse.setCustomer(new CustomerResponse(customerRepository.getParentByCustomerId(schedule.getCustomerId())));
		scheduleResponse.setPet(new PetResponse(customerRepository.getPetByCustomerIdAndPetId(schedule.getCustomerId(), schedule.getPetId())));
		
		return scheduleResponse;
	}
}