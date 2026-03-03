import './App.css'
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AnimatePresence, motion } from 'framer-motion';
import OffersList from './components/teacher/OffersList';
import EnterpriseList from './components/teacher/EnterpriseList';
import EnterpriseDetail from './components/teacher/EnterpriseDetail';
import StudentsList from './components/teacher/StudentsList';
import StudentDetail from './components/teacher/StudentDetail';
import CreateEnterpriseOffer from './components/enterprise/CreateEnterpriseOffer';
import EnterpriseOfferList from './components/enterprise/EnterpriseOfferList';
import OfferDetail from './components/OfferDetail';
import EnterpriseApplications from './components/enterprise/EnterpriseApplications';
import ApplicationDetail from './components/enterprise/ApplicationDetail';
import EnterpriseProfile from './components/enterprise/EnterpriseProfile';
import TeacherOfferDetail from './components/teacher/OfferDetail';
import DebugTeacher from './components/teacher/DebugTeacher';
import LoginPage from './components/LoginPage';
import ResetPassword from './components/ResetPassword';
import RegisterStepper from './components/RegisterStepper';
import RegisterSuccess from './components/RegisterSuccess';
import VerificationPage from './components/VerificationPage';
import ProtectedRoute from './components/ProtectedRoute';
import InternshipDetail from './components/InternshipDetail';
import UserSettings from './components/UserSettings';
import AdminDashboard from './components/admin/AdminDashboard';
import AdminEnterprisesList from './components/admin/EnterprisesList';
import AdminEnterpriseDetail from './components/admin/EnterpriseDetail';
import AdminTeachersList from './components/admin/TeachersList';
import AdminTeacherDetail from './components/admin/TeacherDetail';
import AdminStudentsList from './components/admin/StudentsList';
import AdminStudentDetail from './components/admin/StudentDetail';
import StudentInternshipList from './components/student/StudentInternshipList';
import MyStudentInternship from './components/student/MyStudentInternship';
import StudentProfile from './components/student/StudentProfile';
import Congratulations from './components/Congratulations';

const AnimatedRoutes = () => {
  const location = useLocation();
  
  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<PageWrapper><LoginPage /></PageWrapper>} />
        <Route path="/login" element={<PageWrapper><LoginPage /></PageWrapper>} />
        <Route path="/verification" element={<PageWrapper><VerificationPage /></PageWrapper>} />
        <Route path="/reset-password" element={<PageWrapper><ResetPassword /></PageWrapper>} />
        <Route path="/register" element={<PageWrapper><RegisterStepper /></PageWrapper>} />
        <Route path="/register-success" element={<PageWrapper><RegisterSuccess /></PageWrapper>} />
        <Route path="/felicitations" element={<PageWrapper><Congratulations /></PageWrapper>} />
        <Route path="/stage/:id" element={<PageWrapper><InternshipDetail /></PageWrapper>} />

        <Route element={<ProtectedRoute allowedRoles={['STUDENT', 'ADMIN']} />}> 
          <Route path="/etudiant/stages" element={<PageWrapper><StudentInternshipList /></PageWrapper>} />
          <Route path="/etudiant/mon-stage" element={<PageWrapper><MyStudentInternship /></PageWrapper>} />
          <Route path="/etudiant/profil" element={<PageWrapper><StudentProfile /></PageWrapper>} />
          <Route path="/etudiant/parametres" element={<PageWrapper><UserSettings /></PageWrapper>} />
        </Route>


        <Route element={<ProtectedRoute allowedRoles={['TEACHER', 'ADMIN']} />}>
          <Route path="/enseignant/debug" element={<PageWrapper><DebugTeacher /></PageWrapper>} />
          <Route path="/enseignant/offres" element={<PageWrapper><OffersList /></PageWrapper>} />
          <Route path="/enseignant/offres/:id" element={<PageWrapper><TeacherOfferDetail /></PageWrapper>} />
          <Route path="/enseignant/entreprises" element={<PageWrapper><EnterpriseList /></PageWrapper>} />
          <Route path="/enseignant/entreprises/:id" element={<PageWrapper><EnterpriseDetail /></PageWrapper>} />
          <Route path="/enseignant/etudiants" element={<PageWrapper><StudentsList /></PageWrapper>} />
          <Route path="/enseignant/etudiants/:id" element={<PageWrapper><StudentDetail /></PageWrapper>} />
          <Route path="/enseignant/parametres" element={<PageWrapper><UserSettings /></PageWrapper>} />
        </Route>

        {/* Routes entreprises (protégées) */}
        <Route element={<ProtectedRoute allowedRoles={['ENTERPRISE', 'ADMIN']} />}>
          <Route path="/entreprise/candidatures" element={<PageWrapper><EnterpriseApplications /></PageWrapper>} />
          <Route path="/entreprise/candidatures/:applicationId" element={<PageWrapper><ApplicationDetail /></PageWrapper>} />
          <Route path="/entreprise/offres" element={<PageWrapper><EnterpriseOfferList /></PageWrapper>} />
          <Route path="/entreprise/offres/:id" element={<PageWrapper><OfferDetail /></PageWrapper>} />
          <Route path="/entreprise/creer-offre" element={<PageWrapper><CreateEnterpriseOffer /></PageWrapper>} />
          <Route path="/entreprise/offres/:id/edit" element={<PageWrapper><CreateEnterpriseOffer /></PageWrapper>} />
          <Route path="/entreprise/profil" element={<PageWrapper><EnterpriseProfile /></PageWrapper>} />
          <Route path="/entreprise/parametres" element={<PageWrapper><UserSettings /></PageWrapper>} />
        </Route>

        {/* Routes admin (protégées) */}
        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route path="/admin/dashboard" element={<PageWrapper><AdminDashboard /></PageWrapper>} />
          <Route path="/admin/enterprises" element={<PageWrapper><AdminEnterprisesList /></PageWrapper>} />
          <Route path="/admin/enterprises/:id" element={<PageWrapper><AdminEnterpriseDetail /></PageWrapper>} />
          <Route path="/admin/teachers" element={<PageWrapper><AdminTeachersList /></PageWrapper>} />
          <Route path="/admin/teachers/:id" element={<PageWrapper><AdminTeacherDetail /></PageWrapper>} />
          <Route path="/admin/students" element={<PageWrapper><AdminStudentsList /></PageWrapper>} />
          <Route path="/admin/students/:id" element={<PageWrapper><AdminStudentDetail /></PageWrapper>} />
          <Route path="/admin/offres" element={<PageWrapper><OffersList /></PageWrapper>} />
          <Route path="/admin/offres/:id" element={<PageWrapper><TeacherOfferDetail /></PageWrapper>} />
          <Route path="/admin/settings" element={<PageWrapper><UserSettings /></PageWrapper>} />
        </Route>

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </AnimatePresence>
  );
};

const PageWrapper = ({ children }: { children: React.ReactNode }) => {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.2, ease: "easeOut" }}
      className="min-h-screen"
    >
      {children}
    </motion.div>
  );
};

const App = () => {
  return (
    <Router>
      <AnimatedRoutes />
    </Router>
  );
};

export default App;
