package com.example.admin_backend.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "synonyms")
public class SynonymEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "synonym_name", nullable = false)
    private String synonymName;

    @ManyToOne
    @JoinColumn(name = "keyword_id", nullable = false)
    private KeywordEntity keyword;

    // Default constructor
    public SynonymEntity() {}

    // Parameterized constructor
    public SynonymEntity(String synonymName, KeywordEntity keyword) {
        this.synonymName = synonymName;
        this.keyword = keyword;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSynonymName() { return synonymName; }
    public void setSynonymName(String synonymName) { this.synonymName = synonymName; }

    public KeywordEntity getKeyword() { return keyword; }
    public void setKeyword(KeywordEntity keyword) { this.keyword = keyword; }
}
