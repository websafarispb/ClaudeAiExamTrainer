import { useEffect, useState } from "react";
import FilterBar from "../components/FilterBar";
import QuestionCard from "../components/QuestionCard";
import AnswerResult from "../components/AnswerResult";
import { getRandomQuestion, submitAnswer, getSections } from "../api/questionApi";

export default function PracticePage() {
  const [sections, setSections] = useState([]);
  const [section, setSection] = useState("");
  const [sourceType, setSourceType] = useState("");
  const [question, setQuestion] = useState(null);
  const [selectedOptionId, setSelectedOptionId] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    loadSections();
  }, []);

  const loadSections = async () => {
    try {
      const data = await getSections();
      setSections(data);
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to load sections");
    }
  };

  const loadQuestion = async () => {
    try {
      setError("");
      setResult(null);
      setSelectedOptionId(null);
      const data = await getRandomQuestion(section, sourceType);
      setQuestion(data);
    } catch (e) {
      setQuestion(null);
      setResult(null);
      setError(e?.response?.data?.message || "Failed to load question");
    }
  };

  const handleSubmit = async () => {
    if (!question || !selectedOptionId) return;

    try {
      setError("");
      const data = await submitAnswer(question.id, selectedOptionId);
      setResult(data);
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to submit answer");
    }
  };

  const handleNext = async () => {
    await loadQuestion();
  };

  return (
    <div style={{ maxWidth: "900px", margin: "0 auto", padding: "24px" }}>
      <h1>AI Exam Trainer</h1>
      <h2>Practice Mode</h2>

      <FilterBar
        sections={sections}
        section={section}
        setSection={setSection}
        sourceType={sourceType}
        setSourceType={setSourceType}
        onLoadQuestion={loadQuestion}
      />

      {error && <p style={{ color: "red" }}>{error}</p>}

      <QuestionCard
        question={question}
        selectedOptionId={selectedOptionId}
        setSelectedOptionId={setSelectedOptionId}
        onSubmit={handleSubmit}
        isAnswered={!!result}
      />

      <AnswerResult result={result} onNext={handleNext} />
    </div>
  );
}