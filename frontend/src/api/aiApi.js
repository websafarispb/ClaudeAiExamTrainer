import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

export const generateQuestion = async (payload) => {
  const response = await api.post("/ai/generate-question", payload);
  return response.data;
};

export const generateAndSaveQuestion = async (payload) => {
  const response = await api.post("/ai/generate-and-save-question", payload);
  return response.data;
};