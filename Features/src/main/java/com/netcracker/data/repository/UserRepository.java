package com.netcracker.data.repository;

import com.netcracker.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for User entity
 * @author prokhorovartem
 */
public interface UserRepository extends JpaRepository<User, String> {
}
