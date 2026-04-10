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

export const generatePracticalTask = async (payload) => {
  const response = await api.post("/ai/generate-practical-task", payload);
  return response.data;
};

export const saveGeneratedQuestion = async (payload) => {
  const response = await api.post("/ai/save-generated-question", payload);
  return response.data;
};

export const translateQuestion = async (payload) => {
  const response = await api.post("/ai/translate-question", payload);
  return response.data;
};

export const translatePracticalTask = async (payload) => {
  const response = await api.post("/ai/translate-practical-task", payload);
  return response.data;
};

export const saveGeneratedPracticalTask = async (payload) => {
  const response = await api.post("/ai/save-generated-practical-task", payload);
  return response.data;
};

export const getPracticalTasks = async () => {
  const response = await api.get("/ai/practical-tasks");
  return response.data;
};