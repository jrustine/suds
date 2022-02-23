package net.curmudgeon.suds.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.curmudgeon.suds.controller.exception.MissingRecordException;
import net.curmudgeon.suds.controller.request.ScheduleRequest;
import net.curmudgeon.suds.controller.response.CustomerResponse;
import net.curmudgeon.suds.controller.response.GroomerResponse;
import net.curmudgeon.suds.controller.response.PetResponse;
import net.curmudgeon.suds.controller.response.ScheduleResponse;
import net.curmudgeon.suds.entity.Groomer;
import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Pet;
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
	private static final Logger log = LogManager.getLogger(ScheduleController.class);

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private GroomerRepository groomerRepository;
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	private static DateTimeFormatter appointmentTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
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
		log.debug("found [" + schedules.size() + "] schedule entries");
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
		if (groomer == null) {
			throw new MissingRecordException(employeeNumber);
		}
		
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
		if (parent == null) {
			throw new MissingRecordException(phoneNumber);
		}
		
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59);
		
		List<Schedule> schedules = scheduleRepository.getScheduleForParent(parent, startDateTime, endDateTime);
		List<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();
		schedules.stream().forEach(schedule -> responses.add(populateSchedule(schedule)));

		return responses;
	}

	/**
	 * Default path, saves schedule request. Sample posted JSON:
	 * 
	 *	{
	 *		"appointmentTime": "2022-01-05 10:30",
	 *		"employeeNumber" : "SUDS001",
	 *		"phoneNumber": "(410) 123-1234",
	 *		"petName" : "Fluffernutter"
	 *	}
	 */
	@PostMapping(value="/", consumes="application/json")
	public void saveSchedule(@RequestBody ScheduleRequest scheduleRequest) {

		Groomer groomer = groomerRepository.getGroomerByEmployeeNumber(scheduleRequest.getEmployeeNumber());
		if (groomer == null) {
			throw new MissingRecordException(scheduleRequest.getEmployeeNumber());
		}

		Parent parent = customerRepository.getParentByPhoneNumber(scheduleRequest.getPhoneNumber());
		if (parent == null) {
			throw new MissingRecordException(scheduleRequest.getPhoneNumber());
		}
		
		Pet pet = customerRepository.getPetByPhoneNumberAndName(scheduleRequest.getPhoneNumber(), scheduleRequest.getPetName());
		if (pet == null) {
			throw new MissingRecordException(scheduleRequest.getPhoneNumber() + "/" + scheduleRequest.getPetName());
		}
		
		Schedule schedule = new Schedule();
		schedule.setAppointmentTime(LocalDateTime.parse(scheduleRequest.getAppointmentTime(), appointmentTimeFormatter));
		schedule.setGroomerId(groomer.getGroomerId());
		schedule.setCustomerId(parent.getCustomerId());
		schedule.setPetId(pet.getId());
		
		scheduleRepository.saveSchedule(schedule);
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
