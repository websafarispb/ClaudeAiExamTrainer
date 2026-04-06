import { useLanguage } from "../i18n/LanguageContext";

export default function QuestionCard({
  question,
  selectedOptionId,
  setSelectedOptionId,
  onSubmit,
  isAnswered,
}) {
  const { t } = useLanguage();

  if (!question) {
    return null;
  }

  return (
    <div className="card">
      <div className="badges">
        <span className="badge blue">
          {t.section}: {question.section}
        </span>
        <span className="badge">
          {t.difficulty}: {question.difficulty}
        </span>
      </div>

      <h3>{question.text}</h3>

      <div>
        {question.options?.map((option) => (
          <div
            key={option.id}
            className={`option ${selectedOptionId === option.id ? "selected" : ""}`}
            onClick={() => !isAnswered && setSelectedOptionId(option.id)}
          >
            {option.text}
          </div>
        ))}
      </div>

      {!isAnswered && (
        <button style={{ marginTop: "16px" }} onClick={onSubmit}>
          {t.submit}
        </button>
      )}
    </div>
  );
}