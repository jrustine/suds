package net.curmudgeon.suds.repository;

import java.util.List;

import net.curmudgeon.suds.entity.Parent;
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
 * Interface for the Customer Repository.
 */
public interface CustomerRepository {

	public void saveParent(Parent parent);
	public Parent getParent(String phoneNumber);
	public Parent getParent(String phoneNumber, String firstName, String lastName);
	public List<Parent> getAllParents();
	
	public void savePet(Pet pet);
	public Pet getPet(String phoneNumber, String name);
	public List<Pet> getPetsForParent(String phoneNumber);
	public List<Pet> getAllPets();
}
