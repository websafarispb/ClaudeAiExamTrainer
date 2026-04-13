import { useState } from "react";
import {
  generatePracticalTask,
  translatePracticalTask,
  saveGeneratedPracticalTask,
  getPracticalTasks,
} from "../api/aiApi";
import { useLanguage } from "../i18n/LanguageContext";

export default function PracticalTasksPage() {
  const { t } = useLanguage();

  const [provider, setProvider] = useState("CLAUDE");
  const [domain, setDomain] = useState("Tool Design & MCP Integration");
  const [difficulty, setDifficulty] = useState("HARD");

  const [task, setTask] = useState(null);
  const [translated, setTranslated] = useState(null);
  const [error, setError] = useState("");

  const [isGenerating, setIsGenerating] = useState(false);
  const [isTranslating, setIsTranslating] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [isSaved, setIsSaved] = useState(false);

  const [sourceMode, setSourceMode] = useState("AI");
  const [savedTasks, setSavedTasks] = useState([]);
  const [selectedSavedTaskId, setSelectedSavedTaskId] = useState("");
  const [isLoadingSaved, setIsLoadingSaved] = useState(false);

  const resetGeneratedState = () => {
    setTask(null);
    setTranslated(null);
    setError("");
    setIsSaved(false);
  };

  const handleLoadSavedTasks = async () => {
    try {
      setIsLoadingSaved(true);
      setError("");
      setTask(null);
      setTranslated(null);
      setIsSaved(false);

      const data = await getPracticalTasks();
      setSavedTasks(data);

      if (data.length > 0) {
        setSelectedSavedTaskId(String(data[0].id));
        setTask(data[0]);
      } else {
        setSelectedSavedTaskId("");
      }
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to load saved tasks");
    } finally {
      setIsLoadingSaved(false);
    }
  };

  const handleSelectSavedTask = (taskId) => {
    setSelectedSavedTaskId(taskId);

    const selected = savedTasks.find((item) => String(item.id) === String(taskId));
    if (selected) {
      setTask(selected);
      setTranslated(null);
      setError("");
    }
  };

  const handleGenerate = async () => {
    try {
      setIsGenerating(true);
      setError("");
      setTask(null);
      setTranslated(null);
      setIsSaved(false);

      const data = await generatePracticalTask({
        provider,
        domain,
        difficulty,
      });

      setTask(data);
    } catch (e) {
      if (e?.response?.status === 429) {
        setError(e.response.data?.message || "ChatGPT quota exceeded. Please switch to Claude.");
      } else if (e?.response?.status === 401) {
        setError(e.response.data?.message || "OpenAI API key is invalid or missing.");
      } else if (!e?.response) {
        setError("Network error. Check your internet connection and try again.");
      } else {
        setError(e?.response?.data?.message || "Failed to generate practical task");
      }
    } finally {
      setIsGenerating(false);
    }
  };

  const handleTranslate = async () => {
    if (!task) return;

    try {
      setIsTranslating(true);
      setError("");
      setTranslated(null);

      const data = await translatePracticalTask({
        provider,
        task,
      });

      setTranslated(data);
    } catch (e) {
      if (e?.response?.status === 429) {
        setError(e.response.data?.message || "ChatGPT quota exceeded. Please switch to Claude.");
      } else if (e?.response?.status === 401) {
        setError(e.response.data?.message || "OpenAI API key is invalid or missing.");
      } else if (!e?.response) {
        setError("Network error. Check your internet connection and try again.");
      } else {
        setError(e?.response?.data?.message || "Failed to translate practical task");
      }
    } finally {
      setIsTranslating(false);
    }
  };

  const handleSave = async () => {
    if (!task || sourceMode !== "AI") return;

    try {
      setIsSaving(true);
      setError("");

      await saveGeneratedPracticalTask(task);
      setIsSaved(true);
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to save task");
    } finally {
      setIsSaving(false);
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
            value={sourceMode}
            onChange={(e) => {
              setSourceMode(e.target.value);
              setSavedTasks([]);
              setSelectedSavedTaskId("");
              resetGeneratedState();
            }}
            disabled={isGenerating || isTranslating || isSaving || isLoadingSaved}
          >
            <option value="AI">AI Generate</option>
            <option value="DB">Saved Tasks</option>
          </select>

          {sourceMode === "AI" && (
            <>
              <select
                value={provider}
                onChange={(e) => {
                  setProvider(e.target.value);
                  resetGeneratedState();
                }}
                disabled={isGenerating || isTranslating || isSaving}
              >
                <option value="CLAUDE">CLAUDE</option>
                <option value="CHATGPT">CHATGPT</option>
              </select>

              <input
                value={domain}
                onChange={(e) => {
                  setDomain(e.target.value);
                  resetGeneratedState();
                }}
                style={{ padding: "8px", minWidth: "320px" }}
                disabled={isGenerating || isTranslating || isSaving}
              />

              <select
                value={difficulty}
                onChange={(e) => {
                  setDifficulty(e.target.value);
                  resetGeneratedState();
                }}
                disabled={isGenerating || isTranslating || isSaving}
              >
                <option value="MEDIUM">MEDIUM</option>
                <option value="HARD">HARD</option>
              </select>

              <button onClick={handleGenerate} disabled={isGenerating || isTranslating || isSaving}>
                {isGenerating ? "Generating..." : t.generateTask}
              </button>

              <button
                onClick={handleTranslate}
                disabled={!task || isGenerating || isTranslating || isSaving || !!translated}
              >
                {isTranslating ? "Translating..." : "Translate to Russian"}
              </button>

              <button
                onClick={handleSave}
                disabled={!task || isGenerating || isTranslating || isSaving || isSaved}
              >
                {isSaving ? "Saving..." : "Save"}
              </button>
            </>
          )}

          {sourceMode === "DB" && (
            <>
              <button onClick={handleLoadSavedTasks} disabled={isLoadingSaved || isTranslating}>
                {isLoadingSaved ? "Loading..." : "Load saved tasks"}
              </button>

              <select
                value={selectedSavedTaskId}
                onChange={(e) => handleSelectSavedTask(e.target.value)}
                disabled={!savedTasks.length || isLoadingSaved || isTranslating}
              >
                {savedTasks.length === 0 ? (
                  <option value="">No saved tasks</option>
                ) : (
                  savedTasks.map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.title}
                    </option>
                  ))
                )}
              </select>

              <button
                onClick={handleTranslate}
                disabled={!task || isLoadingSaved || isTranslating || !!translated}
              >
                {isTranslating ? "Translating..." : "Translate to Russian"}
              </button>
            </>
          )}
        </div>

        {!task && !error && !isGenerating && !isLoadingSaved && (
          <div className="spacer-top">
            <p className="empty-state">
              {sourceMode === "AI"
                ? t.practicalHint
                : "Load a saved practical task from the database."}
            </p>
          </div>
        )}

        {isSaved && sourceMode === "AI" && (
          <p style={{ color: "#28a745", marginTop: "12px" }}>
            ✔ Practical task saved
          </p>
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
            {"domain" in task && task.domain && (
              <span className="badge blue">
                {t.domain}: {task.domain}
              </span>
            )}

            {"difficulty" in task && task.difficulty && (
              <span className="badge">
                {t.difficulty}: {task.difficulty}
              </span>
            )}

            {sourceMode === "AI" && <span className="badge">{provider}</span>}
            {sourceMode === "DB" && <span className="badge">DB</span>}
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

      {translated && (
        <div className="card">
          <div className="badges">
            <span className="badge blue">RU</span>
          </div>

          <h3>{translated.title}</h3>

          <p>
            <strong>{t.scenario}:</strong> {translated.scenario}
          </p>

          <p>
            <strong>{t.task}:</strong> {translated.task}
          </p>

          <div className="spacer-top">
            <p>
              <strong>{t.whatToCover}:</strong>
            </p>
            <ul>
              {translated.whatToCover?.map((item, index) => (
                <li key={index}>{item}</li>
              ))}
            </ul>
          </div>

          <div className="spacer-top">
            <p>
              <strong>{t.sampleApproach}:</strong> {translated.sampleApproach}
            </p>
          </div>
        </div>
      )}
    </div>
  );
}