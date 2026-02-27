import BaseHeader from '../BaseHeader';

const leftLinks = [
  { to: '/admin/dashboard', label: 'Dashboard' },
  { to: '/admin/enterprises', label: 'Entreprises' },
  { to: '/admin/teachers', label: 'Enseignants' },
];

const rightLinks = [
  { to: '/admin/students', label: 'Étudiants' },
  { to: '/admin/settings', label: 'Paramètres' },
];

export default function AdminHeader() {
  return (
    <BaseHeader 
      leftLinks={leftLinks} 
      rightLinks={rightLinks}
      leftColor="jaune"
      rightColor="jaune"
    />
  );
}