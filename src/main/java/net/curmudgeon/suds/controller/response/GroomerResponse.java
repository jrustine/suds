package net.curmudgeon.suds.controller.response;

import net.curmudgeon.suds.entity.Groomer;

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
 */
public class GroomerResponse {
	private String groomerId;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	
	public GroomerResponse(Groomer groomer) {
		super();
		this.groomerId = groomer.getGroomerId();
		this.employeeNumber = groomer.getEmployeeNumber();
		this.firstName = groomer.getFirstName();
		this.lastName = groomer.getLastName();
	}

	public String getGroomerId() {
		return groomerId;
	}
	
	public void setGroomerId(String groomerId) {
		this.groomerId = groomerId;
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
}
