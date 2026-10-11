import { useEffect, useState } from 'react';
import { ArrowLeft, CheckCircle2, ChevronRight, Factory, FlaskConical, PackageSearch, QrCode, Search, Truck, Warehouse } from 'lucide-react';
import traceabilityService from '@/services/traceabilityService';
import controleQualiteService from '@/services/controleQualiteService';

const eventLabels = { DEBUT_PRODUCTION: 'Début fabrication', FIN_PRODUCTION: 'Fin fabrication', DEBUT_AFFINAGE: 'Début affinage', FIN_AFFINAGE: 'Fin affinage', CONTROLE_QUALITE: 'Contrôle qualité', EMBALLAGE: 'Conditionnement', LIVRAISON: 'Expédition' };
const formatDate = (value) => value ? new Date(value).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' }) : '-';
const payloadValue = (event, keys) => {
  if (!event?.payload) return '-';

  const value = keys
    .map((key) => event.payload[key])
    .find((candidate) => candidate !== undefined && candidate !== null && candidate !== '');

  return value ?? '-';
};

function SearchBar({ value, onChange, onSubmit, loading }) {
  return <form onSubmit={onSubmit} className="flex gap-2"><div className="relative flex-1"><Search size={17} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" /><input value={value} onChange={(event) => onChange(event.target.value)} placeholder="Rechercher par N° de Lot (ex: LOT-2026-GOUDA-089)..." className="w-full rounded-lg border border-slate-300 bg-white py-3 pl-10 pr-3 text-sm outline-none focus:border-blue-500" /></div><button type="submit" disabled={loading} className="rounded-lg bg-blue-600 px-4 text-sm font-bold text-white hover:bg-blue-700 disabled:opacity-50">Explorer</button></form>;
}

function EmptyState({ recent, onSelect }) {
  return <div className="space-y-6"><div className="rounded-xl border border-dashed border-blue-200 bg-blue-50/50 p-10 text-center"><PackageSearch size={48} className="mx-auto text-blue-400" /><h2 className="mt-4 text-xl font-bold text-slate-800">Entrez un numéro de lot pour explorer sa généalogie</h2><p className="mx-auto mt-2 max-w-xl text-sm text-slate-500">Saisissez un identifiant de lot ci-dessus ou scannez le QR Code du produit pour afficher l'historique complet, les analyses et la chaîne de température.</p><div className="mt-5 flex flex-wrap justify-center gap-2 text-xs text-slate-500"><span>Derniers lots consultés</span>{recent.slice(0, 3).map((event) => <button key={event.eventId} type="button" onClick={() => onSelect(event.payload?.numeroLot)} className="rounded-full bg-white px-3 py-1 font-medium text-blue-700 ring-1 ring-blue-200">{event.payload?.numeroLot || eventLabels[event.eventType] || 'Événement récent'}</button>)}</div></div><section className="rounded-xl border border-slate-200 bg-white"><div className="border-b border-slate-200 px-5 py-4"><h2 className="font-bold text-slate-900">Derniers événements de traçabilité</h2></div>{recent.length === 0 ? <p className="p-6 text-sm text-slate-500">Aucun événement récent.</p> : <div className="divide-y divide-slate-100">{recent.map((event) => <div key={event.eventId} className="flex items-center justify-between px-5 py-3"><div><p className="text-sm font-semibold text-slate-800">{eventLabels[event.eventType] || event.eventType}</p><p className="text-xs text-slate-500">{event.operatorNom || 'Système'} · {formatDate(event.eventDate)}</p></div><ChevronRight size={16} className="text-slate-400" /></div>)}</div>}</section></div>;
}

