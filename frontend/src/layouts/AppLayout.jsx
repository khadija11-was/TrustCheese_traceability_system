import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { LogOut } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import Sidebar from '@/components/layout/SideBar';

function UserBar() {
  const { user, logout } = useAuth();

  return (
    <header className="h-16 shrink-0 border-b border-slate-200/80 bg-white px-6 flex items-center justify-between z-10 shadow-sm">
      {/* Zone gauche du header (ex: titre dynamique ou statut système si besoin) */}
      <div className="flex items-center gap-2">
        <span className="inline-flex items-center rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-medium text-emerald-700 ring-1 ring-inset ring-emerald-600/20">
          Système Opérationnel
        </span>
      </div>

      {/* Profile Utilisateur */}
      <div className="flex items-center gap-3">
        <div className="text-right">
          <p className="text-sm font-semibold text-slate-900 leading-tight">
            {user?.nom || user?.email || 'Utilisateur'}
          </p>
          <p className="text-xs text-slate-500 font-medium capitalize">
            {user?.role?.toLowerCase() || 'Opérateur'}
          </p>
        </div>
        <div className="w-9 h-9 rounded-full bg-slate-900 text-amber-400 font-bold text-xs flex items-center justify-center border border-slate-800 shadow-sm">
          {user?.nom ? user.nom.charAt(0).toUpperCase() : 'U'}
        </div>
        <button
          type="button"
          onClick={logout}
          aria-label="Se déconnecter"
          title="Se déconnecter"
          className="ml-2 inline-flex h-9 w-9 items-center justify-center rounded-md border border-slate-200 text-slate-600 transition-colors hover:border-red-200 hover:bg-red-50 hover:text-red-600 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-500"
        >
          <LogOut className="h-4 w-4" aria-hidden="true" />
        </button>
      </div>
    </header>
  );
}

export default function AppLayout() {
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);

  const toggleSidebar = () => {
    setIsSidebarCollapsed(!isSidebarCollapsed);
  };

  return (
    /* h-screen + overflow-hidden garantissent que la page ne défile pas dans son ensemble */
    <div className="h-screen w-screen flex bg-[#F8FAFC] font-sans antialiased text-[#000000] overflow-hidden">
      
      {/* 1. SIDEBAR : Toujours fixe à gauche sur toute la hauteur */}
      <Sidebar isCollapsed={isSidebarCollapsed} toggleSidebar={toggleSidebar} />

      {/* 2. ZONE PRINCIPALE : Conteneur vertical séparé */}
      <div className="flex-1 flex flex-col min-w-0 h-full overflow-hidden">
        
        {/* Header supérieur fixe */}
        <UserBar />

        {/* 3. OUTLET : Seule cette zone gère le défilement (scroll) */}
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto scrollbar-thin scrollbar-thumb-slate-300">
          <div className="animate-fadeIn pb-10">
            <Outlet />
          </div>
        </main>

      </div>
    </div>
  );
}