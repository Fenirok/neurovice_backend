# Questionnaire Functionality Report

## Overview
This document provides a comprehensive explanation of the questionnaire functionality in the NeuroVice Backend project. The questionnaire system manages assessment sections, questions, and calculates diagnostic metrics based on user responses.

---

## 1. Model Classes

### 1.1 `questionaires` Class
**Package:** `com.project.neurovice_backend.model`  
**Purpose:** Entity class representing a questionnaire section stored in the database.

#### Class Annotations
- `@Entity` - Marks this class as a JPA entity
- `@Table(name = "questionaires")` - Maps to the `questionaires` database table
- `@Data` - Lombok annotation that generates getters, setters, toString, equals, and hashCode methods

#### Fields and Properties

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| `id` | `Long` | Primary key identifier | Auto-generated, unique |
| `sectionId` | `String` | Unique identifier for the section | Not null, unique |
| `sectionName` | `String` | Display name of the section | Optional |
| `description` | `String` | Description of the questionnaire section | Optional |
| `disorderType` | `String` | Type of disorder this section assesses | Not null |
| `questions` | `List<Map<String, Object>>` | JSON array of questions and their properties | Not null, stored as JSONB |
| `displayOrder` | `Integer` | Order in which section appears | Optional |
| `isActive` | `Boolean` | Whether the section is currently active | Default: true |

#### Key Features
- Uses PostgreSQL JSONB for flexible question storage
- Supports dynamic question structures via `Map<String, Object>`
- Enables soft deletion through `isActive` flag
- Maintains display ordering for UI presentation

---

### 1.2 `QuestionnaireMetrics` Class
**Package:** `com.project.neurovice_backend.model`  
**Purpose:** Entity class storing calculated diagnostic metrics from completed questionnaires.

#### Class Annotations
- `@Entity` - Marks this class as a JPA entity
- `@Table(name = "questionnaire_metrics")` - Maps to the `questionnaire_metrics` database table
- `@Data` - Lombok annotation for automatic getters/setters

#### Fields and Properties

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| `id` | `Long` | Primary key identifier | Auto-generated |
| `assessmentId` | `Integer` | Foreign key to assessment | Not null |
| `childId` | `Integer` | Foreign key to child | Not null |
| `inattention` | `Integer` | Inattention disorder score (0 or 1) | Default: 0 |
| `hyperactivity` | `Integer` | Hyperactivity disorder score (0 or 1) | Default: 0 |
| `combined` | `Integer` | Combined ADHD type score (0 or 1) | Default: 0 |
| `odd` | `Integer` | Oppositional Defiant Disorder score (0 or 1) | Default: 0 |
| `conduct` | `Integer` | Conduct disorder score (0 or 1) | Default: 0 |
| `anxietyDepression` | `Integer` | Anxiety/Depression score (0 or 1) | Default: 0 |
| `createdAt` | `LocalDateTime` | Timestamp of record creation | Auto-generated, read-only |

#### Key Features
- Stores binary diagnostic results (0 = negative, 1 = positive)
- Automatically timestamped on creation
- Linked to both assessment and child entities

---

## 2. Repository Interfaces

### 2.1 `questionairesRepository` Interface
**Package:** `com.project.neurovice_backend.repository`  
**Purpose:** Data access layer for questionnaire sections using Spring Data JPA.

#### Interface Declaration
```java
public interface questionairesRepository extends JpaRepository<questionaires, Long>
```

#### Methods

##### `findAllByIsActiveTrueOrderByDisplayOrder()`
**Purpose:** Retrieves all active questionnaire sections ordered by display order.

**Input Parameters:** None

**Output:** `List<questionaires>` - List of active questionnaire sections sorted by display order

**Usage:** Used to fetch questionnaires for display in the UI, ensuring only active sections are shown.

---

##### `findBySectionId(String sectionId)`
**Purpose:** Finds a specific questionnaire section by its unique section ID.

**Input Parameters:**
- `sectionId` (String) - The unique identifier of the section to find

**Output:** `Optional<questionaires>` - Optional containing the questionnaire if found, empty otherwise

**Usage:** Used to validate section existence and retrieve section details during assessment submission.

---

##### `countByIsActiveTrue()`
**Purpose:** Counts the total number of active questionnaire sections.

**Input Parameters:** None

**Output:** `int` - Count of active sections

**Usage:** Used to determine the total number of sections required for assessment completion.

---

### 2.2 `QuestionnaireMetricsRepository` Interface
**Package:** `com.project.neurovice_backend.repository`  
**Purpose:** Data access layer for questionnaire metrics using Spring Data JPA.

#### Interface Declaration
```java
public interface QuestionnaireMetricsRepository extends JpaRepository<QuestionnaireMetrics, Long>
```

#### Methods

##### `findByAssessmentId(Integer assessmentId)`
**Purpose:** Retrieves questionnaire metrics for a specific assessment.

**Input Parameters:**
- `assessmentId` (Integer) - The assessment ID to find metrics for

