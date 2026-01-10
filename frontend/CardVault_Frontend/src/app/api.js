import axios from "axios";
const base = import.meta.env.VITE_API_BASE || "http://localhost:8080/api";
console.log("[API] baseURL =", base);
export const api = axios.create({ baseURL: base, timeout: 10000 });
