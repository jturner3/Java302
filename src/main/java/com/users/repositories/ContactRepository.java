package com.users.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.users.beans.Contact;
import com.users.beans.User;


public interface ContactRepository extends CrudRepository <Contact, Long>{
	
	//Allowing us to find users by the different list parameters we've established
	//We can list the contacts in three different ways it seems
	Contact findByUserIdAndId(long userId, long Id);
	
	//We can now find users by their last name,first, email, twitter, and facebook URL
	List<User> findByLastNameOrFirstNameOrEmailOrTwitterHandleOrFacebookUrlIgnoreCase(
			String lastName, String firstName, String email, String twitterHandle,
			String facebookUrl);
	
	List<Contact> findAllByUserIdOrderByFirstNameAscLastNameAsc(long userId);
	
	
	
}
