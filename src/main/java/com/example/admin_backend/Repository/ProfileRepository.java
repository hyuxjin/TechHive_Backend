package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.ProfileEntity;
import com.example.admin_backend.Entity.UserEntity;
import com.example.admin_backend.Entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {
    // User profile queries
    ProfileEntity findByUser(UserEntity user);
    boolean existsByUser(UserEntity user);

    // Admin profile queries
    ProfileEntity findByAdmin(AdminEntity admin);
    boolean existsByAdmin(AdminEntity admin);

    // You might also want to add some utility queries
    void deleteByUser(UserEntity user);
    void deleteByAdmin(AdminEntity admin);
}