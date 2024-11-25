package com.example.admin_backend.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.admin_backend.Entity.KeywordCategory;
import com.example.admin_backend.Entity.KeywordEntity;
import com.example.admin_backend.Entity.SynonymEntity;
import com.example.admin_backend.Repository.KeywordRepository;
import com.example.admin_backend.Repository.SynonymRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer {

    private final KeywordRepository keywordRepository;
    private final SynonymRepository synonymRepository;

    @Value("classpath:keywords.json")
    private Resource keywordsResource;

    public DataInitializer(KeywordRepository keywordRepository, SynonymRepository synonymRepository) {
        this.keywordRepository = keywordRepository;
        this.synonymRepository = synonymRepository;
    }

    @PostConstruct
    public void initializeData() {
        insertPredefinedKeywords();
    }

    private void insertPredefinedKeywords() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Map<String, Object>>> predefinedKeywords = objectMapper.readValue(
                    keywordsResource.getInputStream(),
                    new TypeReference<Map<String, Map<String, Map<String, Object>>>>() {}
            );

            for (var categoryEntry : predefinedKeywords.entrySet()) {
                KeywordCategory category = KeywordCategory.valueOf(categoryEntry.getKey().toUpperCase());
                Map<String, Map<String, Object>> keywords = categoryEntry.getValue();

                for (var keywordEntry : keywords.entrySet()) {
                    String keyword = keywordEntry.getKey();
                    Map<String, Object> details = keywordEntry.getValue();

                    // Suppress type safety warnings for casting
                    @SuppressWarnings("unchecked")
                    List<String> synonyms = (List<String>) details.get("synonyms");

                    @SuppressWarnings("unchecked")
                    List<String> offices = (List<String>) details.get("offices");

                    KeywordEntity keywordEntity = keywordRepository
                            .findByKeywordName(keyword)
                            .orElseGet(() -> keywordRepository.save(new KeywordEntity(keyword, category, offices)));

                    for (String synonym : synonyms) {
                        if (!synonymRepository.existsBySynonymName(synonym)) {
                            SynonymEntity synonymEntity = new SynonymEntity(synonym, keywordEntity);
                            synonymRepository.save(synonymEntity);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
