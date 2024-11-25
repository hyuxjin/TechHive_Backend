package com.example.admin_backend.Service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import com.example.admin_backend.Repository.KeywordRepository;
import com.example.admin_backend.Repository.SynonymRepository;

@Service
public class SuggestionService {

    private final KeywordRepository keywordRepository;
    private final SynonymRepository synonymRepository;
    private static final int MAX_DISTANCE = 3;  // Max allowable distance for suggestions

    public SuggestionService(KeywordRepository keywordRepository, SynonymRepository synonymRepository) {
        this.keywordRepository = keywordRepository;
        this.synonymRepository = synonymRepository;
    }

    // Fetch suggestions based on user input
    public List<String> getSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        String lowercaseInput = input.toLowerCase();

        System.out.println("Fetching suggestions for input: " + input); 

        // Fetch all keywords and synonyms from the database for fuzzy matching
        List<String> allKeywordsAndSynonyms = new ArrayList<>();
        keywordRepository.findAll().forEach(keyword -> {
            allKeywordsAndSynonyms.add(keyword.getKeywordName().toLowerCase());
            synonymRepository.findByKeywordId(keyword.getId()).forEach(synonym -> 
                allKeywordsAndSynonyms.add(synonym.getSynonymName().toLowerCase()));
        });

        // Check if input matches each word in the database with fuzzy matching
        for (String word : allKeywordsAndSynonyms) {
            if (isMatch(lowercaseInput, word)) {
                suggestions.add(word);
            }
        }

        System.out.println("Suggestions found: " + suggestions);
        return suggestions;
    }

    // Helper method to check for exact match or near match using Levenshtein distance
    private boolean isMatch(String input, String target) {
        return target.startsWith(input) || calculateLevenshteinDistance(input, target) <= MAX_DISTANCE;
    }

    // Levenshtein distance algorithm for fuzzy matching
    private int calculateLevenshteinDistance(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[len1][len2];
    }
}
