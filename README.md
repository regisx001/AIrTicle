# 🤖 AIrTicle - AI-Powered Article Validation System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue.svg)](https://spring.io/projects/spring-ai)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://postgresql.org/)

AIrTicle is a sophisticated AI-powered article validation and approval system built with **Java** and **Spring Boot**. It leverages **Spring AI** for seamless LLM integration to automate the content review process, providing intelligent analysis, detailed feedback, and automated approval decisions.

## 🌟 Features

### 🔍 **Intelligent Article Analysis**

- **Multi-Dimensional Evaluation**: Analyzes content quality, grammar, readability, originality, and SEO optimization
- **AI-Powered Scoring**: Uses advanced language models to provide comprehensive content assessment
- **Configurable Thresholds**: Customizable scoring criteria and approval thresholds

### ⚡ **Automated Workflow**

- **Asynchronous AI Analysis**: Articles are automatically submitted for AI analysis upon creation using `@Async` processing for non-blocking operations
- **Dual Analysis Modes**:
  - **Automatic**: Background AI analysis triggered on article creation
  - **Manual**: On-demand analysis via dedicated endpoint
- **Smart Decision Making**: Automatically classifies articles as `APPROVED`, `REJECTED`, or `REQUIRES_MANUAL_REVIEW`
- **Actionable Feedback**: Generates specific, AI-driven recommendations for content improvement
- **Audit Trail**: Complete history tracking of all analysis activities and status changes

### 🚀 **Modern Architecture**

- **RESTful API**: Clean, well-documented REST endpoints for all operations
- **Database Persistence**: Robust data storage with JPA/Hibernate integration
- **Async Processing**: Non-blocking AI analysis using `@Async` for optimal performance and fast response times
- **Relationship Management**: Proper entity relationships with circular reference protection
- **Dual Workflow**: Supports both automatic (background) and manual (on-demand) AI analysis

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   REST Client   │────│   Controller     │────│    Service      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │                          │
                              │                          ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend/UI   │<---│   JSON Response  │    │   AI Analysis   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                         │
                                                         ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   PostgreSQL    │────│   JPA/Hibernate  │────│   LLM Provider  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## 🔧 Technologies Used

- **Backend**: Java 21, Spring Boot 3.5.3
- **AI Integration**: Spring AI 1.0.0 (OpenAI/Groq compatible)
- **Database**: PostgreSQL with Spring Data JPA
- **Build Tool**: Maven
- **Additional**: Lombok, Jackson, Hibernate

## 📊 Database Schema

### Core Entities

#### 📝 **Articles**

```sql
CREATE TABLE articles (
    id UUID PRIMARY KEY,
    title VARCHAR NOT NULL,
    content TEXT NOT NULL,
    featured_image VARCHAR(500),
    status VARCHAR NOT NULL,
    is_published BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP,
    feedback TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    rejected_at TIMESTAMP,
    rejected_by VARCHAR,
    approved_at TIMESTAMP,
    approved_by VARCHAR
);
```

#### 📈 **Analysis Results**

```sql
CREATE TABLE analyse_results (
    id UUID PRIMARY KEY,
    article_id UUID NOT NULL REFERENCES articles(id),
    decision VARCHAR NOT NULL,
    confidence_score DOUBLE NOT NULL,
    ai_analysis TEXT,
    recommendations TEXT,
    readability_score DOUBLE,
    grammar_score DOUBLE,
    seo_score DOUBLE,
    originality_score DOUBLE,
    analyzed_at TIMESTAMP NOT NULL,
    ai_model VARCHAR NOT NULL,
    processing_time_ms INTEGER
);
```

#### 📋 **Analysis History**

```sql
CREATE TABLE analyse_histories (
    id UUID PRIMARY KEY,
    article_id UUID NOT NULL REFERENCES articles(id),
    from_status VARCHAR,
    to_status VARCHAR,
    performed_by VARCHAR NOT NULL,
    reason TEXT,
    notes TEXT,
    metadata TEXT,
    confidence_score DOUBLE,
    ai_model VARCHAR,
    processing_time_ms INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## 🚦 Article Status Flow

```mermaid
graph TD
    A[DRAFT] --> B[SUBMITTED_FOR_APPROVAL]
    B --> C[UNDER_AI_REVIEW]
    C --> D[AI_APPROVED]
    C --> E[AI_REJECTED]
    C --> F[MANUAL_REVIEW_REQUIRED]
    F --> G[APPROVED]
    F --> H[REJECTED]
    D --> I[PUBLISHED]
    G --> I[PUBLISHED]
    D --> J[ARCHIVED]
    E --> J[ARCHIVED]
    H --> J[ARCHIVED]
```

## 🛠️ Getting Started

### Prerequisites

- **Java 21+** - [Download here](https://openjdk.java.net/)
- **Maven 3.6+** - [Installation guide](https://maven.apache.org/install.html)
- **PostgreSQL 12+** - [Download here](https://www.postgresql.org/download/)
- **AI Provider API Key** - OpenAI, Groq, or compatible service

### 🔧 Installation & Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/regisx001/AIrTicle.git
   cd AIrTicle
   ```

