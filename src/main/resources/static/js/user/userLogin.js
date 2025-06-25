document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("loginForm");

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        try {
            await userAPI.userLogin(email, password);
            location.href = "/userDashboard";
        } catch (e) {
            alert("로그인 실패");
        }
    });
}) ;