**Output:** `Optional<QuestionnaireMetrics>` - Optional containing metrics if found, empty otherwise

**Usage:** Used to check if metrics already exist for an assessment before creating or updating them.

---

## 3. Service Layer

### 3.1 `QuestionnaireService` Class
**Package:** `com.project.neurovice_backend.service`  
**Purpose:** Business logic layer for questionnaire operations.

#### Class Annotations
- `@Service` - Marks this class as a Spring service component
- `@RequiredArgsConstructor` - Lombok annotation that generates a constructor for final fields

#### Dependencies
- `questionairesRepository` - Injected repository for data access

#### Methods

##### `getActiveSections()`
**Purpose:** Retrieves all active questionnaire sections ordered by display order.

**Input Parameters:** None

**Output:** `List<questionaires>` - List of active questionnaire sections

**Functionality:**
- Calls repository method to fetch active sections
- Returns sections sorted by display order for consistent UI presentation

**Usage:** Called by controller to provide questionnaires to frontend clients.

---

##### `countActiveSections()`
**Purpose:** Returns the count of active questionnaire sections.

**Input Parameters:** None

**Output:** `int` - Number of active sections

**Functionality:**
- Retrieves all active sections and returns the size of the list
- Used to determine completion criteria for assessments

**Usage:** Used by AssessmentService to check if all required sections have been completed.

---

## 4. Controller Layer

### 4.1 `QuestionnaireController` Class
**Package:** `com.project.neurovice_backend.controller`  
**Purpose:** REST API endpoint handler for questionnaire-related operations.

#### Class Annotations
- `@RestController` - Marks this class as a REST controller
- `@RequestMapping("/questionaires")` - Base URL path for all endpoints in this controller
- `@RequiredArgsConstructor` - Lombok annotation for dependency injection

#### Dependencies
- `QuestionnaireService` - Injected service for business logic

#### Endpoints

##### `GET /questionaires/getallquestionaires`
**Purpose:** Retrieves all active questionnaire sections for display.

**HTTP Method:** GET

**Request Parameters:** None

**Request Body:** None

**Response:** `List<questionaires>` - JSON array of active questionnaire sections

**Response Status Codes:**
- `200 OK` - Successfully retrieved questionnaires

**Functionality:**
- Delegates to `QuestionnaireService.getActiveSections()`
- Returns only active sections ordered by display order
- Used by frontend to populate questionnaire forms

**Example Response:**
```json
[
  {
    "id": 1,
    "sectionId": "section_1",
    "sectionName": "Inattention",
    "description": "Questions about attention",
    "disorderType": "ADHD",
    "questions": [
      {"q1": "Question text", "type": "scale"},
      {"q2": "Question text", "type": "scale"}
    ],
    "displayOrder": 1,
    "isActive": true
  }
]
```

---

## 5. Integration with Assessment System

### Questionnaire Usage in Assessment Flow

The questionnaire system integrates with the assessment system through the `AssessmentService`:

1. **Section Submission:** When a user submits answers for a questionnaire section, `AssessmentService.submitSection()` validates the section exists using `questionairesRepository.findBySectionId()`

2. **Completion Check:** `AssessmentService` uses `QuestionnaireService.countActiveSections()` to determine if all required sections are completed

3. **Diagnostic Calculation:** Upon completion, `AssessmentService.computeDiagnosis()` calculates diagnostic metrics and saves them to `QuestionnaireMetrics` via `QuestionnaireMetricsRepository`

4. **Metrics Storage:** Diagnostic results (inattention, hyperactivity, combined, ODD, conduct, anxiety/depression) are stored as binary flags (0 or 1) in the `QuestionnaireMetrics` table

---

## 6. Data Flow Summary

```
Client Request
    ↓
QuestionnaireController.getAll()
    ↓
QuestionnaireService.getActiveSections()
    ↓
questionairesRepository.findAllByIsActiveTrueOrderByDisplayOrder()
    ↓
Database Query
    ↓
List<questionaires> Response
```

---

## 7. Key Design Patterns

1. **Repository Pattern:** Separation of data access logic from business logic
2. **Service Layer Pattern:** Business logic encapsulated in service classes
3. **RESTful API:** Standard HTTP methods for resource access
4. **Soft Delete:** Using `isActive` flag instead of hard deletion
5. **JSON Storage:** Flexible question structure using PostgreSQL JSONB

---

## 8. Database Schema

### `questionaires` Table
- Stores questionnaire sections with JSONB questions
- Supports ordering and activation/deactivation
- Unique constraint on `section_id`

### `questionnaire_metrics` Table
- Stores calculated diagnostic results
- Linked to assessments and children
- Binary scoring system (0/1) for each disorder type

---

## Conclusion

The questionnaire functionality provides a flexible system for managing assessment sections, questions, and diagnostic metrics. The architecture follows Spring Boot best practices with clear separation of concerns across Model, Repository, Service, and Controller layers.






