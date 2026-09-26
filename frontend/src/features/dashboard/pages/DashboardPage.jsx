import React, { useEffect, useMemo, useState } from 'react';
import { Activity, AlertTriangle, BarChart3, CheckCircle2, Clock3, Factory, Gauge, Package, RefreshCw, ShieldAlert, Truck } from 'lucide-react';
import metricsService from '@/services/metricsService';

const formatDate = (value) => value ? new Date(value).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' }) : '-';
const levelClass = { CRITIQUE: 'border-red-200 bg-red-50 text-red-700', ELEVEE: 'border-orange-200 bg-orange-50 text-orange-700', MOYENNE: 'border-amber-200 bg-amber-50 text-amber-700' };
const statusClass = { DISPONIBLE: 'bg-emerald-100 text-emerald-700', EN_COURS: 'bg-blue-100 text-blue-700', OCCUPEE: 'bg-orange-100 text-orange-700', MAINTENANCE: 'bg-red-100 text-red-700' };

function KpiCard({ icon: Icon, label, value, detail, tone }) {
  return <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm"><div className="flex items-start justify-between"><div><p className="text-xs font-semibold uppercase tracking-wider text-slate-500">{label}</p><p className="mt-3 text-3xl font-bold text-slate-900">{value}</p><p className="mt-1 text-xs text-slate-500">{detail}</p></div><div className={`rounded-lg p-3 ${tone}`}><Icon size={20} /></div></div></article>;
}

function Card({ title, icon: Icon, children, className = '' }) {
  return <section className={`rounded-xl border border-slate-200 bg-white shadow-sm ${className}`}><div className="flex items-center gap-2 border-b border-slate-100 px-5 py-4"><Icon size={17} className="text-blue-600" /><h2 className="font-bold text-slate-900">{title}</h2></div>{children}</section>;
}

function TankChart({ tanks }) {
  const maxCapacity = Math.max(...tanks.map((tank) => Number(tank.capaciteMaxLitres || 0)), 1);
  return <div className="space-y-4 p-5">{tanks.length === 0 ? <p className="text-sm text-slate-500">Aucune cuve enregistrée.</p> : tanks.map((tank) => <div key={tank.id}><div className="mb-1 flex justify-between text-xs"><span className="font-semibold text-slate-700">{tank.nom}</span><span className={`rounded-full px-2 py-0.5 ${statusClass[tank.statutOperationnel] || 'bg-slate-100 text-slate-600'}`}>{tank.statutOperationnel}</span></div><div className="h-2 overflow-hidden rounded-full bg-slate-100"><div className="h-full rounded-full bg-blue-500" style={{ width: `${Math.max(8, Number(tank.capaciteMaxLitres || 0) / maxCapacity * 100)}%` }} /></div><p className="mt-1 text-[11px] text-slate-400">Capacité {tank.capaciteMaxLitres ?? '-'} L</p></div>)}</div>;
}

function StockDonut({ kpis }) {
  const available = Number(kpis?.lotsDisponibles || 0);
  const blocked = Number(kpis?.lotsBloques || 0);
  const active = Number(kpis?.productionsEnCours || 0);
  const total = available + blocked + active || 1;
  const availablePercent = available / total * 100;
  const blockedPercent = blocked / total * 100;
  return <div className="flex items-center gap-6 p-5"><div className="relative h-32 w-32 shrink-0 rounded-full" style={{ background: `conic-gradient(#22c55e 0 ${availablePercent}%, #ef4444 ${availablePercent}% ${availablePercent + blockedPercent}%, #3b82f6 ${availablePercent + blockedPercent}% 100%)` }}><div className="absolute inset-5 flex items-center justify-center rounded-full bg-white text-center"><span className="text-xl font-bold text-slate-900">{total}</span></div></div><div className="space-y-3 text-sm"><Legend color="bg-emerald-500" label="Lots disponibles" value={available} /><Legend color="bg-red-500" label="Lots bloqués" value={blocked} /><Legend color="bg-blue-500" label="En production" value={active} /></div></div>;
}
function Legend({ color, label, value }) { return <div className="flex items-center gap-2"><span className={`h-2.5 w-2.5 rounded-full ${color}`} /><span className="text-slate-600">{label}</span><strong className="text-slate-900">{value}</strong></div>; }

