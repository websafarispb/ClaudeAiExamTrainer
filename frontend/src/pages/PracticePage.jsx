import { useEffect, useState } from "react";
import FilterBar from "../components/FilterBar";
import QuestionCard from "../components/QuestionCard";
import AnswerResult from "../components/AnswerResult";
import { getPracticeQuestions, submitAnswer, getSections } from "../api/questionApi";
import { translateQuestion } from "../api/aiApi";
import { useLanguage } from "../i18n/LanguageContext";

export default function PracticePage() {
  const { t } = useLanguage();

  const [sections, setSections] = useState([]);
  const [section, setSection] = useState("");
  const [sourceType, setSourceType] = useState("");

  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);

  const [selectedOptionId, setSelectedOptionId] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  const [translated, setTranslated] = useState(null);
  const [isTranslating, setIsTranslating] = useState(false);
  const [provider, setProvider] = useState("CLAUDE");

  const [correctCount, setCorrectCount] = useState(0);
  const [answeredCount, setAnsweredCount] = useState(0);
  const [isFinished, setIsFinished] = useState(false);
  const [wrongQuestions, setWrongQuestions] = useState([]);

  const question = questions[currentIndex] || null;

  const scorePercent =
    answeredCount > 0 ? Math.round((correctCount / answeredCount) * 100) : 0;

  const examScore =
    answeredCount > 0 ? Math.round((correctCount / answeredCount) * 1000) : 0;

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

  const startPractice = async () => {
    try {
      setError("");
      setResult(null);
      setSelectedOptionId(null);
      setTranslated(null);

      setQuestions([]);
      setCurrentIndex(0);
      setCorrectCount(0);
      setAnsweredCount(0);
      setIsFinished(false);
      setWrongQuestions([]);

      const data = await getPracticeQuestions(section, sourceType);
      setQuestions(data);
    } catch (e) {
      setQuestions([]);
      setResult(null);
      setIsFinished(false);
      setError(e?.response?.data?.message || t.loadQuestionError);
    }
  };

  const buildTranslatableQuestion = () => {
    if (!question) return null;

    return {
      question: question.text,
      options: (question.options || []).map((option, index) => ({
        letter: ["A", "B", "C", "D"][index] || String(index + 1),
        text: option.text,
      })),
      explanation: result?.explanation || "",
    };
  };

  const handleTranslate = async () => {
    const questionToTranslate = buildTranslatableQuestion();
    if (!questionToTranslate) return;

    try {
      setIsTranslating(true);
      setError("");
      setTranslated(null);

      const data = await translateQuestion({
        provider,
        question: questionToTranslate,
      });

      setTranslated(data);
    } catch (e) {
      if (e?.response?.status === 429) {
        setError(e.response.data?.message || "AI quota exceeded. Switch to Claude.");
      } else if (e?.response?.status === 401) {
        setError(e.response.data?.message || "API key is invalid or missing.");
      } else if (!e?.response) {
        setError("Network error. Check your internet connection and try again.");
      } else {
        setError(e?.response?.data?.message || "Failed to translate question");
      }
    } finally {
      setIsTranslating(false);
    }
  };

  const handleSubmit = async () => {
    if (!question || !selectedOptionId || result) return;

    try {
      setError("");
      const data = await submitAnswer(question.id, selectedOptionId);

      setResult(data);
      setAnsweredCount((prev) => prev + 1);

      if (data.correct) {
        setCorrectCount((prev) => prev + 1);
      } else {
        setWrongQuestions((prev) => [...prev, question]);
      }
    } catch (e) {
      setError(e?.response?.data?.message || t.submitAnswerError);
    }
  };

  const handleNext = () => {
    setResult(null);
    setSelectedOptionId(null);
    setTranslated(null);

    if (currentIndex + 1 >= questions.length) {
      setIsFinished(true);
      return;
    }

    setCurrentIndex((prev) => prev + 1);
  };

  const retryWrongQuestions = () => {
    if (wrongQuestions.length === 0) {
      return;
    }

    setQuestions(wrongQuestions);

    setCurrentIndex(0);

    setCorrectCount(0);
    setAnsweredCount(0);

    setSelectedOptionId(null);
    setResult(null);
    setTranslated(null);

    setIsFinished(false);

    setWrongQuestions([]);
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
          onLoadQuestion={startPractice}
        />

        {!question && !error && !isFinished && (
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

      {question && !isFinished && (
        <div className="card compact">
          <strong>Question #{question.id}</strong>
          <span style={{ marginLeft: "12px" }}>
            Progress: {currentIndex + 1} / {questions.length}
          </span>
          <span style={{ marginLeft: "12px" }}>
            Correct: {correctCount}
          </span>
        </div>
      )}

      {isFinished && (
        <div className="card">
          <h3>Practice finished</h3>
          <p>Total questions: {answeredCount}</p>
          <p>Correct answers: {correctCount}</p>
          <p>Wrong answers: {answeredCount - correctCount}</p>
          <p>Score: {scorePercent}%</p>
          <p>Exam-style score: {examScore} / 1000</p>

          <button onClick={startPractice}>Restart practice</button>

          {wrongQuestions.length > 0 && (
            <button
              onClick={retryWrongQuestions}
              style={{ marginLeft: "10px" }}
            >
              Retry wrong answers ({wrongQuestions.length})
            </button>
          )}
        </div>
      )}

      {question && !isFinished && (
        <>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "8px",
              margin: "12px 0 16px 0",
            }}
          >
            <label style={{ fontSize: "14px", color: "#333" }}>
              Translate provider:
            </label>

            <select
              value={provider}
              onChange={(e) => setProvider(e.target.value)}
              style={{
                padding: "8px 12px",
                borderRadius: "8px",
                border: "1px solid #ccc",
                background: "#fff",
                cursor: "pointer",
              }}
            >
              <option value="CLAUDE">Claude</option>
              <option value="CHATGPT">ChatGPT</option>
            </select>
          </div>

          <QuestionCard
            question={question}
            selectedOptionId={selectedOptionId}
            setSelectedOptionId={setSelectedOptionId}
            onSubmit={handleSubmit}
            isAnswered={!!result}
          />

          <div className="card compact">
            <button
              onClick={handleTranslate}
              disabled={isTranslating || !question || !!translated}
            >
              {isTranslating ? "Translating..." : "Translate to Russian"}
            </button>
          </div>

          <AnswerResult result={result} onNext={handleNext} />
        </>
      )}

      {translated && !isFinished && (
        <div className="card">
          <div className="badges">
            <span className="badge blue">RU</span>
          </div>

          <h3>{translated.question}</h3>

          <div>
            {translated.options?.map((option) => (
              <div key={option.letter} className="option">
                <strong>{option.letter}.</strong> {option.text}
              </div>
            ))}
          </div>

          {translated.explanation && (
            <div className="spacer-top">
              <p>
                <strong>{t.explanation}:</strong> {translated.explanation}
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}