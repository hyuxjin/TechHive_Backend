package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.KeywordEntity;
import com.example.admin_backend.Entity.KeywordCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<KeywordEntity, Integer> {
    
    Optional<KeywordEntity> findByKeywordName(String keywordName);

    List<KeywordEntity> findByCategory(KeywordCategory category);

    boolean existsByKeywordName(String keywordName);
}
