document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const data = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value,
        name: document.getElementById("name").value
    };

    try {
        await adminAPI.adminRegister(data);
        location.href = "/admin";
    } catch (error) {
        alert("등록 실패" + (error.response?.data?.message || "사용중인 아이디입니다."));
    }
});