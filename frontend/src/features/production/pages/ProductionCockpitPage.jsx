import React, { useEffect, useMemo, useState } from 'react';
import {
  Activity,
  Beaker,
  CalendarClock,
  CheckCircle2,
  ChevronRight,
  Factory,
  Gauge,
  PackagePlus,
  Play,
  ArrowRight,
  Thermometer,
  X
} from 'lucide-react';
import productionService from '@/services/productionService';
import produitService from '@/services/produitService';
import cuveService from '@/services/cuveService';
import lotMPService from '@/services/lotMPService';
import movementStockService from '@/services/movementStockService';

const emptyPlan = { produitId: '', dateDebutPrevue: '', dateFinPrevue: '' };
const emptyStart = { cuveId: '', temperatureCuve: '', phCuve: '' };
const emptyMeasure = { temperatureCuve: '', phCuve: '' };
const emptyMovement = { lotMPId: '', productionId: '', quantite: '' };

const dateTime = (value) =>
  value
    ? new Date(value).toLocaleString('fr-FR', {
        dateStyle: 'short',
        timeStyle: 'short'
      })
    : '-';

const statusText = {
  PLANIFIEE: 'Planifiée',
  EN_COURS: 'En cours',
  TERMINEE: 'Terminée',
  ANNULEE: 'Annulée'
};

