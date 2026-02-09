package com.score.admin.repository;

import com.score.admin.domain.User;
import com.score.admin.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT r FROM User u JOIN u.roleSet r WHERE u.email = :email")
    List<Role> findRolesByEmail(@Param("email") String email);
}

