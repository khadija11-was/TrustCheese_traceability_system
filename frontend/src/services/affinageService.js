import axiosInstance from '@/api/axiosInstance';

const affinageService = {
  getAll: async (statut) => {
    const response = await axiosInstance.get('/affinages', {
      params: statut ? { statut } : undefined,
    });
    return response.data;
  },
  create: async (data) => (await axiosInstance.post('/affinages', data)).data,
  finish: async (id) => (await axiosInstance.patch(`/affinages/${id}/terminer`)).data,
  cancel: async (id) => (await axiosInstance.patch(`/affinages/${id}/annuler`)).data,
};

export default affinageService;
