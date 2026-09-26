import React from 'react';
import { ArrowUpRight, ChevronDown, ShieldCheck, CheckCircle2 } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function HeroSection() {
  return (
    <section id="top" className="relative overflow-hidden bg-[#0F2027] pt-32 text-white lg:pt-40">
      {/* Halos de lumière décoratifs en arrière-plan */}
      <div className="absolute -left-32 top-32 h-96 w-96 rounded-full bg-[#2C5364]/40 blur-3xl" />
      <div className="absolute right-0 top-1/4 h-96 w-96 rounded-full bg-[#F2994A]/10 blur-3xl" />

      <div className="mx-auto grid max-w-7xl items-center gap-12 px-6 pb-20 lg:grid-cols-12 lg:px-8 lg:pb-28">
        
        {/* COLONNE GAUCHE : TEXTE & CTA (5/12) */}
        <div className="relative z-10 lg:col-span-6">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-[#F2C94C]/30 bg-[#F2C94C]/10 px-4 py-1.5 text-xs font-semibold uppercase tracking-[0.18em] text-[#F2C94C]">
            <ShieldCheck size={16} /> Traçabilité industrielle
          </div>

          <h1 className="text-4xl font-black leading-tight tracking-tight sm:text-5xl lg:text-6xl">
            Pilotez votre <span className="text-[#F2C94C]">production</span>,<br />
            Maîtrisez votre <span className="text-[#F2C94C]">traçabilité</span>.
          </h1>

          <p className="mt-6 text-base leading-relaxed text-slate-300 sm:text-lg">
            Une plateforme opérationnelle pour suivre chaque matière, chaque lot et chaque contrôle, avec la précision nécessaire aux chaînes laitières modernes.
          </p>

          <div className="mt-8 flex flex-wrap items-center gap-4">
            <a
              href="#approche"
              className="inline-flex items-center gap-3 rounded-full bg-gradient-to-r from-[#F2994A] to-[#F2C94C] px-7 py-3.5 text-sm font-extrabold tracking-wide text-[#0F2027] shadow-lg shadow-[#F2994A]/20 transition-all duration-300 hover:-translate-y-0.5 hover:shadow-xl hover:shadow-[#F2994A]/30 active:translate-y-0"
            >
              EN SAVOIR PLUS <ArrowUpRight size={18} />
            </a>

            <Link
              to="/login"
              className="inline-flex items-center gap-2 rounded-full border border-white/20 bg-white/5 px-6 py-3.5 text-sm font-bold text-white backdrop-blur-sm transition-all duration-300 hover:bg-white/10 hover:border-white/40"
            >
              Accéder à l'espace
            </Link>
          </div>
        </div>

        {/* COLONNE DROITE : DISPOSITION DE L'IMAGE & BADGES (7/12) */}
        <div className="relative lg:col-span-6 lg:pl-4">
          <div className="relative mx-auto max-w-md lg:max-w-none">
            
            {/* Cadre principal de l'image */}
            <div className="relative overflow-hidden rounded-2xl border border-white/15 bg-slate-900/50 shadow-2xl shadow-[#F2994A]/10 backdrop-blur-sm">
              <img
                src="/HeroPage.jpg"
                alt="Technicienne contrôlant la qualité en laboratoire"
                className="h-[420px] sm:h-[480px] w-full object-cover object-center transition-transform duration-700 hover:scale-105"
              />
              
              {/* Degradé d'assombrissement doux sur l'image */}
              <div className="absolute inset-0 bg-gradient-to-t from-[#0F2027] via-transparent to-transparent opacity-80" />
            </div>

            {/* Badge Flottant 1 : Statut Contrôle Sanitaire (Haut Droite) */}
            <div className="absolute -top-4 -right-2 sm:-right-4 rounded-xl border border-emerald-500/30 bg-[#0F2027]/90 p-3.5 shadow-xl backdrop-blur-md flex items-center gap-3">
              <div className="rounded-lg bg-emerald-500/20 p-2 text-emerald-400">
                <CheckCircle2 size={20} />
              </div>
              <div>
                <p className="text-xs font-bold text-white">Contrôle Sanitaire</p>
                <p className="text-[11px] text-emerald-400 font-semibold">Lots conformes à 100%</p>
              </div>
            </div>

            {/* Badge Flottant 2 : KPI Visibilité (Bas Gauche) */}
            <div className="absolute -bottom-5 -left-2 sm:-left-4 max-w-xs rounded-xl border-l-4 border-[#F2C94C] border-y border-r border-white/10 bg-[#0F2027]/95 px-5 py-4 shadow-2xl backdrop-blur-md">
              <p className="text-3xl font-black text-[#F2C94C]">100%</p>
              <p className="text-xs font-medium text-slate-300 mt-0.5">
                de visibilité sur votre chaîne de fabrication et vos mouvements de stocks.
              </p>
            </div>

          </div>
        </div>

      </div>

      {/* Flèche d'indication de défilement */}
      <a
        href="#approche"
        className="absolute bottom-5 left-1/2 -translate-x-1/2 text-slate-400 transition hover:text-white"
        aria-label="Découvrir la suite"
      >
        <ChevronDown className="animate-bounce" size={26} />
      </a>
    </section>
  );
}