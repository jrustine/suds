package net.curmudgeon.suds.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Pet;
import net.curmudgeon.suds.repository.CustomerRepository;

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
 * Controller for Customer data.
 */
@RestController
@RequestMapping("customer")
public class CustomerController {
	
	@Autowired
	private CustomerRepository customerRepository;

	/**
	 * Default path, returns all customers with pets.
	 */
	@GetMapping(value="/", produces="application/json")
	public List<Parent> getAllParents() {
		List<Parent> parents = customerRepository.getAllParents();
		parents.stream().forEach(parent -> parent.setPets(customerRepository.getPetsForParent(parent.getPhoneNumber())));
		return parents;
	}

	/**
	 * Returns specific customer by phone number.
	 * 
	 * @param phoneNumber
	 */
	@GetMapping(value="/{phoneNumber}", produces="application/json")
	public Parent getParent(@PathVariable String phoneNumber) {
		Parent parent = customerRepository.getParent(phoneNumber);
		if (parent != null)
			parent.setPets(customerRepository.getPetsForParent(parent.getPhoneNumber()));
		return parent;
	}

	/**
	 * Returns all pets.
	 */
	@GetMapping(value="/pets", produces="application/json")
	public List<Pet> getAllPets() {
		return customerRepository.getAllPets();
	}

	/**
	 * Saves out a customer. Sample posted JSON:
	 * 
	 * {
	 * 	"firstName": "Frankie",
	 * 	"lastName": "Miller",
	 * 	"address": {
	 * 		"zipCode": "99999",
	 * 		"city": "AnyCity",
	 * 		"street": "123 Main Street",
	 * 		"state": "DE"
	 * 	},
	 * 	"phoneNumber": "(333) 444-5555",
	 * 	"pets": [
	 * 		{
	 * 		"name": "Wolfie",
	 * 		"type": "Dog",
	 * 		"notes": "Trim paws and sanitary area."
	 * 		},
	 * 		{
	 * 		"name": "Edgar",
	 * 		"type": "Dog",
	 * 		"notes": "Deaf and a little blind"
	 * 		}
	 * 	]
	 * }
	 * 
	 * @param Parent
	 */
	@PostMapping(value="/", consumes="application/json")
	public void saveParent(@RequestBody Parent parent) {
		customerRepository.saveParent(parent);
		
		// Save out pets separately. Phone is not required for
		// each pet, so set it manually.
		for (Pet pet: parent.getPets()) {
			pet.setPhoneNumber(parent.getPhoneNumber());
			customerRepository.savePet(pet);
		}
	}
}
