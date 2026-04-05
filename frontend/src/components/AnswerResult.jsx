export default function AnswerResult({ result, onNext }) {
  if (!result) return null;

  return (
    <div style={{ marginTop: "20px", border: "1px solid #ccc", padding: "16px", borderRadius: "8px" }}>
      <p><strong>Result:</strong> {result.correct ? "Correct" : "Incorrect"}</p>
      <p><strong>Correct answer:</strong> {result.correctAnswer}</p>
      <p><strong>Explanation:</strong> {result.explanation}</p>

      <button onClick={onNext} style={{ marginTop: "10px" }}>
        Next question
      </button>
    </div>
  );
}