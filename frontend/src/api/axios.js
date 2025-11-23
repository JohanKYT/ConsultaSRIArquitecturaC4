import axios from 'axios';

const api = axios.create({
    // IMPORTANTE: Forzamos localhost, porque es lo que entiende tu navegador Chrome/Edge
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

export default api;
