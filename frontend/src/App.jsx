import { BrowserRouter, Link, Route, Routes } from "react-router-dom";
import PracticePage from "./pages/PracticePage";
import AiGeneratePage from "./pages/AiGeneratePage";

export default function App() {
  return (
    <BrowserRouter>
      <div style={{ padding: "16px", borderBottom: "1px solid #ccc", marginBottom: "20px" }}>
        <nav style={{ display: "flex", gap: "16px" }}>
          <Link to="/">Practice</Link>
          <Link to="/ai">AI Generate</Link>
        </nav>
      </div>

      <Routes>
        <Route path="/" element={<PracticePage />} />
        <Route path="/ai" element={<AiGeneratePage />} />
      </Routes>
    </BrowserRouter>
  );
}