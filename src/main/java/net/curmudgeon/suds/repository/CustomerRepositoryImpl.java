package net.curmudgeon.suds.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Pet;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
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
 * Implementation for the Customer Repository.
 */
public class CustomerRepositoryImpl implements CustomerRepository {

	private DynamoDbTable<Parent> parentTable;
	private DynamoDbTable<Pet> petTable;
	
	// Constructor creates table objects.
	public CustomerRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
		parentTable = dynamoDbEnhancedClient.table("Customer", TableSchema.fromBean(Parent.class));
		petTable = dynamoDbEnhancedClient.table("Customer", TableSchema.fromBean(Pet.class));
	}

	/**
	 * Save out a Parent object.
	 * 
	 * @param Parent
	 */
	@Override
	public void saveParent(Parent parent) {
		
		// Create DynamoDB partition key and sort key.
		parent.setCustomerId("CUSTOMER#"+StringUtils.getDigits(parent.getPhoneNumber()));
		parent.setId("PARENT#"+formatName(parent.getFirstName(), parent.getLastName()));
		
		parentTable.putItem(parent);
	}

	/**
	 * Retrieve a parent by phone (assumes only one parent
	 * per customer).
	 * 
	 * @param phone number
	 * @return matching Parent
	 */
	@Override
	public Parent getParent(String phoneNumber) {
		
		// Build attributes including full Customer ID and a string containing
		// the word "PARENT" for matching just parents.
		AttributeValue attrCustomerId = AttributeValue.builder().s("CUSTOMER#"+StringUtils.getDigits(phoneNumber)).build();
		AttributeValue attrId = AttributeValue.builder().s("PARENT").build();

		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":parentIdPrefix", attrId);
		values.put(":customerId", attrCustomerId);
		
		// Build expression. We're looking for a specific Customer ID partition key
		// and any matching IDs that start with "PARENT".
		Expression parentExpression = Expression.builder()
				.expressionValues(values)
				.expression("customerId = :customerId and begins_with(id,:parentIdPrefix)")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest parentRequest = ScanEnhancedRequest.builder().filterExpression(parentExpression).build();
		PageIterable<Parent> parentResults = parentTable.scan(parentRequest);
		
		// Should only be one, and it should throw error if more than one is found but doesn't yet.
		return parentResults.items().iterator().next();
	}

	/**
	 * Retrieve a parent by phone and name.
	 * 
	 * @param phone number
	 * @param first name
	 * @param last name
	 * @return matching Parent
	 */
	@Override
	public Parent getParent(String phoneNumber, String firstName, String lastName) {

		// Create partition and sort keys.
		Key key = Key.builder()
                .partitionValue("CUSTOMER#"+StringUtils.getDigits(phoneNumber))
                .sortValue("PARENT#"+formatName(firstName, lastName))
                .build();
		
		return parentTable.getItem(key);
	}

	/**
	 * Get all parents defined in the Customer table.
	 * 
	 * @return all matching Parent objects
	 */
	@Override
	public List<Parent> getAllParents() {
		
		// Build an attribute with just the word "PARENT".
		AttributeValue attr = AttributeValue.builder().s("PARENT").build();
		
		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":parentIdPrefix", attr);
		
		// Build expression. We're looking for all sort keys that begin with "PARENT".
		Expression parentExpression = Expression.builder()
				.expressionValues(values)
				.expression("begins_with(id,:parentIdPrefix)")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest parentRequest = ScanEnhancedRequest.builder().filterExpression(parentExpression).build();
		PageIterable<Parent> parentResults = parentTable.scan(parentRequest);
		
		// Convert and return results.
		List<Parent> results = new ArrayList<Parent>();
		parentResults.items().forEach(results::add);
		return results;
	}

	/**
	 * Save pet object.
	 * 
	 * @param Pet
	 */
	@Override
	public void savePet(Pet pet) {
		
		// Create DynamoDB partition key and sort key.
		pet.setCustomerId("CUSTOMER#"+StringUtils.getDigits(pet.getPhoneNumber()));
		pet.setId("PET#"+formatName(pet.getName()));
		
		petTable.putItem(pet);
	}

	/**
	 * Get pet by phone number and pet name.
	 * 
	 * @param phone number
	 * @param pet name
	 * @return matching Pet
	 */
	@Override
	public Pet getPet(String phoneNumber, String name) {
		
		// Create partition and sort keys.
		Key key = Key.builder()
                .partitionValue("CUSTOMER#"+StringUtils.getDigits(phoneNumber))
                .sortValue("PET#"+formatName(name))
                .build();
		
		return petTable.getItem(key);
	}

	/**
	 * Get all pets for a Parent by phone number.
	 * 
	 * @param phone number
	 * @return list of matching Pets
	 */
	@Override
	public List<Pet> getPetsForParent(String phoneNumber) {
		
		// Build attributes including full Customer ID and a string containing
		// the word "PET" for matching just the pets.
		AttributeValue attrCustomerId = AttributeValue.builder().s("CUSTOMER#"+StringUtils.getDigits(phoneNumber)).build();
		AttributeValue attrId = AttributeValue.builder().s("PET").build();

		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":petIdPrefix", attrId);
		values.put(":customerId", attrCustomerId);
		
		// Build expression. We're looking for a specific Customer ID partition key
		// and any matching IDs that start with "PET".
		Expression petExpression = Expression.builder()
				.expressionValues(values)
				.expression("customerId = :customerId and begins_with(id,:petIdPrefix)")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest petRequest = ScanEnhancedRequest.builder().filterExpression(petExpression).build();
		PageIterable<Pet> petResults = petTable.scan(petRequest);
		
		// Convert and return results.
		List<Pet> results = new ArrayList<Pet>();
		petResults.items().forEach(results::add);
		return results;
	}

	/**
	 * Get all pets defined in the Customer table.
	 * 
	 * @return all matching Pet objects
	 */
	@Override
	public List<Pet> getAllPets() {
		
		// Build an attribute with just the word "PET".
		AttributeValue attr = AttributeValue.builder().s("PET").build();
		
		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":petIdPrefix", attr);
		
		// Build expression. We're looking for all sort keys that begin with "PET".
		Expression petExpression = Expression.builder()
				.expressionValues(values)
				.expression("begins_with(id,:petIdPrefix)")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest petRequest = ScanEnhancedRequest.builder().filterExpression(petExpression).build();
		PageIterable<Pet> petResults = petTable.scan(petRequest);
		
		// Convert and return results.
		List<Pet> results = new ArrayList<Pet>();
		petResults.items().forEach(results::add);
		return results;
	}

	/**
	 * Scrubs and formats customer name.
	 * 
	 * @param firstName
	 * @param lastName
	 * @return formatted name
	 */
	private String formatName(String firstName, String lastName) {
		return firstName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase() +
				lastName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
	}

	/**
	 * Scrubs and formats pet name.
	 * 
	 * @param name
	 * @return formatted name
	 */
	private String formatName(String name) {
		return name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
	}

}
