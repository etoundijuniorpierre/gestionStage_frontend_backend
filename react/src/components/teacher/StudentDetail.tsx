import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import TeacherHeader from './TeacherHeader';
import { 
  getStudentsByDepartment, 
  getStudentApplications, 
  downloadStudentCV, 
  downloadStudentCoverLetter,
  downloadBulkDocuments 
} from '../../api/teacherApi';
import type { StudentResponseDto } from '../../types/student';
import { type ApplicationResponseDto } from '../../types/application';

export default function StudentDetail() {
  const { id } = useParams<{ id: string }>();
  const [student, setStudent] = useState<StudentResponseDto | null>(null);
  const [applications, setApplications] = useState<ApplicationResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [downloading, setDownloading] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (!id) return;

    const fetchData = async () => {
      setLoading(true);
      try {
        const [studentsRes, appsRes] = await Promise.all([
          getStudentsByDepartment(),
          getStudentApplications(parseInt(id))
        ]);

        const students = studentsRes.data as StudentResponseDto[];
        const foundStudent = students.find(s => s.id === parseInt(id));
        
        if (foundStudent) {
          setStudent(foundStudent);
          setApplications(appsRes.data);
          setError(null);
        } else {
          setError('Étudiant non trouvé');
        }
      } catch (err) {
        console.error('Erreur lors de la récupération des détails:', err);
        setError('Impossible de récupérer les détails de l\'étudiant.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const triggerDownload = (blob: Blob, filename: string) => {
    const url = window.URL.createObjectURL(new Blob([blob]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.parentNode?.removeChild(link);
  };

  const handleDownloadCV = async (appId: number, studentName: string) => {
    try {
      setDownloading(`cv-${appId}`);
      const blob = await downloadStudentCV(appId);
      triggerDownload(blob, `${studentName}_CV.pdf`);
    } catch (err) {
      console.error('Erreur lors du téléchargement du CV:', err);
      alert('Erreur lors du téléchargement du CV');
    } finally {
      setDownloading(null);
    }
  };

  const handleDownloadCL = async (appId: number, studentName: string) => {
    try {
      setDownloading(`cl-${appId}`);
      const blob = await downloadStudentCoverLetter(appId);
      triggerDownload(blob, `${studentName}_LettreMotivation.pdf`);
    } catch (err) {
      console.error('Erreur lors du téléchargement de la lettre de motivation:', err);
      alert('Erreur lors du téléchargement de la lettre de motivation');
    } finally {
      setDownloading(null);
    }
  };

  const handleDownloadAllDocs = async () => {
    if (!student || applications.length === 0) return;
    try {
      setDownloading('bulk');
      const appIds = applications.filter(app => app.hasFiles.hasCV || app.hasFiles.hasCoverLetter).map(app => app.id);
      if (appIds.length === 0) {
        alert("Aucun document à télécharger pour cet étudiant.");
        return;
      }
      const blob = await downloadBulkDocuments(appIds);
      triggerDownload(blob, `${student.name}_${student.firstName}_tous_les_documents.zip`);
    } catch (err) {
      console.error('Erreur lors du téléchargement groupé:', err);
      alert('Erreur lors du téléchargement groupé');
    } finally {
      setDownloading(null);
    }
  };

  return (
    <div className="min-h-screen bg-login-gradient">
      <TeacherHeader />
      <main className="container max-w-4xl mx-auto px-4 py-8">
        <div className="bg-[#e8e0d0] rounded-lg p-6 shadow-lg">
          {/* Bouton retour */}
          <div className="flex items-center justify-between mb-6">
            <button 
              onClick={() => navigate('/enseignant/etudiants')}
              className="flex items-center text-gray-700 hover:text-gray-900"
            >
              <span className="text-xl mr-2">←</span>
              <span className="text-xl font-medium">Retour à la liste</span>
            </button>
            
            {student && applications.length > 0 && (
              <button
                onClick={handleDownloadAllDocs}
                disabled={!!downloading}
                className="bg-[var(--color-vert)] text-white px-4 py-2 rounded shadow hover:bg-opacity-90 transition-all flex items-center disabled:opacity-50"
              >
                {downloading === 'bulk' ? (
                  <>
                    <span className="animate-spin mr-2">⏳</span>
                    Préparation...
                  </>
                ) : (
                  <>
                    <span className="mr-2">📦</span>
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
          ) : student ? (
            <div>
              {/* Informations de l'étudiant */}
              <motion.div 
                className="bg-white rounded-lg shadow-md p-6 mb-6"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3 }}
              >
                <div className="flex items-start">
                  {/* Avatar de l'étudiant */}
                  <div className="w-32 h-32 bg-gray-200 rounded-md overflow-hidden mr-6">
                    <img 
                      src={`https://ui-avatars.com/api/?name=${student.name}+${student.firstName}&background=random&size=128`} 
                      alt={`${student.name} ${student.firstName}`}
                      className="w-full h-full object-cover"
                    />
                  </div>
                  
                  <div className="flex-1">
                    <div>
                      <h1 className="text-2xl font-bold text-[var(--color-dark)]">{student.name} {student.firstName}</h1>
                      <p className="text-gray-600">{student.email}</p>
                      <p className="mt-2">Département: <span className="font-semibold">{student.department}</span></p>
                      <p className="mt-1">Statut: 
                        <span className={`ml-2 px-2 py-0.5 rounded-full text-xs font-medium ${student.onInternship ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>
                          {student.onInternship ? 'En stage' : 'Sans stage'}
                        </span>
                      </p>
                    </div>
                  </div>
                </div>
              </motion.div>
              
              <h2 className="text-xl font-semibold text-[var(--color-dark)] mb-4">Documents et Candidatures</h2>
              
              {applications.length === 0 ? (
                <div className="bg-white rounded-lg shadow-md p-6 text-center text-gray-500">
                  Aucun document disponible pour cet étudiant.
                </div>
              ) : (
                <div className="grid gap-4">
                  {applications.map(app => (
                    <motion.div 
                      key={app.id}
                      className="bg-white rounded-lg shadow-md p-4 flex items-center justify-between"
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                    >
                      <div>
                        <h3 className="font-semibold text-gray-800">{app.offer?.title || "Offre spontanée"}</h3>
                        <p className="text-sm text-gray-600">{app.enterprise.name}</p>
                        <span className={`text-xs px-2 py-0.5 rounded-full mt-1 inline-block ${
                          app.state === 'ACCEPTED' ? 'bg-green-100 text-green-800' :
                          app.state === 'REJECTED' ? 'bg-red-100 text-red-800' :
                          'bg-blue-100 text-blue-800'
                        }`}>
                          {app.state}
                        </span>
                      </div>
                      <div className="flex gap-2">
                        {app.hasFiles.hasCV && (
                          <button
                            onClick={() => handleDownloadCV(app.id, `${student.name}_${student.firstName}`)}
                            disabled={!!downloading}
                            className="bg-gray-100 text-gray-700 px-3 py-1.5 rounded text-sm hover:bg-gray-200 transition-colors flex items-center"
                          >
                            {downloading === `cv-${app.id}` ? '⏳' : '📄 CV'}
                          </button>
                        )}
                        {app.hasFiles.hasCoverLetter && (
                          <button
                            onClick={() => handleDownloadCL(app.id, `${student.name}_${student.firstName}`)}
                            disabled={!!downloading}
                            className="bg-gray-100 text-gray-700 px-3 py-1.5 rounded text-sm hover:bg-gray-200 transition-colors flex items-center"
                          >
                            {downloading === `cl-${app.id}` ? '⏳' : '✉️ LM'}
                          </button>
                        )}
                      </div>
                    </motion.div>
                  ))}
                </div>
              )}
            </div>
          ) : null}
        </div>
      </main>
    </div>
  );
}