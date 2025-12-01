package com.score.admin.repository;

import com.score.admin.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    
    Optional<Page> findByName(String name);
    
    @Query("SELECT DISTINCT p FROM Page p JOIN p.roles r JOIN r.users u WHERE u.username = :username")
    List<Page> findByUsername(@Param("username") String username);
    
    @Query("SELECT DISTINCT p FROM Page p JOIN p.roles r WHERE r.code IN :roleCodes")
    List<Page> findByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}