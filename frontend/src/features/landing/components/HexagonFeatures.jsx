import React, { useState } from 'react';
import { ArrowUpRight, Boxes, ClipboardCheck, Database, Factory, Snowflake } from 'lucide-react';

const features = [
  { title: 'Matières maîtrisées', summary: 'De la ferme au quai.', detail: 'Centralisez fournisseurs, matières premières et réceptions dans une même chaîne de traçabilité.', icon: Boxes },
  { title: 'Fabrication connectée', summary: 'Chaque cuve, chaque étape.', detail: 'Pilotez les ordres de fabrication, les températures et les mesures critiques au fil de la production.', icon: Factory },
  { title: 'Qualité prouvée', summary: 'Décidez avec des faits.', detail: 'Conservez les analyses et décisions qualité pour libérer les lots avec une traçabilité complète.', icon: ClipboardCheck },
  { title: 'Stocks visibles', summary: 'Le bon lot au bon moment.', detail: 'Suivez les consommations, les seuils et les mouvements pour éviter les ruptures et les pertes.', icon: Database },
  { title: 'Chaîne du froid', summary: 'La maîtrise jusqu’au client.', detail: 'Gardez une vision claire des conditions sensibles qui protègent la qualité de vos produits.', icon: Snowflake },
];

export default function HexagonFeatures() {
  const [active, setActive] = useState(1);

  return (
      <section id="approche" className="bg-[#F8F9FA] px-5 py-24 lg:px-8">
        <div className="mx-auto max-w-7xl">
          <div className="max-w-2xl">
            <p className="text-sm font-bold uppercase tracking-[0.2em] text-[#F2994A]">
              Une approche sans angle mort
            </p>
            <h2 className="mt-3 text-4xl font-black tracking-tight text-[#0F2027] sm:text-5xl">
              La confiance se construit à chaque étape.
            </h2>
          </div>

          <div className="mt-14 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              const isActive = active === index;

              return (
                  <button
                      type="button"
                      key={feature.title}
                      onClick={() => setActive(isActive ? -1 : index)}
                      onMouseEnter={() => setActive(index)}
                      className={`group relative min-h-[320px] overflow-hidden p-8 text-center transition-all duration-300 flex flex-col items-center justify-between [clip-path:polygon(25%_0%,75%_0%,100%_50%,75%_100%,25%_100%,0%_50%)] ${
                          isActive
                              ? 'bg-white text-[#1A1A1A] shadow-2xl scale-105'
                              : 'bg-gradient-to-br from-[#142850] to-[#0F2027] text-white hover:opacity-95'
                      }`}
                  >
                    {/* Icône en haut */}
                    <div className="mt-4">
                      <Icon className={isActive ? 'text-[#F2994A]' : 'text-[#F2C94C]'} size={32} />
                    </div>

                    {/* Contenu Texte au centre */}
                    <div className="my-auto px-2">
                      <span className="block text-base font-black leading-snug">{feature.title}</span>
                      <span className={`mt-2 block text-xs leading-relaxed ${isActive ? 'text-slate-600' : 'text-slate-300'}`}>
                    {isActive ? feature.detail : feature.summary}
                  </span>
                    </div>

                    {/* Flèche d'action en bas */}
                    <div className="mb-2">
                      <ArrowUpRight
                          className={`transition-transform group-hover:translate-x-0.5 group-hover:-translate-y-0.5 ${
                              isActive ? 'text-[#F2994A]' : 'text-[#F2C94C]'
                          }`}
                          size={20}
                      />
                    </div>
                  </button>
              );
            })}
          </div>
        </div>
      </section>
  );
}