# AIrTicle

AIrTicle is an AI-powered article approval and validation system built with Java and Spring Boot, leveraging Spring AI for LLM (Large Language Model) integration. It automates the review process for submitted articles by analyzing their content using artificial intelligence, providing detailed feedback, and making approval decisions.

## Features

- **Automated Article Analysis:** Utilizes a large language model (via Spring AI) to review articles for content quality, grammar, originality, readability, and SEO.
- **Approval Workflow:** Articles can be submitted, analyzed, and classified as `APPROVED`, `REJECTED`, or `REQUIRES_MANUAL_REVIEW` based on customizable thresholds.
- **Actionable Feedback:** The system generates specific, actionable feedback and recommendations to help authors improve their submissions.
- **REST API:** Exposes endpoints for submitting articles, triggering reviews, and retrieving approval results.
- **Configurable Scoring:** Flexible configuration for minimum/maximum word count, score thresholds, and evaluation weights (content quality, grammar, originality, SEO).
- **Persistence:** Article data and approval results are stored using Spring Data JPA.

## How It Works

1. **Article Submission:** Articles are submitted via the REST API.
2. **AI Analysis:** On review request, the article is sent to a language model which evaluates:
   - Content quality
   - Grammar
   - Appropriateness
   - Originality
   - SEO
   - Readability
3. **Decision Making:** The AI assigns an overall score and the system determines whether to approve, reject, or suggest manual review.
4. **Feedback & Recommendations:** Authors receive detailed feedback and improvement suggestions generated by the AI.
5. **Result Storage:** All analysis and approval results are persisted for tracking and future reference.

## Technologies Used

- Java
- Spring Boot
- Spring AI (LLM integration)
- Spring Data JPA
- RESTful APIs

## Getting Started

### Prerequisites

- Java 17+
- Maven
- Access to an OpenAI-compatible API or other LLM supported by Spring AI

### Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/regisx001/AIrTicle.git
   cd AIrTicle
   ```

2. **Configure AI Model:**
   - Set your LLM API key and model in `application.properties` or environment variables:
     ```
     spring.ai.openai.api-key=YOUR_API_KEY
     spring.ai.openai.chat.options.model=gpt-3.5-turbo
     ```

3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **API Endpoints:**
   - `POST /api/articles` — Submit a new article
   - `POST /api/articles/{id}/review` — Trigger AI-based review for an article
   - `GET /api/articles` — List articles
   - `GET /api/articles/review-llm` — Get the LLM model used for review

## Example Request

Submit an article:
```http
POST /api/articles
Content-Type: application/json

{
  "title": "How AI is Transforming Content Moderation",
  "content": "Artificial intelligence is changing the way we review and approve content..."
}
```

Request a review:
```http
POST /api/articles/{id}/review
```

## Configuration

You can tune the AI analysis via properties (see `AIAnalysisConfig`):

- `minWordCount`, `maxWordCount`
- `autoApprovalThreshold`, `autoRejectionThreshold`
- `contentQualityWeight`, `grammarWeight`, `originalityWeight`, `seoWeight`
- And more (see source for details).

## License

This project is licensed under the MIT License.

---

**AIrTicle** streamlines editorial processes with state-of-the-art AI, offering fast, fair, and consistent content validation and feedback.
