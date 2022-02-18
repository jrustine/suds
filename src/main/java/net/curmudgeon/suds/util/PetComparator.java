package net.curmudgeon.suds.util;

import java.util.Comparator;

import net.curmudgeon.suds.entity.Pet;

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
 * Comparator for Pet objects.
 */
public class PetComparator implements Comparator<Pet> {

	@Override
	public int compare(Pet pet0, Pet pet1) {
		return pet0.getName().compareTo(pet1.getName());
	}

}
