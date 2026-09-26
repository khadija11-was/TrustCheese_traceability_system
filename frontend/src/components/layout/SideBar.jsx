import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { 
  LayoutDashboard, 
  Package, 
  Factory, 
  ShieldCheck, 
  Truck,
  Search,
  Users, 
  ChevronDown, 
  ChevronRight 
} from 'lucide-react';

const navigationConfig = [
  {
    title: 'Vue d’ensemble',
    icon: LayoutDashboard,
    path: '/dashboard'
  },
  {
    title: 'Approvisionnement & Stock',
    icon: Package,
    path: '/stock',
    children: [
        {
          title: "Configuration",
          path: "/stock/configuration",
          roles: ['ADMINISTRATEUR', 'MAGASINIER', 'RESPONSABLE_PRODUCTION']
        },
        {
          title: "Réception Lots MP",
          path: "/stock/reception-mp",
          roles: ['ADMINISTRATEUR', 'MAGASINIER', 'RESPONSABLE_PRODUCTION']
        }]
  },
  {
    title: 'Fabrication & maturation',
    icon: Factory,
    children: [
      { title: 'Production', path: '/fabrication/production', roles: ['ADMINISTRATEUR', 'RESPONSABLE_PRODUCTION'] },
      { title: 'Affinage', path: '/fabrication/affinage' }
    ]
  },
  {
    title: 'Qualité & conformité',
    icon: ShieldCheck,
    path: '/qualite'
  },
  {
    title: 'Expédition & suivi',
    icon: Truck,
    path: '/expedition'
  },
  {
    title: 'Traçabilité interne',
    icon: Search,
    path: '/tracabilite'
  },
  {
    title: 'Administration',
    icon: Users,
    children: [
      { title: 'Gestion des utilisateurs', path: '/admin/utilisateurs' }
    ]
  }
];

export default function Sidebar() {
  const { hasRole } = useAuth();
  const [openMenus, setOpenMenus] = useState({
    'Fabrication & maturation': true,
    'Administration': false
  });

  const toggleMenu = (title) => {
    setOpenMenus((prev) => ({
      ...prev,
      [title]: !prev[title]
    }));
  };

  return (
    <aside className="w-64 h-screen bg-slate-900 text-slate-100 flex flex-col border-r border-slate-800">
      {/* Brand / Header */}
      <div className="p-5 border-b border-slate-800 flex items-center space-x-3">
        <div className="w-8 h-8 rounded-lg bg-amber-500 flex items-center justify-center text-slate-900 font-bold">
          TC
        </div>
        <span className="font-semibold text-lg tracking-wide">TrustCheese</span>
      </div>

      {/* Navigation Links */}
      <nav className="flex-1 overflow-y-auto p-4 space-y-1">
        {navigationConfig.map((item) => {
          const Icon = item.icon;
          const hasChildren = item.children && item.children.length > 0;
          const isOpen = openMenus[item.title];

          return (
            <div key={item.title} className="space-y-1">
              {hasChildren ? (
                /* Item avec sous-menus */
                <div>
                  <button
                    onClick={() => toggleMenu(item.title)}
                    className="w-full flex items-center justify-between px-3 py-2.5 text-sm font-medium rounded-lg text-slate-300 hover:bg-slate-800 hover:text-white transition-colors"
                  >
                    <div className="flex items-center space-x-3">
                      <Icon className="w-5 h-5 text-slate-400" />
                      <span>{item.title}</span>
                    </div>
                    {isOpen ? (
                      <ChevronDown className="w-4 h-4 text-slate-400" />
                    ) : (
                      <ChevronRight className="w-4 h-4 text-slate-400" />
                    )}
                  </button>

                  {/* Dynamic Submenu list */}
                  {isOpen && (
                    <div className="ml-9 pl-2 border-l border-slate-700 space-y-1 mt-1">
                      {item.children.filter((child) => !child.roles || hasRole(...child.roles)).map((child) => (
                        <NavLink
                          key={child.title}
                          to={child.path}
                          className={({ isActive }) => `block px-3 py-2 text-sm rounded-md transition-colors ${isActive ? 'bg-slate-800 text-amber-400' : 'text-slate-400 hover:text-amber-400 hover:bg-slate-800/50'}`}
                        >
                          {child.title}
                        </NavLink>
                      ))}
                    </div>
                  )}
                </div>
              ) : (
                /* Item simple sans sous-menu */
                <NavLink
                  to={item.path}
                  className={({ isActive }) => `flex items-center space-x-3 px-3 py-2.5 text-sm font-medium rounded-lg transition-colors ${isActive ? 'bg-slate-800 text-white' : 'text-slate-300 hover:bg-slate-800 hover:text-white'}`}
                >
                  <Icon className="w-5 h-5 text-slate-400" />
                  <span>{item.title}</span>
                </NavLink>
              )}
            </div>
          );
        })}
      </nav>

      {/* Footer / User section */}
      <div className="p-4 border-t border-slate-800 text-xs text-slate-500 text-center">
        TrustCheese v1.0
      </div>
    </aside>
  );
}