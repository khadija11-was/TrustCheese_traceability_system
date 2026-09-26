import React from 'react';
import { ArrowRight, Menu, X } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useState } from 'react';

const links = [
  { label: 'Notre approche', href: '#approche' },
  { label: 'Cycle industriel', href: '#cycle' },
  { label: 'Performance', href: '#performance' },
];

export default function Navbar() {
  const [open, setOpen] = useState(false);

  return (
    <header className="fixed inset-x-0 top-0 z-50 border-b border-white/10 bg-[#0F2027]/85 backdrop-blur-xl">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-5 py-4 lg:px-8">
        <a href="#top" className="shrink-0">
          <img src="/logo.png" alt="TrustCheese" className="h-9 w-auto" />
        </a>
        <nav className="hidden items-center gap-8 lg:flex">
          {links.map((link) => <a key={link.href} href={link.href} className="text-sm font-medium text-slate-300 transition hover:text-[#F2C94C]">{link.label}</a>)}
        </nav>
        <Link to="/login" className="hidden items-center gap-2 rounded-full bg-[#F2994A] px-5 py-2.5 text-sm font-bold text-[#0F2027] transition hover:bg-[#F2C94C] sm:inline-flex">Connexion <ArrowRight size={16} /></Link>
        <button type="button" onClick={() => setOpen(!open)} className="text-white lg:hidden" aria-label="Menu">{open ? <X /> : <Menu />}</button>
      </div>
      {open && <nav className="border-t border-white/10 px-5 py-4 lg:hidden">{links.map((link) => <a key={link.href} href={link.href} onClick={() => setOpen(false)} className="block py-3 text-sm text-slate-300">{link.label}</a>)}<Link to="/login" className="mt-2 inline-flex items-center gap-2 rounded-full bg-[#F2994A] px-4 py-2 text-sm font-bold text-[#0F2027]">Connexion <ArrowRight size={15} /></Link></nav>}
    </header>
  );
}
