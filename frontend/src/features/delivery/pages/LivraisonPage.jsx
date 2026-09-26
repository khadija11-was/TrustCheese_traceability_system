import React, { useEffect, useMemo, useState } from 'react';
import { ChevronDown, ChevronRight, MapPin, Package, Plus, Search, Send, X } from 'lucide-react';
import livraisonService from '@/services/livraisonService';
import clientService from '@/services/clientService';
import produitService from '@/services/produitService';

const statuses = [
  { value: '', label: 'Tous' },
  { value: 'PLANIFIEE', label: 'En préparation' },
  { value: 'EN_PREPARATION', label: 'Préparée' },
  { value: 'EXPEDIEE', label: 'En transit' },
  { value: 'LIVREE', label: 'Livrée' },
  { value: 'ANNULEE', label: 'Annulée' },
];

const statusLabels = Object.fromEntries(
  statuses.map((status) => [status.value, status.label])
);

const emptyForm = { 
  clientId: '', 
  dateLivraisonPrevue: '', 
  adresseLivraison: '', 
  ville: '', 
  pays: '', 
  temperatureMoyenneCamion: '', 
  lignes: [{ produitId: '', quantiteLots: 1 }] 
};

const formatDate = (value) => 
  value ? new Date(value).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' }) : '-';

function statusClass(status) {
  return status === 'LIVREE' 
    ? 'bg-emerald-100 text-emerald-700' 
    : status === 'EXPEDIEE' 
    ? 'bg-blue-100 text-blue-700' 
    : status === 'ANNULEE' 
    ? 'bg-red-100 text-red-700' 
    : 'bg-amber-100 text-amber-700';
}

function DeliveryCard({ item, selected, onClick }) {
  const lotCount = item.lignes?.reduce((sum, line) => sum + (line.quantiteLots || 0), 0) || 0;

  return (
    <button 
      type="button" 
      onClick={onClick} 
      className={`w-full rounded-lg border bg-white p-4 text-left transition ${
        selected ? 'border-blue-500 ring-2 ring-blue-500' : 'border-slate-200 hover:border-blue-300'
      }`}
    >
      <div className="flex items-start justify-between gap-2">
        <span className="font-mono text-xs font-bold text-slate-600">
          {item.numeroLivraison}
        </span>
        <span className={`rounded-full px-2 py-1 text-[10px] font-bold ${statusClass(item.statut)}`}>
          {statusLabels[item.statut] || item.statut}
        </span>
      </div>

      <p className="mt-3 font-bold text-slate-900">{item.clientNom}</p>
      
      <p className="mt-1 text-xs text-slate-500">
        {item.ville || '-'}{item.pays ? `, ${item.pays}` : ''}
      </p>

      <div className="mt-3 flex justify-between text-xs text-slate-500">
        <span>{formatDate(item.dateLivraisonPrevue)}</span>
        <span>{lotCount} lot(s)</span>
      </div>
    </button>
  );
}

