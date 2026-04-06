import { useEffect, useState } from "react";
import { getSections, getTestQuestions, submitAnswer } from "../api/questionApi";

export default function TestPage() {
  const [sections, setSections] = useState([]);
  const [section, setSection] = useState("");
  const [sourceType, setSourceType] = useState("");
  const [count, setCount] = useState(5);

  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOptionId, setSelectedOptionId] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [finished, setFinished] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [startedAt, setStartedAt] = useState(null);
  const [retryMode, setRetryMode] = useState(false);

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

  const startTest = async () => {
    try {
      setRetryMode(false);
      setError("");
      setFinished(false);
      setResult(null);
      setAnswers([]);
      setCurrentIndex(0);
      setSelectedOptionId(null);
      setStartedAt(Date.now());

      const data = await getTestQuestions(count, section, sourceType);
      setQuestions(data);
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to start test");
    }
  };

  const currentQuestion = questions[currentIndex];

  const handleNext = () => {
    if (!selectedOptionId || !currentQuestion) return;

    const selectedOption = currentQuestion.options.find(
      (opt) => opt.id === selectedOptionId
    );

    const updatedAnswers = [
      ...answers,
      {
        questionId: currentQuestion.id,
        questionText: currentQuestion.text,
        section: currentQuestion.section,
        selectedOptionId,
        selectedAnswerText: selectedOption ? selectedOption.text : "",
      },
    ];

    setAnswers(updatedAnswers);
    setSelectedOptionId(null);

    if (currentIndex + 1 < questions.length) {
      setCurrentIndex(currentIndex + 1);
    } else {
      finishTest(updatedAnswers);
    }
  };

  const finishTest = async (finalAnswers) => {
    try {
      let correctCount = 0;
      const detailedResults = [];

      for (const answer of finalAnswers) {
        const response = await submitAnswer(
          answer.questionId,
          answer.selectedOptionId
        );

        if (response.correct) {
          correctCount++;
        }

        const originalQuestion = questions.find(
          (q) => q.id === answer.questionId
        );

        detailedResults.push({
          questionId: answer.questionId,
          questionText: answer.questionText,
          section: answer.section,
          selectedOptionId: answer.selectedOptionId,
          selectedAnswerText: answer.selectedAnswerText,
          correct: response.correct,
          correctAnswer: response.correctAnswer,
          explanation: response.explanation,
          question: originalQuestion,
        });
      }

      const total = finalAnswers.length;
      const incorrectCount = total - correctCount;
      const score1000 = Math.round((correctCount / total) * 1000);
      const elapsedMs = startedAt ? Date.now() - startedAt : 0;

      setResult({
        total,
        correct: correctCount,
        incorrect: incorrectCount,
        score1000,
        elapsedMs,
        details: detailedResults,
      });

      setFinished(true);
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to finish test");
    }
  };

  const resetTest = () => {
    setQuestions([]);
    setCurrentIndex(0);
    setSelectedOptionId(null);
    setAnswers([]);
    setFinished(false);
    setResult(null);
    setError("");
    setStartedAt(null);
    setRetryMode(false);
  };

  const retryMistakes = () => {
    if (!result) return;

    const wrongQuestions = result.details
      .filter((item) => !item.correct && item.question)
      .map((item) => item.question);

    if (!wrongQuestions.length) return;

    setQuestions(wrongQuestions);
    setCurrentIndex(0);
    setSelectedOptionId(null);
    setAnswers([]);
    setFinished(false);
    setResult(null);
    setError("");
    setStartedAt(Date.now());
    setRetryMode(true);
  };

  const formatElapsedTime = (ms) => {
    const totalSeconds = Math.floor(ms / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes}m ${seconds}s`;
  };

  const progressPercent =
    questions.length > 0
      ? Math.round((currentIndex / questions.length) * 100)
      : 0;

  return (
    <div className="container">
      <div className="page-header">
        <h2 className="page-title">
          {retryMode ? "Retry Mistakes Mode" : "Mini Test Mode"}
        </h2>
        <p className="page-subtitle">
          {retryMode
            ? "Retry only the questions you answered incorrectly."
            : "Create a short quiz to check your progress and review mistakes afterwards."}
        </p>
      </div>

      {!questions.length && !finished && (
        <div className="card">
          <div className="controls-row">
            <select value={section} onChange={(e) => setSection(e.target.value)}>
              <option value="">All sections</option>
              {sections.map((item) => (
                <option key={item} value={item}>
                  {item}
                </option>
              ))}
            </select>

            <select
              value={sourceType}
              onChange={(e) => setSourceType(e.target.value)}
            >
              <option value="">All sources</option>
              <option value="STATIC">STATIC</option>
              <option value="AI_GENERATED">AI_GENERATED</option>
            </select>

            <select value={count} onChange={(e) => setCount(Number(e.target.value))}>
              <option value={5}>5 questions</option>
              <option value={10}>10 questions</option>
              <option value={15}>15 questions</option>
            </select>

            <button onClick={startTest}>Start test</button>
          </div>

          <div className="spacer-top">
            <p className="empty-state">
              Configure your mini test and click <strong>Start test</strong>.
            </p>
          </div>

          {error && <p style={{ color: "red", marginTop: "12px" }}>{error}</p>}
        </div>
      )}

      {questions.length > 0 && !finished && currentQuestion && (
        <div className="card">
          <div className="progress-wrapper">
            <div className="progress-label">
              <span>
                Question {currentIndex + 1} of {questions.length}
              </span>
              <span>{progressPercent}% completed</span>
            </div>

            <div className="progress-bar">
              <div
                className="progress-fill"
                style={{
                  width: `${(currentIndex / questions.length) * 100}%`,
                }}
              />
            </div>
          </div>

          <div className="badges">
            <span className="badge blue">{currentQuestion.section}</span>
            <span className="badge">{currentQuestion.difficulty}</span>
          </div>

          <h3>{currentQuestion.text}</h3>

          <div>
            {currentQuestion.options.map((opt) => (
              <div
                key={opt.id}
                className={`option ${selectedOptionId === opt.id ? "selected" : ""}`}
                onClick={() => setSelectedOptionId(opt.id)}
              >
                {opt.text}
              </div>
            ))}
          </div>

          <button
            style={{ marginTop: "16px" }}
            onClick={handleNext}
            disabled={!selectedOptionId}
          >
            {currentIndex + 1 === questions.length ? "Finish test" : "Next"}
          </button>

          {error && <p style={{ color: "red", marginTop: "12px" }}>{error}</p>}
        </div>
      )}

      {finished && result && (
        <div className="card">
          <h3>Test Result</h3>
          <p><strong>Total:</strong> {result.total}</p>
          <p><strong>Correct:</strong> {result.correct}</p>
          <p><strong>Incorrect:</strong> {result.incorrect}</p>
          <p><strong>Score:</strong> {result.score1000} / 1000</p>
          <p><strong>Passing score:</strong> 700</p>
          <p>
            <strong>Status:</strong>{" "}
            {result.score1000 >= 700 ? "Passed" : "Not passed yet"}
          </p>
          <p><strong>Elapsed time:</strong> {formatElapsedTime(result.elapsedMs)}</p>

          <div style={{ marginTop: "20px" }}>
            <h4>Mistakes Review</h4>

            {result.details
              .filter((item) => !item.correct)
              .map((item, index) => (
                <div key={index} className="card" style={{ marginTop: "12px" }}>
                  <p><strong>Section:</strong> {item.section}</p>
                  <p><strong>Question:</strong> {item.questionText}</p>
                  <p><strong>Your answer:</strong> {item.selectedAnswerText}</p>
                  <p><strong>Correct answer:</strong> {item.correctAnswer}</p>
                  <p><strong>Explanation:</strong> {item.explanation}</p>
                </div>
              ))}

            {result.incorrect === 0 && (
              <p className="empty-state">Perfect score. No mistakes to review.</p>
            )}
          </div>

          <div
            style={{
              marginTop: "20px",
              display: "flex",
              gap: "12px",
              flexWrap: "wrap",
            }}
          >
            {result.incorrect > 0 && (
              <button onClick={retryMistakes}>Retry mistakes only</button>
            )}

            <button onClick={resetTest}>Start new test</button>
          </div>
        </div>
      )}
    </div>
  );
}