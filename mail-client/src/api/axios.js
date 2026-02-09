import axios from "axios";

const API_BASE_URL = "http://localhost:8080"

const api = axios.create({
    baseURL: API_BASE_URL
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if(token){
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

//Auto Logout on JWT Expiry
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if(error.response?.status === 401){
            localStorage.removeItem("token");
            window.location.replace("/login");
        }
        return Promise.reject(error);
    }
);

export default api;