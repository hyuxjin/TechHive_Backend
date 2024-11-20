package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.SuperUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperUserRepository extends JpaRepository<SuperUserEntity, Integer> {

    // Find by username (case-sensitive)
    Optional<SuperUserEntity> findBySuperUsername(String superUsername);

    // Find by ID number
    Optional<SuperUserEntity> findBySuperUserIdNumber(String superUserIdNumber);

    // Find by email and password (case-sensitive)
    Optional<SuperUserEntity> findByEmailAndSuperUserPassword(String email, String superUserPassword);

    // Find by email
    Optional<SuperUserEntity> findByEmail(String email);
}
