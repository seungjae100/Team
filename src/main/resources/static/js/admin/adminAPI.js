function adminLogin(username, password) {
    return apiClient.post("/api/admin/login", {username, password});
}

function adminRegister(data) {
    return apiClient.post("/api/admin/register", data);
}

function userRegister(data) {
    return apiClient.post("/api/admin/userRegister", data);
};

window.adminAPI = {
    adminLogin,
    adminRegister,
    userRegister
};