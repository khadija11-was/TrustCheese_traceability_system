import axiosInstance from '@/api/axiosInstance';

const movementStockService = {
  getAll: async () => (await axiosInstance.get('/mouvements-stock')).data,
  getByProduction: async (productionId) => (await axiosInstance.get(`/mouvements-stock/production/${productionId}`)).data,
  create: async (data) => (await axiosInstance.post('/mouvements-stock', data)).data,
};

export default movementStockService;
