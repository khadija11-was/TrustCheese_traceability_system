import React, { useState } from 'react';
import { Box, ClipboardCheck, Factory, FlaskConical, Leaf, Snowflake, Truck, CheckCircle2, ArrowRight } from 'lucide-react';

const steps = [
  { 
    title: 'Matière première', 
    detail: 'Lait brut et fournisseurs identifiés.', 
    icon: Leaf,
    kpi: 'Réception & Analyse',
    metric: 'Conforme à 100%',
    status: 'Lot fournisseur vérifié'
  },
  { 
    title: 'Fabrication', 
    detail: 'Transformation, caillage et suivi des cuves.', 
    icon: Factory,
    kpi: 'Température Cuve',
    metric: '32°C ± 0.5°C',
    status: 'Mouthier & Pasteurisation'
  },
  { 
    title: 'Affinage', 
    detail: 'Caves à température contrôlée.', 
    icon: Box,
    kpi: 'Hygrométrie & Durée',
    metric: '85% HR - 45 Jours',
    status: 'Cave de maturation A3'
  },
  { 
    title: 'Contrôle qualité', 
    detail: 'Validation du pH, de l’humidité et de l’extrait sec.', 
    icon: ClipboardCheck,
    kpi: 'Test pH & Extrait Sec',
    metric: 'pH 5.2 (Conforme)',
    status: 'Libération du lot autorisée'
  },
  { 
    title: 'Logistique', 
    detail: 'Transport frigorifique et mouvements de stock.', 
    icon: Truck,
    kpi: 'Traçabilité Expédition',
    metric: 'Double entrée stock',
    status: 'Expédié vers Entrepôt'
  },
  { 
    title: 'Distribution GMS', 
    detail: 'Suivi précis de la chaîne froide jusqu au client.', 
    icon: Snowflake,
    kpi: 'Chaîne du froid',
    metric: '2°C à 6°C',
    status: 'Livraison contrôlée'
  },
];

export default function ManufacturingCycle() {
  const [active, setActive] = useState(3);
  const activeStep = steps[active];

  return (
    <section id="cycle" className="bg-white px-5 py-24 lg:px-8 overflow-hidden">
      <div className="mx-auto max-w-7xl">
        
        {/* EN-TÊTE DE LA SECTION */}
        <div className="flex flex-col justify-between gap-6 md:flex-row md:items-end">
          <div>
            <p className="text-sm font-bold uppercase tracking-[0.2em] text-[#F2994A]">
              Le cycle TrustCheese
            </p>
            <h2 className="mt-3 text-3xl font-black tracking-tight text-[#0F2027] sm:text-4xl lg:text-5xl">
              Du lait au rayon, une même vérité.
            </h2>
          </div>
          <div className="hidden md:flex items-center gap-3 rounded-2xl bg-[#F8F9FA] px-5 py-3 border border-slate-200 shadow-sm">
            <FlaskConical className="text-[#F2994A]" size={32} />
            <span className="text-xs font-bold text-[#0F2027] uppercase tracking-wider">
              Contrôle continu en laboratoire
            </span>
          </div>
        </div>

        {/* STEPPER HORIZONTAL / LIGNE DU TEMPS */}
        <div className="relative mt-16">
          
          {/* Ligne connectrice d'arrière-plan */}
          <div className="absolute top-8 left-8 right-8 hidden h-1 bg-slate-100 md:block -z-0" />

          <div className="grid gap-6 sm:grid-cols-2 md:grid-cols-6 relative z-10">
            {steps.map((step, index) => {
              const Icon = step.icon;
              const isActive = active === index;

              return (
                <button
                  type="button"
                  key={step.title}
                  onClick={() => setActive(index)}
                  className="group text-left focus:outline-none"
                >
                  <div
                    className={`relative flex flex-col items-start p-4 rounded-2xl transition-all duration-300 border ${
                      isActive
                        ? 'bg-[#0F2027] text-white border-[#F2994A] shadow-xl scale-105'
                        : 'bg-[#F8F9FA] text-slate-800 border-slate-200/80 hover:bg-white hover:border-[#F2C94C] hover:shadow-md'
                    }`}
                  >
                    {/* Badge Icône */}
                    <div
                      className={`mb-4 flex h-12 w-12 items-center justify-center rounded-xl transition-all duration-300 ${
                        isActive
                          ? 'bg-gradient-to-r from-[#F2994A] to-[#F2C94C] text-[#0F2027] shadow-md'
                          : 'bg-white border border-slate-200 text-[#2C5364] group-hover:border-[#F2994A]'
                      }`}
                    >
                      <Icon size={22} />
                    </div>

                    <span
                      className={`block text-[11px] font-extrabold uppercase tracking-widest ${
                        isActive ? 'text-[#F2C94C]' : 'text-[#F2994A]'
                      }`}
                    >
                      Étape 0{index + 1}
                    </span>

                    <h3
                      className={`mt-1 text-sm font-black leading-tight ${
                        isActive ? 'text-white' : 'text-[#0F2027]'
                      }`}
                    >
                      {step.title}
                    </h3>

                    <p
                      className={`mt-2 text-xs leading-relaxed line-clamp-2 ${
                        isActive ? 'text-slate-300' : 'text-slate-500'
                      }`}
                    >
                      {step.detail}
                    </p>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* PANNEAU DE DÉTAILS INTERACTIF DU STEP ACTIF */}
        <div className="mt-10 rounded-2xl border border-slate-200 bg-gradient-to-r from-[#F8F9FA] to-slate-50 p-6 md:p-8 shadow-sm">
          <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
            <div className="flex items-center gap-4">
              <div className="rounded-xl bg-[#0F2027] p-3.5 text-[#F2C94C]">
                <activeStep.icon size={28} />
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-[#F2994A] uppercase tracking-wider">Focus Phase {active + 1}</span>
                  <span className="inline-flex items-center gap-1 rounded-full bg-emerald-100 px-2.5 py-0.5 text-[11px] font-bold text-emerald-800">
                    <CheckCircle2 size={12} /> Traçabilité active
                  </span>
                </div>
                <h4 className="text-xl font-black text-[#0F2027] mt-0.5">{activeStep.title}</h4>
              </div>
            </div>

            {/* KPIs Métiers de l'étape */}
            <div className="grid grid-cols-2 sm:grid-cols-2 gap-6 w-full md:w-auto border-t md:border-t-0 md:border-l border-slate-200 pt-4 md:pt-0 md:pl-8">
              <div>
                <p className="text-xs font-semibold text-slate-500">{activeStep.kpi}</p>
                <p className="text-base font-black text-[#0F2027] mt-0.5">{activeStep.metric}</p>
              </div>
              <div>
                <p className="text-xs font-semibold text-slate-500">Contrôle Métier</p>
                <p className="text-base font-black text-[#F2994A] mt-0.5">{activeStep.status}</p>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>
  );
}