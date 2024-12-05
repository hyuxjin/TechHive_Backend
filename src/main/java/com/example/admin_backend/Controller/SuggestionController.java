package com.example.admin_backend.Controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.admin_backend.Service.SuggestionService;

@RestController
@RequestMapping("/api/suggestions")  // Base path for all suggestion-related APIs
@CrossOrigin(origins = "http://localhost:3000")  // Allow requests from your frontend origin
public class SuggestionController {

    private final SuggestionService suggestionService;

    // Constructor injection for SuggestionService
    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    // GET endpoint to fetch suggestions based on user input
    @GetMapping
    public List<String> getSuggestions(@RequestParam String input) {
        // Call the service to get word suggestions based on the input
        return suggestionService.getSuggestions(input);
    }
}
