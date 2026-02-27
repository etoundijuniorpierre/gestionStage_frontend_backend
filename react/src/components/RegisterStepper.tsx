import RegisterStep1 from './RegisterStep1';
import RegisterStep2 from './RegisterStep2';
import RegisterStep3Enterprise from './RegisterStep3Enterprise';
import type { EnterpriseFormData } from './RegisterStep3Enterprise';
import RegisterStep3Student from './RegisterStep3Student';
import type { StudentFormData } from './RegisterStep3Student';
import RegisterStep3Teacher from './RegisterStep3Teacher';
import type { TeacherFormData } from './RegisterStep3Teacher';
import RegisterStep4Code from './RegisterStep4Code';
import { useState } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import logo from '../assets/logo.png';
import { registerStudent, registerEnterprise, registerTeacher } from '../api/registrationApi';
import { useRegistrationStore } from '../store/registrationStore';
import { useNavigate } from 'react-router-dom';

const RegisterStepper = () => {
  const { step, formData, setShowSuccess, setStep, setFormData } = useRegistrationStore();
  const [registerLoading, setRegisterLoading] = useState(false);
  const navigate = useNavigate();

  let stepContent = null;
  if (step === 1) {
    stepContent = (
      <RegisterStep1 />
    );
  } else if (step === 2) {
    stepContent = (
      <RegisterStep2 />
    );
  } else if (step === 3) {
    const handleRegister = async (data: StudentFormData | EnterpriseFormData | TeacherFormData) => {
      setRegisterLoading(true);
      try {
        if (formData.type === 'enterprise') {
          const enterpriseData = data as EnterpriseFormData;
          await registerEnterprise({
            name: enterpriseData.name || '',
            email: enterpriseData.email || '',
            matriculation: enterpriseData.matriculation || '',
            password: formData.password || '',
            contact: enterpriseData.contact || '',
            location: enterpriseData.location || '',
            city: enterpriseData.city || '', 
            sectorOfActivity: enterpriseData.sectorOfActivity || '',
            country: enterpriseData.country || '',
            logo: enterpriseData.logo,
          });
        } else if (formData.type === 'student') {
          const studentData = data as StudentFormData;
          await registerStudent({
            name: studentData.name || '',
            firstName: studentData.firstName || '',
            email: formData.email || '',
            password: formData.password || '',
            sector: studentData.sector || '',
            languages: studentData.languages || [],
            department: studentData.department || '',
            githubLink: studentData.githubLink || '',
            linkedinLink: studentData.linkedinLink || '',
          });
        } else if (formData.type === 'teacher') {
          const teacherData = data as TeacherFormData;
          await registerTeacher({
            name: teacherData.lastName || '',
            firstName: teacherData.firstName || '',
            department: teacherData.department || '',
            email: formData.email || '',
            password: formData.password || '',
          });
        }
        setFormData((prev) => ({ ...prev, [formData.type!]: data }));
        setStep(4);
      } catch (error) {
        console.error('Erreur lors de l\'inscription:', error);
        const err = error as { message?: string; userStatus?: { email: string; status: string; message: string } };
        
        // Gérer les comptes existants inactifs - erreur personnalisée avec userStatus
        if (err.userStatus?.status === 'INACTIF') {
          navigate(`/verification?email=${encodeURIComponent(err.userStatus.email)}&message=${encodeURIComponent(err.userStatus.message)}`);
        }
        // Le reste des erreurs est géré par les composants individuels
      } finally {
        setRegisterLoading(false);
      }
    };
    if (formData.type === 'enterprise') {
      stepContent = (
        <RegisterStep3Enterprise
          onPrev={() => setStep(2)}
          onFinish={handleRegister}
        />
      );
    } else if (formData.type === 'student') {
      stepContent = (
        <RegisterStep3Student
          onPrev={() => setStep(2)}
          onFinish={handleRegister}
        />
      );
    } else if (formData.type === 'teacher') {
      stepContent = (
        <RegisterStep3Teacher
          onPrev={() => setStep(2)}
          onFinish={handleRegister}
        />
      );
    }
  } else if (step === 4) {
    stepContent = (
      <RegisterStep4Code
        email={formData.email}
        accountType={formData.type}
        onSuccess={() => {
          setShowSuccess(true);
          navigate('/felicitations');
        }}
        onCancel={() => setStep(1)}
      />
    );
  }

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-login-gradient">
      <div className="flex flex-col items-center justify-center mb-2">
        <img src={logo} alt="Logo" className="max-w-[280px] w-full" />
        <p className="text-[#e1d3c1] text-center text-5xl tracking-[0.8em] ml-[35px]">ELITE</p>
      </div>
      <h1 className="text-[#b79056] mb-[-20px] mt-2 text-center mx-auto text-2xl">INSCRIPTION</h1><br/>
      <div className="w-full max-w-[380px] border border-[3px] p-4 border-[#B79056]">
        {registerLoading && <div className="text-center py-2 text-gray-700">Inscription en cours...</div>}
        <AnimatePresence mode="wait" initial={false}>
          <motion.div
            key={step}
            initial={{ opacity: 0, x: 40 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -40 }}
            transition={{ duration: 0.35, ease: 'easeInOut' }}
          >
            {stepContent}
          </motion.div>
        </AnimatePresence>
      </div>
    </div>
  );
};

export default RegisterStepper;