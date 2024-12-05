package com.example.admin_backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_backend.Entity.SuperUserEntity;

@Repository
public interface SuperUserRepository extends JpaRepository<SuperUserEntity, Integer> {
    Optional<SuperUserEntity> findBySuperUsername(String superUsername);

    Optional<SuperUserEntity> findBySuperUserIdNumber(String superUserIdNumber);

    Optional<SuperUserEntity> findByEmail(String email);

}

    
