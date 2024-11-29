package com.example.admin_backend.Service;

import com.example.admin_backend.Entity.*;
import com.example.admin_backend.Repository.*;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.example.admin_backend.Repository.ReportRepository;


@Service
public class UserReportService {

    private final KeywordRepository keywordRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final LocationService locationService;
    private final SynonymRepository synonymRepository;
    private final FeedbackService feedbackService;

    private final Map<String, List<String>> keywordToOfficesMap;
    private final Map<String, List<String>> synonymToOfficesMap;

    public UserReportService(
            KeywordRepository keywordRepository,
            ReportRepository reportRepository,
            UserRepository userRepository,
            LocationService locationService,
            SynonymRepository synonymRepository,
            FeedbackService feedbackService) {
        this.keywordRepository = keywordRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.locationService = locationService;
        this.synonymRepository = synonymRepository;
        this.feedbackService = feedbackService;

        keywordToOfficesMap = new HashMap<>();
        synonymToOfficesMap = new HashMap<>();
        initializeConcernedOfficeMappings();
    }

  public ReportEntity updateFlagStatus(int reportId, Boolean isFlagged) {
    ReportEntity report = reportRepository.findByReportId(reportId).orElse(null);

    if (report != null) {
        report.setIsFlagged(isFlagged);
        return reportRepository.save(report);
    }
    return null; // Report not found
}


    public ReportEntity submitReport(String description, List<String> imagePaths, UserEntity user,
                                   Double latitude, Double longitude, String buildingName) {
        validateAndSanitizeDescription(description);

        if (buildingName == null || buildingName.trim().isEmpty()) {
            buildingName = locationService.findNearestBuilding(latitude, longitude);
        }

        ReportEntity report = new ReportEntity(
            description,
            buildingName,
            imagePaths.size() > 0 ? imagePaths.get(0) : null,
            imagePaths.size() > 1 ? imagePaths.get(1) : null,
            imagePaths.size() > 2 ? imagePaths.get(2) : null,
            classifyReport(description),
            "",
            user,
            LocalDateTime.now(),
            ReportStatus.PENDING,
            false,
            latitude,
            longitude
        );

        String concernedOffices = determineConcernedOffice(description, report);
        report.setConcernedOffice(concernedOffices);

        reportRepository.saveAndFlush(report);

        feedbackService.createFeedbackForReport(report);

        if (latitude != null && longitude != null) {
            locationService.saveUserLocation(latitude, longitude, user.getIdNumber(), buildingName);
        }

        return report;
    }

    private void validateAndSanitizeDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (description.length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters long");
        }
        if (description.length() > 500) {
            throw new IllegalArgumentException("Description must be less than 500 characters");
        }
    }

    private String classifyReport(String description) {
        if (containsKeywordsOrSynonyms(description, getKeywordsByCategory(KeywordCategory.CRITICALEMERGENCY))) {
            return "Critical Emergency";
        }
        if (containsKeywordsOrSynonyms(description, getKeywordsByCategory(KeywordCategory.URGENTSITUATION))) {
            return "Urgent Situation";
        }
        return "General Report";
    }

    public List<String> getKeywordsByCategory(KeywordCategory category) {
        return keywordRepository.findByCategory(category)
            .stream()
            .map(KeywordEntity::getKeywordName)
            .collect(Collectors.toList());
    }

    private boolean containsKeywordsOrSynonyms(String description, List<String> keywords) {
        String lowerDescription = description.toLowerCase();
        for (String keyword : keywords) {
            if (lowerDescription.contains(keyword.toLowerCase())) {
                return true;
            }
            for (String synonym : getSynonyms(keyword)) {
                if (lowerDescription.contains(synonym.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getSynonyms(String keyword) {
        Optional<KeywordEntity> keywordEntity = keywordRepository.findByKeywordName(keyword);
        if (keywordEntity.isPresent()) {
            return synonymRepository.findByKeywordId(keywordEntity.get().getId())
                .stream()
                .map(SynonymEntity::getSynonymName)
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String determineConcernedOffice(String description, ReportEntity report) {
        String lowerCaseDescription = description.toLowerCase();
        List<String> concernedOffices = new ArrayList<>();

        keywordToOfficesMap.forEach((keyword, offices) -> {
            if (lowerCaseDescription.contains(keyword)) {
                concernedOffices.addAll(offices);
            }
        });

        synonymToOfficesMap.forEach((synonym, offices) -> {
            if (lowerCaseDescription.contains(synonym)) {
                concernedOffices.addAll(offices);
            }
        });

        if (concernedOffices.isEmpty()) {
            concernedOffices.add("General Office");
            report.setRequiresReview(true);
            logUnmatchedReport(description);
        }

        return String.join(", ", new HashSet<>(concernedOffices));
    }

    private void initializeConcernedOfficeMappings() {
        keywordToOfficesMap.put("emergency", Arrays.asList("Safety Office", "Health Services"));
        // Add more mappings as needed
    }

    private void logUnmatchedReport(String description) {
        try (FileWriter writer = new FileWriter("unmatched_reports.log", true)) {
            writer.write(LocalDateTime.now() + ": " + description + "\n");
        } catch (IOException e) {
            System.err.println("Error logging unmatched report: " + e.getMessage());
        }
    }

    public UserEntity findUserById(int userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    public List<ReportEntity> getReportsByUserId(int userId) {
        List<ReportEntity> reports = reportRepository.findByUser_UserId(userId);
        reports.sort((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()));
        return reports;
    }

    public Map<String, Integer> getReportStatusCounts(int userId) {
        Map<String, Integer> statusCounts = new HashMap<>();
        statusCounts.put("pending", reportRepository.countByStatusAndUser_UserId(ReportStatus.PENDING, userId));
        statusCounts.put("acknowledged", reportRepository.countByStatusAndUser_UserId(ReportStatus.ACKNOWLEDGED, userId));
        statusCounts.put("in_progress", reportRepository.countByStatusAndUser_UserId(ReportStatus.IN_PROGRESS, userId));
        statusCounts.put("resolved", reportRepository.countByStatusAndUser_UserId(ReportStatus.RESOLVED, userId));
        return statusCounts;
    }

    public void addOfficesToKeyword(String keywordName, List<String> newOffices) {
        KeywordEntity keywordEntity = keywordRepository.findByKeywordName(keywordName)
                .orElseThrow(() -> new RuntimeException("Keyword not found: " + keywordName));

        List<String> offices = keywordEntity.getOffices();
        if (offices == null) {
            offices = new ArrayList<>();
        }

        for (String office : newOffices) {
            if (!offices.contains(office)) {
                offices.add(office);
            }
        }

        keywordEntity.setOffices(offices);
        keywordRepository.save(keywordEntity);
    }
}