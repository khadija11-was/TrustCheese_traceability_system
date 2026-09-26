import React, { useState } from 'react';
import { Box, ClipboardCheck, Factory, FlaskConical, Leaf, Snowflake, Truck } from 'lucide-react';

const steps = [
  { title: 'Matière première', detail: 'Lait brut et fournisseurs identifiés.', icon: Leaf },
  { title: 'Fabrication', detail: 'Transformation, caillage et suivi des cuves.', icon: Factory },
  { title: 'Affinage', detail: 'Caves à température contrôlée.', icon: Box },
  { title: 'Contrôle qualité', detail: 'Validation du pH, de l’humidité et de l’extrait sec avant emballage.', icon: ClipboardCheck },
  { title: 'Logistique', detail: 'Transport frigorifique et mouvements de stock.', icon: Truck },
  { title: 'Distribution GMS', detail: 'Suivi précis de la chaîne froide entre 2°C et 6°C.', icon: Snowflake },
];

export default function ManufacturingCycle() {
  const [active, setActive] = useState(3);
  return <section id="cycle" className="bg-white px-5 py-24 lg:px-8"><div className="mx-auto max-w-7xl"><div className="flex flex-col justify-between gap-6 md:flex-row md:items-end"><div><p className="text-sm font-bold uppercase tracking-[0.2em] text-[#F2994A]">Le cycle TrustCheese</p><h2 className="mt-3 text-4xl font-black tracking-tight text-[#0F2027] sm:text-5xl">Du lait au rayon, une même vérité.</h2></div><FlaskConical className="hidden text-[#F2C94C] md:block" size={42} /></div><div className="relative mt-16 grid gap-4 md:grid-cols-6">{steps.map((step, index) => { const Icon = step.icon; const isActive = active === index; return <button type="button" key={step.title} onClick={() => setActive(index)} className="group relative text-left"><div className={`mb-5 flex h-14 w-14 items-center justify-center rounded-2xl border transition-all ${isActive ? 'border-[#F2994A] bg-[#F2994A] text-[#0F2027] shadow-lg shadow-[#F2994A]/30' : 'border-slate-200 bg-[#F8F9FA] text-[#2C5364] group-hover:border-[#F2C94C]'}`}><Icon size={24} /></div><span className="block text-xs font-bold uppercase tracking-wider text-[#F2994A]">0{index + 1}</span><h3 className="mt-2 font-black text-[#0F2027]">{step.title}</h3><p className={`mt-2 text-sm leading-6 ${isActive ? 'text-slate-700' : 'text-slate-400'}`}>{step.detail}</p>{index < steps.length - 1 && <span className="absolute left-14 top-7 hidden h-px w-[calc(100%-1rem)] bg-slate-200 md:block" />}</button>; })}</div></div></section>;
}
