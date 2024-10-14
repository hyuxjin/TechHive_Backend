package com.example.admin_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_backend.Entity.AdminEntity;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {
    AdminEntity findByAdminname(String adminname);
    AdminEntity findByIdNumber(String idNumber);
    AdminEntity findByEmailAndPassword(String email, String password);
}
