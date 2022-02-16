package net.curmudgeon.suds.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.curmudgeon.suds.entity.Groomer;
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
 * Implementation for the Groomer Repository.
 */
public class GroomerRepositoryImpl implements GroomerRepository {
	private DynamoDbTable<Groomer> groomerTable;
	
	// Constructor creates table objects.
	public GroomerRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
		groomerTable = dynamoDbEnhancedClient.table("Groomer", TableSchema.fromBean(Groomer.class));
	}

	/**
	 * Save out a Groomer object. Groomer information is versioned.
	 * 
	 * @param Groomer
	 */
	@Override
	public void saveGroomer(Groomer groomer) {
		
		String groomerId = "GROOMER#" + groomer.getEmployeeNumber();
		
		// Does groomer already exist?
		Groomer existingGroomer = getGroomer(groomer.getEmployeeNumber());
		if (existingGroomer != null) {

			// Create new versioned record.
			Integer newVersion = existingGroomer.getLatestVersion() + 1;
			groomer.setGroomerId(groomerId);
			groomer.setLatestVersion(null);
			groomer.setVersion("v" + newVersion);
			groomerTable.putItem(groomer);
			
			// Replace the v0 record.
			groomer.setVersion("v0");
			groomer.setLatestVersion(newVersion);
			groomerTable.putItem(groomer);
			
		} else {
			
			// Create first versioned record.
			groomer.setGroomerId(groomerId);
			groomer.setVersion("v1");
			groomer.setLatestVersion(null);
			groomerTable.putItem(groomer);
			
			// Create the v0 record.
			groomer.setVersion("v0");
			groomer.setLatestVersion(1);
			groomerTable.putItem(groomer);
		}
	}

	/**
	 * Get a groomer by employee number. This returns the v0 record.
	 * 
	 * @param employee number
	 * @return matching Groomer
	 */
	@Override
	public Groomer getGroomer(String employeeNumber) {
		Groomer groomer = null;
		
		// Build attributes including full Groomer ID and a string containing
		// the "v0" sort key to get the latest version.
		AttributeValue attrGroomerId = AttributeValue.builder().s("GROOMER#" + employeeNumber).build();
		AttributeValue attrVersion = AttributeValue.builder().s("v0").build();

		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":groomerId", attrGroomerId);
		values.put(":version0", attrVersion);
		
		// Build expression. We're looking for a specific Groomer ID partition key
		// and the v0 version record.
		Expression groomerExpression = Expression.builder()
				.expressionValues(values)
				.expression("groomerId = :groomerId and version = :version0")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest groomerRequest = ScanEnhancedRequest.builder().filterExpression(groomerExpression).build();
		PageIterable<Groomer> groomerResults = groomerTable.scan(groomerRequest);
		
		// Should only be one, and it should throw error if more than one is found but doesn't yet.
		if (groomerResults.items().iterator().hasNext())
			groomer = groomerResults.items().iterator().next();
		
		return groomer;
	}

	/**
	 * Returns a list of all active groomers.
	 * 
	 * @return all matching Groomers
	 */
	@Override
	public List<Groomer> getAllGroomers() {
		
		// Build attribute, a string containing the "v0" sort key to just get the latest versions.
		AttributeValue attrVersion = AttributeValue.builder().s("v0").build();

		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":version0", attrVersion);
		
		// Build expression. We're looking for a specific Groomer ID partition key
		// and the v0 version record.
		Expression groomerExpression = Expression.builder()
				.expressionValues(values)
				.expression("version = :version0")
				.build();
		
		// Scan table for results.
		ScanEnhancedRequest groomerRequest = ScanEnhancedRequest.builder().filterExpression(groomerExpression).build();
		PageIterable<Groomer> groomerResults = groomerTable.scan(groomerRequest);

		// Convert and return results.
		List<Groomer> results = new ArrayList<Groomer>();
		groomerResults.items().forEach(results::add);
		return results;
	}

}
