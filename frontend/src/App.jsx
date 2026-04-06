import { BrowserRouter, NavLink, Route, Routes } from "react-router-dom";
import PracticePage from "./pages/PracticePage";
import AiGeneratePage from "./pages/AiGeneratePage";
import TestPage from "./pages/TestPage";
import PracticalTasksPage from "./pages/PracticalTasksPage";
import { useLanguage } from "./i18n/LanguageContext";

export default function App() {
  const { language, setLanguage, t } = useLanguage();

  return (
    <BrowserRouter>
      <header className="app-header">
        <div className="app-header-inner">
          <div className="app-brand">
            <span className="app-brand-title">{t.appTitle}</span>
            <span className="app-brand-subtitle">{t.mvp}</span>
          </div>

          <nav className="app-nav">
            <NavLink
              to="/"
              end
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              {t.navPractice}
            </NavLink>

            <NavLink
              to="/test"
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              {t.navMiniTest}
            </NavLink>

            <NavLink
              to="/ai"
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              {t.navAiGenerate}
            </NavLink>

            <NavLink
              to="/practical"
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              {t.navPracticalTasks}
            </NavLink>
          </nav>

          <div className="language-switcher">
            <button
              className={language === "EN" ? "lang-btn active" : "lang-btn"}
              onClick={() => setLanguage("EN")}
            >
              EN
            </button>
            <button
              className={language === "RU" ? "lang-btn active" : "lang-btn"}
              onClick={() => setLanguage("RU")}
            >
              RU
            </button>
          </div>
        </div>
      </header>

      <main className="app-main">
        <Routes>
          <Route path="/" element={<PracticePage />} />
          <Route path="/test" element={<TestPage />} />
          <Route path="/ai" element={<AiGeneratePage />} />
          <Route path="/practical" element={<PracticalTasksPage />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}