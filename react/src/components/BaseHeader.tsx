import { NavLink } from 'react-router-dom';
import logo from '../assets/logo.png';
import HeaderActions from './HeaderActions';
import AnimatedNavLink from './AnimatedNavLink';

interface NavItem {
  to: string;
  label: string;
}

interface BaseHeaderProps {
  leftLinks?: NavItem[];
  rightLinks?: NavItem[];
  leftColor?: 'emeraude' | 'jaune';
  rightColor?: 'emeraude' | 'jaune';
}

export default function BaseHeader({ 
  leftLinks = [], 
  rightLinks = [], 
  leftColor = 'emeraude',
  rightColor = 'jaune' 
}: BaseHeaderProps) {
  return (
    <header className="w-full flex items-end justify-center mb-7 px-8 pt-3 bg-transparent select-none relative">
      {/* HeaderActions - Positionné absolument à droite, complètement hors du flux */}
      <div className="absolute right-8 top-3">
        <HeaderActions logoutPath="/login" />
      </div>

      {/* Flux principal : Navigation gauche + Logo + Navigation droite - Centré sans interférence */}
      <div className="flex items-end gap-8">
        {/* Navigation gauche */}
        {leftLinks.length > 0 && (
          <nav className="flex gap-8 items-center">
            {leftLinks.map(link => (
              <AnimatedNavLink key={link.to} {...link} color={leftColor} />
            ))}
          </nav>
        )}

        {/* Logo central */}
        <NavLink to="/">
          <img src={logo} alt="Logo" className="h-12 w-auto" />
        </NavLink>

        {/* Navigation droite */}
        {rightLinks.length > 0 && (
          <nav className="flex gap-8 items-center">
            {rightLinks.map(link => (
              <AnimatedNavLink key={link.to} {...link} color={rightColor} />
            ))}
          </nav>
        )}
      </div>
    </header>
  );
}
