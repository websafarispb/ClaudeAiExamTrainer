import { useLanguage } from "../i18n/LanguageContext";

export default function FilterBar({
  sections,
  section,
  setSection,
  sourceType,
  setSourceType,
  onLoadQuestion,
}) {
  const { t } = useLanguage();

  return (
    <div className="controls-row">
      <select value={section} onChange={(e) => setSection(e.target.value)}>
        <option value="">{t.allSections}</option>
        {sections.map((item) => (
          <option key={item} value={item}>
            {item}
          </option>
        ))}
      </select>

      <select
        value={sourceType}
        onChange={(e) => setSourceType(e.target.value)}
      >
        <option value="">{t.allSources}</option>
        <option value="STATIC">{t.static}</option>
        <option value="AI_GENERATED">{t.aiGenerated}</option>
      </select>

      <button onClick={onLoadQuestion}>{t.getRandomQuestion}</button>
    </div>
  );
}