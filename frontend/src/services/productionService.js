import axiosInstance from '@/api/axiosInstance';

const productionService = {
  getAll: async () => (await axiosInstance.get('/productions')).data,
  create: async (data) => (await axiosInstance.post('/productions', data)).data,
  start: async (id, data) => (await axiosInstance.patch(`/productions/${id}/start`, null, { params: data })).data,
  updateMeasurements: async (id, data) => (await axiosInstance.patch(`/productions/${id}/measurements`, data)).data,
  complete: async (id, nombreLotsProduits) => (await axiosInstance.patch(`/productions/${id}/complete`, { nombreLotsProduits })).data,
};

export default productionService;
