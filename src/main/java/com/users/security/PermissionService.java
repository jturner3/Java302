package com.users.security;

import static com.users.security.Role.ROLE_ADMIN;
import static com.users.security.Role.ROLE_USER;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.users.repositories.ContactRepository;
import com.users.repositories.UserRepository;

//Let's Spring know what's going on and it's not in an encapsulated state
//Not sure what else exactly 
@Service
public class PermissionService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepository contactRepo;
	
	//Getting and creating a key "token" to verify with the username and password data that is entered
	//Will return an authentication if it matches up with what is in the data.sql
	private UsernamePasswordAuthenticationToken getToken() {
		return (UsernamePasswordAuthenticationToken) 
				getContext().getAuthentication();
}
	//Finding the User id by matching it with the email
	public long findCurrentUserId(){
		return userRepo.findByEmail(getToken().getName()).get(0).getId();
	}
	//Establishes the roles that we put in our Role enum and gives the users a role of ADMIN OR USER
	//Gives them the authority for those roles and returns them
	public boolean hasRole(Role role) {
		for (GrantedAuthority ga : getToken().getAuthorities()) {
			if (role.toString().equals(ga.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	//Allows us to edit users if we are Admins but not users
	public boolean canAccessUser(long userId) {
		return hasRole(ROLE_ADMIN) || (hasRole(ROLE_USER) && findCurrentUserId() == userId);
	}
	
	//Matches the user role with the contactId 
	public boolean canEditContact(long contactId) {
		return hasRole(ROLE_USER)
				&& contactRepo.findByUserIdAndId(findCurrentUserId(), contactId) != null;
	}
}