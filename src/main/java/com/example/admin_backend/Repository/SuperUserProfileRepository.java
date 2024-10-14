package com.example.admin_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.admin_backend.Entity.SuperUserEntity;
import com.example.admin_backend.Entity.SuperUserProfileEntity;

public interface SuperUserProfileRepository extends JpaRepository<SuperUserProfileEntity, Integer> {

    // This will create a query based on the 'superuser' field in the SuperUserProfileEntity
    SuperUserProfileEntity findBySuperuser(SuperUserEntity superuser);
}
