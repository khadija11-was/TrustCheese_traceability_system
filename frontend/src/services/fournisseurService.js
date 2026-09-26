import axiosInstance from '@/api/axiosInstance';

const fournisseurService = {
  getAll: async () => (await axiosInstance.get('/fournisseurs')).data,
  search: async (keyword) => (await axiosInstance.get('/fournisseurs/search', { params: { keyword } })).data,
  create: async (data) => (await axiosInstance.post('/fournisseurs', data)).data,
  update: async (id, data) => (await axiosInstance.put(`/fournisseurs/${id}`, data)).data,
  remove: async (id) => axiosInstance.delete(`/fournisseurs/${id}`),
};

export default fournisseurService;