function Modal({ title, children, onClose }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 p-4">
      <div className="w-full max-w-lg rounded-xl bg-white shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4">
          <h2 className="text-lg font-bold text-slate-900">{title}</h2>
          <button type="button" onClick={onClose} aria-label="Fermer">
            <X size={20} className="text-slate-400" />
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

function Input({ label, ...props }) {
  return (
    <label className="block space-y-1 text-sm font-medium text-slate-700">
      <span>{label}</span>
      <input
        {...props}
        className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-blue-500"
      />
    </label>
  );
}

function Select({ label, children, ...props }) {
  return (
    <label className="block space-y-1 text-sm font-medium text-slate-700">
      <span>{label}</span>
      <select
        {...props}
        className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-blue-500"
      >
        {children}
      </select>
    </label>
  );
}

function ProductionCard({ production, onStart, onMeasure, onComplete }) {
  return (
    <article className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="font-mono text-xs font-bold text-slate-500">
            {production.numeroProduction}
          </p>
          <h3 className="mt-1 font-bold text-slate-900">
            {production.produitNom}
          </h3>
        </div>
        {production.cuveNom && (
          <span className="rounded-full bg-blue-50 px-2 py-1 text-xs font-semibold text-blue-700">
            {production.cuveNom}
          </span>
        )}
      </div>

      {production.statut === 'PLANIFIEE' && (
        <div className="mt-4 space-y-2 text-xs text-slate-500">
          <p className="flex items-center gap-2">
            <CalendarClock size={14} /> {dateTime(production.dateDebutPrevue)}
          </p>
          <p>
            Responsable :{' '}
            <strong className="font-semibold text-slate-700">
              {production.operateurNom || 'Non affecté'}
            </strong>
          </p>
        </div>
      )}

      {production.statut === 'EN_COURS' && (
        <div className="mt-4 grid grid-cols-2 gap-2 text-xs">
          <div className="rounded-md bg-slate-50 p-2">
            <span className="text-slate-500">Début</span>
            <strong className="mt-1 block text-slate-900">
              {dateTime(production.dateDebut)}
            </strong>
          </div>
          <div className="rounded-md bg-slate-50 p-2">
            <span className="text-slate-500">Température</span>
            <strong className="mt-1 block text-slate-900">
              {production.temperatureCuve ?? '-'} °C
            </strong>
          </div>
          <div className="rounded-md bg-slate-50 p-2">
            <span className="text-slate-500">pH actuel</span>
            <strong className="mt-1 block text-slate-900">
              {production.phCuve ?? '-'}
            </strong>
          </div>
          <div className="col-span-2 rounded-md bg-slate-50 p-2">
            <span className="text-slate-500">Opérateur</span>
            <strong className="mt-1 block text-slate-900">
              {production.operateurNom || 'Non affecté'}
            </strong>
          </div>
        </div>
      )}

      {production.statut === 'TERMINEE' && (
        <div className="mt-4 space-y-2 text-xs text-slate-500">
          <div className="flex items-center justify-between">
            <span>{production.nombreLotsProduits} lots produits</span>
            <span>{dateTime(production.dateFin)}</span>
          </div>
          <p>
            Opérateur :{' '}
            <strong className="font-semibold text-slate-700">
              {production.operateurNom || 'Non affecté'}
            </strong>
          </p>
          <button
            type="button"
            onClick={() => { window.location.href = `/fabrication/affinage?productionId=${production.id}`; }}
            className="mt-2 inline-flex items-center gap-1 rounded-md bg-amber-400 px-3 py-2 text-xs font-semibold text-slate-950 hover:bg-amber-500"
          >
            Entrer en affinage <ArrowRight size={13} />
          </button>
        </div>
      )}

      <div className="mt-4 flex justify-end gap-2">
        {production.statut === 'PLANIFIEE' && (
          <button
            type="button"
            onClick={() => onStart(production)}
            className="inline-flex items-center gap-1 rounded-md bg-blue-600 px-3 py-2 text-xs font-semibold text-white hover:bg-blue-700"
          >
            <Play size={13} /> Démarrer
          </button>
        )}
        {production.statut === 'EN_COURS' && (
          <>
            <button
              type="button"
              onClick={() => onMeasure(production)}
              className="rounded-md border border-slate-300 px-3 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50"
            >
              Saisir mesure pH
            </button>
            <button
              type="button"
              onClick={() => onComplete(production)}
              className="inline-flex items-center gap-1 rounded-md bg-emerald-600 px-3 py-2 text-xs font-semibold text-white hover:bg-emerald-700"
            >
              <CheckCircle2 size={13} /> Finaliser
            </button>
          </>
        )}
      </div>
    </article>
  );
}

export default function ProductionCockpitPage() {
  const [productions, setProductions] = useState([]);
  const [products, setProducts] = useState([]);
  const [coves, setCuves] = useState([]);
  const [availableCuves, setAvailableCuves] = useState([]);
  const [lots, setLots] = useState([]);
  const [movements, setMovements] = useState([]);
  const [tab, setTab] = useState('movements');
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(emptyPlan);
  const [saving, setSaving] = useState(false);
  const [loadingAvailableCuves, setLoadingAvailableCuves] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const [p, pr, c, l, m] = await Promise.all([
        productionService.getAll(),
        produitService.getAll(),
        cuveService.getAll(),
        lotMPService.getAll(),
        movementStockService.getAll()
      ]);
      setProductions(p);
      setProducts(pr);
      setCuves(c);
      setLots(l);
      setMovements(m);
      setAvailableCuves(
        c.filter((item) => item.statutOperationnel === 'DISPONIBLE')
      );
    } catch (e) {
      setError(
        e.response?.data?.message ||
          'Impossible de charger le cockpit de production.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const update = (event) =>
    setForm((current) => ({
      ...current,
      [event.target.name]: event.target.value
    }));

  const openPlan = () => {
    setForm(emptyPlan);
    setModal({ type: 'plan' });
  };

  const submit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      if (modal.type === 'plan')
        await productionService.create({
          produitId: Number(form.produitId),
          dateDebutPrevue: `${form.dateDebutPrevue}:00`,
          dateFinPrevue: form.dateFinPrevue
            ? `${form.dateFinPrevue}:00`
            : null
        });
      if (modal.type === 'start')
        await productionService.start(modal.item.id, {
          cuveId: Number(form.cuveId),
          temperatureCuve: Number(form.temperatureCuve),
          phCuve: Number(form.phCuve)
        });
      if (modal.type === 'measure')
        await productionService.updateMeasurements(modal.item.id, {
          temperatureCuve: Number(form.temperatureCuve),
          phCuve: Number(form.phCuve)
        });
      if (modal.type === 'complete')
        await productionService.complete(
          modal.item.id,
          Number(form.nombreLotsProduits)
        );
      if (modal.type === 'movement')
        await movementStockService.create({
          lotMPId: Number(form.lotMPId),
          productionId: Number(form.productionId),
          quantite: Number(form.quantite),
          dateMouvement: new Date().toISOString().slice(0, 19)
        });
      setModal(null);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Opération impossible.');
    } finally {
      setSaving(false);
    }
  };

  const grouped = useMemo(
    () => ({
      PLANIFIEE: productions.filter((p) => p.statut === 'PLANIFIEE'),
      EN_COURS: productions.filter((p) => p.statut === 'EN_COURS'),
      TERMINEE: productions.filter((p) => p.statut === 'TERMINEE')
    }),
    [productions]
  );

  const openStart = async (item) => {
    setLoadingAvailableCuves(true);
    setError('');

    try {
      const available = await cuveService.getAvailable();
      setAvailableCuves(available);
      setForm(emptyStart);
      setModal({ type: 'start', item });
    } catch (e) {
      setError(
        e.response?.data?.message ||
          'Impossible de charger les cuves disponibles.'
      );
    } finally {
      setLoadingAvailableCuves(false);
    }
  };

  const openMeasure = (item) => {
    setForm({
      temperatureCuve: item.temperatureCuve ?? '',
      phCuve: item.phCuve ?? ''
    });
    setModal({ type: 'measure', item });
  };

  const openComplete = (item) => {
    setForm({ nombreLotsProduits: '' });
    setModal({ type: 'complete', item });
  };

  const modalBody =
    modal?.type === 'plan' ? (
      <form onSubmit={submit} className="space-y-4 p-6">
        <Select
          label="Produit"
          name="produitId"
          value={form.produitId}
          onChange={update}
          required
        >
          <option value="">Sélectionner un produit</option>
          {products.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nom}
            </option>
          ))}
        </Select>
        <Input
          label="Début prévu"
          type="datetime-local"
          name="dateDebutPrevue"
          value={form.dateDebutPrevue}
          onChange={update}
          required
        />
        <Input
          label="Fin prévue"
          type="datetime-local"
          name="dateFinPrevue"
          value={form.dateFinPrevue}
          onChange={update}
        />
        <Actions saving={saving} />
      </form>
    ) : modal?.type === 'start' ? (
      <form onSubmit={submit} className="space-y-4 p-6">
        <Select
          label="Cuve"
          name="cuveId"
          value={form.cuveId}
          onChange={update}
          required
        >
          <option value="">
            {loadingAvailableCuves
              ? 'Chargement des cuves...'
              : 'Sélectionner une cuve'}
          </option>
          {availableCuves.map((c) => (
            <option key={c.id} value={c.id}>
              {c.nom} · {c.capaciteMaxLitres} L
            </option>
          ))}
        </Select>
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Température °C"
            type="number"
            step="0.1"
            name="temperatureCuve"
            value={form.temperatureCuve}
            onChange={update}
            required
          />
          <Input
            label="pH"
            type="number"
            step="0.01"
            name="phCuve"
            value={form.phCuve}
            onChange={update}
            required
          />
        </div>
        <Actions saving={saving} label="Démarrer" />
      </form>
    ) : modal?.type === 'complete' ? (
      <form onSubmit={submit} className="space-y-4 p-6">
        <Input
          label="Nombre de lots produits"
          type="number"
          min="1"
          name="nombreLotsProduits"
          value={form.nombreLotsProduits}
          onChange={update}
          required
        />
        <Actions saving={saving} label="Finaliser" />
      </form>
    ) : modal?.type === 'movement' ? (
      <form onSubmit={submit} className="space-y-4 p-6">
        <Select
          label="Production destinataire"
          name="productionId"
          value={form.productionId}
          onChange={update}
          required
        >
          <option value="">Sélectionner un OF</option>
          {grouped.EN_COURS.map((p) => (
            <option key={p.id} value={p.id}>
              {p.numeroProduction} · {p.produitNom}
            </option>
          ))}
        </Select>
        <Select
          label="Lot de matière première"
          name="lotMPId"
          value={form.lotMPId}
          onChange={update}
          required
        >
          <option value="">Sélectionner un lot</option>
          {lots.map((l) => (
            <option key={l.id} value={l.id}>
              {l.numeroLot} · {l.matierePremiereNom} ({l.quantiteRestante}{' '}
              {l.uniteMesure})
            </option>
          ))}
        </Select>
        <Input
          label="Quantité"
          type="number"
          min="0.01"
          step="0.01"
          name="quantite"
          value={form.quantite}
          onChange={update}
          required
        />
        <Actions saving={saving} label="Enregistrer la sortie" />
      </form>
    ) : (
      <form onSubmit={submit} className="space-y-4 p-6">
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Température °C"
            type="number"
            step="0.1"
            name="temperatureCuve"
            value={form.temperatureCuve}
            onChange={update}
            required
          />
          <Input
            label="pH"
            type="number"
            step="0.01"
            name="phCuve"
            value={form.phCuve}
            onChange={update}
            required
          />
        </div>
        <Actions saving={saving} label="Enregistrer" />
      </form>
    );

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 border-b border-slate-200 pb-5 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-sm font-semibold uppercase tracking-widest text-blue-600">
            Atelier de transformation
          </p>
          <h1 className="mt-1 text-3xl font-bold tracking-tight text-slate-900">
            Cockpit de production
          </h1>
          <p className="mt-2 text-sm text-slate-500">
            Pilotez les ordres de fabrication, les cuves et les consommations en
            temps réel.
          </p>
        </div>
        <button
          type="button"
          onClick={openPlan}
          className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-bold text-white hover:bg-blue-700"
        >
          <Factory size={17} /> Planifier une production
        </button>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {loading ? (
        <p className="rounded-xl bg-white p-10 text-center text-sm text-slate-500">
          Chargement de l'atelier...
        </p>
      ) : (
        <>
          <div className="grid gap-5 xl:grid-cols-3">
            {[
              ['PLANIFIEE', 'Production planifiée', 'bg-slate-100'],
              ['EN_COURS', 'Production en cours', 'bg-blue-50'],
              ['TERMINEE', 'Production terminée', 'bg-emerald-50']
            ].map(([key, title, color]) => (
              <div key={key} className={`rounded-xl p-3 ${color}`}>
                <div className="mb-3 flex items-center justify-between">
                  <h2 className="text-sm font-bold text-slate-800">{title}</h2>
                  <span className="rounded-full bg-white px-2 py-1 text-xs font-bold text-slate-500">
                    {grouped[key].length}
                  </span>
                </div>
                <div className="space-y-3">
                  {grouped[key].map((p) => (
                    <ProductionCard
                      key={p.id}
                      production={p}
                      onStart={openStart}
                      onMeasure={openMeasure}
                      onComplete={openComplete}
                    />
                  ))}
                  {grouped[key].length === 0 && (
                    <p className="rounded-lg border border-dashed border-slate-300 p-5 text-center text-xs text-slate-500">
                      Aucun ordre dans cette colonne.
                    </p>
                  )}
                </div>
              </div>
            ))}
          </div>

          <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
            <div className="flex flex-wrap gap-2 border-b border-slate-200 px-4">
              <button
                type="button"
                onClick={() => setTab('movements')}
                className={`border-b-2 px-4 py-3 text-sm font-semibold ${
                  tab === 'movements'
                    ? 'border-blue-600 text-slate-900'
                    : 'border-transparent text-slate-500'
                }`}
              >
                <PackagePlus size={15} className="mr-2 inline" />
                Consommations & demandes MP
              </button>
              <button
                type="button"
                onClick={() => setTab('cuves')}
                className={`border-b-2 px-4 py-3 text-sm font-semibold ${
                  tab === 'cuves'
                    ? 'border-blue-600 text-slate-900'
                    : 'border-transparent text-slate-500'
                }`}
              >
                <Gauge size={15} className="mr-2 inline" />
                Supervision des cuves
              </button>
            </div>

            {tab === 'movements' ? (
              <div className="p-5">
                <div className="mb-4 flex items-center justify-between">
                  <h2 className="font-bold text-slate-900">
                    Sorties de matières premières
                  </h2>
                  <button
                    type="button"
                    onClick={() => {
                      setForm(emptyMovement);
                      setModal({ type: 'movement' });
                    }}
                    className="inline-flex items-center gap-1 rounded-md bg-blue-600 px-3 py-2 text-xs font-semibold text-white"
                  >
                    <PackagePlus size={14} /> Nouvelle demande / sortie
                  </button>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-sm">
                    <thead className="bg-slate-50 text-xs uppercase text-slate-500">
                      <tr>
                        <th className="px-4 py-3">OF</th>
                        <th className="px-4 py-3">Lot MP</th>
                        <th className="px-4 py-3">Quantité</th>
                        <th className="px-4 py-3">Date</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {movements.map((m) => (
                        <tr key={m.id}>
                          <td className="px-4 py-3 font-semibold">
                            {productions.find((p) => p.id === m.productionId)
                              ?.numeroProduction || `#${m.productionId}`}
                          </td>
                          <td className="px-4 py-3">{m.numeroLot}</td>
                          <td className="px-4 py-3">{m.quantite}</td>
                          <td className="px-4 py-3 text-slate-500">
                            {dateTime(m.dateMouvement)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                  {movements.length === 0 && (
                    <p className="p-6 text-center text-sm text-slate-500">
                      Aucune consommation enregistrée.
                    </p>
                  )}
                </div>
              </div>
            ) : (
              <div className="grid gap-4 p-5 sm:grid-cols-2 xl:grid-cols-4">
                {coves.map((cuve) => (
                  <div
                    key={cuve.id}
                    className="rounded-lg border border-slate-200 p-4"
                  >
                    <div className="flex items-center justify-between">
                      <h3 className="font-bold text-slate-900">{cuve.nom}</h3>
                      <span
                        className={`rounded-full px-2 py-1 text-[10px] font-semibold ${
                          cuve.statutOperationnel === 'DISPONIBLE'
                            ? 'bg-emerald-100 text-emerald-700'
                            : 'bg-amber-100 text-amber-700'
                        }`}
                      >
                        {cuve.statutOperationnel}
                      </span>
                    </div>
                    <div className="mt-4 flex items-center gap-2 text-sm text-slate-500">
                      <Beaker size={16} /> Capacité {cuve.capaciteMaxLitres} L
                    </div>
                    <div className="mt-3 flex items-center gap-2 text-sm text-slate-500">
                      <Activity size={16} />{' '}
                      {productions.find(
                        (p) => p.cuveId === cuve.id && p.statut === 'EN_COURS'
                      )?.produitNom || 'Aucune production active'}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </>
      )}

      {modal && (
        <Modal
          title={
            modal.type === 'plan'
              ? 'Planifier une production'
              : modal.type === 'movement'
              ? 'Nouvelle demande / sortie stock'
              : modal.type === 'start'
              ? 'Démarrer la production'
              : modal.type === 'measure'
              ? 'Saisir une mesure'
              : 'Finaliser la production'
          }
          onClose={() => setModal(null)}
        >
          {modalBody}
        </Modal>
      )}
    </section>
  );
}

function Actions({ saving, label = 'Enregistrer' }) {
  return (
    <div className="flex justify-end border-t border-slate-200 pt-4">
      <button
        type="submit"
        disabled={saving}
        className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-bold text-white disabled:opacity-50"
      >
        {saving ? 'Enregistrement...' : label}
      </button>
    </div>
  );
}