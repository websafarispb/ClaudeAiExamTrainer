import { useState } from "react";
import { useLanguage } from "../i18n/LanguageContext";
import {
  generateQuestion,
  saveGeneratedQuestion,
  translateQuestion,
} from "../api/aiApi";

export default function AiGeneratePage() {
  const { t } = useLanguage();

  const [provider, setProvider] = useState("CLAUDE");
  const [domain, setDomain] = useState("Prompt Engineering & Structured Output");
  const [difficulty, setDifficulty] = useState("HARD");
  const [mode, setMode] = useState("standard");

  const [generated, setGenerated] = useState(null);
  const [saved, setSaved] = useState(null);
  const [error, setError] = useState("");
  const [isGenerating, setIsGenerating] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [isGeneratedSaved, setIsGeneratedSaved] = useState(false);
  const [translated, setTranslated] = useState(null);
  const [isTranslating, setIsTranslating] = useState(false);

  const payload = { provider, domain, difficulty, mode };

  const handleGenerate = async () => {
    setGenerated(null);
    setTranslated(null);
    setSaved(null);
    setError("");
    setIsGeneratedSaved(false);
    setIsGenerating(true);

    try {
      const result = await generateQuestion(payload);
      setGenerated(result);
      setError("");
    } catch (error) {
      if (error?.response?.status === 429) {
        setError(error.response.data?.message || "ChatGPT quota exceeded. Please switch to Claude.");
      } else if (error?.response?.status === 401) {
        setError(error.response.data?.message || "OpenAI API key is invalid or missing.");
      } else if (!error?.response) {
        setError("Network error. Check your internet connection and try again.");
      } else {
        setError(error.response.data?.message || "Failed to generate question.");
      }
    } finally {
      setIsGenerating(false);
    }
  };

  const handleTranslate = async () => {
    if (!generated) return;

    try {
      setIsTranslating(true);
      setError("");
      setTranslated(null);

      const data = await translateQuestion({
        provider,
        question: generated,
      });

      setTranslated(data);
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to translate question");
    } finally {
      setIsTranslating(false);
    }
  };

  const handleSave = async () => {
    if (!generated) return;

    try {
      setIsSaving(true);
      setError("");
      setSaved(null);

      const data = await saveGeneratedQuestion(generated);
      setSaved(data);
      setIsGeneratedSaved(true);
    } catch (e) {
      setError(e?.response?.data?.message || t.generateAndSaveError);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="container">
      <div className="page-header">
        <h2 className="page-title">{t.aiGenerateTitle}</h2>
        <p className="page-subtitle">{t.aiGenerateSubtitle}</p>
      </div>

      <div className="card">
        <div className="controls-row">
          <select
            value={provider}
            onChange={(e) => setProvider(e.target.value)}
            disabled={isGenerating || isSaving}
          >
            <option value="CLAUDE">CLAUDE</option>
            <option value="CHATGPT">CHATGPT</option>
          </select>

          <input
            value={domain}
            onChange={(e) => setDomain(e.target.value)}
            style={{ padding: "8px", minWidth: "320px" }}
            disabled={isGenerating || isSaving}
          />

          <select
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value)}
            disabled={isGenerating || isSaving}
          >
            <option value="MEDIUM">MEDIUM</option>
            <option value="HARD">HARD</option>
          </select>

          <select
            value={mode}
            onChange={(e) => setMode(e.target.value)}
            disabled={isGenerating || isSaving}
          >
            <option value="standard">standard</option>
            <option value="harder">harder</option>
            <option value="similar">similar</option>
          </select>

          <button onClick={handleGenerate} disabled={isGenerating || isSaving}>
            {isGenerating ? "Generating..." : t.generate}
          </button>

          <button
            onClick={handleSave}
            disabled={!generated || isGenerating || isSaving || isGeneratedSaved}
          >
            {isSaving ? "Saving..." : "Save"}
          </button>

          <button
            onClick={handleTranslate}
            disabled={!generated || isGenerating || isSaving || isTranslating || translated}
          >
            {isTranslating ? "Translating..." : "Translate to Russian"}
          </button>
        </div>

        {!generated && !saved && !error && !isGenerating && (
          <div className="spacer-top">
            <p className="empty-state">{t.aiGenerateHint}</p>
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

      {generated && (
        <div className="card">
          <div className="badges">
            <span className="badge blue">
              {t.domain}: {generated.domain}
            </span>
            <span className="badge">
              {t.difficulty}: {generated.difficulty}
            </span>
            <span className="badge">{provider}</span>
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
              <strong>{t.correctAnswer}:</strong> {generated.correctAnswer}
            </p>
            <p>
              <strong>{t.explanation}:</strong> {generated.explanation}
            </p>
          </div>
           {isGeneratedSaved && (
                <p style={{ color: "#28a745", marginTop: "12px", fontWeight: "500" }}>
                  ✔ Question saved successfully
                </p>
              )}
        </div>
      )}

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

          <div className="spacer-top">
            <p>
              <strong>{t.explanation}:</strong> {translated.explanation}
            </p>
          </div>
        </div>
      )}

      {saved && (
        <div className="card">
          <h3>{t.savedQuestion}</h3>
          <p><strong>{t.id}:</strong> {saved.id}</p>
          <p><strong>{t.section}:</strong> {saved.section}</p>
          <p><strong>{t.difficulty}:</strong> {saved.difficulty}</p>
          <p><strong>{t.text}:</strong> {saved.text}</p>
        </div>
      )}
    </div>
  );
}