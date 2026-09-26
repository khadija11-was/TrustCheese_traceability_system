import axiosInstance from '@/api/axiosInstance';

const controleQualiteService = {
  getAll: async () => (await axiosInstance.get('/controles-qualite')).data,
  create: async (data) => (await axiosInstance.post('/controles-qualite', data)).data,
};

export default controleQualiteService;
