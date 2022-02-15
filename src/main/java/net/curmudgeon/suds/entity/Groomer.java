package net.curmudgeon.suds.entity;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

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
@DynamoDbBean
public class Groomer {
	private String groomerId;
	private String version;
	private Integer latestVersion;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private String homePhoneNumber;
	private List<WorkSchedule> workSchedule;

	@DynamoDbPartitionKey
	public String getGroomerId() {
		return groomerId;
	}

	public void setGroomerId(String groomerId) {
		this.groomerId = groomerId;
	}

	@DynamoDbSortKey
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(Integer latestVersion) {
		this.latestVersion = latestVersion;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getHomePhoneNumber() {
		return homePhoneNumber;
	}

	public void setHomePhoneNumber(String homePhoneNumber) {
		this.homePhoneNumber = homePhoneNumber;
	}

	public List<WorkSchedule> getWorkSchedule() {
		return workSchedule;
	}

	public void setWorkSchedule(List<WorkSchedule> workSchedule) {
		this.workSchedule = workSchedule;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
