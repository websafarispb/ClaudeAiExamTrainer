export default function QuestionCard({
  question,
  selectedOptionId,
  setSelectedOptionId,
  onSubmit,
  isAnswered,
}) {
  if (!question) {
    return <p>No question loaded yet.</p>;
  }

  return (
    <div style={{ border: "1px solid #ccc", padding: "20px", borderRadius: "8px" }}>
      <h3>{question.text}</h3>

      <p><strong>Section:</strong> {question.section}</p>
      <p><strong>Difficulty:</strong> {question.difficulty}</p>

      <div>
        {question.options?.map((option) => (
          <label key={option.id} style={{ display: "block", marginBottom: "10px" }}>
            <input
              type="radio"
              name="answer"
              value={option.id}
              checked={selectedOptionId === option.id}
              disabled={isAnswered}
              onChange={() => setSelectedOptionId(option.id)}
              style={{ marginRight: "8px" }}
            />
            {option.text}
          </label>
        ))}
      </div>

      {!isAnswered && (
        <button onClick={onSubmit} disabled={!selectedOptionId}>
          Submit answer
        </button>
      )}
    </div>
  );
}