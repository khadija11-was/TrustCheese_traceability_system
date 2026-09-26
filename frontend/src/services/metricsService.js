import axiosInstance from '@/api/axiosInstance';

const metricsService = {
  get: async () => (await axiosInstance.get('/metrics')).data,
};

export default metricsService;
