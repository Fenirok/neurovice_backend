# Questionnaire Functionality Report

## Overview

This document provides a structured technical report of the questionnaire and assessment workflow currently implemented in the NeuroVice Backend project. The report describes the main classes, methods, data flow, and the required changes needed to align the implementation with the intended diagnostic and AI-based assessment pipeline.

---

## 1. QuestionnaireService Class

### Purpose

Contains the core business logic for retrieving questionnaire sections and determining completion status.

### 1.1 Method: getActiveSections()

Functionality
Retrieves all questionnaire sections that are marked as active and returns them in display order. This controls what the frontend displays to the parent during assessment.

Input
None

Output
List<questionaires>
→ A sorted list of active questionnaire sections.

Internal Working

- Calls the repository to fetch all active rows
- Orders the results by displayOrder
- Returns the final ordered list to the controller or calling service

Example Code Snippet

```java
public List<questionaires> getActiveSections() {
    return questionnaireRepository.findAllByIsActiveTrueOrderByDisplayOrder();
}
```

### 1.2 Method: countActiveSections()

Functionality
Counts how many questionnaire sections are currently active. This is used to decide whether the assessment has covered all required sections.

Input
None

Output
int
→ Total number of active questionnaire sections.

Internal Working

- Fetches all active sections
- Returns the size of the list

Example Code Snippet

```java
public int countActiveSections() {
    return questionnaireRepository.findAllByIsActiveTrueOrderByDisplayOrder().size();
}
```

---

## 2. AssessmentService Class

### Purpose

Coordinates the full assessment lifecycle, including section submission, response validation, persistence of section answers, and questionnaire diagnostic computation.

### 2.1 Method: submitSection(assessmentId, request)

Functionality
Processes answers submitted for one questionnaire section and stores them in the assessment section table.

Input

- assessmentId: Integer
- request: SectionSubmitRequest containing sectionId and answers

Output
SectionSubmitResponse
→ Returns success status and whether all sections have been completed.

Internal Working

- Validates that the assessment exists
- Validates that the requested section exists
- Checks answer values are within the valid range of 1 to 5
- Converts answers to JSON and stores them in assessment_sections
- Calculates sum and average scores for the submitted section
- Checks whether all active sections have been submitted
- Updates assessment status to QUESTIONNAIRE_COMPLETED when all sections are done

Example Code Snippet

```java
@Transactional
public SectionSubmitResponse submitSection(Integer assessmentId, SectionSubmitRequest request) {
    assessment assessment = assessmentRepository.findById(Long.valueOf(assessmentId))
            .orElseThrow(() -> new NotFoundException("Assessment not found"));

    questionaires questionnaire = questionnaireRepository
            .findBySectionId(request.getSectionId())
            .orElseThrow(() -> new NotFoundException("Section not found"));

    Map<String, Integer> answers = request.getAnswers();
    int sum = answers.values().stream().mapToInt(Integer::intValue).sum();
    double avg = answers.values().stream().mapToInt(Integer::intValue).average().orElse(0);

    return new SectionSubmitResponse(true, true);
}
```

### 2.2 Method: computeDiagnosis(assessmentId, childId)

Functionality
Computes questionnaire-based diagnostic counts using submitted responses.

Input

- assessmentId: Integer
- childId: Integer

Output
QuestionnaireMetrics object saved in the database

Internal Working

- Loads all question responses from assessment_sections
- Aggregates category counts for inattention, hyperactivity, ODD, conduct, and anxiety/depression
- Stores the computed counts in QuestionnaireMetrics

Example Code Snippet

```java
@Transactional
public void computeDiagnosis(Integer assessmentId, Integer childId) {
    Map<Integer, Integer> answers = loadAllQuestionAnswers(assessmentId);

    int inattentionCount = countScores(answers, 1, 9, 4, 5);
    int hyperactivityCount = countScores(answers, 10, 18, 4, 5);

    QuestionnaireMetrics metrics = questionnaireMetricsRepository
            .findByAssessmentId(assessmentId)
            .orElse(new QuestionnaireMetrics());

    metrics.setAssessmentId(assessmentId);
    metrics.setChildId(childId);
    metrics.setInattention(inattentionCount);
    metrics.setHyperactivity(hyperactivityCount);

    questionnaireMetricsRepository.save(metrics);
}
```

---

## 3. GameDataController Class

### Purpose

Receives game event data from the frontend and forwards it to the service layer.

### 3.1 Endpoint: POST /api/game-data

Functionality
Accepts gameplay-related metrics and passes them onward for processing.

Input
JSON request body containing gameplay statistics such as click counts, arrow input counts, accuracy values, and time-related metrics.

Output
A success response indicating that the data was stored.

Internal Working

- Validates the incoming payload
- Calls the game metrics service for processing
- Returns a response to the frontend

Example Code Snippet

```java
@PostMapping
public ResponseEntity<?> receiveGameData(@RequestBody GameDataRequest data) {
    if (data.getTotalClicks() == null || data.getArrowPressCount() == null) {
        return ResponseEntity.badRequest().body("Invalid game data payload");
    }

    service.processAndStore(data);
    return ResponseEntity.ok(Map.of("status", "stored"));
}
```

---

## 4. GameMetricsService Class

### Purpose

Contains the actual business logic for computing gameplay-based behavioral indicators and saving them.

### 4.1 Method: processAndStore(dto)

Functionality
Processes the raw game payload, calculates derived behavioral metrics, and stores them in the database.

Input
GameDataRequest object containing gameplay values