2. **Set up PostgreSQL Database**

   ```sql
   CREATE DATABASE approve_system;
   CREATE USER admin WITH PASSWORD 'admin123';
   GRANT ALL PRIVILEGES ON DATABASE approve_system TO admin;
   ```

3. **Configure Environment Variables**

   ```bash
   export GROQ_API_KEY="your_groq_api_key_here"
   export GROQ_BASE_URL="https://api.groq.com/openai/v1"
   export GROQ_MODEL="llama3-8b-8192"
   ```

4. **Update Application Configuration**
   Edit `src/main/resources/application.yaml`:

   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/approve_system
       username: admin
       password: admin123
   ```

5. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### 📝 **Article Management**

#### Create Article (with Automatic AI Analysis)

```http
POST /api/articles
Content-Type: application/json

{
  "title": "The Future of AI in Content Creation",
  "content": "Artificial intelligence is revolutionizing how we create, review, and optimize content...",
  "featuredImage": "https://example.com/image.jpg"
}
```

**Response (Immediate):**

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "The Future of AI in Content Creation",
  "content": "Artificial intelligence is revolutionizing...",
  "status": "DRAFT",
  "isPublished": false,
  "createdAt": "2025-07-30T10:00:00",
  "updatedAt": "2025-07-30T10:00:00"
}
```

> **Note**: AI analysis starts automatically in the background using `@Async` processing. The article status will update once analysis completes.

#### Get All Articles (Paginated)

```http
GET /api/articles?page=0&size=10&sort=createdAt,desc
```

**Response:**

```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "title": "The Future of AI in Content Creation",
      "status": "APPROVED",
      "isPublished": false,
      "createdAt": "2025-07-30T10:00:00",
      "updatedAt": "2025-07-30T10:05:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

#### Get Single Article

```http
GET /api/articles/{articleId}
```

**Response:**

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "The Future of AI in Content Creation",
  "content": "Full article content...",
  "featuredImage": "https://example.com/image.jpg",
  "status": "APPROVED",
  "isPublished": false,
  "feedback": "Article demonstrates excellent quality...",
  "createdAt": "2025-07-30T10:00:00",
  "updatedAt": "2025-07-30T10:05:00",
  "approvedAt": "2025-07-30T10:05:00",
  "approvedBy": "AI System"
}
```

### 🤖 **AI Analysis**

#### Manual Article Review (On-Demand)

```http
POST /api/articles/{articleId}/review
```

**Response:**

```json
{
  "id": "456e7890-e89b-12d3-a456-426614174000",
  "decision": "APPROVED",
  "confidenceScore": 0.92,
  "aiAnalysis": "This article demonstrates excellent writing quality with clear structure and engaging content...",
  "recommendations": "Consider adding more specific examples in section 3 to enhance reader engagement...",
  "readabilityScore": 8.5,
  "grammarScore": 9.2,
  "seoScore": 7.8,
  "originalityScore": 9.1,
  "analyzedAt": "2025-07-30T10:05:00",
  "aiModel": "llama3-8b-8192",
  "processingTimeMs": 2340
}
```

> **Use Case**: Trigger manual analysis for articles that need re-evaluation or for testing purposes.

#### Get Latest Analysis Result

```http
GET /api/articles/{articleId}/review
```

**Response:**

```json
{
  "id": "456e7890-e89b-12d3-a456-426614174000",
  "articleId": "123e4567-e89b-12d3-a456-426614174000",
  "decision": "APPROVED",
  "confidenceScore": 0.92,
  "aiAnalysis": "Comprehensive analysis results...",
  "recommendations": "Improvement suggestions...",
  "readabilityScore": 8.5,
  "grammarScore": 9.2,
  "seoScore": 7.8,
  "originalityScore": 9.1,
  "analyzedAt": "2025-07-30T10:05:00",
  "aiModel": "llama3-8b-8192",
  "processingTimeMs": 2340
}
```

### 📊 **History & Tracking**

#### Get Article Analysis History

```http
GET /api/articles/{articleId}/history
```

**Response:**

```json
[
  {
    "id": "789e0123-e89b-12d3-a456-426614174000",
    "articleId": "123e4567-e89b-12d3-a456-426614174000",
    "fromStatus": "DRAFT",
    "toStatus": "APPROVED",
    "performedBy": "AI System",
    "reason": "Article meets all quality standards with high confidence score",
    "notes": "Excellent content structure and originality",
    "confidenceScore": 0.92,
    "aiModel": "llama3-8b-8192",
    "processingTimeMs": 2340,
    "createdAt": "2025-07-30T10:05:00",
    "updatedAt": "2025-07-30T10:05:00"
  },
  {
    "id": "456e7890-e89b-12d3-a456-426614174001",
    "articleId": "123e4567-e89b-12d3-a456-426614174000",
    "fromStatus": null,
    "toStatus": "DRAFT",
    "performedBy": "user123",
    "reason": "Article created",
    "notes": "Initial article submission",
    "createdAt": "2025-07-30T10:00:00",
    "updatedAt": "2025-07-30T10:00:00"
  }
]
```

