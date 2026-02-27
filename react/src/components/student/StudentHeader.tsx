import BaseHeader from '../BaseHeader';

const leftLinks = [
  { to: "/etudiant/stages", label: "Liste des stages" },
  { to: "/etudiant/mon-stage", label: "Mon stage" },
];

const rightLinks = [
  { to: "/etudiant/profil", label: "Mon profil" },
];

export default function EtudiantHeader() {
  return (
    <BaseHeader 
      leftLinks={leftLinks} 
      rightLinks={rightLinks}
      leftColor="jaune"
      rightColor="jaune"
    />
  );
}
