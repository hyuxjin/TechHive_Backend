package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.OfficeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficeRepository extends JpaRepository<OfficeEntity, Long> {
    List<OfficeEntity> findAllByStatus(boolean status);
}