## 🔄 **Analysis Workflow**

### Automatic Analysis (Default)

1. **Article Creation** → Immediate response with `DRAFT` status
2. **Background Processing** → `@Async` AI analysis starts automatically
3. **Status Update** → Article status changes to `APPROVED`, `REJECTED`, or `MANUAL_REVIEW_REQUIRED`
4. **History Logging** → Analysis results and status changes are recorded

### Manual Analysis (On-Demand)

1. **Trigger Review** → `POST /api/articles/{id}/review`
2. **Synchronous Processing** → Immediate AI analysis
3. **Direct Response** → Analysis results returned immediately
4. **Status Update** → Article status updated based on results

## 📋 **Complete API Reference**

| Method | Endpoint                     | Description                                  | Response Type          |
| ------ | ---------------------------- | -------------------------------------------- | ---------------------- |
| `GET`  | `/api/articles`              | Get paginated list of articles               | `Page<Article>`        |
| `GET`  | `/api/articles/{id}`         | Get single article by ID                     | `Article`              |
| `POST` | `/api/articles`              | Create new article (triggers async analysis) | `Article`              |
| `POST` | `/api/articles/{id}/review`  | Manual AI analysis (synchronous)             | `AnalyseResult`        |
| `GET`  | `/api/articles/{id}/review`  | Get latest analysis result                   | `AnalyseResult`        |
| `GET`  | `/api/articles/{id}/history` | Get article analysis history                 | `List<AnalyseHistory>` |

## ⚙️ Configuration

### AI Analysis Settings

The system uses configurable parameters for analysis (see `AIAnalysisConfig`):

```java
@ConfigurationProperties(prefix = "ai.analysis")
public class AIAnalysisConfig {
    private int minWordCount = 100;
    private int maxWordCount = 5000;
    private double autoApprovalThreshold = 0.8;
    private double autoRejectionThreshold = 0.4;
    private double contentQualityWeight = 0.3;
    private double grammarWeight = 0.25;
    private double originalityWeight = 0.25;
    private double seoWeight = 0.2;
}
```

### Custom Configuration Example

```yaml
ai:
  analysis:
    min-word-count: 150
    max-word-count: 4000
    auto-approval-threshold: 0.85
    auto-rejection-threshold: 0.35
    content-quality-weight: 0.35
    grammar-weight: 0.25
    originality-weight: 0.25
    seo-weight: 0.15
```

## 🔍 Analysis Criteria

The AI evaluation considers multiple factors:

### 📊 **Scoring Metrics**

1. **Content Quality (30%)**: Relevance, depth, structure, and engagement
2. **Grammar (25%)**: Language correctness, syntax, and readability
3. **Originality (25%)**: Uniqueness and plagiarism detection
4. **SEO Optimization (20%)**: Meta information, keyword usage, and structure

### 🎯 **Decision Logic**

- **Score ≥ 0.8**: ✅ **AUTO_APPROVED**
- **Score ≤ 0.4**: ❌ **AUTO_REJECTED**
- **0.4 < Score < 0.8**: 👤 **MANUAL_REVIEW_REQUIRED**

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Example Test Cases

```bash
# Test article creation (triggers automatic async AI analysis)
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Article","content":"This is a test article content with sufficient length to meet minimum requirements for analysis. The content should be comprehensive enough to trigger meaningful AI analysis and provide valuable feedback for content improvement."}'

# Check article status after creation (may still be DRAFT if analysis is ongoing)
curl -X GET http://localhost:8080/api/articles/{articleId}

# Trigger manual AI analysis (synchronous)
curl -X POST http://localhost:8080/api/articles/{articleId}/review

# Get latest analysis results
curl -X GET http://localhost:8080/api/articles/{articleId}/review

# Get complete analysis history
curl -X GET http://localhost:8080/api/articles/{articleId}/history

# Get paginated articles list
curl -X GET "http://localhost:8080/api/articles?page=0&size=5&sort=createdAt,desc"
```

### Testing Workflow

1. **Create Article**: Returns immediately with `DRAFT` status
2. **Wait for Analysis**: Background processing completes (typically 2-5 seconds)
3. **Check Status**: Article status updates to final decision
4. **Review Results**: Get detailed analysis and recommendations
5. **Track History**: View complete audit trail

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **regisx001** - _Initial work_ - [GitHub](https://github.com/regisx001)

## 🙏 Acknowledgments

- Spring AI team for excellent LLM integration
- OpenAI/Groq for providing powerful language models
- Spring Boot community for robust framework support

---

**AIrTicle** - Revolutionizing content validation with AI-powered intelligence! 🚀
