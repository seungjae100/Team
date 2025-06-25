document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("loginForm");

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        try {
            await adminAPI.adminLogin(username, password);
            location.href = "/adminDashboard";
        } catch (e) {
            alert("로그인 실패");
        }
    });
});