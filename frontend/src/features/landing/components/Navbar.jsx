import React, { useState, useEffect } from 'react';
import { ArrowRight, Menu, X } from 'lucide-react';
import { Link } from 'react-router-dom';

const links = [
  { label: 'Notre approche', href: '#approche' },
  { label: 'Cycle industriel', href: '#cycle' },
  { label: 'Performance', href: '#performance' },
];

export default function Navbar() {
  const [open, setOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  // Détection du scroll pour ajuster le style de la Navbar
  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 20) {
        setScrolled(true);
      } else {
        setScrolled(false);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <header
      className={`fixed inset-x-0 top-0 z-50 transition-all duration-300 ${
        scrolled
          ? 'bg-white/95 backdrop-blur-md py-2.5 shadow-md border-b border-slate-200/80'
          : 'bg-white/80 backdrop-blur-sm py-4 border-b border-transparent'
      }`}
    >
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 lg:px-8">
        
        {/* LOGO */}
        <a href="#top" className="group shrink-0 flex items-center">
          <img
            src="/logo.png"
            alt="TrustCheese"
            className={`w-auto object-contain transition-all duration-300 ${
              scrolled ? 'h-12' : 'h-14 md:h-16'
            } group-hover:scale-105`}
          />
        </a>

        {/* NAVIGATION DESKTOP */}
        <nav className="hidden items-center gap-8 lg:flex">
          {links.map((link) => (
            <a
              key={link.href}
              href={link.href}
              className="text-sm font-semibold text-slate-700 transition-colors duration-200 hover:text-[#F2994A]"
            >
              {link.label}
            </a>
          ))}
        </nav>

        {/* BOUTON CONNEXION DESKTOP */}
        <div className="hidden sm:flex sm:items-center">
          <Link
            to="/login"
            className="group inline-flex items-center gap-2 rounded-full bg-gradient-to-r from-[#F2994A] to-[#F2C94C] px-5 py-2.5 text-sm font-bold text-[#0F2027] shadow-sm transition-all duration-300 hover:shadow-md hover:shadow-[#F2994A]/25 hover:brightness-105 active:scale-95"
          >
            Connexion
            <ArrowRight size={16} className="transition-transform duration-200 group-hover:translate-x-1" />
          </Link>
        </div>

        {/* BOUTON MENU MOBILE */}
        <button
          type="button"
          onClick={() => setOpen(!open)}
          className="rounded-lg p-2 text-slate-700 hover:bg-slate-100 lg:hidden focus:outline-none"
          aria-label="Menu"
        >
          {open ? <X size={26} /> : <Menu size={26} />}
        </button>
      </div>

      {/* NAVIGATION MOBILE */}
      {open && (
        <nav className="border-t border-slate-100 bg-white px-6 py-5 shadow-xl lg:hidden space-y-3">
          {links.map((link) => (
            <a
              key={link.href}
              href={link.href}
              onClick={() => setOpen(false)}
              className="block rounded-lg px-3 py-2 text-base font-semibold text-slate-700 hover:bg-slate-50 hover:text-[#F2994A] transition"
            >
              {link.label}
            </a>
          ))}
          <div className="pt-2">
            <Link
              to="/login"
              onClick={() => setOpen(false)}
              className="flex w-full items-center justify-center gap-2 rounded-full bg-gradient-to-r from-[#F2994A] to-[#F2C94C] px-5 py-3 text-sm font-bold text-[#0F2027] shadow-sm"
            >
              Connexion <ArrowRight size={16} />
            </Link>
          </div>
        </nav>
      )}
    </header>
  );
}