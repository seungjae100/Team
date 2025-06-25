document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const data = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        name: document.getElementById("name").value,
        position: document.getElementById("position").value,
    };

    try {
        await adminAPI.userRegister(data);
        location.href = "/";
    } catch (error) {
        alert("등록 실패: " + (error.response?.data?.message || "에러발생"));
    }
});