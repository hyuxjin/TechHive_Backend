package com.example.admin_backend.Service;

import com.example.admin_backend.Entity.*;
import com.example.admin_backend.Repository.*;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;


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


    // Method to submit a report
    public ReportEntity submitReport(String description, List<String> imagePaths, UserEntity user,
                                     Double latitude, Double longitude, String buildingName) {

        validateAndSanitizeDescription(description);

        if (buildingName == null || buildingName.trim().isEmpty()) {
            buildingName = locationService.findNearestBuilding(latitude, longitude);
        }

        ReportEntity report = new ReportEntity(
            description, buildingName,
            imagePaths.size() > 0 ? imagePaths.get(0) : null,
            imagePaths.size() > 1 ? imagePaths.get(1) : null,
            imagePaths.size() > 2 ? imagePaths.get(2) : null,
            classifyReport(description), "",
            user, LocalDateTime.now(), ReportStatus.PENDING, false,
            latitude, longitude
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

    // Validate and sanitize the description
    private void validateAndSanitizeDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty. Please provide details.");
        }
        if (description.length() < 10) {  // Example: set a minimum of 10 characters
            throw new IllegalArgumentException("Description is too short; please provide more detail.");
        }
        if (description.length() > 500) {
            throw new IllegalArgumentException("Description is too long; please keep it under 500 characters.");
        }
        // Sanitize by removing any potentially harmful characters
        description = description.replaceAll("[^a-zA-Z0-9.,!?\\s]", "").trim();
    }

    // Method to add offices to an existing keyword
    public void addOfficesToKeyword(String keywordName, List<String> newOffices) {
        KeywordEntity keywordEntity = keywordRepository.findByKeywordName(keywordName)
                .orElseThrow(() -> new RuntimeException("Keyword not found: " + keywordName));

        List<String> offices = keywordEntity.getOffices();

        if (offices == null) {
            offices = new ArrayList<>();
        }

        // Add each new office only if it’s not already present
        for (String office : newOffices) {
            if (!offices.contains(office)) {
                offices.add(office);
            }
        }

        // Update and save the keyword entity with the new offices list
        keywordEntity.setOffices(offices);
        keywordRepository.save(keywordEntity);
    }

    private void initializeConcernedOfficeMappings() {
        // Critical Emergency keywords and their respective offices
        keywordToOfficesMap.put("medical emergency", Arrays.asList("CIT-U Clinic - Health Services", "Safety and Security Office"));
        keywordToOfficesMap.put("fire", Arrays.asList("CIT-U Clinic - Health Services", "Safety and Security Office", "Local Fire Department"));
        keywordToOfficesMap.put("unauthorized access", Arrays.asList("TSG (Technical Support Group) - Technical Assistance", "IT Security Office"));
        keywordToOfficesMap.put("campus security", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("personal safety", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("suspicious activity", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("accident", Arrays.asList("CIT-U Clinic - Health Services", "Safety and Security Office" ));
        keywordToOfficesMap.put("explosion", Arrays.asList("Safety and Security Office", "Local Fire Department", "CIT-U Clinic - Health Services", "Local Police Department"));
        keywordToOfficesMap.put("gas leak", Arrays.asList("Safety and Security Office", "PACUBAS - Janitorial Services", "Local Fire Department"));
        keywordToOfficesMap.put("active shooter", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("intruder", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("assault", Arrays.asList("Safety and Security Office", "CIT-U Clinic - Health Services"));
        keywordToOfficesMap.put("building collapse", Arrays.asList("Safety and Security Office", "CIT-U Clinic - Health Services" ));
        keywordToOfficesMap.put("emergency", Arrays.asList("CIT-U Clinic - Health Services", "Safety and Security Office"));
        keywordToOfficesMap.put("evacuation", Arrays.asList("Safety and Security Office", "CIT-U Clinic - Health Services"));
        keywordToOfficesMap.put("injury", Arrays.asList("CIT-U Clinic - Health Services", "Safety and Security Office"));
        keywordToOfficesMap.put("flood", Arrays.asList("Safety and Security Office", "CIT-U Clinic - Health Services", "University Administration", "Emergency Response Team"));
        keywordToOfficesMap.put("electrical hazard", Arrays.asList("Safety and Security Office", "CIT-U Clinic - Health Services", "TSG (Technical Support Group) - Technical Assistance"));
        keywordToOfficesMap.put("security breach", Arrays.asList("Safety and Security Office", "IT Security Office", "TSG (Technical Support Group) - Technical Assistance", "University Administration"));
        keywordToOfficesMap.put("lockdown", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("earthquake", Arrays.asList("Safety and Security Office",  "CIT-U Clinic - Health Services", "University Administration", "Emergency Response Team"));
        keywordToOfficesMap.put("blood", Arrays.asList("CIT-U Clinic - Health Services", "Safety and Security Office"));
        keywordToOfficesMap.put("suicide", Arrays.asList("CIT-U Guidance Center - Mental Health Services", "Safety and Security Office", "CIT-U Clinic - Health Services"));
        keywordToOfficesMap.put("suicide attempt", Arrays.asList("CIT-U Guidance Center - Mental Health Services", "Safety and Security Office", "CIT-U Clinic - Health Services"));
        keywordToOfficesMap.put("knife", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("fall", Arrays.asList("CIT-U Clinic - Health Services", "Safety and Security Office" ));
    
        // Urgent Situation keywords and their respective offices
        keywordToOfficesMap.put("illness", Arrays.asList("CIT-U Clinic - Health Services"));
        keywordToOfficesMap.put("minor injury", Arrays.asList("CIT-U Clinic - Health Services"));
        keywordToOfficesMap.put("peer conflict", Arrays.asList("SSO (Student Success Office) - Student Concerns", "CIT-U Guidance Center - Mental Health Services"));
        keywordToOfficesMap.put("network issues", Arrays.asList("TSG (Technical Support Group) - Technical Assistance"));
        keywordToOfficesMap.put("hardware failure", Arrays.asList("TSG (Technical Support Group) - Technical Assistance"));
        keywordToOfficesMap.put("damaged equipment", Arrays.asList("OPC (Office of the Property Custodian)"));
        keywordToOfficesMap.put("lost property", Arrays.asList("Safety and Security Office", "OPC (Office of the Property Custodian)"));
        keywordToOfficesMap.put("payment issues", Arrays.asList("FAO (Finance and Accounting Office)"));
        keywordToOfficesMap.put("academic difficulty", Arrays.asList("SSO (Student Success Office) - Student Concerns", "CIT-U Guidance Center - Mental Health Services"));
        keywordToOfficesMap.put("mental health support", Arrays.asList("CIT-U Guidance Center - Mental Health Services", "SSO (Student Success Office) - Student Concerns"));
        keywordToOfficesMap.put("counseling", Arrays.asList("CIT-U Guidance Center - Mental Health Services"));
        keywordToOfficesMap.put("cleaning request", Arrays.asList("PACUBAS - Janitorial Services"));
        keywordToOfficesMap.put("restocking supplies", Arrays.asList("PACUBAS - Janitorial Services"));
        keywordToOfficesMap.put("pest control", Arrays.asList("PACUBAS - Janitorial Services"));
        keywordToOfficesMap.put("power outage", Arrays.asList("TSG (Technical Support Group) - Technical Assistance", "Safety and Security Office"));
        keywordToOfficesMap.put("missing person", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("hazardous materials", Arrays.asList("Safety and Security Office"));
        keywordToOfficesMap.put("vandalism", Arrays.asList("Safety and Security Office", "OPC (Office of the Property Custodian)", "SSO (Student Success Office) - Student Concerns", "CIT-U Guidance Center - Mental Health Services"));
        keywordToOfficesMap.put("broken equipment", Arrays.asList("OPC (Office of the Property Custodian)"));
        keywordToOfficesMap.put("water leak", Arrays.asList("PACUBAS - Janitorial Services"));
        keywordToOfficesMap.put("technical failure", Arrays.asList("TSG (Technical Support Group) - Technical Assistance"));
    
        // General Report keywords and their respective offices
        keywordToOfficesMap.put("festival", Arrays.asList("SSO (Student Success Office) - Student Activities", "Safety and Security Office", "OPC (Office of the Property Custodian)", "CORE - Livestream, Audio/Visual Equipment", "PACUBAS - Janitorial Services", "University Administration"));
        keywordToOfficesMap.put("competition", Arrays.asList("SSO (Student Success Office) - Student Activities", "Safety and Security Office", "OPC (Office of the Property Custodian)", "CORE - Livestream, Audio/Visual Equipment", "PACUBAS - Janitorial Services", "University Administration"));
        keywordToOfficesMap.put("workshop", Arrays.asList("SSO (Student Success Office) - Student Activities", "Safety and Security Office", "OPC (Office of the Property Custodian)", "CORE - Livestream, Audio/Visual Equipment", "PACUBAS - Janitorial Services"));
        keywordToOfficesMap.put("color's day", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS", "University Administration"));
        keywordToOfficesMap.put("founder's day", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS", "University Administration"));
        keywordToOfficesMap.put("university day", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS", "University Administration"));
        keywordToOfficesMap.put("conferment day", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS", "University Administration"));
        keywordToOfficesMap.put("parangal", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS", "University Administration"));
        keywordToOfficesMap.put("crowning", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS", "University Administration"));
        keywordToOfficesMap.put("mental health awareness month", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS"));
        keywordToOfficesMap.put("intramurals", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS"));
        keywordToOfficesMap.put("acquaintance", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS"));
        keywordToOfficesMap.put("commencement rites", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS", "University Administration"));
        keywordToOfficesMap.put("graduation day", Arrays.asList("SSO", "Safety and Security Office", "OPC", "CORE", "PACUBAS", "University Administration"));
    
        // Populate the synonym-to-office map
        keywordToOfficesMap.forEach((keyword, offices) -> {
            List<String> synonyms = getSynonyms(keyword);
            for (String synonym : synonyms) {
                synonymToOfficesMap.put(synonym, offices);
            }
        });
    }

    public List<String> getKeywordsByCategory(KeywordCategory category) {
        return keywordRepository.findByCategory(category)
                                .stream()
                                .map(KeywordEntity::getKeywordName)
                                .collect(Collectors.toList());
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

    private String classifyReport(String description) {
        if (containsKeywordsOrSynonyms(description, getKeywordsByCategory(KeywordCategory.CRITICALEMERGENCY))) {
            return "Critical Emergency";
        }
        if (containsKeywordsOrSynonyms(description, getKeywordsByCategory(KeywordCategory.URGENTSITUATION))) {
            return "Urgent Situation";
        }
        return "General Report";
    }

    private boolean containsKeywordsOrSynonyms(String description, List<String> keywords) {
        for (String keyword : keywords) {
            if (description.contains(keyword)) {
                return true;
            }
            for (String synonym : getSynonyms(keyword)) {
                if (description.contains(synonym)) {
                    return true;
                }
            }
        }
        return false;
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

    private void logUnmatchedReport(String description) {
        System.out.println("Unmatched report description: " + description);

        try (FileWriter writer = new FileWriter("unmatched_reports.log", true)) {
            writer.write(LocalDateTime.now() + ": " + description + "\n");
        } catch (IOException e) {
            e.printStackTrace();
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

    public List<ReportEntity> getPendingReportsForNovember2024() {
        LocalDateTime startOfMonth = LocalDateTime.of(2024, Month.NOVEMBER, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
    
        return reportRepository.findByStatusAndSubmittedAtBetween(
            ReportStatus.PENDING,
            startOfMonth,
            endOfMonth
        );
    }
}