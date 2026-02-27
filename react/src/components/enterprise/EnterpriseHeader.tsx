import BaseHeader from '../BaseHeader';

const leftLinks = [
  { to: '/entreprise/offres', label: 'Listes des offres' },
  { to: '/entreprise/candidatures', label: 'Candidatures' },
];

const rightLinks = [
  { to: '/entreprise/profil', label: 'Profil entreprise' },
  { to: '/entreprise/parametres', label: 'Paramètre' },
];

export default function EntrepriseHeader() {
  return (
    <BaseHeader 
      leftLinks={leftLinks} 
      rightLinks={rightLinks}
      leftColor="jaune"
      rightColor="jaune"
    />
  );
}
