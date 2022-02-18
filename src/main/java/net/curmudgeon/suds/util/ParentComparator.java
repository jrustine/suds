package net.curmudgeon.suds.util;

import java.util.Comparator;

import net.curmudgeon.suds.entity.Parent;

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
 * Comparator for Parent objects.
 */
public class ParentComparator implements Comparator<Parent> {

	@Override
	public int compare(Parent parent0, Parent parent1) {
		return parent0.getLastName().compareTo(parent1.getLastName());
	}
}
