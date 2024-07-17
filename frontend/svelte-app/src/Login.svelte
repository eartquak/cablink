<script>
    async function checkUserAndRedirect() {
    try {
        const response = await fetch('api/user/me');

        if (response.status === 200) {
            console.log('User exists, redirecting to entry page');
            window.location = '/registration';
        } else {
            const data = await response.json();
            if (data.data === "Could not find user") {
                console.log(data.data);
                window.location = '/registration';
            } else {
                console.log('Unexpected response:', data);
                window.location = '/api';
            }
        }
    } catch (error) {
        console.error('Error checking user:', error);

        window.location = '/api'; 
    }
}
</script>

<style>

    body {
        font-family: Arial, sans-serif;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        margin: 0;
        background-color: #f3f6f8;
    }

    .login-container {
        text-align: center;
        background-color: #ffffff;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        max-width: 400px; 
        width: 100%; 
    }

    .logo {
        width: 200px; 
        margin-bottom: 20px; 
    }

    .google-login {
        padding: 12px 20px;
        background-color: #4285F4; 
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        cursor: pointer;
        transition: background-color 0.3s ease-in-out;
    }

    .google-login:hover {
        background-color: #3367D6; 
    }
</style>

<div class="login-container">
    <img src="cab_logo.png" alt="Cab Logo" class="logo">
    <h2>Welcome</h2>
    <button class="google-login" on:click={checkUserAndRedirect}>Login with Google</button>
</div>