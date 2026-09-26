import axiosInstance from '@/api/axiosInstance';

const livraisonService = {
  getAll: async () => (await axiosInstance.get('/livraisons')).data,
  getById: async (id) => (await axiosInstance.get(`/livraisons/${id}`)).data,
  create: async (data) => (await axiosInstance.post('/livraisons', data)).data,
  prepare: async (id) => (await axiosInstance.post(`/livraisons/${id}/preparer`)).data,
  ship: async (id) => (await axiosInstance.post(`/livraisons/${id}/expedier`)).data,
  confirm: async (id) => (await axiosInstance.post(`/livraisons/${id}/confirmer`)).data,
  cancel: async (id) => (await axiosInstance.post(`/livraisons/${id}/annuler`)).data,
};

export default livraisonService;