Output
Saves an ADHDRawGameMetrics record in the database.

Internal Working

- Sets the entity identifiers
- Maps request fields into raw metric columns
- Calculates base scores such as accuracy, attention decay, randomness, burst intensity, spam intensity, direction change rate, and hold impulsivity
- Computes higher-level indices such as hyperactivity, inattention, ADHD composite, ODD index, conduct index, and anxiety index
- Saves the record and triggers final metric computation

### 4.2 Supporting Calculations

The service includes several internal metric calculation methods:

- calculateAccuracy()
- calculateAttentionDecay()
- calculateArrowRandomness()
- calculateBurstIntensity()
- calculateSpamIntensity()
- calculateDirectionChangeRate()
- calculateHoldImpulsivity()
- calculateHyperactivity()
- calculateInattention()
- calculateADHDComposite()
- calculateODDIndex()
- calculateConductIndex()
- calculateAnxietyIndex()

Example Code Snippet

```java
private double calculateHyperactivity(ADHDRawGameMetrics entity) {
    double spam = entity.getSpamIntensity();
    double burst = entity.getBurstIntensity();
    double direction = entity.getDirectionChangeRate();
    double holdImpulsivity = entity.getHoldImpulsivity();

    return 0.35 * spam
            + 0.25 * burst
            + 0.25 * direction
            + 0.15 * holdImpulsivity;
}
```

---

## 5. ChildFinalMetricsService Class

### Purpose

Combines questionnaire and game metrics to generate final diagnostic indicators for a child.

### 5.1 Method: computeAndStoreFinalMetrics(childId)

Functionality
Computes a final set of behavioral metrics using the latest gameplay session, prior session history, and questionnaire-derived values.

Input
childId: Long

Output
Saves a final metrics record for the child.

Internal Working

- Loads all game sessions for the child
- Takes the latest session as the current sample
- Uses history for trend calculation
- Retrieves questionnaire-based values from the assessment section data
- Applies a weighted formula to produce final inattention, hyperactivity, ADHD composite, ODD, conduct, and anxiety metrics
- Persists the result into the final metrics table

Example Code Snippet

```java
private double calculateFinalMetric(Double questionnaire, Double latest, Double historyAvg) {
    if (questionnaire == null) questionnaire = 0.0;
    if (latest == null) latest = 0.0;
    if (historyAvg == null) historyAvg = 0.0;

    double trend = latest - historyAvg;

    return 0.4 * questionnaire
            + 0.3 * latest
            + 0.2 * historyAvg
            + 0.1 * trend;
}
```

---

## 6. AnalysisService Class

### Purpose

Executes the final analysis step by sending the computed feature set to the AI service for risk prediction.

### 6.1 Method: run(childId)

Functionality
Builds a feature map from the final metrics and sends it to the AI prediction endpoint.

Input
childId: Long

Output
AnalysisResponse containing the predicted risk score.

Internal Working

- Loads the final metrics for the child
- Converts the metrics into a feature dictionary
- Sends the feature map to the AI service
- Stores the result in the ADHD analysis table

Example Code Snippet

```java
Map<String, Object> features = new HashMap<>();
features.put("adhd_composite", nz(metrics.getAdhdComposite()));
features.put("inattention", nz(metrics.getInattention()));
features.put("hyperactivity", nz(metrics.getHyperactivity()));
features.put("anxiety_index", nz(metrics.getAnxietyIndex()));
features.put("conduct_index", nz(metrics.getConductIndex()));
features.put("odd_index", nz(metrics.getOddIndex()));

Double risk = aiService.getAdhdRisk(features);
```

---

## 7. Current Implementation Gaps and Required Changes

### 7.1 Questionnaire Logic

The current implementation partially covers questionnaire flow, but the real business logic is still spread between AssessmentService and QuestionnaireService. The recommended change is to move full submission, validation, and diagnosis responsibilities into QuestionnaireService so that the service layer is cleaner and more maintainable.

### 7.2 Diagnosis Rules

The current computeDiagnosis() method calculates raw counts, but the implementation does not yet fully apply the detailed Vanderbilt-style or report-based diagnosis thresholds. The system should explicitly determine:

- Inattention
- Hyperactivity
- Combined ADHD
- ODD
- Conduct
- Anxiety/Depression

### 7.3 Game Metric Context

The current game flow uses a hardcoded child ID and does not fully use the assessment context. The system should persist the actual childId and assessmentId associated with each gameplay session.

### 7.4 AI Pipeline Structure

The report-style architecture expects dedicated components such as:

- MetricExtractionUtility
- DataPreprocessor
- DisorderModelManager
- PredictionService

These are not yet implemented as separate classes in the current codebase. The analysis flow should be reorganized into a formal pipeline for preprocessing, prediction, and final result selection.

### 7.5 Final Metrics Integration

The final metrics service should use the questionnaire diagnosis results more directly rather than relying only on section-level averages. This will make the final child diagnosis more consistent with the intended assessment logic.

---

## 8. Summary

The current backend already contains the main building blocks for questionnaire handling, game metric computation, and AI-based analysis. However, the workflow is still partially implemented and needs further refinement in terms of service responsibility, diagnosis logic, assessment-aware game storage, and structured AI prediction processing.

---

## 9. Conclusion

The system has a strong foundation for questionnaire-based assessment and game-driven behavioral analysis. To fully meet the intended architecture described in the report, the implementation should be refactored so that questionnaire scoring, game metric extraction, and AI prediction are handled through a clearer and more modular pipeline.
