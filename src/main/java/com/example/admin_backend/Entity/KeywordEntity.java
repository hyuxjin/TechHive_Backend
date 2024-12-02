package com.example.admin_backend.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "keywords")
public class KeywordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "keyword_name", nullable = false, unique = true)
    private String keywordName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private KeywordCategory category;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SynonymEntity> synonyms;

    // New field for storing offices related to this keyword
    @Convert(converter = StringListConverter.class)
    @Column(name = "offices")
    private List<String> offices;

    // Default constructor
    public KeywordEntity() {}

    // Parameterized constructor including offices
    public KeywordEntity(String keywordName, KeywordCategory category, List<String> offices) {
        this.keywordName = keywordName;
        this.category = category;
        this.offices = offices;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeywordName() {
        return keywordName;
    }

    public void setKeywordName(String keywordName) {
        this.keywordName = keywordName;
    }

    public KeywordCategory getCategory() {
        return category;
    }

    public void setCategory(KeywordCategory category) {
        this.category = category;
    }

    public List<SynonymEntity> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<SynonymEntity> synonyms) {
        this.synonyms = synonyms;
    }

    public List<String> getOffices() {
        return offices;
    }

    public void setOffices(List<String> offices) {
        this.offices = offices;
    }
}
