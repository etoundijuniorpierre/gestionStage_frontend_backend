import type { ReactNode } from 'react';
import PageTransition from './PageTransition';
import { usePageLoading } from '../hooks/usePageLoading';

interface AppLayoutProps {
  children: ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  const { isLoading } = usePageLoading();

  return (
    <div className="min-h-screen bg-[var(--background-color)]">
      <PageTransition isLoading={isLoading}>
        {children}
      </PageTransition>
    </div>
  );
}
