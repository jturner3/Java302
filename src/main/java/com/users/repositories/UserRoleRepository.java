package com.users.repositories;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.users.beans.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

	List<UserRole> findByUserId(long userId);
	
}