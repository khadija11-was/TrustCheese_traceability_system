import React, { useEffect, useMemo, useState } from 'react';
import { CalendarDays, Pencil, Plus, Search, Trash2, X } from 'lucide-react';
import lotMPService from '@/services/lotMPService';
import fournisseurService from '@/services/fournisseurService';
import matierePremiereService from '@/services/matierePremiereService';

const emptyForm = { quantite: '', dateReception: '', fournisseurId: '', matierePremiereId: '' };

const formatDate = (value) => value ? new Date(value).toLocaleDateString('fr-FR') : '-';
const formatQuantity = (value, unit) => `${value ?? '-'} ${unit || ''}`.trim();
const statusLabel = {
  DISPONIBLE: 'disponible',
  PARTIELLEMENT_UTILISE: 'partiellement utilisé',
  EPUISE: 'épuisé',
};

function ReceptionModal({ form, suppliers, materials, loading, onChange, onSubmit, onClose }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 p-4">
      <div className="w-full max-w-lg rounded-xl bg-white shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4">
          <h2 className="text-lg font-bold text-slate-900">Nouvelle réception</h2>
          <button type="button" onClick={onClose} aria-label="Fermer" className="text-slate-400 hover:text-slate-700"><X size={20} /></button>
        </div>
        <form onSubmit={onSubmit} className="space-y-4 p-6">
          <label className="block space-y-1 text-sm font-medium text-slate-700">
            <span>Matière première *</span>
            <select name="matierePremiereId" value={form.matierePremiereId} onChange={onChange} required className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-amber-500">
              <option value="">Sélectionner une matière</option>
              {materials.map((material) => <option key={material.id} value={material.id}>{material.nom} ({material.code})</option>)}
            </select>
          </label>
          <label className="block space-y-1 text-sm font-medium text-slate-700">
            <span>Fournisseur *</span>
            <select name="fournisseurId" value={form.fournisseurId} onChange={onChange} required className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-amber-500">
              <option value="">Sélectionner un fournisseur</option>
              {suppliers.map((supplier) => <option key={supplier.id} value={supplier.id}>{supplier.nom} ({supplier.codeFournisseur})</option>)}
            </select>
          </label>
          <div className="grid gap-4 sm:grid-cols-2">
            <label className="block space-y-1 text-sm font-medium text-slate-700">
              <span>Quantité *</span>
              <input name="quantite" type="number" min="0.01" step="0.01" value={form.quantite} onChange={onChange} required className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-amber-500" />
            </label>
            <label className="block space-y-1 text-sm font-medium text-slate-700">
              <span>Date de réception *</span>
              <input name="dateReception" type="date" value={form.dateReception} onChange={onChange} required className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-amber-500" />
            </label>
          </div>
          <div className="flex justify-end gap-3 border-t border-slate-200 pt-4">
            <button type="button" onClick={onClose} className="rounded-lg px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100">Annuler</button>
            <button type="submit" disabled={loading} className="rounded-lg bg-amber-400 px-4 py-2 text-sm font-bold text-slate-950 hover:bg-amber-500 disabled:opacity-50">{loading ? 'Enregistrement...' : 'Créer la réception'}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function ReceptionLotsPage() {
  const [lots, setLots] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [materials, setMaterials] = useState([]);
  const [search, setSearch] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [lotData, supplierData, materialData] = await Promise.all([
        lotMPService.getAll(), fournisseurService.getAll(), matierePremiereService.getAll(),
      ]);
      setLots(lotData);
      setSuppliers(supplierData);
      setMaterials(materialData);
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Impossible de charger les réceptions.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const visibleLots = useMemo(() => lots.filter((lot) => {
    const content = `${lot.numeroLot} ${lot.matierePremiereNom} ${lot.fournisseurNom}`.toLowerCase();
    return content.includes(search.toLowerCase()) && (!typeFilter || lot.etatStock === typeFilter);
  }), [lots, search, typeFilter]);

  const handleChange = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }));

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      await lotMPService.create({
        quantite: Number(form.quantite),
        dateReception: `${form.dateReception}T00:00:00`,
        fournisseurId: Number(form.fournisseurId),
        matierePremiereId: Number(form.matierePremiereId),
      });
      setModalOpen(false);
      setForm(emptyForm);
      await loadData();
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Création de la réception impossible.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="space-y-5">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Gestion des réceptions</h1>
          <p className="mt-1 text-sm text-slate-500">Suivi des lots de matières premières avec traçabilité.</p>
        </div>
        <button type="button" onClick={() => setModalOpen(true)} className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"><Plus size={16} /> Nouvelle réception</button>
      </div>

      <div className="flex flex-col gap-3 rounded-lg border border-slate-200 bg-white p-2 sm:flex-row">
        <div className="relative flex-1"><Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Rechercher par lot ou fournisseur..." className="w-full rounded-md border border-slate-200 py-2 pl-9 pr-3 text-sm outline-none focus:border-blue-500" /></div>
        <select value={typeFilter} onChange={(event) => setTypeFilter(event.target.value)} className="rounded-md border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500"><option value="">Tous les statuts</option><option value="DISPONIBLE">Disponible</option><option value="PARTIELLEMENT_UTILISE">Partiellement utilisé</option><option value="EPUISE">Épuisé</option></select>
      </div>

      {error && <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}
      <div className="overflow-hidden rounded-lg border border-slate-200 bg-white">
        {loading ? <p className="p-8 text-center text-sm text-slate-500">Chargement des réceptions...</p> : visibleLots.length === 0 ? <p className="p-8 text-center text-sm text-slate-500">Aucune réception trouvée.</p> : <div className="overflow-x-auto"><table className="w-full min-w-[900px] text-left text-sm"><thead className="bg-slate-50 text-[10px] uppercase tracking-wide text-slate-500"><tr><th className="px-5 py-3">Matière / lot</th><th className="px-5 py-3">Unité</th><th className="px-5 py-3">Quantité</th><th className="px-5 py-3">Fournisseur</th><th className="px-5 py-3">Réception</th><th className="px-5 py-3">Statut</th><th className="px-5 py-3">Restant / seuil</th><th className="px-5 py-3 text-right">Actions</th></tr></thead><tbody className="divide-y divide-slate-100 text-slate-700">{visibleLots.map((lot) => <tr key={lot.id} className="hover:bg-slate-50"><td className="px-5 py-4"><p className="font-semibold text-slate-900">{lot.matierePremiereNom}</p><p className="mt-1 font-mono text-xs text-slate-500">{lot.numeroLot}</p></td><td className="px-5 py-4 font-medium">{lot.uniteMesure || '-'}</td><td className="px-5 py-4 font-medium">{formatQuantity(lot.quantite, lot.uniteMesure)}</td><td className="px-5 py-4">{lot.fournisseurNom}</td><td className="px-5 py-4 text-slate-500">{formatDate(lot.dateReception)}</td><td className="px-5 py-4"><span className={`rounded-full px-2 py-1 text-[10px] font-semibold ${lot.etatStock === 'DISPONIBLE' ? 'bg-emerald-100 text-emerald-700' : lot.etatStock === 'EPUISE' ? 'bg-red-100 text-red-700' : 'bg-amber-100 text-amber-700'}`}>{statusLabel[lot.etatStock] || lot.etatStock || '-'}</span></td><td className="px-5 py-4 font-medium">{formatQuantity(lot.quantiteRestante, lot.uniteMesure)} / {formatQuantity(lot.seuilStock, lot.uniteMesure)}</td><td className="px-5 py-4 text-right"><div className="inline-flex gap-2 text-slate-400"><button type="button" disabled aria-label="Modifier indisponible" title="Modification non disponible par l'API" className="cursor-not-allowed"><Pencil size={15} /></button><button type="button" disabled aria-label="Suppression indisponible" title="Suppression non disponible par l'API" className="cursor-not-allowed"><Trash2 size={15} /></button></div></td></tr>)}</tbody></table></div>}
      </div>
      {modalOpen && <ReceptionModal form={form} suppliers={suppliers} materials={materials} loading={saving} onChange={handleChange} onSubmit={handleSubmit} onClose={() => setModalOpen(false)} />}
    </section>
  );
}
