package com.users.security;

import static com.users.security.Role.ADMIN;
import static com.users.security.Role.USER;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;


import com.users.repositories.UserRepository;

//Let's Spring know what's going on and it's not in an encapsulated state
//Not sure what else exactly 
@Service
public class PermissionService {

	@Autowired
	private UserRepository userRepo;
	
	//Getting and creating a key "token" to verify with the username and password data that is entered
	//Will return an authentication if it matches up with what is in the data.sql
	private UsernamePasswordAuthenticationToken getToken() {
		return (UsernamePasswordAuthenticationToken) 
				getContext().getAuthentication();
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
	public boolean canEditUser(long userId) {
		long currentUserId = userRepo.findByEmail(getToken().getName()).get(0).getId();
		return hasRole(ADMIN) || (hasRole(USER) && currentUserId == userId);
	}
	
}