function Milestone({ number, icon: Icon, title, children }) {
  return <article className="relative border-l-2 border-blue-200 pb-7 pl-8 last:pb-0"><span className="absolute -left-4 flex h-8 w-8 items-center justify-center rounded-full bg-blue-600 text-xs font-bold text-white ring-4 ring-white">{String(number).padStart(2, '0')}</span><div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm"><div className="flex items-center gap-3"><Icon size={19} className="text-blue-600" /><h3 className="font-bold text-slate-900">{title}</h3></div><div className="mt-4 text-sm text-slate-600">{children}</div></div></article>;
}

function TraceabilityTimeline({ data, qualityControl, onBack }) {
  const { lot, matieresPremieres = [], events = [] } = data;
  const byType = (type) => events
    .filter((event) => event.eventType === type)
    .sort((left, right) => new Date(right.eventDate) - new Date(left.eventDate))[0];
  const latestByType = (type) => events
    .filter((event) => event.eventType === type)
    .sort((left, right) => new Date(right.eventDate) - new Date(left.eventDate))[0];
  const start = byType('DEBUT_PRODUCTION');
  const end = byType('FIN_PRODUCTION');
  const startAge = byType('DEBUT_AFFINAGE');
  const endAge = byType('FIN_AFFINAGE');
  const quality = latestByType('CONTROLE_QUALITE');
  const qualityValue = (field) => qualityControl?.[field] ?? payloadValue(quality, [field]);
  const qualityOperator = qualityControl?.utilisateurNom || quality?.operatorNom || '-';
  const delivery = byType('LIVRAISON');
  return (
    <div className="space-y-5">
      <button type="button" onClick={onBack} className="inline-flex items-center gap-2 text-sm font-semibold text-blue-600 hover:text-blue-800">
        <ArrowLeft size={16} /> Nouvelle recherche
      </button>
      <div className="rounded-xl border border-slate-200 bg-white p-5">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <p className="font-mono text-xs font-bold text-slate-500">Lot produit fini</p>
            <h2 className="mt-1 text-2xl font-bold text-slate-900">{lot?.numeroLot}</h2>
          </div>
          <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-bold text-emerald-700">{lot?.statut || '-'}</span>
        </div>
        <p className="mt-2 text-sm text-slate-500">Expiration: {lot?.dateExpiration || '-'}</p>
      </div>

      <div className="ml-3">
        <Milestone number={1} icon={Warehouse} title="Matières premières">
          <p>Matières consommées: {matieresPremieres.length}</p>
          {matieresPremieres.length ? matieresPremieres.map((item) => (
            <div key={item.lotMPId || item.matierePremiereId} className="mt-2 border-t border-slate-100 pt-2">
              <p><strong>{item.nomMatierePremiere || '-'}</strong> · Lot MP: <strong>{item.numeroLotMP || '-'}</strong></p>
              <p>Fournisseur: <strong>{item.fournisseurNom || '-'}</strong></p>
              <p>Quantité utilisée: <strong>{item.quantiteUtilisee ?? '-'}</strong> {item.uniteMesure || ''}</p>
              <p>Date de réception: <strong>{formatDate(item.dateReception)}</strong></p>
            </div>
          )) : <p className="mt-2">Aucune matière première retournée.</p>}
        </Milestone>

        <Milestone number={2} icon={Factory} title="Fabrication & emprésurage">
          <p>Début: <strong>{formatDate(start?.eventDate)}</strong></p>
          <p>Production: <strong>{payloadValue(start, ['numeroProduction'])}</strong></p>
          <p>Cuve: <strong>{payloadValue(start, ['cuve'])}</strong></p>
          <p>Température cuve: <strong>{payloadValue(start, ['temperatureCuve'])} °C</strong></p>
          <p>pH cuve: <strong>{payloadValue(start, ['phCuve'])}</strong></p>
          <p>Fin: <strong>{formatDate(end?.eventDate)}</strong></p>
        </Milestone>

        <Milestone number={3} icon={FlaskConical} title="Affinage sous contrôle micro-climatique">
          <p>Début cave: <strong>{formatDate(startAge?.eventDate)}</strong></p>
          <p>Température: <strong>{payloadValue(endAge, ['temperature'])} °C</strong> · Humidité: <strong>{payloadValue(endAge, ['humidite'])} %</strong></p>
          <p>Fin: <strong>{formatDate(endAge?.eventDate)}</strong></p>
          <p>Observations: <strong>{payloadValue(endAge, ['observations'])}</strong></p>
        </Milestone>

        <Milestone number={4} icon={CheckCircle2} title="Agrément sanitaire & contrôle qualité">
          <p>Responsable: <strong>{qualityOperator}</strong></p>
          <p>Décision: <strong>{qualityValue('decision')}</strong></p>
          <p>Analyses: pH {qualityValue('ph')} · Extrait sec {qualityValue('extraitSec')}</p>
          <p>Température: <strong>{qualityValue('temperature')} °C</strong> · Texture: <strong>{qualityValue('texture')}</strong></p>
          <p>Notes: <strong>{qualityValue('notes')}</strong></p>
        </Milestone>

        <Milestone number={5} icon={QrCode} title="Conditionnement & QR Code">
          <p>Événement d'emballage: <strong>{formatDate(byType('EMBALLAGE')?.eventDate)}</strong></p>
          <p className="text-xs text-slate-500">Les informations QR/hash seront affichées lorsqu'elles seront présentes dans le payload.</p>
        </Milestone>

        <Milestone number={6} icon={Truck} title="Expédition & chaîne du froid">
          <p>Bon de livraison: <strong>{payloadValue(delivery, ['numeroLivraison'])}</strong></p>
          <p>Client: <strong>{payloadValue(delivery, ['clientNom'])}</strong></p>
          <p>Température camion: <strong>{payloadValue(delivery, ['temperatureMoyenneCamion'])} °C</strong></p>
        </Milestone>
      </div>
    </div>
  );
}

