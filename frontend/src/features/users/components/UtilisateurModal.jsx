import React, { useState, useEffect } from 'react';

// Si tu n'as pas de liste d'enum dynamique, on définit les valeurs attendues par le backend
const ROLES = [
  { value: 'ADMINISTRATEUR', label: 'Administrateur' },
  { value: 'MAGASINIER', label: 'Magasinier' },
  { value: 'RESPONSABLE_PRODUCTION', label: 'Responsable Production' },
  { value: 'RESPONSABLE_QUALITE', label: 'Responsable Qualité' },
  { value: 'RESPONSABLE_LOGISTIQUE', label: 'Responsable Logistique' }
];

export default function UtilisateurModal({ isOpen, onClose, onSubmit, utilisateur, loading }) {
  const [formData, setFormData] = useState({
    nom: '',
    email: '',
    motDePasse: '',
    role: 'RESPONSABLE_PRODUCTION'
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (utilisateur) {
      setFormData({
        nom: utilisateur.nom || '',
        email: utilisateur.email || '',
        motDePasse: '', // Laissé vide à la modification sauf changement volontaire
        role: utilisateur.role || 'RESPONSABLE_PRODUCTION'
      });
    } else {
      setFormData({
        nom: '',
        email: '',
        motDePasse: '',
        role: 'RESPONSABLE_PRODUCTION'
      });
    }
    setErrors({});
  }, [utilisateur, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: null }));
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.nom.trim()) newErrors.nom = 'Le nom est obligatoire';
    if (!formData.email.trim()) newErrors.email = "L'email est obligatoire";
    if (!utilisateur && (!formData.motDePasse || formData.motDePasse.length < 8)) {
      newErrors.motDePasse = 'Le mot de passe doit contenir au moins 8 caractères';
    }
    if (!formData.role) newErrors.role = 'Le rôle est obligatoire';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    // Si on est en modification et que le mdp n'est pas renseigné, on ne l'envoie pas
    const payload = { ...formData };
    if (utilisateur && !payload.motDePasse) {
      delete payload.motDePasse;
    }

    onSubmit(payload);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="bg-[#101525] border border-slate-800 w-full max-w-md rounded-2xl p-6 shadow-2xl relative">
        <h2 className="text-xl font-bold text-white mb-4">
          {utilisateur ? "Modifier l'utilisateur" : 'Ajouter un utilisateur'}
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-400 uppercase mb-1">
              Nom complet
            </label>
            <input
              type="text"
              name="nom"
              value={formData.nom}
              onChange={handleChange}
              className="w-full bg-[#182035] border border-slate-700 rounded-lg px-3 py-2 text-white text-sm focus:outline-none focus:border-[#FFD700]"
              placeholder="ex: Ahmed Alami"
            />
            {errors.nom && <span className="text-xs text-red-400 mt-1">{errors.nom}</span>}
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 uppercase mb-1">
              Email
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="w-full bg-[#182035] border border-slate-700 rounded-lg px-3 py-2 text-white text-sm focus:outline-none focus:border-[#FFD700]"
              placeholder="ahmed@trustcheese.ma"
            />
            {errors.email && <span className="text-xs text-red-400 mt-1">{errors.email}</span>}
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 uppercase mb-1">
              Mot de passe {utilisateur && '(laisser vide pour ne pas modifier)'}
            </label>
            <input
              type="password"
              name="motDePasse"
              value={formData.motDePasse}
              onChange={handleChange}
              className="w-full bg-[#182035] border border-slate-700 rounded-lg px-3 py-2 text-white text-sm focus:outline-none focus:border-[#FFD700]"
              placeholder="••••••••"
            />
            {errors.motDePasse && <span className="text-xs text-red-400 mt-1">{errors.motDePasse}</span>}
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 uppercase mb-1">
              Rôle
            </label>
            <select
              name="role"
              value={formData.role}
              onChange={handleChange}
              className="w-full bg-[#182035] border border-slate-700 rounded-lg px-3 py-2 text-white text-sm focus:outline-none focus:border-[#FFD700]"
            >
              {ROLES.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label} ({r.value})
                </option>
              ))}
            </select>
            {errors.role && <span className="text-xs text-red-400 mt-1">{errors.role}</span>}
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-800">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm text-slate-400 hover:text-white transition-colors"
            >
              Annuler
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-2 bg-[#FFD700] hover:bg-[#e6c200] text-black font-semibold text-sm rounded-lg transition-colors disabled:opacity-50"
            >
              {loading ? 'Enregistrement...' : utilisateur ? 'Mettre à jour' : 'Créer'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}