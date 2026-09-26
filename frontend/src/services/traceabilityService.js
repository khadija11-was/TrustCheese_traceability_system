import axiosInstance from '@/api/axiosInstance';

const traceabilityService = {
  searchByLotNumber: async (numeroLot) => (await axiosInstance.get('/traceability/lots/search', { params: { numeroLot } })).data,
  getRecentEvents: async () => (await axiosInstance.get('/traceability/events/recent')).data,
};

export default traceabilityService;
