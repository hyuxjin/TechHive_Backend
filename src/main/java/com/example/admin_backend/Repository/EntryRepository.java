package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.EntryEntity;
import com.example.admin_backend.Entity.UserEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends JpaRepository<EntryEntity, Long> {
    List<EntryEntity> findByUser(UserEntity user);
}