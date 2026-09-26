import axiosInstance from '@/api/axiosInstance';

const cuveService = {
  getAll: async () => (await axiosInstance.get('/cuves')).data,
  getAvailable: async () => (await axiosInstance.get('/cuves/disponibles')).data,
};

export default cuveService;
