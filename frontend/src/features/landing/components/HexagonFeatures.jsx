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

  // Séparation des données : 3 sur la première ligne, 2 sur la deuxième
  const topFeatures = features.slice(0, 3);
  const bottomFeatures = features.slice(3, 5);

  const renderHexagon = (feature, index) => {
    const Icon = feature.icon;
    const isActive = active === index;

    return (
      <div
        key={feature.title}
        onClick={() => setActive(index)}
        className={`relative p-[3px] transition-all duration-300 cursor-pointer [clip-path:polygon(25%_0%,75%_0%,100%_50%,75%_100%,25%_100%,0%_50%)] ${
          isActive
            ? 'bg-gradient-to-r from-[#F2994A] via-[#F2C94C] to-[#F2994A] scale-105 shadow-xl drop-shadow-[0_10px_20px_rgba(242,201,76,0.3)]'
            : 'bg-transparent hover:scale-102'
        }`}
      >
        <button
          type="button"
          className={`group relative h-full w-full min-h-[300px] sm:min-h-[320px] p-6 text-center transition-all duration-300 flex flex-col items-center justify-between [clip-path:polygon(25%_0%,75%_0%,100%_50%,75%_100%,25%_100%,0%_50%)] ${
            isActive
              ? 'bg-white text-[#0F2027]'
              : 'bg-gradient-to-br from-[#142850] to-[#0F2027] text-white hover:brightness-110'
          }`}
        >
          {/* Icône en haut */}
          <div className="mt-4">
            <Icon className={isActive ? 'text-[#F2994A]' : 'text-[#F2C94C]'} size={34} />
          </div>

          {/* Contenu Texte au centre */}
          <div className="my-auto px-2">
            <span className="block text-base font-black leading-snug">{feature.title}</span>
            <span className={`mt-2 block text-xs leading-relaxed ${isActive ? 'text-slate-600 font-medium' : 'text-slate-300'}`}>
              {isActive ? feature.detail : feature.summary}
            </span>
          </div>

          {/* Flèche d'action en bas */}
          <div className="mb-3">
            <ArrowUpRight
              className={`transition-transform duration-300 group-hover:translate-x-1 group-hover:-translate-y-1 ${
                isActive ? 'text-[#F2994A]' : 'text-[#F2C94C]'
              }`}
              size={22}
            />
          </div>
        </button>
      </div>
    );
  };

  return (
    <section id="approche" className="bg-[#F8F9FA] px-5 py-24 lg:px-8">
      <div className="mx-auto max-w-6xl">
        <div className="text-center md:text-left max-w-2xl">
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-[#F2994A]">
            Une approche sans angle mort
          </p>
          <h2 className="mt-3 text-3xl font-black tracking-tight text-[#0F2027] sm:text-4xl lg:text-5xl">
            La confiance se construit à chaque étape.
          </h2>
        </div>

        {/* Grille Hexagonale : Première Ligne (3 Hexagones) */}
        <div className="mt-14 grid gap-6 sm:grid-cols-2 lg:grid-cols-3 items-center justify-center">
          {topFeatures.map((feature, idx) => renderHexagon(feature, idx))}
        </div>

        {/* Grille Hexagonale : Deuxième Ligne (2 Hexagones centrés) */}
        <div className="mt-6 flex flex-col sm:flex-row justify-center gap-6 items-center">
          {bottomFeatures.map((feature, idx) => (
            <div key={feature.title} className="w-full sm:w-[calc(50%-12px)] lg:w-[calc(33.333%-16px)]">
              {renderHexagon(feature, idx + 3)}
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}