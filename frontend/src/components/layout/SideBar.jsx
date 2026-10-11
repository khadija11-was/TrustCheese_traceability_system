import React, { useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
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
      }
    ]
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
  const location = useLocation();

  const [openMenus, setOpenMenus] = useState({
    'Fabrication & maturation': true,
    'Approvisionnement & Stock': true,
    'Administration': false
  });

  const toggleMenu = (title) => {
    setOpenMenus((prev) => ({
      ...prev,
      [title]: !prev[title]
    }));
  };

  return (
    <aside className="w-64 h-screen sticky top-0 flex-shrink-0 bg-[#0B0F17] text-slate-300 flex flex-col border-r border-slate-800/60 select-none z-50">
      
      {/* 1. Header avec Logo qui prend toute la largeur disponible */}
      {/* 1. Header avec l'icône /logo-icon.png et le texte stylisé */}
    <div className="px-6 py-5 flex items-center space-x-3.5 border-b border-slate-800/50">
      {/* L'icône du logo uniquement */}
      <img 
        src="/logo-icon.png" 
        alt="TrustCheese Icon" 
        className="w-9 h-9 object-contain shrink-0"
        onError={(e) => {
          // Fallback si l'image n'est pas trouvée
          e.target.style.display = 'none';
        }}
      />

      {/* Le texte stylisé en HTML/Tailwind */}
      <div className="flex flex-col">
        <div className="text-base font-extrabold tracking-wider leading-none flex items-center">
          <span className="text-white">TRUST</span>
          <span className="text-amber-400">CHEESE</span>
        </div>
        <span className="text-[9px] font-semibold text-slate-400 uppercase tracking-widest mt-1">
          Traçabilité Industrielle
        </span>
      </div>
    </div>

      {/* 2. Navigation */}
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1.5 scrollbar-thin scrollbar-thumb-slate-800">
        {navigationConfig.map((item) => {
          const Icon = item.icon;
          const hasChildren = item.children && item.children.length > 0;
          const isOpen = openMenus[item.title];

          const isChildActive = hasChildren && item.children.some(child => location.pathname === child.path);

          return (
            <div key={item.title} className="space-y-1">
              {hasChildren ? (
                /* Item avec Sous-Menu */
                <div>
                  <button
                    onClick={() => toggleMenu(item.title)}
                    className={`w-full flex items-center justify-between px-3.5 py-2.5 text-sm font-medium rounded-xl transition-all duration-200 group ${
                      isChildActive 
                        ? 'text-amber-400 bg-amber-500/10 border border-amber-500/20' 
                        : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/60'
                    }`}
                  >
                    {/* Icône + Titre alignés à gauche */}
                    <div className="flex items-center space-x-3 min-w-0 pr-2">
                      <Icon className={`w-4 h-4 shrink-0 transition-colors ${isChildActive ? 'text-amber-400' : 'text-slate-400 group-hover:text-slate-200'}`} />
                      <span className="text-xs font-semibold tracking-wide text-left truncate">{item.title}</span>
                    </div>

                    {/* Flèche verrouillée complètement à droite */}
                    <div className="shrink-0 pl-1">
                      {isOpen ? (
                        <ChevronDown className="w-4 h-4 text-slate-500 transition-transform duration-200" />
                      ) : (
                        <ChevronRight className="w-4 h-4 text-slate-500 transition-transform duration-200" />
                      )}
                    </div>
                  </button>

                  {/* Sous-Menu avec indicateur visuel de fil d'ariane */}
                  {isOpen && (
                    <div className="ml-5 pl-3 border-l border-slate-800/80 space-y-1 mt-1.5">
                      {item.children
                        .filter((child) => !child.roles || hasRole(...child.roles))
                        .map((child) => (
                          <NavLink
                            key={child.title}
                            to={child.path}
                            className={({ isActive }) =>
                              `block px-3 py-2 text-xs font-medium rounded-lg transition-all duration-150 relative ${
                                isActive
                                  ? 'text-amber-400 bg-amber-400/10 font-semibold border-l-2 border-amber-400 -ml-[13px] pl-4'
                                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/40'
                              }`
                            }
                          >
                            {child.title}
                          </NavLink>
                        ))}
                    </div>
                  )}
                </div>
              ) : (
                /* Item Simple */
                <NavLink
                  to={item.path}
                  className={({ isActive }) =>
                    `flex items-center space-x-3 px-3.5 py-2.5 text-xs font-semibold rounded-xl transition-all duration-200 ${
                      isActive
                        ? 'bg-amber-500 text-slate-950 font-bold shadow-lg shadow-amber-500/20'
                        : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/60'
                    }`
                  }
                >
                  <Icon className="w-4 h-4 shrink-0" />
                  <span>{item.title}</span>
                </NavLink>
              )}
            </div>
          );
        })}
      </nav>

      {/* 3. Pied de page minimaliste */}
      <div className="p-4 border-t border-slate-800/50 text-[11px] text-slate-500 text-center font-medium tracking-wider">
        TrustCheese v1.0
      </div>

    </aside>
  );
}