function CreateForm({ clients, products, form, saving, onChange, onLineChange, onAddLine, onRemoveLine, onSubmit, onClose }) {
  const client = clients.find((item) => String(item.id) === String(form.clientId));
  const available = products.length > 0;

  return (
    <form onSubmit={onSubmit} className="space-y-5">
      <div className="flex items-center justify-between border-b border-slate-200 pb-4">
        <div>
          <p className="text-xs font-semibold uppercase tracking-widest text-blue-600">
            Nouvelle opération
          </p>
          <h2 className="mt-1 text-xl font-bold text-slate-900">Créer une livraison</h2>
        </div>
        <button type="button" onClick={onClose} aria-label="Fermer" className="text-slate-400 hover:text-slate-700">
          <X size={20} />
        </button>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <label className="text-sm font-medium text-slate-700">
          <span>Client *</span>
          <select 
            name="clientId" 
            value={form.clientId} 
            onChange={onChange} 
            required 
            className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-blue-500"
          >
            <option value="">Sélectionner un client</option>
            {clients.map((item) => (
              <option key={item.id} value={item.id}>{item.nom}</option>
            ))}
          </select>
        </label>

        <label className="text-sm font-medium text-slate-700">
          <span>Date prévue</span>
          <input 
            name="dateLivraisonPrevue" 
            type="datetime-local" 
            value={form.dateLivraisonPrevue} 
            onChange={onChange} 
            className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-blue-500" 
          />
        </label>
      </div>

      <div className="rounded-lg bg-slate-50 p-4">
        <p className="mb-3 text-sm font-bold text-slate-800">Destination</p>
        <div className="grid gap-3 sm:grid-cols-3">
          <input 
            name="adresseLivraison" 
            value={form.adresseLivraison} 
            onChange={onChange} 
            placeholder={client?.adresse || 'Adresse'} 
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm" 
          />
          <input 
            name="ville" 
            value={form.ville} 
            onChange={onChange} 
            placeholder={client?.ville || 'Ville'} 
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm" 
          />
          <input 
            name="pays" 
            value={form.pays} 
            onChange={onChange} 
            placeholder="Pays" 
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm" 
          />
        </div>
      </div>

      <div>
        <div className="mb-3 flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-800">Lignes de produits</h3>
          <button type="button" onClick={onAddLine} className="inline-flex items-center gap-1 text-xs font-bold text-blue-600">
            <Plus size={14} /> Ajouter un produit
          </button>
        </div>

        <div className="space-y-3">
          {form.lignes.map((line, index) => (
            <div key={index} className="flex gap-2">
              <select 
                name="produitId" 
                value={line.produitId} 
                onChange={(event) => onLineChange(index, event)} 
                required 
                className="min-w-0 flex-1 rounded-lg border border-slate-300 px-3 py-2 text-sm"
              >
                <option value="">{available ? 'Sélectionner un produit' : 'Aucun produit disponible'}</option>
                {products.map((product) => (
                  <option key={product.id} value={product.id}>{product.nom}</option>
                ))}
              </select>

              <input 
                name="quantiteLots" 
                type="number" 
                min="1" 
                value={line.quantiteLots} 
                onChange={(event) => onLineChange(index, event)} 
                required 
                className="w-24 rounded-lg border border-slate-300 px-3 py-2 text-sm" 
              />

              {form.lignes.length > 1 && (
                <button type="button" onClick={() => onRemoveLine(index)} className="px-2 text-slate-400 hover:text-red-600" aria-label="Supprimer la ligne">
                  <X size={17} />
                </button>
              )}
            </div>
          ))}
        </div>

        <p className="mt-2 text-xs text-slate-500">
          La disponibilité des lots LIBERE est vérifiée par le backend lors de la préparation.
        </p>
      </div>

      <div className="flex justify-end gap-3 border-t border-slate-200 pt-4">
        <button type="button" onClick={onClose} className="rounded-lg px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100">
          Annuler
        </button>
        <button type="submit" disabled={saving} className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-bold text-white disabled:opacity-50">
          {saving ? 'Création...' : 'Créer la livraison'}
        </button>
      </div>
    </form>
  );
}

