import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { BadgeCheck, Beaker, CalendarDays, Check, ChevronRight, ClipboardCheck, Factory, Leaf, MapPin, PackageCheck, Search, Share2, ShieldCheck, Snowflake, Truck } from 'lucide-react';
import publicTraceabilityService from '@/services/publicTraceabilityService';

const API_ORIGIN = 'http://localhost:8080';
const milestoneIcons = {
  DEBUT_PRODUCTION: Factory,
  FIN_PRODUCTION: Factory,
  DEBUT_AFFINAGE: Snowflake,
  FIN_AFFINAGE: Snowflake,
  CONTROLE_QUALITE: Beaker,
  EMBALLAGE: PackageCheck,
  LIVRAISON: Truck,
};
const formatDate = (value, options = { day: 'numeric', month: 'long', year: 'numeric' }) => value ? new Date(value).toLocaleDateString('fr-FR', options) : 'Non renseignée';
const formatDateTime = (value) => value ? new Date(value).toLocaleString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' }) : 'Date non renseignée';

function BrandHeader() {
  return <header className="border-b border-amber-900/10 bg-[#FDFBF7]/90"><div className="mx-auto flex max-w-6xl items-center justify-center px-5 py-5"><a href="/" aria-label="TrustCheese accueil"><img src="/logo.png" alt="TrustCheese" className="h-12 w-auto object-contain" /></a></div></header>;
}

function TrustSeal({ numeroLot }) {
  return <div className="flex flex-wrap items-center justify-center gap-3 rounded-full border border-emerald-700/20 bg-white/80 px-5 py-3 shadow-sm"><span className="flex h-8 w-8 items-center justify-center rounded-full bg-emerald-600 text-white"><Check size={18} strokeWidth={3} /></span><span className="text-sm font-semibold text-emerald-800">Lot officiel & tracé</span><span className="hidden h-5 w-px bg-slate-200 sm:block" /><span className="font-mono text-sm font-bold text-[#0F2027]">{numeroLot}</span></div>;
}

function LotIdentity({ lot }) {
  const expiration = lot?.dateExpiration ? new Date(`${lot.dateExpiration}T00:00:00`) : null;
  const daysLeft = expiration ? Math.ceil((expiration - new Date()) / 86400000) : null;
  const freshness = daysLeft === null ? 'Date inconnue' : daysLeft < 0 ? 'Date dépassée' : daysLeft <= 14 ? `${daysLeft} jours restants` : 'Fraîcheur préservée';
  return <div className="grid gap-px overflow-hidden rounded-xl border border-amber-900/10 bg-amber-900/10 sm:grid-cols-3"><div className="bg-white p-4"><p className="text-[11px] font-semibold uppercase tracking-wider text-slate-500">N° de lot</p><p className="mt-1 font-mono text-sm font-bold text-[#0F2027]">{lot?.numeroLot || '-'}</p></div><div className="bg-white p-4"><p className="flex items-center gap-1 text-[11px] font-semibold uppercase tracking-wider text-slate-500"><CalendarDays size={13} /> Date de fabrication</p><p className="mt-1 text-sm font-semibold text-[#0F2027]">{formatDate(lot?.dateProduction)}</p></div><div className="bg-white p-4"><p className="text-[11px] font-semibold uppercase tracking-wider text-slate-500">Date limite</p><p className="mt-1 text-sm font-semibold text-[#0F2027]">{formatDate(lot?.dateExpiration)}</p><p className={`mt-1 text-xs ${daysLeft !== null && daysLeft < 0 ? 'text-red-600' : 'text-emerald-700'}`}>{freshness}</p></div></div>;
}

function Journey({ steps }) {
  if (!steps?.length) return <p className="rounded-lg bg-white p-5 text-sm text-slate-500">Les étapes détaillées de ce lot ne sont pas encore publiées.</p>;
  return <div className="space-y-0">{steps.map((step, index) => { const Icon = milestoneIcons[step.type] || ClipboardCheck; return <article key={`${step.type}-${step.date}-${index}`} className="relative border-l border-amber-400/70 pb-7 pl-8 last:border-transparent last:pb-0"><span className="absolute -left-[17px] flex h-8 w-8 items-center justify-center rounded-full border border-amber-300 bg-[#FDFBF7] text-amber-700"><Icon size={15} /></span><div className="rounded-xl border border-amber-900/10 bg-white p-4 shadow-[0_4px_20px_rgba(15,32,39,0.04)]"><div className="flex flex-wrap items-start justify-between gap-2"><div><p className="text-[10px] font-bold uppercase tracking-[0.16em] text-amber-700">Étape {String(index + 1).padStart(2, '0')}</p><h3 className="mt-1 font-semibold text-[#0F2027]">{step.titre || step.type}</h3></div><span className="rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-semibold text-emerald-800">{step.statut || 'Enregistrée'}</span></div><p className="mt-3 text-xs text-slate-500">Le {formatDateTime(step.date)}</p></div></article>; })}</div>;
}

