import React from 'react';
import { ArrowUpRight, CheckCircle2 } from 'lucide-react';

const metrics = [
  ['100%', 'Traçabilité des lots'],
  ['24/7', 'Visibilité opérationnelle'],
  ['2°–6°C', 'Chaîne du froid suivie'],
];

export default function MetricsAndFooter() {
  return <><section id="performance" className="bg-[#0F2027] px-5 py-20 text-white lg:px-8"><div className="mx-auto max-w-7xl"><div className="grid gap-10 md:grid-cols-3">{metrics.map(([value, label]) => <div key={label} className="border-l border-[#F2C94C]/50 pl-5"><p className="text-4xl font-black text-[#F2C94C]">{value}</p><p className="mt-2 text-sm text-slate-300">{label}</p></div>)}</div><div className="mt-20 grid gap-8 border-t border-white/10 pt-10 md:grid-cols-[1.4fr_1fr_1fr]"><div><img src="/logo.png" alt="TrustCheese" className="h-10 w-auto" /><p className="mt-5 max-w-sm text-sm leading-6 text-slate-400">La traçabilité industrielle pensée pour les métiers du lait, de la production à la distribution.</p></div><div><h3 className="font-bold text-white">Explorer</h3><a href="#approche" className="mt-4 flex items-center gap-2 text-sm text-slate-400 hover:text-[#F2C94C]">Notre approche <ArrowUpRight size={14} /></a><a href="#cycle" className="mt-3 flex items-center gap-2 text-sm text-slate-400 hover:text-[#F2C94C]">Cycle industriel <ArrowUpRight size={14} /></a></div><div><h3 className="font-bold text-white">Conformité</h3><p className="mt-4 flex items-center gap-2 text-sm text-slate-400"><CheckCircle2 size={15} className="text-[#F2C94C]" /> HACCP</p><p className="mt-3 flex items-center gap-2 text-sm text-slate-400"><CheckCircle2 size={15} className="text-[#F2C94C]" /> ISO-ready</p></div></div></div></section><footer className="bg-[#09171D] px-5 py-5 text-center text-xs text-slate-500 lg:px-8">© 2026 TrustCheese. Traçabilité industrielle.</footer></>;
}
