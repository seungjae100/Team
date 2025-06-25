const apiClient = axios.create({
    withCredentials: true
});

window.apiClient = apiClient;