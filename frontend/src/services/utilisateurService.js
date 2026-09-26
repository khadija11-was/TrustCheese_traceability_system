import axiosInstance from '@/api/axiosInstance';

export const utilisateurService = {
  // Récupérer tous les utilisateurs
  getAll: async () => {
    const response = await axiosInstance.get('/utilisateurs');
    return response.data;
  },

  // Récupérer un utilisateur par ID
  getById: async (id) => {
    const response = await axiosInstance.get(`/utilisateurs/${id}`);
    return response.data;
  },

  // Créer un utilisateur
  create: async (data) => {
    const response = await axiosInstance.post('/utilisateurs', data);
    return response.data;
  },

  // Modifier un utilisateur
  update: async (id, data) => {
    const response = await axiosInstance.put(`/utilisateurs/${id}`, data);
    return response.data;
  },

  // Activer un utilisateur
  activer: async (id) => {
    const response = await axiosInstance.patch(`/utilisateurs/${id}/activer`);
    return response.data;
  },

  // Désactiver un utilisateur
  desactiver: async (id) => {
    const response = await axiosInstance.patch(`/utilisateurs/${id}/desactiver`);
    return response.data;
  },

  // Rechercher par nom
  rechercherParNom: async (nom) => {
    const response = await axiosInstance.get('/utilisateurs/recherche', {
      params: { nom }
    });
    return response.data;
  },

  // Filtrer par rôle
  getByRole: async (role) => {
    const response = await axiosInstance.get(`/utilisateurs/role/${role}`);
    return response.data;
  }
};