export default function FilterBar({
  sections,
  section,
  setSection,
  sourceType,
  setSourceType,
  onLoadQuestion,
}) {
  return (
    <div style={{ display: "flex", gap: "12px", marginBottom: "20px", flexWrap: "wrap" }}>
      <select value={section} onChange={(e) => setSection(e.target.value)} style={{ padding: "8px", minWidth: "280px" }}>
        <option value="">All sections</option>
        {sections.map((item) => (
          <option key={item} value={item}>
            {item}
          </option>
        ))}
      </select>

      <select
        value={sourceType}
        onChange={(e) => setSourceType(e.target.value)}
        style={{ padding: "8px" }}
      >
        <option value="">All sources</option>
        <option value="STATIC">STATIC</option>
        <option value="AI_GENERATED">AI_GENERATED</option>
      </select>

      <button onClick={onLoadQuestion}>Get random question</button>
    </div>
  );
}