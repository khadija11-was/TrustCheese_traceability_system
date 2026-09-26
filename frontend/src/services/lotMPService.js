import axiosInstance from '@/api/axiosInstance';

const lotMPService = {
  getAll: async () => (await axiosInstance.get('/lots-mp')).data,
  create: async (data) => (await axiosInstance.post('/lots-mp', data)).data,
};

export default lotMPService;
