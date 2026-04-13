import { useEffect, useState } from "react";
import FilterBar from "../components/FilterBar";
import QuestionCard from "../components/QuestionCard";
import AnswerResult from "../components/AnswerResult";
import { getRandomQuestion, submitAnswer, getSections } from "../api/questionApi";
import { translateQuestion } from "../api/aiApi";
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
  const [translated, setTranslated] = useState(null);
  const [isTranslating, setIsTranslating] = useState(false);
  const [provider, setProvider] = useState("CLAUDE");


  useEffect(() => {
    loadSections();
  }, []);

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
      setTranslated(null);

      const data = await getRandomQuestion(section, sourceType);
      setQuestion(data);
    } catch (e) {
      setQuestion(null);
      setResult(null);
      setError(e?.response?.data?.message || t.loadQuestionError);
    }
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

      {question && (
        <>
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
        </>
      )}

      <AnswerResult result={result} onNext={handleNext} />

      {translated && (
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