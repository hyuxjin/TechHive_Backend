package com.example.admin_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.admin_backend.Entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUsername(String username);
    UserEntity findByIdNumber(String idNumber);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(Integer userId); // Add this line
}