function ProductTrace({ data }) {
  const { produit, lot, matieresPremieres = [], etapes = [] } = data;
  const imageUrl = produit?.imageUrl ? (produit.imageUrl.startsWith('http') ? produit.imageUrl : `${API_ORIGIN}${produit.imageUrl}`) : null;
  return <main className="mx-auto max-w-5xl space-y-8 px-5 py-10"><div className="flex justify-center"><TrustSeal numeroLot={lot?.numeroLot} /></div><section className="grid overflow-hidden rounded-2xl border border-amber-900/10 bg-white shadow-[0_18px_60px_rgba(15,32,39,0.08)] md:grid-cols-[0.9fr_1.1fr]">{imageUrl ? <div className="min-h-64 bg-[#F2EEE5]"><img src={imageUrl} alt={produit?.nom || 'Produit'} className="h-full max-h-[420px] w-full object-cover" /></div> : <div className="flex min-h-64 items-center justify-center bg-gradient-to-br from-[#F5ECD7] to-[#EEE1C3]"><Leaf size={64} className="text-amber-700/50" /></div>}<div className="flex flex-col justify-center p-7 sm:p-10"><p className="text-xs font-bold uppercase tracking-[0.2em] text-amber-700">L'art du fromage, en toute transparence</p><h1 className="mt-3 font-serif text-4xl font-semibold text-[#0F2027]">{produit?.nom || 'Fromage TrustCheese'}</h1><p className="mt-4 leading-7 text-slate-600">{produit?.description || 'Un produit élaboré avec soin, dont le parcours est documenté à chaque étape.'}</p><div className="mt-7"><LotIdentity lot={lot} /></div></div></section><section className="grid gap-8 md:grid-cols-[0.85fr_1.15fr]"><div className="space-y-7"><section><div className="flex items-center gap-3"><span className="flex h-9 w-9 items-center justify-center rounded-full bg-amber-100 text-amber-800"><Leaf size={18} /></span><div><p className="text-xs font-bold uppercase tracking-[0.16em] text-amber-700">Pureté & origine</p><h2 className="font-serif text-2xl font-semibold text-[#0F2027]">Matières premières</h2></div></div><div className="mt-4 flex flex-wrap gap-2">{matieresPremieres.length ? matieresPremieres.map((item) => <div key={item.id || item.code || item.nom} className="rounded-xl border border-amber-900/10 bg-white px-4 py-3"><p className="text-sm font-semibold text-[#0F2027]">{item.nom}</p><p className="mt-1 text-xs text-emerald-700">Sélectionnée & contrôlée</p></div>) : <p className="text-sm text-slate-500">Les matières premières ne sont pas renseignées.</p>}</div></section><section className="rounded-xl border border-emerald-800/15 bg-emerald-50/70 p-5"><div className="flex items-center gap-3"><ShieldCheck size={22} className="text-emerald-700" /><h2 className="font-serif text-xl font-semibold text-[#0F2027]">Engagement sanitaire</h2></div><p className="mt-3 text-sm leading-6 text-slate-600">Ce produit est élaboré dans un établissement agréé et contrôlé selon les exigences sanitaires applicables, afin de garantir la sécurité du consommateur.</p><div className="mt-4 flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-emerald-800"><BadgeCheck size={17} /> Qualité & conformité</div></section></div><section><div className="mb-5 flex items-center gap-3"><span className="flex h-9 w-9 items-center justify-center rounded-full bg-amber-100 text-amber-800"><MapPin size={18} /></span><div><p className="text-xs font-bold uppercase tracking-[0.16em] text-amber-700">De la ferme à votre table</p><h2 className="font-serif text-2xl font-semibold text-[#0F2027]">Le voyage du fromage</h2></div></div><Journey steps={etapes} /></section></section><footer className="flex flex-col items-center justify-between gap-4 border-t border-amber-900/10 pt-6 sm:flex-row"><p className="text-xs text-slate-500">Données de traçabilité fournies par TrustCheese · Lot {lot?.numeroLot}</p><button type="button" onClick={() => window.print()} className="inline-flex items-center gap-2 rounded-lg bg-[#0F2027] px-4 py-2.5 text-sm font-semibold text-white hover:bg-[#203A43]"><Share2 size={16} /> Télécharger / partager le certificat</button></footer></main>;
}

export default function PublicTraceabilityPage() {
  const { numeroLot: routeLot } = useParams();
  const [query, setQuery] = useState(routeLot || '');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(Boolean(routeLot));
  const [error, setError] = useState('');

  const search = async (event) => {
    event?.preventDefault();
    if (!query.trim()) return;
    setLoading(true);
    setError('');
    try {
      setData(await publicTraceabilityService.getByLotNumber(query.trim()));
    } catch (requestError) {
      setData(null);
      setError(requestError.response?.data?.message || 'Aucune traçabilité publique trouvée pour ce numéro de lot.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (routeLot) search();
  }, [routeLot]);

  return <div className="min-h-screen bg-[#FDFBF7] text-[#0F2027]"><BrandHeader /><div className="mx-auto max-w-3xl px-5 pt-8"><form onSubmit={search} className="flex gap-2 rounded-xl border border-amber-900/10 bg-white p-2 shadow-sm"><div className="relative flex-1"><Search size={17} className="absolute left-3 top-1/2 -translate-y-1/2 text-amber-700" /><input aria-label="Numéro de lot" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Numéro de lot (ex. LOT-2026-GOUDA-089)" className="w-full bg-transparent py-2.5 pl-10 pr-3 text-sm outline-none placeholder:text-slate-400" /></div><button type="submit" disabled={loading || !query.trim()} className="rounded-lg bg-amber-500 px-5 py-2 text-sm font-bold text-[#0F2027] hover:bg-amber-400 disabled:opacity-50">{loading ? 'Recherche...' : 'Tracer'}</button></form></div>{error && <div className="mx-auto mt-6 max-w-3xl rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}{data ? <ProductTrace data={data} /> : !loading && <div className="mx-auto max-w-3xl px-5 py-24 text-center"><div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-amber-100 text-amber-800"><Search size={28} /></div><h1 className="mt-6 font-serif text-3xl font-semibold text-[#0F2027]">Découvrez l'histoire de votre fromage</h1><p className="mx-auto mt-3 max-w-xl text-sm leading-6 text-slate-600">Scannez le QR code du produit ou saisissez son numéro de lot pour consulter son origine, ses ingrédients et les étapes de sa fabrication.</p></div>}</div>;
}
