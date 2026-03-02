# 🎨 Internship Management - Frontend React

Application frontend React pour la gestion de stages, déployée sur Vercel avec connexion à l'API Spring Boot sur Render.

## 📋 Vue d'ensemble

### Stack technique
- **Framework** : React 19 avec TypeScript
- **Build tool** : Vite (pas Create React App)
- **Styling** : Tailwind CSS v4
- **Routing** : React Router v7
- **Animations** : Framer Motion
- **HTTP Client** : Axios
- **State Management** : Zustand
- **Forms** : React Hook Form
- **Déploiement** : Vercel

### Fonctionnalités principales
- **Authentification** avec JWT
- **Tableau de bord** par rôle
- **Gestion des offres** de stage
- **Système de candidatures**
- **Upload de fichiers** (CV, lettres de motivation)
- **Notifications** temps réel
- **Animations** de chargement
- **Interface responsive** design

## 🚀 Déploiement en Production

### Configuration de production
- **URL** : `https://your-app.vercel.app`
- **Branche** : `prod`
- **API Backend** : `https://internship-management-api.onrender.com/api`
- **Build** : Automatique sur push vers `prod`

### Variables d'environnement (Production)
```bash
# URL de l'API backend
REACT_APP_API_URL=https://internship-management-api.onrender.com/api

# Autres variables (si nécessaires)
REACT_APP_ENV=production
REACT_APP_VERSION=1.0.0
```

### Workflow de déploiement
1. **Développement** sur branche `develop`
2. **Tests** et validation
3. **Merge** vers branche `prod`
4. **Déploiement automatique** sur Vercel

```bash
# Déployer en production
git checkout prod
git merge develop
git push origin prod
```

## 🛠️ Installation et Démarrage

### Prérequis
- Node.js 18+
- npm ou yarn
- Git

### Démarrage local

#### 1. Cloner le projet
```bash
git clone <repository-url>
cd gestionStage_frontend_backend/react
```

#### 2. Installation des dépendances
```bash
npm install
# ou
yarn install
```

#### 3. Configuration locale
```bash
# Créer le fichier .env.local
cp .env.example .env.local

# Éditer avec la configuration locale
nano .env.local
```

#### 4. Démarrer l'application
```bash
npm run dev
# ou
yarn dev
```

L'application démarre sur `http://localhost:5173`

### Build de production
```bash
npm run build
# ou
yarn build
```

## 📁 Structure du projet

```
react/
├── public/
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── components/          # Composants réutilisables
│   │   ├── common/        # Header, Footer, Loading...
│   │   ├── forms/         # Formulaires réutilisables
│   │   └── ui/           # Composants UI (Button, Input...)
│   ├── pages/              # Pages principales
│   │   ├── auth/          # Login, Register...
│   │   ├── student/       # Pages étudiant
│   │   ├── teacher/       # Pages enseignant
│   │   ├── enterprise/     # Pages entreprise
│   │   └── admin/         # Pages admin
│   ├── hooks/              # Hooks personnalisés
│   │   ├── useAuth.ts      # Hook d'authentification
│   │   ├── useApi.ts       # Hook pour appels API
│   │   └── usePageLoading.ts # Hook de chargement
│   ├── services/           # Services API
│   │   ├── api.ts         # Configuration Axios
│   │   ├── authService.ts  # Service authentification
│   │   └── userService.ts  # Service utilisateurs
│   ├── store/              # State management
│   │   └── authStore.ts   # Store Zustand
│   ├── types/              # Types TypeScript
│   │   ├── auth.ts        # Types authentification
│   │   └── api.ts         # Types API
│   ├── utils/              # Utilitaires
│   │   ├── constants.ts    # Constantes
│   │   └── helpers.ts     # Fonctions utilitaires
│   ├── styles/             # Styles globaux
│   │   └── globals.css    # CSS Tailwind
│   ├── App.tsx             # Composant principal
│   └── main.tsx          # Point d'entrée
├── package.json            # Dépendances et scripts
├── tsconfig.json          # Configuration TypeScript
├── tailwind.config.js     # Configuration Tailwind
├── vite.config.ts         # Configuration Vite
├── vercel.json           # Configuration déploiement Vercel
└── README.md             # Ce fichier
```

