import BaseHeader from '../BaseHeader';

const leftLinks = [
  { to: '/enseignant/entreprises', label: 'Entreprises' },
  { to: '/enseignant/offres', label: 'Offres' },
];

const rightLinks = [
    { to: '/enseignant/etudiants', label: 'Etudiants' },
  { to: '/enseignant/parametres', label: 'Paramètres' },
];

export default function TeacherHeader() {
  return (
    <BaseHeader 
      leftLinks={leftLinks} 
      rightLinks={rightLinks}
      leftColor="jaune"
      rightColor="jaune"
    />
  );
}
