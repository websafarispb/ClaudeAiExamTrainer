import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

export const getRandomQuestion = async (section, sourceType) => {
  const params = {};
  if (section) params.section = section;
  if (sourceType) params.sourceType = sourceType;

  const response = await api.get("/questions/random", { params });
  return response.data;
};

export const submitAnswer = async (questionId, selectedOptionId) => {
  const response = await api.post(`/questions/${questionId}/submit-answer`, {
    selectedOptionId,
  });
  return response.data;
};

export const getSections = async () => {
  const response = await api.get("/questions/sections");
  return response.data;
};

export const getTestQuestions = async (count, section, sourceType) => {
  const params = { count };
  if (section) params.section = section;
  if (sourceType) params.sourceType = sourceType;

  const response = await api.get("/questions/test", { params });
  return response.data;
};