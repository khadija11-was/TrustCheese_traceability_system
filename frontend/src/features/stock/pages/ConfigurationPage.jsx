import React, { useEffect, useState } from 'react';
import { Pencil, Plus, Search, Trash2, X } from 'lucide-react';
import fournisseurService from '@/services/fournisseurService';
import matierePremiereService from '@/services/matierePremiereService';
import produitService from '@/services/produitService';

const units = [
  { value: 'KG', label: 'Kilogrammes (kg)' },
  { value: 'L', label: 'Litres (L)' },
  { value: 'G', label: 'Grammes (g)' },
  { value: 'ML', label: 'Millilitres (ml)' },
  { value: 'UNITE', label: 'Unités' },
];

const emptyMaterial = { nom: '', code: '', uniteMesure: 'KG', description: '', seuilStock: '' };
const emptySupplier = { nom: '', email: '', telephone: '', adresse: '' };
const emptyProduct = { nom: '', description: '', quantiteStandardLot: '', dureeConservationJours: '', image: null };
const productImageTypes = ['image/jpeg', 'image/png', 'image/webp'];
const productImageMaxSize = 5 * 1024 * 1024;

const productImageUrl = (imageUrl) => imageUrl ? `http://localhost:8080${imageUrl}` : '';

function Modal({ title, children, onClose }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 p-4">
      <div className="w-full max-w-lg rounded-xl bg-white shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4">
          <h2 className="text-lg font-bold text-slate-900">{title}</h2>
          <button type="button" onClick={onClose} className="text-slate-400 hover:text-slate-700" aria-label="Fermer">
            <X size={20} />
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

function Field({ label, name, value, onChange, required = false, ...props }) {
  return (
    <label className="block space-y-1 text-sm font-medium text-slate-700">
      <span>{label}{required && ' *'}</span>
      <input
        {...props}
        name={name}
        value={value}
        onChange={onChange}
        required={required}
        className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 outline-none focus:border-amber-500 focus:ring-2 focus:ring-amber-100"
      />
    </label>
  );
}

function MaterialForm({ value, editing, loading, onChange, onSubmit, onClose }) {
  return (
    <Modal title={editing ? 'Modifier la matière première' : 'Ajouter une matière première'} onClose={onClose}>
      <form onSubmit={onSubmit} className="space-y-4 p-6">
        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="Désignation" name="nom" value={value.nom} onChange={onChange} required placeholder="Lait cru" />
          <Field label="Code" name="code" value={value.code} onChange={onChange} required placeholder="MP-LAIT-001" />
        </div>
        <label className="block space-y-1 text-sm font-medium text-slate-700">
          <span>Unité de mesure</span>
          <select name="uniteMesure" value={value.uniteMesure} onChange={onChange} className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 outline-none focus:border-amber-500" required>
            {units.map((unit) => <option key={unit.value} value={unit.value}>{unit.label}</option>)}
          </select>
        </label>
        <Field label="Seuil de stock" name="seuilStock" type="number" min="0" step="0.01" value={value.seuilStock} onChange={onChange} placeholder="0" />
        <label className="block space-y-1 text-sm font-medium text-slate-700">
          <span>Description</span>
          <textarea name="description" value={value.description} onChange={onChange} rows="3" className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 outline-none focus:border-amber-500" />
        </label>
        <FormActions loading={loading} editing={editing} onClose={onClose} />
      </form>
    </Modal>
  );
}

function SupplierForm({ value, editing, loading, onChange, onSubmit, onClose }) {
  return (
    <Modal title={editing ? 'Modifier le fournisseur' : 'Ajouter un fournisseur'} onClose={onClose}>
      <form onSubmit={onSubmit} className="space-y-4 p-6">
        <Field label="Nom du fournisseur" name="nom" value={value.nom} onChange={onChange} required placeholder="Ferme A" />
        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="Email" name="email" type="email" value={value.email} onChange={onChange} placeholder="contact@ferme.ma" />
          <Field label="Téléphone" name="telephone" value={value.telephone} onChange={onChange} placeholder="06 00 00 00 00" />
        </div>
        <Field label="Adresse / localisation" name="adresse" value={value.adresse} onChange={onChange} placeholder="Région, ville" />
        <FormActions loading={loading} editing={editing} onClose={onClose} />
      </form>
    </Modal>
  );
}

function ProductForm({ value, editing, loading, error, onChange, onImageChange, onSubmit, onClose }) {
  return (
    <Modal title={editing ? 'Modifier le produit' : 'Ajouter un produit'} onClose={onClose}>
      <form onSubmit={onSubmit} className="space-y-4 p-6">
        <Field label="Nom du produit" name="nom" value={value.nom} onChange={onChange} required placeholder="Gouda affinée" />
        <label className="block space-y-1 text-sm font-medium text-slate-700">
          <span>Description</span>
          <textarea name="description" value={value.description} onChange={onChange} rows="3" className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 outline-none focus:border-amber-500" placeholder="Description du produit" />
        </label>
        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="Quantité standard par lot" name="quantiteStandardLot" type="number" min="0.01" step="0.01" value={value.quantiteStandardLot} onChange={onChange} required placeholder="250" />
          <Field label="Conservation (jours)" name="dureeConservationJours" type="number" min="1" step="1" value={value.dureeConservationJours} onChange={onChange} required placeholder="90" />
        </div>
        <label className="block space-y-2 text-sm font-medium text-slate-700">
          <span>Image du produit{!editing && ' *'}</span>
          <input id="product-image" name="image" type="file" accept="image/jpeg,image/png,image/webp" onChange={onImageChange} className="sr-only" />
          <label htmlFor="product-image" className="flex cursor-pointer items-center gap-4 rounded-lg border border-dashed border-slate-300 bg-slate-50 p-3 hover:border-amber-500 hover:bg-amber-50/40">
            {value.image ? <img src={URL.createObjectURL(value.image)} alt="Aperçu du produit" className="h-16 w-16 rounded-md object-cover" /> : <div className="flex h-16 w-16 items-center justify-center rounded-md bg-slate-200 text-xs text-slate-500">Aperçu</div>}
            <div><p className="text-sm font-semibold text-slate-700">{value.image ? value.image.name : 'Choisir une image'}</p><p className="mt-1 text-xs text-slate-500">JPG, PNG ou WEBP, 5 MB maximum</p></div>
          </label>
        </label>
        {error && <p className="rounded-md bg-red-50 px-3 py-2 text-xs text-red-700">{error}</p>}
        <FormActions loading={loading} editing={editing} onClose={onClose} />
      </form>
    </Modal>
  );
}

function FormActions({ loading, editing, onClose }) {
  return (
    <div className="flex justify-end gap-3 border-t border-slate-200 pt-4">
      <button type="button" onClick={onClose} className="rounded-lg px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100">Annuler</button>
      <button type="submit" disabled={loading} className="rounded-lg bg-amber-400 px-4 py-2 text-sm font-bold text-slate-950 hover:bg-amber-500 disabled:opacity-50">
        {loading ? 'Enregistrement...' : editing ? 'Enregistrer' : 'Ajouter'}
      </button>
    </div>
  );
}

export default function ConfigurationPage() {
  const [tab, setTab] = useState('materials');
  const [materials, setMaterials] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(emptyMaterial);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [materialData, supplierData, productData] = await Promise.all([
        matierePremiereService.getAll(),
        fournisseurService.getAll(),
        produitService.getAll(),
      ]);
      setMaterials(materialData);
      setSuppliers(supplierData);
      setProducts(productData);
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Impossible de charger les référentiels.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const openCreate = () => {
    setForm(tab === 'materials' ? emptyMaterial : tab === 'suppliers' ? emptySupplier : emptyProduct);
    setFormError('');
    setModal({ type: tab, item: null });
  };

  const openEdit = (type, item) => {
    setForm(type === 'materials' ? {
      nom: item.nom || '', code: item.code || '', uniteMesure: item.uniteMesure || 'KG', description: item.description || '', seuilStock: item.seuilStock ?? '',
    } : type === 'suppliers' ? {
      nom: item.nom || '', email: item.email || '', telephone: item.telephone || '', adresse: item.adresse || '',
    } : {
      nom: item.nom || '', description: item.description || '', quantiteStandardLot: item.quantiteStandardLot ?? '', dureeConservationJours: item.dureeConservationJours ?? '', image: null,
    });
    setFormError('');
    setModal({ type, item });
  };

  const handleChange = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }));

  const handleImageChange = (event) => {
    const image = event.target.files?.[0];
    if (!image) return;
    if (!productImageTypes.includes(image.type)) {
      setFormError('Format non supporté. Utilisez une image JPG, PNG ou WEBP.');
      event.target.value = '';
      return;
    }
    if (image.size > productImageMaxSize) {
      setFormError("L'image ne doit pas dépasser 5 MB.");
      event.target.value = '';
      return;
    }
    setFormError('');
    setForm((current) => ({ ...current, image }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (modal.type === 'products' && !modal.item && !form.image) {
      setFormError("L'image du produit est obligatoire.");
      return;
    }
    setSaving(true);
    setError('');
    setFormError('');
    try {
      const payload = { ...form };
      if (payload.seuilStock === '') payload.seuilStock = null;
      if (modal.type === 'materials') {
        if (modal.item) await matierePremiereService.update(modal.item.id, payload);
        else await matierePremiereService.create(payload);
      } else if (modal.type === 'suppliers' && modal.item) {
        await fournisseurService.update(modal.item.id, payload);
      } else if (modal.type === 'suppliers') {
        await fournisseurService.create(payload);
      } else if (modal.item) {
        await produitService.update(modal.item.id, payload);
      } else {
        await produitService.create(payload);
      }
      setModal(null);
      await loadData();
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Enregistrement impossible.');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (type, item) => {
    if (!window.confirm(`Supprimer ${item.nom} ?`)) return;
    try {
      if (type === 'materials') await matierePremiereService.remove(item.id);
      else await fournisseurService.remove(item.id);
      await loadData();
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Suppression impossible.');
    }
  };

  const visibleItems = (tab === 'materials' ? materials : tab === 'suppliers' ? suppliers : products).filter((item) => {
    const value = `${item.nom} ${item.code || ''} ${item.email || ''} ${item.adresse || ''} ${item.description || ''}`.toLowerCase();
    return value.includes(search.toLowerCase());
  });

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 border-b border-slate-200 pb-6 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-sm font-semibold uppercase tracking-widest text-amber-600">Approvisionnement</p>
          <h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">Référentiels d'approvisionnement</h1>
          <p className="mt-2 text-sm text-slate-500">Centralisez les matières premières, les partenaires et les produits de votre chaîne d'approvisionnement.</p>
        </div>
        <button type="button" onClick={openCreate} className="inline-flex items-center justify-center gap-2 rounded-lg bg-amber-400 px-4 py-2.5 text-sm font-bold text-slate-950 hover:bg-amber-500">
          <Plus size={18} /> Ajouter {tab === 'materials' ? 'une matière' : tab === 'suppliers' ? 'un fournisseur' : 'un produit'}
        </button>
      </div>

      <div className="flex gap-2 border-b border-slate-200">
        <button type="button" onClick={() => { setTab('materials'); setSearch(''); }} className={`border-b-2 px-4 py-3 text-sm font-semibold ${tab === 'materials' ? 'border-amber-500 text-slate-900' : 'border-transparent text-slate-500'}`}>Matières premières ({materials.length})</button>
        <button type="button" onClick={() => { setTab('suppliers'); setSearch(''); }} className={`border-b-2 px-4 py-3 text-sm font-semibold ${tab === 'suppliers' ? 'border-amber-500 text-slate-900' : 'border-transparent text-slate-500'}`}>Fournisseurs ({suppliers.length})</button>
        <button type="button" onClick={() => { setTab('products'); setSearch(''); }} className={`border-b-2 px-4 py-3 text-sm font-semibold ${tab === 'products' ? 'border-amber-500 text-slate-900' : 'border-transparent text-slate-500'}`}>Produits ({products.length})</button>
      </div>

      <div className="relative max-w-md">
        <Search size={17} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
        <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Rechercher..." className="w-full rounded-lg border border-slate-300 bg-white py-2.5 pl-10 pr-4 text-sm outline-none focus:border-amber-500" />
      </div>

      {error && <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}
      <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        {loading ? <p className="p-8 text-center text-sm text-slate-500">Chargement des référentiels...</p> : visibleItems.length === 0 ? <p className="p-8 text-center text-sm text-slate-500">Aucune donnée trouvée.</p> : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="px-5 py-4">{tab === 'products' ? 'Image' : 'Code'}</th><th className="px-5 py-4">{tab === 'products' ? 'Produit' : 'Nom'}</th>
                  {tab === 'materials' ? <><th className="px-5 py-4">Unité</th><th className="px-5 py-4">Seuil</th></> : tab === 'suppliers' ? <><th className="px-5 py-4">Contact</th><th className="px-5 py-4">Localisation</th></> : <><th className="px-5 py-4">Description</th><th className="px-5 py-4">Quantité standard</th><th className="px-5 py-4">Conservation</th></>}
                  <th className="px-5 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                {visibleItems.map((item) => <tr key={item.id} className="hover:bg-slate-50">
                  <td className="px-5 py-4">{tab === 'products' ? (item.imageUrl ? <img src={productImageUrl(item.imageUrl)} alt={item.nom} className="h-12 w-12 rounded-md object-cover" /> : <div className="flex h-12 w-12 items-center justify-center rounded-md bg-slate-100 text-[10px] text-slate-400">Sans image</div>) : <span className="font-mono text-xs text-slate-500">{item.codeFournisseur || item.code || '-'}</span>}</td>
                  <td className="px-5 py-4 font-semibold text-slate-900">{item.nom}</td>
                  {tab === 'materials' ? <><td className="px-5 py-4">{item.uniteMesure || '-'}</td><td className="px-5 py-4">{item.seuilStock ?? '-'}</td></> : tab === 'suppliers' ? <><td className="px-5 py-4">{item.email || item.telephone || '-'}</td><td className="px-5 py-4">{item.adresse || '-'}</td></> : <><td className="max-w-xs px-5 py-4">{item.description || '-'}</td><td className="px-5 py-4">{item.quantiteStandardLot}</td><td className="px-5 py-4">{item.dureeConservationJours} jours</td></>}
                  <td className="px-5 py-4 text-right"><div className="inline-flex gap-2"><button type="button" onClick={() => openEdit(tab, item)} className="rounded-md p-2 text-slate-500 hover:bg-slate-100 hover:text-slate-900" aria-label="Modifier"><Pencil size={16} /></button><button type="button" onClick={() => handleDelete(tab, item)} className="rounded-md p-2 text-red-500 hover:bg-red-50" aria-label="Supprimer"><Trash2 size={16} /></button></div></td>
                </tr>)}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {modal?.type === 'materials' && <MaterialForm value={form} editing={Boolean(modal.item)} loading={saving} onChange={handleChange} onSubmit={handleSubmit} onClose={() => setModal(null)} />}
      {modal?.type === 'suppliers' && <SupplierForm value={form} editing={Boolean(modal.item)} loading={saving} onChange={handleChange} onSubmit={handleSubmit} onClose={() => setModal(null)} />}
      {modal?.type === 'products' && <ProductForm value={form} editing={Boolean(modal.item)} loading={saving} error={formError} onChange={handleChange} onImageChange={handleImageChange} onSubmit={handleSubmit} onClose={() => setModal(null)} />}
    </section>
  );
}
