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
        // Handle network errors or other exceptions
        window.location = '/api'; // Redirect to error page or default page
    }
}
</script>


<style>
    /* Global styles */
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
        max-width: 400px; /* Limit width for better readability */
        width: 100%; /* Ensure full width responsiveness */
    }

    .logo {
        width: 200px; /* Adjust size as needed */
        margin-bottom: 20px; /* Space between logo and button */
    }

    .google-login {
        padding: 12px 20px;
        background-color: #4285F4; /* Google blue color */
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        cursor: pointer;
        transition: background-color 0.3s ease-in-out;
    }

    .google-login:hover {
        background-color: #3367D6; /* Darker Google blue color on hover */
    }
</style>

<div class="login-container">
    <img src="cab_logo.png" alt="Cab Logo" class="logo">
    <h2>Welcome</h2>
    <button class="google-login" on:click={checkUserAndRedirect}>Login with Google</button>
</div>
