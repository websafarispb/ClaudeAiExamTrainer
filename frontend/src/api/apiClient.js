import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  withCredentials: true,
  timeout: 120000,
});

export default apiClient;