package com.database.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.database.database.model.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {
	
	List<UserModel> findByUsername(String username);

}
