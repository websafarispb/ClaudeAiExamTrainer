export default function AnswerResult({ result, onNext }) {
  if (!result) return null;

  return (
    <div className="card">
      <div className={`result ${result.correct ? "correct" : "incorrect"}`}>
        Result: {result.correct ? "Correct" : "Incorrect"}
      </div>

      <p>
        <strong>Correct answer:</strong> {result.correctAnswer}
      </p>

      <p>
        <strong>Explanation:</strong> {result.explanation}
      </p>

      <button onClick={onNext}>Next question</button>
    </div>
  );
}