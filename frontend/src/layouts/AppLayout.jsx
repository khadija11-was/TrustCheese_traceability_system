import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import Sidebar from '@/components/layout/SideBar';

function UserBar() {
  const { user } = useAuth();

  return (
    <header className="h-16 shrink-0 border-b border-slate-200 bg-white px-6 flex items-center justify-end">
      <div className="text-right">
        <p className="text-sm font-semibold text-slate-900">{user?.nom || user?.email}</p>
        <p className="text-xs text-slate-500">{user?.role || 'Utilisateur'}</p>
      </div>
    </header>
  );
}

export default function AppLayout() {
  // État pour gérer la réduction de la sidebar (utile sur petits écrans ou pour gagner de l'espace)
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);

  const toggleSidebar = () => {
    setIsSidebarCollapsed(!isSidebarCollapsed);
  };

  return (
    <div className="min-h-screen flex bg-[#F8FAFC] font-sans antialiased text-[#000000]">
      {/* 1. SIDEBAR : Fixe à gauche avec les couleurs de la charte TrustCheese */}
      <Sidebar isCollapsed={isSidebarCollapsed} toggleSidebar={toggleSidebar} />

      {/* 2. ZONE PRINCIPALE : Navbar + Contenu dynamique */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Navbar supérieure avec infos utilisateur et contrôles */}
        <UserBar />

        {/* 3. OUTLET : Zone d'affichage des pages (Dashboard, Quality, Traceability...) */}
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          {/* Un conteneur fluide qui s'adapte à toutes les pages */}
          <div className="animate-fadeIn">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}