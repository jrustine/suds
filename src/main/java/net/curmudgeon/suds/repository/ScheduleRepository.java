package net.curmudgeon.suds.repository;

import java.time.LocalDateTime;
import java.util.List;

import net.curmudgeon.suds.entity.Groomer;
import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Schedule;

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
 * Interface for the Schedule Repository.
 */
public interface ScheduleRepository {

	public void saveSchedule(Schedule schedule);
	public List<Schedule> getSchedule(LocalDateTime start, LocalDateTime end);
	public List<Schedule> getScheduleForGroomer(Groomer groomer, LocalDateTime start, LocalDateTime end);
	public List<Schedule> getScheduleForParent(Parent parent, LocalDateTime start, LocalDateTime end);
}