## 🔧 Configuration

### Variables d'environnement

#### .env.example
```bash
# Configuration API
VITE_API_URL=http://localhost:9080/api

# Configuration application
VITE_ENV=development
VITE_VERSION=1.0.0

# Configuration (optionnelle)
VITE_ENABLE_MOCKS=false
VITE_LOG_LEVEL=debug
```

#### .env.local (non versionné)
```bash
# Remplacer avec vos valeurs locales
VITE_API_URL=http://localhost:9080/api
```

### Configuration Axios
```typescript
// src/services/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

## 🎨 Composants Principaux

### Structure des composants
```typescript
// Exemple de composant typé
interface ButtonProps {
  children: React.ReactNode;
  variant?: 'primary' | 'secondary';
  onClick?: () => void;
  disabled?: boolean;
}

const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  onClick,
  disabled = false,
}) => {
  return (
    <button
      className={`btn btn-${variant}`}
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
};

export default Button;
```

### Composants réutilisables
- **Header** : Navigation avec authentification
- **LoadingSpinner** : Animations de chargement
- **ProtectedRoute** : Routes protégées par JWT
- **FileUpload** : Upload de fichiers avec drag & drop
- **Modal** : Modales réutilisables
- **DataTable** : Tableaux avec pagination

## 📱 Pages et Routes

### Structure des routes
```typescript
// src/App.tsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from './components/common/ProtectedRoute';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Routes publiques */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* Routes protégées */}
        <Route path="/student/*" element={
          <ProtectedRoute requiredRole="STUDENT">
            <StudentRoutes />
          </ProtectedRoute>
        } />
        
        <Route path="/teacher/*" element={
          <ProtectedRoute requiredRole="TEACHER">
            <TeacherRoutes />
          </ProtectedRoute>
        } />
        
        <Route path="/enterprise/*" element={
          <ProtectedRoute requiredRole="ENTERPRISE">
            <EnterpriseRoutes />
          </ProtectedRoute>
        } />
        
        <Route path="/admin/*" element={
          <ProtectedRoute requiredRole="ADMIN">
            <AdminRoutes />
          </ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  );
}
```

### Pages par rôle
- **Étudiant** : Liste des stages, candidatures, profil
- **Enseignant** : Validation d'offres, gestion d'entreprises
- **Entreprise** : Publication d'offres, gestion des candidatures
- **Admin** : Tableau de bord, gestion des utilisateurs

## 🎭 Animations et Transitions

### Framer Motion
```typescript
// Exemple de transition de page
import { motion, AnimatePresence } from 'framer-motion';

const PageTransition = ({ children }) => (
  <AnimatePresence mode="wait">
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.3 }}
    >
      {children}
    </motion.div>
  </AnimatePresence>
);
```

### Animations implémentées
- **Transitions de page** : Fade et slide
- **Chargement** : Spinners et skeletons
- **Hover effects** : Survol des boutons et liens
- **Modal animations** : Apparition/disparition fluide

## 🧪 Tests

### Lancer les tests
```bash
# Tests unitaires
npm test

# Tests avec coverage
npm test -- --coverage

# Tests E2E (si configurés)
npm run test:e2e
```

### Structure des tests
```
src/
├── components/
│   └── __tests__/          # Tests des composants
├── hooks/
│   └── __tests__/          # Tests des hooks
├── services/
│   └── __tests__/          # Tests des services
└── utils/
    └── __tests__/          # Tests des utilitaires
```

## 🎨 Styling avec Tailwind CSS

### Configuration personnalisée
```javascript
// tailwind.config.js
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          emeraude: '#00A896',
          jaune: '#FFD700',
          light: '#FFFFFF',
        },
      },
      fontFamily: {
        'poiret': ['Poiret One', 'cursive'],
      },
    },
  },
  plugins: [],
};
```

### Classes utilitaires
```css
/* src/styles/globals.css */
@import 'tailwindcss/base';
@import 'tailwindcss/components';
@import 'tailwindcss/utilities';

