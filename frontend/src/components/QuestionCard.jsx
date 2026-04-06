export default function QuestionCard({
  question,
  selectedOptionId,
  setSelectedOptionId,
  onSubmit,
  isAnswered,
}) {
  if (!question) return <div className="card compact">
                          <p className="empty-state">
                            No question loaded yet. Select filters and click <strong>Get random question</strong>.
                          </p>
                        </div>

  return (
    <div className="card">
      <div className="badges">
        <span className="badge blue">{question.section}</span>
        <span className="badge">{question.difficulty}</span>
        <span className="badge">{question.sourceType}</span>
      </div>

      <h3>{question.text}</h3>

      <div>
        {question.options.map((opt) => (
          <div
            key={opt.id}
            className={`option ${selectedOptionId === opt.id ? "selected" : ""}`}
            onClick={() => !isAnswered && setSelectedOptionId(opt.id)}
          >
            {opt.text}
          </div>
        ))}
      </div>

      {!isAnswered && (
        <button style={{ marginTop: "16px" }} onClick={onSubmit}>
          Submit
        </button>
      )}
    </div>
  );
}