export default function DashboardPage() {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try { const data = await metricsService.get(); if (!cancelled) { setMetrics(data); setError(''); } }
      catch (requestError) { if (!cancelled) setError(requestError.response?.data?.message || 'Les métriques sont momentanément indisponibles.'); }
      finally { if (!cancelled) setLoading(false); }
    };
    load();
    const intervalId = window.setInterval(load, 60000);
    return () => { cancelled = true; window.clearInterval(intervalId); };
  }, []);

  const data = metrics || { kpis: {}, alerts: [], productionsEnCours: [], cuves: [], livraisonsAvenir: [], recentActivities: [] };
  const health = useMemo(() => { const total = Number(data.kpis.lotsDisponibles || 0) + Number(data.kpis.lotsBloques || 0); return total ? Math.round(Number(data.kpis.lotsDisponibles || 0) / total * 100) : 100; }, [data.kpis]);

  return <section className="space-y-6"><header className="flex flex-col gap-3 border-b border-slate-200 pb-5 sm:flex-row sm:items-end sm:justify-between"><div><p className="text-sm font-semibold uppercase tracking-widest text-blue-600">Pilotage opérationnel</p><h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">Tableau de bord</h1><p className="mt-2 text-sm text-slate-500">Une vue synthétique de l'activité TrustCheese.</p></div><span className="inline-flex w-fit items-center gap-2 rounded-full bg-emerald-50 px-3 py-1.5 text-xs font-bold text-emerald-700"><span className="h-2 w-2 rounded-full bg-emerald-500" />Système opérationnel</span></header>{error && <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}{loading && !metrics ? <div className="rounded-xl bg-white p-10 text-center text-sm text-slate-500">Chargement du tableau de bord...</div> : <><div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4"><KpiCard icon={Factory} label="Productions en cours" value={data.kpis.productionsEnCours ?? 0} detail="Ordres actifs" tone="bg-blue-50 text-blue-600" /><KpiCard icon={Package} label="Lots disponibles" value={data.kpis.lotsDisponibles ?? 0} detail={`${data.kpis.lotsBloques ?? 0} lot(s) bloqué(s)`} tone="bg-emerald-50 text-emerald-600" /><KpiCard icon={Truck} label="Livraisons du jour" value={data.kpis.livraisonsAujourdhui ?? 0} detail="Planning logistique" tone="bg-orange-50 text-orange-600" /><KpiCard icon={ShieldAlert} label="Santé globale" value={`${health}%`} detail="Disponibilité des lots" tone={health > 80 ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'} /></div><div className="grid gap-5 xl:grid-cols-8"><div className="space-y-5 xl:col-span-5"><Card title="Occupation & état des cuves" icon={Gauge}><TankChart tanks={data.cuves} /></Card><Card title="Répartition des lots" icon={BarChart3}><StockDonut kpis={data.kpis} /></Card></div><Card title="Centre de vigilance" icon={AlertTriangle} className="xl:col-span-3"><div className="max-h-[390px] space-y-3 overflow-y-auto p-5">{data.alerts.length === 0 ? <p className="text-sm text-slate-500">Aucune alerte active.</p> : data.alerts.map((alert, index) => <div key={`${alert.referenceId}-${index}`} className={`rounded-lg border p-3 ${levelClass[alert.niveau] || 'border-slate-200 bg-slate-50 text-slate-700'}`}><div className="flex items-center justify-between text-[10px] font-bold uppercase"><span>{alert.type || 'Alerte'}</span><span>{alert.niveau || 'INFO'}</span></div><p className="mt-1 text-sm">{alert.message}</p><p className="mt-2 text-[11px] opacity-70">{formatDate(alert.date)}</p></div>)}</div></Card></div><div className="grid gap-5 xl:grid-cols-2"><Card title="Productions en cours" icon={Factory}><div className="divide-y divide-slate-100">{data.productionsEnCours.length === 0 ? <p className="p-5 text-sm text-slate-500">Aucune production active.</p> : data.productionsEnCours.map((production) => <div key={production.id} className="flex items-center justify-between gap-3 p-4"><div><p className="font-mono text-xs font-bold text-slate-500">{production.numeroProduction}</p><p className="font-semibold text-slate-900">{production.produitNom}</p><p className="mt-1 text-xs text-slate-500">{production.cuveNom || 'Cuve non affectée'} · début {formatDate(production.dateDebut)}</p></div><span className="rounded-full bg-blue-50 px-2 py-1 text-[10px] font-bold text-blue-700">En cours</span></div>)}</div></Card><Card title="Livraisons à venir" icon={Truck}><div className="divide-y divide-slate-100">{data.livraisonsAvenir.length === 0 ? <p className="p-5 text-sm text-slate-500">Aucune livraison planifiée.</p> : data.livraisonsAvenir.map((delivery) => <div key={delivery.id} className="flex items-center justify-between gap-3 p-4"><div><p className="font-mono text-xs font-bold text-slate-500">{delivery.numeroLivraison}</p><p className="font-semibold text-slate-900">{delivery.clientNom}</p><p className="mt-1 text-xs text-slate-500">Prévue le {formatDate(delivery.dateLivraisonPrevue)}</p></div><span className="rounded-full bg-orange-50 px-2 py-1 text-[10px] font-bold text-orange-700">{delivery.statut}</span></div>)}</div></Card></div><Card title="Activité récente" icon={Activity}><div className="grid gap-3 p-5 md:grid-cols-2">{data.recentActivities.length === 0 ? <p className="text-sm text-slate-500">Aucune activité récente.</p> : data.recentActivities.map((activity) => <div key={activity.eventId} className="flex items-start gap-3 rounded-lg bg-slate-50 p-3"><div className="mt-1 rounded-full bg-blue-100 p-1.5 text-blue-600"><Clock3 size={14} /></div><div><p className="text-sm font-semibold text-slate-800">{activity.eventType}</p><p className="text-xs text-slate-500">{activity.numeroLot || 'Lot non précisé'} · {activity.operatorNom || 'Système'}</p><p className="mt-1 text-[11px] text-slate-400">{formatDate(activity.eventDate)}</p></div></div>)}</div></Card></>}</section>;
}
