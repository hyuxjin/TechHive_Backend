package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.SuperUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperUserRepository extends JpaRepository<SuperUserEntity, Integer> {

    // Match the entity field names exactly and return Optional
    Optional<SuperUserEntity> findBySuperusername(String superusername);

    Optional<SuperUserEntity> findBySuperuseridNumber(String superuseridNumber);

    Optional<SuperUserEntity> findByEmailAndSuperuserpassword(String email, String superuserpassword);

    Optional<SuperUserEntity> findBySuperuserId(int superuserId);
}
