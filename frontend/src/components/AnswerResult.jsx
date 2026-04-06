import { useLanguage } from "../i18n/LanguageContext";

export default function AnswerResult({ result, onNext }) {
  const { t } = useLanguage();

  if (!result) return null;

  return (
    <div className="card">
      <div className={`result ${result.correct ? "correct" : "incorrect"}`}>
        {t.result}: {result.correct ? t.correct : t.incorrect}
      </div>

      <p>
        <strong>{t.correctAnswer}:</strong> {result.correctAnswer}
      </p>

      <p>
        <strong>{t.explanation}:</strong> {result.explanation}
      </p>

      <button onClick={onNext}>{t.next}</button>
    </div>
  );
}