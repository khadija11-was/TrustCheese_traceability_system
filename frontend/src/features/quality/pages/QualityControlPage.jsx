import React, { useEffect, useMemo, useState } from 'react';
import { Beaker, CheckCircle2, ClipboardCheck, Search, Thermometer, X } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import affinageService from '@/services/affinageService';
import controleQualiteService from '@/services/controleQualiteService';

const emptyForm = { temperature: '', ph: '', extraitSec: '', texture: '', notes: '', decision: 'LIBERE' };
const textures = ['Pâte molle', 'Ferme', 'Souple', 'Grainée', 'Friable'];
const decisionMeta = {
  LIBERE: { label: 'Conforme / Libéré', className: 'border-emerald-300 bg-emerald-50 text-emerald-800' },
  BLOQUE: { label: 'Non conforme / Bloqué', className: 'border-red-300 bg-red-50 text-red-800' },
};
const formatDate = (value) => value ? new Date(value).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' }) : '-';

function MetricField({ label, icon: Icon, ...props }) {
  return <label className="block space-y-1 text-sm font-medium text-slate-700"><span className="flex items-center gap-2"><Icon size={15} className="text-blue-600" />{label}</span><input {...props} className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100" /></label>;
}

function InspectionForm({ selected, user, form, saving, readOnly, onChange, onSubmit, onCancel }) {
  return <form onSubmit={onSubmit} className="space-y-6">
    <div className="flex items-start justify-between border-b border-slate-200 pb-4"><div><p className="font-mono text-xs font-bold text-slate-500">{selected.numeroAffinage}</p><h2 className="mt-1 text-xl font-bold text-slate-900">Fiche de contrôle qualité</h2><p className="mt-1 text-sm text-slate-500">{selected.numeroProduction} · {selected.nombreLots ?? 0} lot(s)</p></div><span className="rounded-full bg-blue-50 px-3 py-1 text-xs font-bold text-blue-700">{selected.nombreLots ?? 0} lots</span></div>
    <div className="grid gap-4 rounded-lg bg-slate-50 p-4 sm:grid-cols-2"><div><span className="text-xs uppercase text-slate-500">Inspecteur</span><p className="mt-1 font-semibold text-slate-900">{user?.nom || user?.email || 'Utilisateur connecté'}</p></div><div><span className="text-xs uppercase text-slate-500">Date du contrôle</span><p className="mt-1 font-semibold text-slate-900">{formatDate(new Date())}</p></div><div className="sm:col-span-2"><span className="text-xs uppercase text-slate-500">Lots associés</span><div className="mt-2 flex flex-wrap gap-2">{(selected.numerosLots || []).map((lot) => <span key={lot} className="rounded bg-white px-2 py-1 font-mono text-xs text-slate-700 ring-1 ring-slate-200">{lot}</span>)}</div></div></div>
    <section><h3 className="mb-3 text-sm font-bold uppercase tracking-wide text-slate-700">Analyses physico-chimiques</h3><div className="grid gap-4 sm:grid-cols-3"><MetricField label="Température (°C)" icon={Thermometer} name="temperature" type="number" step="0.01" min="-50" value={form.temperature} onChange={onChange} required={!readOnly} readOnly={readOnly} /><MetricField label="pH" icon={Beaker} name="ph" type="number" step="0.01" min="0" max="14" value={form.ph} onChange={onChange} required={!readOnly} readOnly={readOnly} /><MetricField label="Extrait sec (%)" icon={ClipboardCheck} name="extraitSec" type="number" step="0.01" min="0" value={form.extraitSec} onChange={onChange} required={!readOnly} readOnly={readOnly} /></div></section>
    <section><h3 className="mb-3 text-sm font-bold uppercase tracking-wide text-slate-700">Évaluation organoleptique</h3><div className="flex flex-wrap gap-2">{textures.map((texture) => <button type="button" key={texture} disabled={readOnly} onClick={() => onChange({ target: { name: 'texture', value: texture } })} className={`rounded-full border px-3 py-1.5 text-xs font-semibold ${form.texture === texture ? 'border-blue-600 bg-blue-600 text-white' : 'border-slate-300 text-slate-600 hover:border-blue-400 disabled:cursor-default disabled:hover:border-slate-300'}`}>{texture}</button>)}</div><input name="texture" value={form.texture} onChange={onChange} readOnly={readOnly} placeholder="Texture libre..." className="mt-3 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 read-only:bg-slate-50" /><textarea name="notes" value={form.notes} onChange={onChange} readOnly={readOnly} rows="3" placeholder="Notes et observations..." className="mt-3 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 read-only:bg-slate-50" /></section>
    <section><h3 className="mb-3 text-sm font-bold uppercase tracking-wide text-slate-700">Décision qualité</h3><div className="grid gap-3 sm:grid-cols-2">{Object.entries(decisionMeta).map(([value, meta]) => <label key={value} className={`rounded-lg border p-3 ${form.decision === value ? meta.className : 'border-slate-200 bg-white text-slate-500'} ${readOnly ? '' : 'cursor-pointer'}`}><input type="radio" name="decision" value={value} checked={form.decision === value} onChange={onChange} disabled={readOnly} className="sr-only" /><span className="font-bold">{meta.label}</span></label>)}</div></section>
    <div className="flex justify-end gap-3 border-t border-slate-200 pt-4"><button type="button" onClick={onCancel} className="rounded-lg px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100">Fermer</button>{!readOnly && <button type="submit" disabled={saving} className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-bold text-white hover:bg-blue-700 disabled:opacity-50"><CheckCircle2 size={16} />{saving ? 'Enregistrement...' : 'Enregistrer le contrôle'}</button>}</div>
  </form>;
}

export default function QualityControlPage() {
  const { user } = useAuth();
  const [affinages, setAffinages] = useState([]);
  const [controls, setControls] = useState([]);
  const [selected, setSelected] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [search, setSearch] = useState('');
  const [decisionFilter, setDecisionFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const load = async () => { setLoading(true); setError(''); try { const [affinageData, controlData] = await Promise.all([affinageService.getAll('TERMINE'), controleQualiteService.getAll()]); setAffinages(affinageData); setControls(controlData); } catch (e) { setError(e.response?.data?.message || 'Impossible de charger les contrôles qualité.'); } finally { setLoading(false); } };
  useEffect(() => { load(); }, []);

  const controlledAffinageIds = useMemo(() => new Set(controls.flatMap((control) => control.affinageId ? [control.affinageId] : [])), [controls]);
  const visible = useMemo(() => affinages.filter((item) => `${item.numeroAffinage} ${item.numeroProduction}`.toLowerCase().includes(search.toLowerCase()) && (!decisionFilter || (controlledAffinageIds.has(item.id) ? decisionFilter !== 'A_CONTROLER' : decisionFilter === 'A_CONTROLER'))), [affinages, search, decisionFilter, controlledAffinageIds]);
  const update = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  const select = (item) => {
    const control = controls.find((itemControl) => itemControl.affinageId === item.id);
    setSelected(item);
    setForm(control ? {
      temperature: control.temperature ?? '',
      ph: control.ph ?? '',
      extraitSec: control.extraitSec ?? '',
      texture: control.texture ?? '',
      notes: control.notes ?? '',
      decision: control.decision ?? 'LIBERE',
    } : emptyForm);
  };
  const submit = async (event) => { event.preventDefault(); setSaving(true); setError(''); try { await controleQualiteService.create({ affinageId: selected.id, temperature: Number(form.temperature), ph: Number(form.ph), extraitSec: Number(form.extraitSec), texture: form.texture || null, notes: form.notes || null, decision: form.decision }); setSelected(null); await load(); } catch (e) { setError(e.response?.data?.message || 'Enregistrement du contrôle impossible.'); } finally { setSaving(false); } };

  return <section className="space-y-6"><div className="border-b border-slate-200 pb-5"><p className="text-sm font-semibold uppercase tracking-widest text-blue-600">Qualité & conformité</p><h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">Contrôles qualité</h1><p className="mt-2 text-sm text-slate-500">Inspectez les affinages terminés et libérez les lots conformes.</p></div>{error && <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}<div className="grid min-h-[620px] gap-0 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm lg:grid-cols-[40%_60%]"><aside className="border-b border-slate-200 bg-slate-50 lg:border-b-0 lg:border-r"><div className="border-b border-slate-200 p-4"><div className="relative"><Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Rechercher un affinage..." className="w-full rounded-lg border border-slate-300 bg-white py-2 pl-9 pr-3 text-sm outline-none focus:border-blue-500" /></div><select value={decisionFilter} onChange={(event) => setDecisionFilter(event.target.value)} className="mt-3 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm outline-none focus:border-blue-500"><option value="">Tous les états</option><option value="A_CONTROLER">À contrôler</option><option value="CONTROLE">Contrôlé</option></select></div><div className="space-y-2 p-3">{loading ? <p className="p-5 text-center text-sm text-slate-500">Chargement de la file...</p> : visible.map((item) => { const controlled = controlledAffinageIds.has(item.id); return <button type="button" key={item.id} onClick={() => select(item)} className={`w-full rounded-lg border bg-white p-4 text-left transition ${selected?.id === item.id ? 'border-blue-500 ring-2 ring-blue-500' : 'border-slate-200 hover:border-blue-300'} ${controlled ? 'opacity-60' : ''}`}><div className="flex items-start justify-between"><div><p className="font-mono text-xs font-bold text-slate-500">{item.numeroAffinage}</p><p className="mt-1 font-bold text-slate-900">{item.numeroProduction}</p></div><span className={`rounded-full px-2 py-1 text-[10px] font-bold ${controlled ? 'bg-slate-100 text-slate-500' : 'bg-amber-100 text-amber-700'}`}>{controlled ? 'Contrôlé' : 'À contrôler'}</span></div><p className="mt-3 text-xs text-slate-500">{item.nombreLots ?? 0} lot(s) · fin {formatDate(item.dateFinReelle)}</p></button>; })}</div></aside><main className="p-5 sm:p-7">{selected ? <InspectionForm selected={selected} user={user} form={form} saving={saving} readOnly={controlledAffinageIds.has(selected.id)} onChange={update} onSubmit={submit} onCancel={() => setSelected(null)} /> : <div className="flex h-full min-h-[520px] flex-col items-center justify-center text-center text-slate-500"><ClipboardCheck size={42} className="text-blue-300" /><h2 className="mt-4 text-lg font-bold text-slate-700">Sélectionnez un affinage à gauche</h2><p className="mt-2 max-w-sm text-sm">Choisissez un affinage terminé pour débuter l'inspection.</p></div>}</main></div></section>;
}
