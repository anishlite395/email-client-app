import api from "./axios"; 


export const registerUser = (data) => api.post(`/auth/addNewUser`,data);

export const loginUser = (data) => api.post(`/auth/login`,data);