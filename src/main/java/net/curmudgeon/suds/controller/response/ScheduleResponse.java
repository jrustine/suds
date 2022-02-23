package net.curmudgeon.suds.controller.response;

import java.time.LocalDateTime;

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
 * POJO containing full information for a schedule entry. Since the database
 * just stores ids, this object allows us to return expanded information to 
 * the user.
 */
public class ScheduleResponse {
	private String scheduleId;
	private LocalDateTime appointmentTime;
	private GroomerResponse groomer;
	private CustomerResponse customer;
	private PetResponse pet;
	
	public String getScheduleId() {
		return scheduleId;
	}
	
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	public LocalDateTime getAppointmentTime() {
		return appointmentTime;
	}
	
	public void setAppointmentTime(LocalDateTime appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	
	public GroomerResponse getGroomer() {
		return groomer;
	}
	
	public void setGroomer(GroomerResponse groomer) {
		this.groomer = groomer;
	}
	
	public CustomerResponse getCustomer() {
		return customer;
	}
	
	public void setCustomer(CustomerResponse customer) {
		this.customer = customer;
	}
	
	public PetResponse getPet() {
		return pet;
	}

	public void setPet(PetResponse pet) {
		this.pet = pet;
	}
}
