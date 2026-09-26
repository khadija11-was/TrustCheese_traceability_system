import React, { useState, useEffect } from 'react';
import { utilisateurService } from '@/services/utilisateurService';
import UtilisateurModal from '../components/UtilisateurModal';

export default function UserManagementPage() {
  const [utilisateurs, setUtilisateurs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRole, setSelectedRole] = useState('');
  
  // Gestion du Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  // Charger la liste initiale
  const fetchUtilisateurs = async () => {
    setLoading(true);
    try {
      const data = await utilisateurService.getAll();
      setUtilisateurs(data);
    } catch (err) {
      console.error('Erreur lors du chargement des utilisateurs:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUtilisateurs();
  }, []);

  // Recherche dynamique par nom
  const handleSearch = async (e) => {
    const value = e.target.value;
    setSearchTerm(value);
    setSelectedRole('');

    if (value.trim() === '') {
      fetchUtilisateurs();
      return;
    }

    try {
      const data = await utilisateurService.rechercherParNom(value);
      setUtilisateurs(data);
    } catch (err) {
      console.error('Erreur lors de la recherche:', err);
    }
  };

  // Filtrer par rôle
  const handleRoleFilter = async (e) => {
    const role = e.target.value;
    setSelectedRole(role);
    setSearchTerm('');

    if (!role) {
      fetchUtilisateurs();
      return;
    }

    try {
      const data = await utilisateurService.getByRole(role);
      setUtilisateurs(data);
    } catch (err) {
      console.error('Erreur lors du filtre par rôle:', err);
    }
  };

  // Basculer le statut Actif / Désactivé
  const handleToggleStatus = async (user) => {
    try {
      if (user.actif) {
        await utilisateurService.desactiver(user.id);
      } else {
        await utilisateurService.activer(user.id);
      }
      fetchUtilisateurs();
    } catch (err) {
      console.error('Erreur lors du changement de statut:', err);
    }
  };

  // Enregistrer (Création ou Modif)
  const handleSubmitModal = async (formData) => {
    setSubmitLoading(true);
    try {
      if (selectedUser) {
        await utilisateurService.update(selectedUser.id, formData);
      } else {
        await utilisateurService.create(formData);
      }
      setIsModalOpen(false);
      setSelectedUser(null);
      fetchUtilisateurs();
    } catch (err) {
      console.error("Erreur lors de l'enregistrement:", err);
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* En-tête + Bouton d'ajout */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-5">
        <div>
          <h1 className="text-2xl font-bold text-white">Gestion des Utilisateurs</h1>
          <p className="text-sm text-slate-400 mt-1">
            Gérez les comptes d'accès, rôles et statuts d'activation.
          </p>
        </div>
        <button
          onClick={() => {
            setSelectedUser(null);
            setIsModalOpen(true);
          }}
          className="px-4 py-2 bg-[#FFD700] hover:bg-[#e6c200] text-black font-semibold text-sm rounded-lg transition-colors flex items-center justify-center gap-2"
        >
          <span>+</span> Ajouter un utilisateur
        </button>
      </div>

      {/* Barre de recherche et Filtres */}
      <div className="flex flex-col md:flex-row gap-4">
        <input
          type="text"
          placeholder="Rechercher par nom..."
          value={searchTerm}
          onChange={handleSearch}
          className="flex-1 bg-[#101525] border border-slate-800 rounded-lg px-4 py-2 text-white text-sm focus:outline-none focus:border-[#FFD700]"
        />

        <select
          value={selectedRole}
          onChange={handleRoleFilter}
          className="bg-[#101525] border border-slate-800 rounded-lg px-4 py-2 text-slate-300 text-sm focus:outline-none focus:border-[#FFD700]"
        >
          <option value="">Tous les rôles</option>
          <option value="ADMINISTRATEUR">ADMINISTRATEUR</option>
          <option value="MAGASINIER">MAGASINIER</option>
          <option value="RESPONSABLE_PRODUCTION">RESPONSABLE_PRODUCTION</option>
          <option value="RESPONSABLE_QUALITE">RESPONSABLE_QUALITE</option>
          <option value="RESPONSABLE_LOGISTIQUE">RESPONSABLE_LOGISTIQUE</option>
        </select>
      </div>

      {/* Tableau des utilisateurs */}
      <div className="bg-[#101525] border border-slate-800 rounded-xl overflow-hidden shadow-xl">
        <table className="w-full text-left text-sm">
          <thead className="bg-[#182035] text-slate-400 text-xs uppercase border-b border-slate-800">
            <tr>
              <th className="px-6 py-4">Nom</th>
              <th className="px-6 py-4">Email</th>
              <th className="px-6 py-4">Rôle</th>
              <th className="px-6 py-4">Statut</th>
              <th className="px-6 py-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60 text-slate-300">
            {loading ? (
              <tr>
                <td colSpan="5" className="px-6 py-8 text-center text-slate-500">
                  Chargement des données...
                </td>
              </tr>
            ) : utilisateurs.length === 0 ? (
              <tr>
                <td colSpan="5" className="px-6 py-8 text-center text-slate-500">
                  Aucun utilisateur trouvé.
                </td>
              </tr>
            ) : (
              utilisateurs.map((u) => (
                <tr key={u.id} className="hover:bg-slate-800/30 transition-colors">
                  <td className="px-6 py-4 font-medium text-white">{u.nom}</td>
                  <td className="px-6 py-4 text-slate-400">{u.email}</td>
                  <td className="px-6 py-4">
                    <span className="px-2.5 py-1 bg-slate-800 text-slate-300 rounded-md text-xs font-mono border border-slate-700">
                      {u.role}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        u.actif
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                          : 'bg-red-500/10 text-red-400 border border-red-500/20'
                      }`}
                    >
                      {u.actif ? 'Actif' : 'Inactif'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right space-x-2">
                    <button
                      onClick={() => {
                        setSelectedUser(u);
                        setIsModalOpen(true);
                      }}
                      className="px-2.5 py-1 text-xs text-slate-300 hover:text-white bg-slate-800 hover:bg-slate-700 rounded transition-colors"
                    >
                      Éditer
                    </button>
                    <button
                      onClick={() => handleToggleStatus(u)}
                      className={`px-2.5 py-1 text-xs rounded transition-colors ${
                        u.actif
                          ? 'bg-red-500/10 text-red-400 hover:bg-red-500/20'
                          : 'bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20'
                      }`}
                    >
                      {u.actif ? 'Désactiver' : 'Activer'}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Modal unique pour Création / Édition */}
      <UtilisateurModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedUser(null);
        }}
        onSubmit={handleSubmitModal}
        utilisateur={selectedUser}
        loading={submitLoading}
      />
    </div>
  );
}