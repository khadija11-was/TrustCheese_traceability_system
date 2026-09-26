import axiosInstance from '@/api/axiosInstance';

const toFormData = (data) => {
  const formData = new FormData();
  formData.append('nom', data.nom.trim());
  formData.append('description', data.description?.trim() || '');
  formData.append('quantiteStandardLot', data.quantiteStandardLot);
  formData.append('dureeConservationJours', data.dureeConservationJours);
  if (data.image) formData.append('image', data.image);
  return formData;
};

const produitService = {
  getAll: async () => (await axiosInstance.get('/produits')).data,
  create: async (data) => (await axiosInstance.post('/produits', toFormData(data), { headers: { 'Content-Type': 'multipart/form-data' } })).data,
  update: async (id, data) => (await axiosInstance.put(`/produits/${id}`, toFormData(data), { headers: { 'Content-Type': 'multipart/form-data' } })).data,
  remove: async (id) => axiosInstance.delete(`/produits/${id}`),
};

export default produitService;
