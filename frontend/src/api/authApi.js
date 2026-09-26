import axiosInstance from '@/api/axiosInstance';

const normalizeUser = (user) => {
  if (!user) return null;

  const role = typeof user.role === 'string'
    ? user.role.replace(/^ROLE_/, '')
    : user.role;

  return { ...user, role };
};

const authApi = {
  login: async (credentials) => {
    const response = await axiosInstance.post('/auth/login', {
      email: credentials.email || credentials.username,
      motDePasse: credentials.motDePasse || credentials.password,
    });

    return normalizeUser(response.data);
  },

  refresh: async () => {
    const response = await axiosInstance.post('/auth/refresh');
    return normalizeUser(response.data);
  },

  logout: () => axiosInstance.post('/auth/logout'),
};

export default authApi;