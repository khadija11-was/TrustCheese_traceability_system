import React from 'react';
import { ArrowUpRight, ChevronDown, ShieldCheck } from 'lucide-react';

export default function HeroSection() {
  return (
    <section id="top" className="relative overflow-hidden bg-[#0F2027] pt-32 text-white lg:pt-40">
      <div className="absolute -left-32 top-48 h-96 w-96 rounded-full bg-[#2C5364]/30 blur-3xl" />
      <div className="mx-auto grid max-w-7xl items-center gap-14 px-5 pb-20 lg:grid-cols-[0.9fr_1.1fr] lg:px-8 lg:pb-28">
        <div className="relative z-10 animate-[fadeUp_700ms_ease-out_both]">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-[#F2C94C]/30 bg-[#F2C94C]/10 px-3 py-1.5 text-xs font-semibold uppercase tracking-[0.18em] text-[#F2C94C]"><ShieldCheck size={14} /> Traçabilité industrielle</div>
          <h1 className="max-w-2xl text-5xl font-black leading-[0.98] tracking-tight sm:text-6xl lg:text-7xl">Built for the <span className="text-[#F2C94C]">future</span> of cheese traceability.</h1>
          <p className="mt-7 max-w-xl text-lg leading-8 text-slate-300">Une plateforme opérationnelle pour suivre chaque matière, chaque lot et chaque contrôle, avec la précision nécessaire aux chaînes laitières modernes.</p>
          <a href="#approche" className="mt-9 inline-flex items-center gap-3 rounded-full bg-[#F2994A] px-6 py-3.5 text-sm font-black tracking-wide text-[#0F2027] transition hover:-translate-y-1 hover:bg-[#F2C94C]">EN SAVOIR PLUS <ArrowUpRight size={18} /></a>
        </div>
        <div className="relative min-h-[420px] lg:min-h-[560px]">
          <div className="absolute inset-6 rotate-3 border border-[#F2C94C]/50 [clip-path:polygon(15%_0,100%_0,85%_100%,0_100%)]" />
          <div className="absolute inset-0 overflow-hidden [clip-path:polygon(15%_0,100%_0,85%_100%,0_100%)]"><img src="/HeroPage.jpg" alt="Technicienne contrôlant la qualité en laboratoire" className="h-full w-full object-cover" /><div className="absolute inset-0 bg-gradient-to-tr from-[#0F2027]/70 via-transparent to-[#F2C94C]/10" /></div>
          <div className="absolute bottom-7 left-0 max-w-xs border-l-2 border-[#F2C94C] bg-[#0F2027]/85 px-5 py-4 backdrop-blur-md"><p className="text-2xl font-black text-[#F2C94C]">100%</p><p className="text-sm text-slate-300">de visibilité sur votre chaîne de production</p></div>
        </div>
      </div>
      <a href="#approche" className="absolute bottom-5 left-1/2 -translate-x-1/2 text-slate-400 transition hover:text-white" aria-label="Découvrir la suite"><ChevronDown className="animate-bounce" /></a>
    </section>
  );
}
