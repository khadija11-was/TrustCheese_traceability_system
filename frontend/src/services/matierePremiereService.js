import axiosInstance from '@/api/axiosInstance';

const matierePremiereService = {
  getAll: async () => (await axiosInstance.get('/matieres-premieres')).data,
  search: async (keyword) => (await axiosInstance.get('/matieres-premieres/search', { params: { keyword } })).data,
  create: async (data) => (await axiosInstance.post('/matieres-premieres', data)).data,
  update: async (id, data) => (await axiosInstance.put(`/matieres-premieres/${id}`, data)).data,
  remove: async (id) => axiosInstance.delete(`/matieres-premieres/${id}`),
};

export default matierePremiereService;