function DeliveryDetail({ item, onAction }) {
  const [openLine, setOpenLine] = useState(null);

  if (!item) {
    return (
      <div className="flex min-h-[520px] flex-col items-center justify-center text-center text-slate-500">
        <Send size={42} className="text-blue-300" />
        <h2 className="mt-4 text-lg font-bold text-slate-700">Sélectionnez une livraison</h2>
        <p className="mt-2 text-sm">Consultez le détail ou créez un nouveau bon de livraison.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 border-b border-slate-200 pb-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="font-mono text-xs font-bold text-slate-500">{item.numeroLivraison}</p>
          <h2 className="mt-1 text-2xl font-bold text-slate-900">Bon de livraison</h2>
        </div>
        <span className={`rounded-full px-3 py-1 text-xs font-bold ${statusClass(item.statut)}`}>
          {statusLabels[item.statut] || item.statut}
        </span>
      </div>

      <div className="grid gap-3 rounded-lg bg-slate-50 p-4 text-sm sm:grid-cols-3">
        <div>
          <span className="text-xs text-slate-500">Client</span>
          <p className="font-semibold text-slate-900">{item.clientNom}</p>
        </div>
        <div>
          <span className="text-xs text-slate-500">Responsable</span>
          <p className="font-semibold text-slate-900">{item.responsableNom || '-'}</p>
        </div>
        <div>
          <span className="text-xs text-slate-500">Prévue / réelle</span>
          <p className="font-semibold text-slate-900">
            {formatDate(item.dateLivraisonPrevue)}<br />{formatDate(item.dateLivraisonReelle)}
          </p>
        </div>
      </div>

      <div className="flex items-start gap-3 text-sm text-slate-600">
        <MapPin size={18} className="mt-0.5 text-blue-600" />
        <span>{item.adresseLivraison || '-'}, {item.ville || '-'}, {item.pays || '-'}</span>
      </div>

      <div>
        <h3 className="mb-3 text-sm font-bold uppercase tracking-wide text-slate-700">Lignes d'expédition</h3>
        <div className="space-y-2">
          {(item.lignes || []).map((line) => (
            <div key={line.id} className="rounded-lg border border-slate-200">
              <button 
                type="button" 
                onClick={() => setOpenLine(openLine === line.id ? null : line.id)} 
                className="flex w-full items-center justify-between p-4 text-left"
              >
                <span>
                  <span className="font-semibold text-slate-900">{line.produitNom}</span>
                  <span className="ml-3 text-xs text-slate-500">{line.quantiteLots} lot(s)</span>
                </span>
                {openLine === line.id ? <ChevronDown size={17} /> : <ChevronRight size={17} />}
              </button>

              {openLine === line.id && (
                <div className="border-t border-slate-100 bg-slate-50 p-3">
                  {(line.lots || []).map((lot) => (
                    <div key={lot.id} className="flex items-center justify-between border-b border-slate-200 py-2 text-xs last:border-0">
                      <span className="font-mono font-semibold">{lot.numeroLot}</span>
                      <span>{lot.dateExpiration || '-'}</span>
                      <span className="rounded-full bg-white px-2 py-1">{lot.statut}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      <div className="flex flex-wrap gap-2 border-t border-slate-200 pt-4">
        {item.statut === 'PLANIFIEE' && (
          <button type="button" onClick={() => onAction('prepare', item.id)} className="rounded-lg bg-amber-500 px-3 py-2 text-xs font-bold text-white">
            Préparer
          </button>
        )}
        {item.statut === 'EN_PREPARATION' && (
          <button type="button" onClick={() => onAction('ship', item.id)} className="rounded-lg bg-blue-600 px-3 py-2 text-xs font-bold text-white">
            Expédier
          </button>
        )}
        {item.statut === 'EXPEDIEE' && (
          <button type="button" onClick={() => onAction('confirm', item.id)} className="rounded-lg bg-emerald-600 px-3 py-2 text-xs font-bold text-white">
            Confirmer la livraison
          </button>
        )}
        {['PLANIFIEE', 'EN_PREPARATION'].includes(item.statut) && (
          <button type="button" onClick={() => onAction('cancel', item.id)} className="rounded-lg border border-red-200 px-3 py-2 text-xs font-bold text-red-600">
            Annuler
          </button>
        )}
      </div>
    </div>
  );
}

export default function LivraisonPage() {
  const [deliveries, setDeliveries] = useState([]);
  const [clients, setClients] = useState([]);
  const [products, setProducts] = useState([]);
  const [selected, setSelected] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [creating, setCreating] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const [deliveryData, clientData, productData] = await Promise.all([
        livraisonService.getAll(),
        clientService.getAll(),
        produitService.getAll(),
      ]);
      setDeliveries(deliveryData);
      setClients(clientData);
      setProducts(productData);
      setSelected((current) => current ? deliveryData.find((item) => item.id === current.id) || null : null);
    } catch (e) {
      setError(e.response?.data?.message || 'Impossible de charger les livraisons.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const visible = useMemo(
    () => deliveries.filter((item) => 
      `${item.numeroLivraison} ${item.clientNom}`.toLowerCase().includes(search.toLowerCase()) && 
      (!filter || item.statut === filter)
    ),
    [deliveries, search, filter]
  );

  const update = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: name === 'clientId' ? value : value }));

    if (name === 'clientId') {
      const client = clients.find((item) => String(item.id) === value);
      setForm((current) => ({ 
        ...current, 
        clientId: value, 
        adresseLivraison: client?.adresse || '', 
        ville: client?.ville || '' 
      }));
    }
  };

  const updateLine = (index, event) => 
    setForm((current) => ({
      ...current,
      lignes: current.lignes.map((line, lineIndex) => 
        lineIndex === index ? { ...line, [event.target.name]: event.target.value } : line
      )
    }));

  const submit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      const created = await livraisonService.create({
        clientId: Number(form.clientId),
        dateLivraisonPrevue: form.dateLivraisonPrevue ? `${form.dateLivraisonPrevue}:00` : null,
        adresseLivraison: form.adresseLivraison,
        ville: form.ville,
        pays: form.pays,
        lignes: form.lignes.map((line) => ({
          produitId: Number(line.produitId),
          quantiteLots: Number(line.quantiteLots),
        })),
      });
      setCreating(false);
      setSelected(created);
      setForm(emptyForm);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Création de la livraison impossible.');
    } finally {
      setSaving(false);
    }
  };

  const action = async (type, id) => {
    try {
      const updated = await livraisonService[type](id);
      setSelected(updated);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Action impossible.');
    }
  };

  return (
    <section className="space-y-6">
      <div className="border-b border-slate-200 pb-5">
        <p className="text-sm font-semibold uppercase tracking-widest text-blue-600">
          Expédition & suivi
        </p>
        <h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">
          Gestion des livraisons
        </h1>
        <p className="mt-2 text-sm text-slate-500">
          Pilotez les bons de livraison et le suivi des lots expédiés.
        </p>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="grid min-h-[650px] gap-0 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm lg:grid-cols-[40%_60%]">
        <aside className="border-b border-slate-200 bg-slate-50 lg:border-b-0 lg:border-r">
          <div className="border-b border-slate-200 p-4">
            <div className="relative">
              <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              <input 
                value={search} 
                onChange={(event) => setSearch(event.target.value)} 
                placeholder="Rechercher une livraison ou un client..." 
                className="w-full rounded-lg border border-slate-300 bg-white py-2 pl-9 pr-3 text-sm outline-none focus:border-blue-500" 
              />
            </div>

            <div className="mt-3 flex flex-wrap gap-1">
              {statuses.map((status) => (
                <button 
                  key={status.value} 
                  type="button" 
                  onClick={() => setFilter(status.value)} 
                  className={`rounded-full px-2.5 py-1 text-[11px] font-semibold ${
                    filter === status.value ? 'bg-blue-600 text-white' : 'bg-white text-slate-500 ring-1 ring-slate-200'
                  }`}
                >
                  {status.label}
                </button>
              ))}
            </div>

            <button 
              type="button" 
              onClick={() => { setCreating(true); setSelected(null); setForm(emptyForm); }} 
              className="mt-4 inline-flex w-full items-center justify-center gap-2 rounded-lg bg-blue-600 px-3 py-2 text-sm font-bold text-white hover:bg-blue-700"
            >
              <Plus size={16} /> Nouvelle livraison
            </button>
          </div>

          <div className="space-y-2 overflow-y-auto p-3">
            {loading ? (
              <p className="p-6 text-center text-sm text-slate-500">Chargement...</p>
            ) : (
              visible.map((item) => (
                <DeliveryCard 
                  key={item.id} 
                  item={item} 
                  selected={selected?.id === item.id} 
                  onClick={() => { setCreating(false); setSelected(item); }} 
                />
              ))
            )}
          </div>
        </aside>

        <main className="p-5 sm:p-7">
          {creating ? (
            <CreateForm 
              clients={clients} 
              products={products} 
              form={form} 
              saving={saving} 
              onChange={update} 
              onLineChange={updateLine} 
              onAddLine={() => setForm((current) => ({ ...current, lignes: [...current.lignes, { produitId: '', quantiteLots: 1 }] }))} 
              onRemoveLine={(index) => setForm((current) => ({ ...current, lignes: current.lignes.filter((_, lineIndex) => lineIndex !== index) }))} 
              onSubmit={submit} 
              onClose={() => setCreating(false)} 
            />
          ) : (
            <DeliveryDetail item={selected} onAction={action} />
          )}
        </main>
      </div>
    </section>
  );
}