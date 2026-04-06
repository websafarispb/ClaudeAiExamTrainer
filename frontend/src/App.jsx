import { BrowserRouter, NavLink, Route, Routes } from "react-router-dom";
import PracticePage from "./pages/PracticePage";
import AiGeneratePage from "./pages/AiGeneratePage";
import TestPage from "./pages/TestPage";

function PracticalTasksPage() {
  return (
    <div className="container">
      <div className="card">
        <h2>Practical Tasks</h2>
        <p>This page is coming next.</p>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <header className="app-header">
        <div className="app-header-inner">
          <div className="app-brand">
            <span className="app-brand-title">AI Exam Trainer</span>
            <span className="app-brand-subtitle">MVP</span>
          </div>

          <nav className="app-nav">
            <NavLink
              to="/"
              end
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              Practice
            </NavLink>

            <NavLink
              to="/test"
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              Mini Test
            </NavLink>

            <NavLink
              to="/ai"
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              AI Generate
            </NavLink>

            <NavLink
              to="/practical"
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              Practical Tasks
            </NavLink>
          </nav>
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