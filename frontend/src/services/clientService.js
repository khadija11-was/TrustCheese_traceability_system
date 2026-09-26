import axiosInstance from '@/api/axiosInstance';

const clientService = {
  getAll: async () => (await axiosInstance.get('/clients')).data,
};

export default clientService;
