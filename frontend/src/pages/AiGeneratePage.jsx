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
    <div style={{ maxWidth: "900px", margin: "0 auto", padding: "24px" }}>
      <h1>AI Exam Trainer</h1>
      <h2>AI Generate</h2>

      <div style={{ display: "flex", gap: "12px", flexWrap: "wrap", marginBottom: "20px" }}>
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

      {error && <p style={{ color: "red" }}>{error}</p>}

      {generated && (
        <div style={{ border: "1px solid #ccc", padding: "20px", borderRadius: "8px", marginBottom: "20px" }}>
          <p><strong>Domain:</strong> {generated.domain}</p>
          <p><strong>Difficulty:</strong> {generated.difficulty}</p>
          <h3>{generated.question}</h3>

          <ul>
            {generated.options?.map((option) => (
              <li key={option.letter}>
                <strong>{option.letter}:</strong> {option.text}
              </li>
            ))}
          </ul>

          <p><strong>Correct answer:</strong> {generated.correctAnswer}</p>
          <p><strong>Explanation:</strong> {generated.explanation}</p>
        </div>
      )}

      {saved && (
        <div style={{ border: "1px solid #ccc", padding: "20px", borderRadius: "8px" }}>
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