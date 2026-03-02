// Version ultra-minimaliste pour diagnostiquer
console.log('🔍 App_debug.tsx: Chargement du composant App');

const App = () => {
  console.log('🔍 App_debug.tsx: Rendu du composant App');
  
  return (
    <div style={{ 
      padding: '20px', 
      background: 'linear-gradient(45deg, #ff6b6b, #4ecdc4)', 
      color: 'white',
      fontFamily: 'Arial, sans-serif',
      minHeight: '100vh'
    }}>
      <h1>🎯 FRONTEND TEST - PAGE VISIBLE!</h1>
      <p>✅ React fonctionne</p>
      <p>✅ Styles appliqués</p>
      <p>✅ Composant rendu</p>
      <p>🕐 Timestamp: {new Date().toLocaleTimeString()}</p>
    </div>
  );
};

export default App;
