function userLogin(email, password) {
    return apiClient.post("/api/user/login", {email, password});
};

window.userAPI = {
    userLogin,

};