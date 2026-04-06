# AI Exam Trainer

Simple fullstack web application for practicing AI-related exam questions.

## 🚀 Features

- Practice mode with random questions
- Filtering by section and source type
- Multiple choice questions
- Answer validation with explanation
- AI-generated questions (basic integration)

## 🛠 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- H2 (in-memory DB)

### Frontend
- React
- Vite
- Axios

---

## ▶️ How to run

### 1. Backend

```bash
./mvnw spring-boot:run 
```

Backend runs on:
👉 http://localhost:8080

### 2. Frontend
cd frontend
```bash
npm install
npm run dev
```

Frontend runs on:
👉 http://localhost:5173
 (or 5174)

### 📡 API Endpoints
GET /api/questions/random — get random question
POST /api/questions/answer — submit answer
GET /api/questions/sections — get available sections

### 📚 Data

Questions are loaded from JSON files.

### 🎯 Future Improvements
Exam mode (score, timer)
Better UI/UX
Full AI integration (Claude/OpenAI)
Authentication
Database (PostgreSQL)
Test coverage

###
👨‍💻 Author

Evgenii Stepanov email: penumbraspb@gmail.com