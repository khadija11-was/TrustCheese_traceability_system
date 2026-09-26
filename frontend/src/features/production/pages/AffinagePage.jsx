import React, { useEffect, useMemo, useState } from 'react';
import { CheckCircle2, Factory, Plus, XCircle } from 'lucide-react';
import { useSearchParams } from 'react-router-dom';
import affinageService from '@/services/affinageService';
import productionService from '@/services/productionService';

const emptyForm = { productionId: '', temperature: '', humidite: '', observations: '' };

const formatDate = (value) => value ? new Date(value).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' }) : '-';
const statusLabel = { EN_COURS: 'En cours', TERMINE: 'Terminé', ANNULE: 'Annulé' };

function Modal({ children, onClose }) {
  return <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 p-4"><div className="w-full max-w-lg rounded-xl bg-white shadow-2xl"><div className="flex justify-end border-b border-slate-200 px-6 py-3"><button type="button" onClick={onClose} aria-label="Fermer"><XCircle size={20} className="text-slate-400" /></button></div>{children}</div></div>;
}

export default function AffinagePage() {
  const [searchParams] = useSearchParams();
  const [affinages, setAffinages] = useState([]);
  const [productions, setProductions] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [modalOpen, setModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const load = async () => {
    setLoading(true);
    try {
      const [affinageData, productionData] = await Promise.all([affinageService.getAll(), productionService.getAll()]);
      setAffinages(affinageData);
      setProductions(productionData.filter((production) => production.statut === 'TERMINEE'));
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Impossible de charger les affinages.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  useEffect(() => {
    const productionId = searchParams.get('productionId');
    if (productionId) {
      setForm((current) => ({ ...current, productionId }));
      setModalOpen(true);
    }
  }, [searchParams]);

  const availableProductions = useMemo(() => productions.filter((production) => !affinages.some((affinage) => affinage.productionId === production.id && affinage.statut !== 'ANNULE')), [productions, affinages]);

  const update = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }));

  const submit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      await affinageService.create({
        productionId: Number(form.productionId),
        temperature: Number(form.temperature),
        humidite: Number(form.humidite),
        observations: form.observations || null,
      });
      setModalOpen(false);
      setForm(emptyForm);
      await load();
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Création de l’affinage impossible.');
    } finally {
      setSaving(false);
    }
  };

  const changeStatus = async (action, id) => {
    try {
      await action(id);
      await load();
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Mise à jour impossible.');
    }
  };

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 border-b border-slate-200 pb-5 sm:flex-row sm:items-end sm:justify-between">
        <div><p className="text-sm font-semibold uppercase tracking-widest text-amber-600">Maturation</p><h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">Gestion des affinages</h1><p className="mt-2 text-sm text-slate-500">Suivez les lots en cave et contrôlez leur cycle de maturation.</p></div>
        <button type="button" onClick={() => { setForm(emptyForm); setModalOpen(true); }} className="inline-flex items-center gap-2 rounded-lg bg-amber-400 px-4 py-2.5 text-sm font-bold text-slate-950 hover:bg-amber-500"><Plus size={17} /> Nouvel affinage</button>
      </div>
      {error && <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}
      <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        {loading ? <p className="p-10 text-center text-sm text-slate-500">Chargement des affinages...</p> : affinages.length === 0 ? <p className="p-10 text-center text-sm text-slate-500">Aucun affinage enregistré.</p> : <div className="overflow-x-auto"><table className="w-full min-w-[900px] text-left text-sm"><thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500"><tr><th className="px-5 py-4">Affinage</th><th className="px-5 py-4">Production</th><th className="px-5 py-4">Début</th><th className="px-5 py-4">Température</th><th className="px-5 py-4">Humidité</th><th className="px-5 py-4">Lots</th><th className="px-5 py-4">Statut</th><th className="px-5 py-4 text-right">Actions</th></tr></thead><tbody className="divide-y divide-slate-100 text-slate-700">{affinages.map((item) => <tr key={item.id} className="hover:bg-slate-50"><td className="px-5 py-4 font-mono text-xs font-bold text-slate-600">{item.numeroAffinage}</td><td className="px-5 py-4 font-semibold text-slate-900">{item.numeroProduction || `Production #${item.productionId}`}</td><td className="px-5 py-4">{formatDate(item.dateDebutReelle)}</td><td className="px-5 py-4">{item.temperature} °C</td><td className="px-5 py-4">{item.humidite} %</td><td className="px-5 py-4">{item.nombreLots ?? '-'}</td><td className="px-5 py-4"><span className={`rounded-full px-2 py-1 text-xs font-semibold ${item.statut === 'EN_COURS' ? 'bg-amber-100 text-amber-700' : item.statut === 'TERMINE' ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'}`}>{statusLabel[item.statut] || item.statut}</span></td><td className="px-5 py-4 text-right">{item.statut === 'EN_COURS' && <div className="inline-flex gap-2"><button type="button" onClick={() => changeStatus(affinageService.finish, item.id)} className="rounded-md p-2 text-emerald-600 hover:bg-emerald-50" aria-label="Terminer"><CheckCircle2 size={16} /></button><button type="button" onClick={() => changeStatus(affinageService.cancel, item.id)} className="rounded-md p-2 text-red-500 hover:bg-red-50" aria-label="Annuler"><XCircle size={16} /></button></div>}</td></tr>)}</tbody></table></div>}
      </div>
      {modalOpen && <Modal onClose={() => setModalOpen(false)}><form onSubmit={submit} className="space-y-4 p-6"><h2 className="text-lg font-bold text-slate-900">Enregistrer un affinage</h2><label className="block space-y-1 text-sm font-medium text-slate-700"><span>Production terminée *</span><select name="productionId" value={form.productionId} onChange={update} required className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-amber-500"><option value="">Sélectionner une production</option>{availableProductions.map((production) => <option key={production.id} value={production.id}>{production.numeroProduction} · {production.produitNom}</option>)}</select></label><div className="grid grid-cols-2 gap-4"><label className="block space-y-1 text-sm font-medium text-slate-700"><span>Température °C *</span><input name="temperature" type="number" step="0.1" value={form.temperature} onChange={update} required className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-amber-500" /></label><label className="block space-y-1 text-sm font-medium text-slate-700"><span>Humidité % *</span><input name="humidite" type="number" step="0.1" min="0" max="100" value={form.humidite} onChange={update} required className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-amber-500" /></label></div><label className="block space-y-1 text-sm font-medium text-slate-700"><span>Observations</span><textarea name="observations" value={form.observations} onChange={update} rows="3" className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-amber-500" /></label><div className="flex justify-end border-t border-slate-200 pt-4"><button type="submit" disabled={saving} className="inline-flex items-center gap-2 rounded-lg bg-amber-400 px-4 py-2 text-sm font-bold text-slate-950 disabled:opacity-50"><Factory size={16} />{saving ? 'Enregistrement...' : 'Démarrer l’affinage'}</button></div></form></Modal>}
    </section>
  );
}
