package com.example.admin_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_backend.Entity.AdminEntity;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {

    AdminEntity findByAdminname(String adminname);

    AdminEntity findByIdNumber(String idNumber);
<<<<<<< HEAD

    Optional<AdminEntity> findByEmail(String email);

    Optional<AdminEntity> findByEmailAndResetCode(String email, String resetCode);

    // Add the method to support case-insensitive email search
    Optional<AdminEntity> findByEmailIgnoreCase(String email);
}
=======
    AdminEntity findByEmailAndPassword(String email, String password);
}
>>>>>>> parent of f6a8264 (Revert "Implement password hashing for Admin and SuperUser")
