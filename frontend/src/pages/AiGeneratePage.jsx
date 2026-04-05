import { useState } from "react";
import { generateQuestion, generateAndSaveQuestion } from "../api/aiApi";

export default function AiGeneratePage() {
  const [domain, setDomain] = useState("Prompt Engineering & Structured Output");
  const [difficulty, setDifficulty] = useState("HARD");
  const [mode, setMode] = useState("standard");
  const [generated, setGenerated] = useState(null);
  const [saved, setSaved] = useState(null);
  const [error, setError] = useState("");

  const payload = { domain, difficulty, mode };

  const handleGenerate = async () => {
    try {
      setError("");
      setSaved(null);
      const data = await generateQuestion(payload);
      setGenerated(data);
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to generate question");
    }
  };

  const handleGenerateAndSave = async () => {
    try {
      setError("");
      const data = await generateAndSaveQuestion(payload);
      setSaved(data);
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to generate and save question");
    }
  };

  return (
    <div className="container">
      <div className="page-header">
        <h2 className="page-title">AI Generate</h2>
        <p className="page-subtitle">
          Generate new scenario-based questions for targeted exam preparation.
        </p>
      </div>

      <div className="card">
        <div className="controls-row">
          <input
            value={domain}
            onChange={(e) => setDomain(e.target.value)}
            style={{ padding: "8px", minWidth: "320px" }}
          />

          <select value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HARD">HARD</option>
          </select>

          <select value={mode} onChange={(e) => setMode(e.target.value)}>
            <option value="standard">standard</option>
            <option value="harder">harder</option>
            <option value="similar">similar</option>
          </select>

          <button onClick={handleGenerate}>Generate</button>
          <button onClick={handleGenerateAndSave}>Generate & Save</button>
        </div>

        {!generated && !saved && !error && (
          <div className="spacer-top">
            <p className="empty-state">
              Choose a domain, difficulty, and mode, then generate a new question.
            </p>
          </div>
        )}

        {error && (
          <p style={{ color: "red", marginTop: "12px" }}>
            {error}
          </p>
        )}
      </div>

      {generated && (
        <div className="card">
          <div className="badges">
            <span className="badge blue">{generated.domain}</span>
            <span className="badge">{generated.difficulty}</span>
          </div>

          <h3>{generated.question}</h3>

          <div>
            {generated.options?.map((option) => (
              <div key={option.letter} className="option">
                <strong>{option.letter}.</strong> {option.text}
              </div>
            ))}
          </div>

          <div className="spacer-top">
            <p>
              <strong>Correct answer:</strong> {generated.correctAnswer}
            </p>
            <p>
              <strong>Explanation:</strong> {generated.explanation}
            </p>
          </div>
        </div>
      )}

      {saved && (
        <div className="card">
          <h3>Saved Question</h3>
          <p><strong>ID:</strong> {saved.id}</p>
          <p><strong>Section:</strong> {saved.section}</p>
          <p><strong>Difficulty:</strong> {saved.difficulty}</p>
          <p><strong>Text:</strong> {saved.text}</p>
        </div>
      )}
    </div>
  );
}