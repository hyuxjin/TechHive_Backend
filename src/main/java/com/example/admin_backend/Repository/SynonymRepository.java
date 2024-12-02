package com.example.admin_backend.Repository;

import com.example.admin_backend.Entity.SynonymEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SynonymRepository extends JpaRepository<SynonymEntity, Integer> {

    boolean existsBySynonymName(String synonymName);

    List<SynonymEntity> findByKeywordId(Integer keywordId);
}
