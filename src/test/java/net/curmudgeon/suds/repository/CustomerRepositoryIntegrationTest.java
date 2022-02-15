package net.curmudgeon.suds.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Pet;
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
public class CustomerRepositoryIntegrationTest {
	
	@Autowired
	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	@Autowired
	private CustomerRepository customerRepository;

	private static final String PHONE1 = "(410) 123-1234";
	private static final String PHONE2 = "(443) 999-8888";

	@BeforeAll
	public void setup() throws Exception {
		
		// Create new, empty table.
		DynamoDbTable<Parent> parentTable = dynamoDbEnhancedClient.table("Customer", TableSchema.fromBean(Parent.class));		
		try {
			parentTable.deleteTable();
		} catch (ResourceNotFoundException e) {
			// Do nothing, table doesn't exist.
		}
		parentTable.createTable();
	}
	
	@Test
	public void a_testParentSavesAndRetrieves() throws Exception {
		Map<String,String> address = new HashMap<String,String>();
		address.put("street", "123 Main Street");
		address.put("city", "Baltimore");
		address.put("state", "MD");
		address.put("zipCode", "21213");

		Parent parent = new Parent();
		parent.setFirstName("Joan");
		parent.setLastName("Doe");
		parent.setPhoneNumber(PHONE1);
		parent.setAddress(address);
		
		customerRepository.saveParent(parent);
		
		Map<String,String> anotherAddress = new HashMap<String,String>();
		anotherAddress.put("street", "123 Main Street");
		anotherAddress.put("city", "Crofton");
		anotherAddress.put("state", "MD");
		anotherAddress.put("zipCode", "21114");

		Parent anotherParent = new Parent();
		anotherParent.setFirstName("John");
		anotherParent.setLastName("Smith");
		anotherParent.setPhoneNumber(PHONE2);
		anotherParent.setAddress(anotherAddress);
		
		customerRepository.saveParent(anotherParent);

		Parent result = customerRepository.getParent(PHONE1, parent.getFirstName(), parent.getLastName());
		
		assertNotNull(result);
		assertEquals(result.getAddress().size(), 4, "address size of " + result.getAddress().size() + " does not equal 4");
		assertEquals(result.getFirstName(), parent.getFirstName(), "first name " + result.getFirstName() + " is not " + parent.getFirstName());
		assertEquals(result.getLastName(), parent.getLastName(), "last name " + result.getLastName() + " is not " + parent.getLastName());
		assertEquals(result.getPhoneNumber(), parent.getPhoneNumber(), "phone number " + result.getPhoneNumber() + " is not " + parent.getPhoneNumber());

		Parent result2 = customerRepository.getParent(PHONE1);
		
		assertNotNull(result2);
		assertEquals(result2.getAddress().size(), 4, "address size of " + result2.getAddress().size() + " does not equal 4");
		assertEquals(result2.getFirstName(), parent.getFirstName(), "first name " + result2.getFirstName() + " is not " + parent.getFirstName());
		assertEquals(result2.getLastName(), parent.getLastName(), "last name " + result2.getLastName() + " is not " + parent.getLastName());
		assertEquals(result2.getPhoneNumber(), parent.getPhoneNumber(), "phone number " + result2.getPhoneNumber() + " is not " + parent.getPhoneNumber());

		Parent anotherResult = customerRepository.getParent(PHONE2, anotherParent.getFirstName(), anotherParent.getLastName());
		
		assertNotNull(anotherResult);
		assertEquals(anotherResult.getAddress().size(), 4, "address size of " + anotherResult.getAddress().size() + " does not equal 4");
		assertEquals(anotherResult.getFirstName(), anotherParent.getFirstName(), "first name " + anotherResult.getFirstName() + " is not " + anotherParent.getFirstName());
		assertEquals(anotherResult.getLastName(), anotherParent.getLastName(), "last name " + anotherResult.getLastName() + " is not " + anotherParent.getLastName());
		assertEquals(anotherResult.getPhoneNumber(), anotherParent.getPhoneNumber(), "phone number " + anotherResult.getPhoneNumber() + " is not " + anotherParent.getPhoneNumber());

		Parent anotherResult2 = customerRepository.getParent(PHONE2);
		
		assertNotNull(anotherResult2);
		assertEquals(anotherResult2.getAddress().size(), 4, "address size of " + anotherResult2.getAddress().size() + " does not equal 4");
		assertEquals(anotherResult2.getFirstName(), anotherParent.getFirstName(), "first name " + anotherResult2.getFirstName() + " is not " + anotherParent.getFirstName());
		assertEquals(anotherResult2.getLastName(), anotherParent.getLastName(), "last name " + anotherResult2.getLastName() + " is not " + anotherParent.getLastName());
		assertEquals(anotherResult2.getPhoneNumber(), anotherParent.getPhoneNumber(), "phone number " + anotherResult2.getPhoneNumber() + " is not " + anotherParent.getPhoneNumber());
	}
	
