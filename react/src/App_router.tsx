import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

const TestPage = () => {
  return (
    <div style={{ padding: '20px', background: 'white', color: 'black' }}>
      <h1>Page de Test - Frontend Fonctionnel!</h1>
      <p>Si vous voyez ce message, le frontend fonctionne.</p>
      <p>URL API: {import.meta.env.VITE_API_URL || 'Non configurée'}</p>
    </div>
  );
};

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<TestPage />} />
        <Route path="*" element={<TestPage />} />
      </Routes>
    </Router>
  );
};

export default App;
