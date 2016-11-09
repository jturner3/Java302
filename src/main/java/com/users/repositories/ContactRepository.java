package com.users.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.users.beans.Contact;


public interface ContactRepository extends CrudRepository <Contact, Long>{
	
	//Allowing us to find users by the different list parameters we've established
	//We can list the contacts in three different ways it seems
	Contact findByUserIdAndId(long userId, long Id);
	
	List<Contact> findByUserIdAndLastName(long userId, String lastName);
	
	List<Contact> findByUserIdAndEmail(long userId, String email);
	
	List<Contact> findAllByUserIdOrderByFirstNameAscLastNameAsc(long userId);
	
	
	
}
