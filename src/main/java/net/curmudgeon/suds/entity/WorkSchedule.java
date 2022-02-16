package net.curmudgeon.suds.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

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
public class WorkSchedule {
	private DayOfWeek day;
	private LocalTime start;
	private LocalTime end;

	public WorkSchedule() { }
	
	public WorkSchedule(DayOfWeek day, LocalTime start, LocalTime end) {
		super();
		this.day = day;
		this.start = start;
		this.end = end;
	}

	public DayOfWeek getDay() {
		return day;
	}

	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	public LocalTime getStart() {
		return start;
	}

	public void setStart(LocalTime start) {
		this.start = start;
	}

	public LocalTime getEnd() {
		return end;
	}

	public void setEnd(LocalTime end) {
		this.end = end;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