export default function TraceabilityPage() {
  const [query, setQuery] = useState('');
  const [recent, setRecent] = useState([]);
  const [trace, setTrace] = useState(null);
  const [qualityControl, setQualityControl] = useState(null);
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [error, setError] = useState('');
  useEffect(() => { traceabilityService.getRecentEvents().then(setRecent).catch(() => setError('Impossible de charger les derniers événements.')).finally(() => setInitialLoading(false)); }, []);
  const search = async (eventOrValue) => {
    eventOrValue?.preventDefault?.();
    const value = typeof eventOrValue === 'string' ? eventOrValue : query;
    if (!value.trim()) return;
    setLoading(true);
    setError('');
    setQualityControl(null);
    try {
      const traceData = await traceabilityService.searchByLotNumber(value.trim());
      setTrace(traceData);
      try {
        const controls = await controleQualiteService.getByLot(traceData.lot.id);
        const latestControl = controls.sort((left, right) => new Date(right.dateControle) - new Date(left.dateControle))[0] || null;
        setQualityControl(latestControl);
      } catch {
        setQualityControl(null);
      }
    } catch (requestError) {
      setTrace(null);
      setError(requestError.response?.data?.message || 'Lot introuvable.');
    } finally {
      setLoading(false);
    }
  };
  return <section className="space-y-6"><div className="border-b border-slate-200 pb-5"><p className="text-sm font-semibold uppercase tracking-widest text-blue-600">Traçabilité interne</p><h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">Généalogie des lots</h1><p className="mt-2 text-sm text-slate-500">Suivez chaque lot de la matière première à la livraison.</p></div><SearchBar value={query} onChange={setQuery} onSubmit={search} loading={loading} />{error && <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}{initialLoading ? <p className="rounded-xl bg-white p-10 text-center text-sm text-slate-500">Chargement des événements...</p> : trace ? <TraceabilityTimeline data={trace} qualityControl={qualityControl} onBack={() => { setTrace(null); setQualityControl(null); }} /> : <EmptyState recent={recent} onSelect={(value) => { setQuery(value || ''); if (value) search(value); }} />}</section>;
}
