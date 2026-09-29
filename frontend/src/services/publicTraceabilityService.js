import axios from 'axios';

const publicApi = axios.create({
  baseURL: 'http://localhost:8080/api/public',
  headers: { Accept: 'application/json' },
});

const publicTraceabilityService = {
  getByLotNumber: async (numeroLot) => (
    await publicApi.get(`/traceability/lots/${encodeURIComponent(numeroLot)}`)
  ).data,
};

export default publicTraceabilityService;
