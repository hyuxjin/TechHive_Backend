package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.AdminEntity;
import com.example.admin_backend.Entity.SuperUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<SuperUserEntity, Integer> {

    // SuperUser-specific queries
    Optional<SuperUserEntity> findByEmail(String email);
    Optional<SuperUserEntity> findByEmailAndResetCode(String email, String resetCode);

    // Admin-specific queries
    Optional<AdminEntity> findAdminByEmail(String email);
    Optional<AdminEntity> findAdminByEmailAndResetCode(String email, String resetCode);
}
