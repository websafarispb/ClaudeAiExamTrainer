import { useState } from "react";
import { generatePracticalTask } from "../api/aiApi";
import { useLanguage } from "../i18n/LanguageContext";

export default function PracticalTasksPage() {
  const { t } = useLanguage();

  const [provider, setProvider] = useState("CLAUDE");
  const [domain, setDomain] = useState("Tool Design & MCP Integration");
  const [difficulty, setDifficulty] = useState("HARD");
  const [task, setTask] = useState(null);
  const [error, setError] = useState("");
  const [isGenerating, setIsGenerating] = useState(false);

  const handleGenerate = async () => {
    try {
      setIsGenerating(true);
      setError("");
      setTask(null);

      const data = await generatePracticalTask({
        provider,
        domain,
        difficulty,
      });

      setTask(data);
    } catch (e) {
      setError(e?.response?.data?.message || t.generatePracticalTaskError);
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className="container">
      <div className="page-header">
        <h2 className="page-title">{t.practicalTitle}</h2>
        <p className="page-subtitle">{t.practicalSubtitle}</p>
      </div>

      <div className="card">
        <div className="controls-row">
          <select
            value={provider}
            onChange={(e) => setProvider(e.target.value)}
            disabled={isGenerating}
          >
            <option value="CLAUDE">CLAUDE</option>
            <option value="CHATGPT">CHATGPT</option>
          </select>

          <input
            value={domain}
            onChange={(e) => setDomain(e.target.value)}
            style={{ padding: "8px", minWidth: "320px" }}
            disabled={isGenerating}
          />

          <select
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value)}
            disabled={isGenerating}
          >
            <option value="MEDIUM">MEDIUM</option>
            <option value="HARD">HARD</option>
          </select>

          <button onClick={handleGenerate} disabled={isGenerating}>
            {isGenerating ? "Generating..." : t.generateTask}
          </button>
        </div>

        {!task && !error && !isGenerating && (
          <div className="spacer-top">
            <p className="empty-state">{t.practicalHint}</p>
          </div>
        )}

        {isGenerating && (
          <div className="spacer-top">
            <p className="empty-state">
              Claude response may take a few seconds. Please wait...
            </p>
          </div>
        )}

        {error && (
          <p style={{ color: "red", marginTop: "12px" }}>
            {error}
          </p>
        )}
      </div>

      {task && (
        <div className="card">
          <div className="badges">
            <span className="badge blue">
              {t.domain}: {task.domain}
            </span>
            <span className="badge">
              {t.difficulty}: {task.difficulty}
            </span>
            <span className="badge">{provider}</span>
          </div>

          <h3>{task.title}</h3>

          <p>
            <strong>{t.scenario}:</strong> {task.scenario}
          </p>

          <p>
            <strong>{t.task}:</strong> {task.task}
          </p>

          <div className="spacer-top">
            <p>
              <strong>{t.whatToCover}:</strong>
            </p>
            <ul>
              {task.whatToCover?.map((item, index) => (
                <li key={index}>{item}</li>
              ))}
            </ul>
          </div>

          <div className="spacer-top">
            <p>
              <strong>{t.sampleApproach}:</strong> {task.sampleApproach}
            </p>
          </div>
        </div>
      )}
    </div>
  );
}