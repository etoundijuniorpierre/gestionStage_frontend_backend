import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import TeacherHeader from './TeacherHeader';
import { getStudentsByDepartment, downloadBulkDocumentsByStudents } from '../../api/teacherApi';
import { type StudentResponseDto } from '../../types/student';

export default function StudentsList() {
  const [students, setStudents] = useState<StudentResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [downloading, setDownloading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    getStudentsByDepartment()
      .then(res => {
        setStudents(res.data);
        setError(null);
      })
      .catch(err => {
        console.error('Erreur lors de la récupération des étudiants:', err);
        setError('Impossible de récupérer la liste des étudiants. Veuillez réessayer plus tard.');
      })
      .finally(() => setLoading(false));
  }, []);

  const handleStudentClick = (studentId: number) => {
    navigate(`/enseignant/etudiants/${studentId}`);
  };

  const handleBulkDownload = async () => {
    if (students.length === 0) return;
    try {
      setDownloading(true);
      const studentIds = students.map(s => s.id);
      const blob = await downloadBulkDocumentsByStudents(studentIds);
      
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `documents_etudiants_departement.zip`);
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
    } catch (err) {
      console.error('Erreur lors du téléchargement groupé:', err);
      alert('Erreur lors du téléchargement des documents des étudiants');
    } finally {
      setDownloading(false);
    }
  };

  return (
    <div className="min-h-screen bg-login-gradient">
      <TeacherHeader />
      <main className="container max-w-4xl mx-auto px-4 py-8">
        <div className="bg-[#e8e0d0] rounded-lg p-6 shadow-lg">
          <div className="flex flex-col md:flex-row md:items-center justify-between mb-6 gap-4">
            <h1 className="text-2xl font-semibold text-[var(--color-dark)]">
              Voir la liste de vos étudiants ({students.length})
            </h1>
            
            {students.length > 0 && (
              <button
                onClick={handleBulkDownload}
                disabled={downloading}
                className="bg-[var(--color-vert)] text-white px-4 py-2 rounded shadow hover:bg-opacity-90 transition-all flex items-center justify-center disabled:opacity-50"
              >
                {downloading ? (
                  <>
                    <span className="animate-spin mr-2">⏳</span>
                    Téléchargement...
                  </>
                ) : (
                  <>
                    <span className="mr-2">📁</span>
                    Tout télécharger (.zip)
                  </>
                )}
              </button>
            )}
          </div>
          
          {loading ? (
            <div className="flex justify-center py-8">Chargement...</div>
          ) : error ? (
            <div className="text-red-500 py-4">{error}</div>
          ) : students.length === 0 ? (
            <div className="text-center py-8 text-gray-600">Aucun étudiant trouvé dans votre département.</div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {students.map((student) => (
                <motion.div 
                  key={student.id}
                  className="bg-white rounded-lg shadow-md overflow-hidden cursor-pointer"
                  whileHover={{ y: -5, transition: { duration: 0.2 } }}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.3 }}
                >
                  <div className="p-4" onClick={() => handleStudentClick(student.id)}>
                    <div className="flex items-start mb-3">
                      <div className="flex-1">
                        <h3 className="text-lg font-semibold text-[var(--color-dark)]">
                          Développeur front-end
                          {student.onInternship && <span className="ml-2 text-xs bg-green-100 text-green-800 px-2 py-0.5 rounded-full">En stage</span>}
                        </h3>
                        <p className="text-sm text-gray-600">{student.name} {student.firstName}</p>
                        <p className="text-sm text-gray-600 mt-1">email: {student.email}</p>
                      </div>
                      <div className="w-16 h-16 bg-gray-200 rounded-md overflow-hidden">
                        <img 
                          src={`https://ui-avatars.com/api/?name=${student.name}+${student.firstName}&background=random`} 
                          alt={`${student.name} ${student.firstName}`}
                          className="w-full h-full object-cover"
                        />
                      </div>
                    </div>
                  </div>
            
                </motion.div>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}