	@Test
	public void b_testPetSavesAndRetrieves() throws Exception {
		Pet pet = new Pet();
		pet.setPhoneNumber(PHONE1);
		pet.setName("Buddy");
		pet.setType("Dog");
		pet.setNotes("A little bitey in the harness.");
		
		customerRepository.savePet(pet);
		
		Pet anotherPet = new Pet();
		anotherPet.setPhoneNumber(PHONE1);
		anotherPet.setName("Fluffernutter");
		anotherPet.setType("Dog");
		anotherPet.setNotes("Likes peanut butter treats.");
		
		customerRepository.savePet(anotherPet);

		Pet yetAnotherPet = new Pet();
		yetAnotherPet.setPhoneNumber(PHONE2);
		yetAnotherPet.setName("Sparky");
		yetAnotherPet.setType("Cat");
		yetAnotherPet.setNotes("So mean, why are we washing cats?");
		
		customerRepository.savePet(yetAnotherPet);

		Pet result = customerRepository.getPet(PHONE1, pet.getName());
		
		assertNotNull(result);
		assertEquals(result.getPhoneNumber(), pet.getPhoneNumber(), "phone of " + result.getPhoneNumber() + " does not equal " + pet.getPhoneNumber());
		assertEquals(result.getName(), pet.getName(), "name of " + result.getName() + " does not equal " + pet.getName());
		assertEquals(result.getType(), pet.getType(), "type of " + result.getType() + " does not equal " + pet.getType());
		assertEquals(result.getNotes(), pet.getNotes(), "name of " + result.getNotes() + " does not equal " + pet.getNotes());

		Pet anotherResult = customerRepository.getPet(PHONE1, anotherPet.getName());
		
		assertNotNull(anotherResult);
		assertEquals(anotherResult.getPhoneNumber(), anotherPet.getPhoneNumber(), "phone of " + anotherResult.getPhoneNumber() + " does not equal " + anotherPet.getPhoneNumber());
		assertEquals(anotherResult.getName(), anotherPet.getName(), "name of " + anotherResult.getName() + " does not equal " + anotherPet.getName());
		assertEquals(anotherResult.getType(), anotherPet.getType(), "type of " + anotherResult.getType() + " does not equal " + anotherPet.getType());
		assertEquals(anotherResult.getNotes(), anotherPet.getNotes(), "name of " + anotherResult.getNotes() + " does not equal " + anotherPet.getNotes());

		Pet yetAnotherResult = customerRepository.getPet(PHONE2, yetAnotherPet.getName());
		
		assertNotNull(yetAnotherResult);
		assertEquals(yetAnotherResult.getPhoneNumber(), yetAnotherPet.getPhoneNumber(), "phone of " + yetAnotherResult.getPhoneNumber() + " does not equal " + yetAnotherPet.getPhoneNumber());
		assertEquals(yetAnotherResult.getName(), yetAnotherPet.getName(), "name of " + yetAnotherResult.getName() + " does not equal " + yetAnotherPet.getName());
		assertEquals(yetAnotherResult.getType(), yetAnotherPet.getType(), "type of " + yetAnotherResult.getType() + " does not equal " + yetAnotherPet.getType());
		assertEquals(yetAnotherResult.getNotes(), yetAnotherPet.getNotes(), "name of " + yetAnotherResult.getNotes() + " does not equal " + yetAnotherPet.getNotes());
	}
	
	@Test
	public void c_testQueriesBasedOnPreviousTests() {
		
		// Get just parents.
		List<Parent> parents = customerRepository.getAllParents();
		System.out.println("Just parents");
		parents.stream().forEach(item -> System.out.println(item));

		// Get just pets.
		List<Pet> pets = customerRepository.getAllPets();
		System.out.println("Just pets");
		pets.stream().forEach(item -> System.out.println(item));

		// Retrieve pets for a specific customer.
		List<Pet> petsForCustomer = customerRepository.getPetsForParent(PHONE1);
		System.out.println("Pets for customer " + PHONE1);
		petsForCustomer.stream().forEach(item -> System.out.println(item));		
	}
}
