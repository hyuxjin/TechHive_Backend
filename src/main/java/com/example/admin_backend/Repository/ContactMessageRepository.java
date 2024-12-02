package com.example.admin_backend.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.admin_backend.Entity.ContactMessageEntity;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessageEntity, Long> {
}