/* Variables CSS personnalisées */
:root {
  --color-emeraude: #00A896;
  --color-jaune: #FFD700;
  --color-light: #FFFFFF;
  --font-family-poiret: 'Poiret One', cursive;
}
```

## 📱 Responsive Design

### Breakpoints utilisés
- **Mobile** : < 768px
- **Tablette** : 768px - 1024px
- **Desktop** : > 1024px

### Stratégie responsive
```typescript
// Exemple de composant responsive
const ResponsiveComponent = () => {
  const [isMobile, setIsMobile] = useState(false);

  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth < 768);
    };
    
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <div className={isMobile ? 'mobile-layout' : 'desktop-layout'}>
      {/* Contenu adaptatif */}
    </div>
  );
};
```

## 🔐 Sécurité

### JWT Management
```typescript
// src/hooks/useAuth.ts
export const useAuth = () => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [user, setUser] = useState(null);

  const login = async (credentials) => {
    try {
      const response = await authService.login(credentials);
      setToken(response.token);
      setUser(response.user);
      localStorage.setItem('token', response.token);
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
  };

  return { token, user, login, logout };
};
```

### Validation des données
- **Form validation** : React Hook Form avec Yup
- **Input sanitization** : Nettoyage des entrées utilisateur
- **XSS prevention** : Échappement des données

## 📊 Performance

### Optimisations
- **Code splitting** : Lazy loading des routes
- **Memoization** : React.memo, useMemo, useCallback
- **Bundle size** : Analyse avec vite-bundle-analyzer
- **Images** : Optimisation et formats modernes

### Monitoring
```typescript
// Exemple de monitoring des performances
const usePerformanceMonitoring = () => {
  useEffect(() => {
    // Monitoring du temps de chargement
    const observer = new PerformanceObserver((list) => {
      for (const entry of list.getEntries()) {
        console.log('Performance:', entry.name, entry.duration);
      }
    });
    
    observer.observe({ entryTypes: ['measure'] });
    
    return () => observer.disconnect();
  }, []);
};
```

## 🚨 Dépannage

### Problèmes courants

#### 1. Problèmes de connexion API
```bash
# Vérifier la configuration
echo $VITE_API_URL

# Tester la connexion
curl $VITE_API_URL/health
```

#### 2. Problèmes de build
```bash
# Nettoyer et réinstaller
rm -rf node_modules package-lock.json
npm install

# Build en mode production
npm run build
```

#### 3. Problèmes de styles
```bash
# Vérifier Tailwind
npx tailwindcss -i ./src/styles/input.css -o ./src/styles/output.css --watch
```

## 🤝 Développement

### Standards de code
- **TypeScript** : Typage strict
- **ESLint** : Linting automatique
- **Prettier** : Formatage du code
- **Husky** : Pre-commit hooks

### Git workflow
```bash
# Créer une branche feature
git checkout -b feature/nouvelle-fonctionnalite

# Commiter avec message conventionnel
git add .
git commit -m "feat: ajouter nouvelle fonctionnalité"

# Push et créer PR
git push origin feature/nouvelle-fonctionnalite
```

### Conventions de nommage
- **Composants** : PascalCase (UserProfile.tsx)
- **Hooks** : camelCase avec préfixe use (useAuth.ts)
- **Services** : camelCase (authService.ts)
- **Types** : camelCase (UserType.ts)

## 📝 Notes de version

### v1.0.0 (Production)
- ✅ Déploiement sur Vercel
- ✅ Authentification JWT complète
- ✅ Interface responsive design
- ✅ Animations Framer Motion
- ✅ Upload de fichiers
- ✅ Notifications temps réel
- ✅ Refactoring des headers

---

**🎯 Frontend prêt pour la production !**
