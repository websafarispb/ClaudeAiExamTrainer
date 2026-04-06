import { useEffect, useState } from "react";
import FilterBar from "../components/FilterBar";
import QuestionCard from "../components/QuestionCard";
import AnswerResult from "../components/AnswerResult";
import { getRandomQuestion, submitAnswer, getSections } from "../api/questionApi";
import { useLanguage } from "../i18n/LanguageContext";

export default function PracticePage() {
  const { t } = useLanguage();

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
      setError(e?.response?.data?.message || t.loadingSectionsError);
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
      setError(e?.response?.data?.message || t.loadQuestionError);
    }
  };

  const handleSubmit = async () => {
    if (!question || !selectedOptionId) return;

    try {
      setError("");
      const data = await submitAnswer(question.id, selectedOptionId);
      setResult(data);
    } catch (e) {
      setError(e?.response?.data?.message || t.submitAnswerError);
    }
  };

  const handleNext = async () => {
    await loadQuestion();
  };

  return (
    <div className="container">
      <div className="page-header">
        <h2 className="page-title">{t.practiceTitle}</h2>
        <p className="page-subtitle">{t.practiceSubtitle}</p>
      </div>

      <div className="card">
        <FilterBar
          sections={sections}
          section={section}
          setSection={setSection}
          sourceType={sourceType}
          setSourceType={setSourceType}
          onLoadQuestion={loadQuestion}
        />

        {!question && !error && (
          <div className="spacer-top">
            <p className="empty-state">{t.noQuestionLoaded}</p>
          </div>
        )}

        {error && (
          <p style={{ color: "red", marginTop: "12px" }}>
            {error}
          </p>
        )}
      </div>

      {question && (
        <QuestionCard
          question={question}
          selectedOptionId={selectedOptionId}
          setSelectedOptionId={setSelectedOptionId}
          onSubmit={handleSubmit}
          isAnswered={!!result}
        />
      )}

      <AnswerResult result={result} onNext={handleNext} />
    </div>
  );
}