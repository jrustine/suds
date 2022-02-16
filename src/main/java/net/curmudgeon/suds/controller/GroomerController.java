package net.curmudgeon.suds.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.curmudgeon.suds.entity.Groomer;
import net.curmudgeon.suds.repository.GroomerRepository;

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
@RequestMapping("groomer")
public class GroomerController {

	@Autowired
	private GroomerRepository groomerRepository;
	
	/**
	 * Default path, returns all groomers.
	 */
	@GetMapping(value="/", produces="application/json")
	public List<Groomer> getAllGroomers() {
		return groomerRepository.getAllGroomers();
	}

	/**
	 * Returns specific groomer by employee number.
	 * 
	 * @param employeeNumber
	 */
	@GetMapping(value="/{employeeNumber}", produces="application/json")
	public Groomer getGroomer(@PathVariable String employeeNumber) {
		return groomerRepository.getGroomer(employeeNumber);
	}
	
	/**
	 * Saves out a groomer. Sample posted JSON:
	 * 
	 *	{
	 *		"employeeNumber": "SUDS004",
	 *		"firstName": "Belmont",
	 *		"lastName": "Oaks",
	 *		"homePhoneNumber": "(410) 555-7777",
	 *		"workSchedule": [
	 *			{
	 *				"day": "MONDAY",
	 *				"start": 8,
	 *				"end": 17
	 *			},
	 *			{
	 *				"day": "WEDNESDAY",
	 *				"start": 8,
	 *				"end": 17
	 *			},
	 *			{
	 *				"day": "FRIDAY",
	 *				"start": 8,
	 *				"end": 17
	 *			}
	 *		]
	 *	}
	 * 
	 * @param groomer
	 */
	@PostMapping(value="/", consumes="application/json")
	public void saveGroomer(@RequestBody Groomer groomer) {
		groomerRepository.saveGroomer(groomer);
	}	
}
