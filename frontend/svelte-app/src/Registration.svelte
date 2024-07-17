<!-- src/Registration.svelte -->
<script>
    let phNo = '';
    let errorMessage = '';
    let userType = ''; 

    async function checkUserAndRedirect() {
        try {
            const response = await fetch('api/user/me');

            if (response.status === 200) {
                console.log('User exists, redirecting to entrypage');
                window.location = '/entrypage';
            } else {
                console.log('User does not exist or error fetching data');

            }
        } catch (error) {
            console.error('Error checking user:', error);

        }
    }

    checkUserAndRedirect();

    async function handleRegistration() {

        if (!isValidPhoneNumber(phNo)) {
            errorMessage = 'Please enter a valid 10-digit phone number.';
            blinkError();
            return;
        }

        try {

            const registrationResponse = await fetch('api/user/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    phNo: phNo,
                    userType: userType 
                })
            });

            if (registrationResponse.status === 201 || registrationResponse.status === 409) {

                window.location = '/entrypage'; 

            } else {
                const errorData = await registrationResponse.json();
                errorMessage = errorData.message || 'Registration failed.';
                console.error('Registration failed:', errorMessage);

            }

        } catch (error) {
            console.error('Error during registration:', error);
            errorMessage = 'Error during registration.';
        }
    }

    function isValidPhoneNumber(number) {

        const cleaned = number.replace(/\D/g, '');

        return cleaned.length === 10;
    }

    function blinkError() {
        const errorElement = document.querySelector('.error-message');
        errorElement.classList.add('blink');

        setTimeout(() => {
            errorElement.classList.remove('blink');
        }, 1000); 
    }

    function resetError() {
        if (isValidPhoneNumber(phNo)) {
            errorMessage = '';
        }
    }
</script>

<style>

    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        margin: 0;
        background-color: #f3f6f8;
    }

    .registration-container {
        text-align: center;
        background-color: #ffffff;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        max-width: 400px; 
        width: 90%; 
    }

    .registration-container h2 {
        margin-bottom: 20px;
        color: #333;
    }

    .error-message {
        color: #dc3545;
        font-size: 0.9rem;
        margin-top: 10px;
        opacity: 1; 
        transition: opacity 0.3s ease-in-out;
    }

    .blink {
        animation: blink-animation 1s steps(5, start) infinite;
    }

    @keyframes blink-animation {
        to {
            visibility: hidden;
        }
    }

    form {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    label {
        font-weight: bold;
        margin-bottom: 8px;
    }

    input[type="tel"],
    select {
        width: 100%;
        padding: 8px;
        margin: 6px 0;
        font-size: 1rem;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }

    input[type="tel"]:focus,
    select:focus {
        outline: none;
        border-color: #007bff;
        box-shadow: 0 0 0 0.1rem rgba(0, 123, 255, 0.25);
    }

    small {
        color: #6c757d;
    }

    button[type="submit"] {
        padding: 12px 20px;
        background-color: #007bff;
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        cursor: pointer;
        transition: background-color 0.3s ease-in-out;
    }

    button[type="submit"]:hover {
        background-color: #0056b3;
    }
</style>

<div class="registration-container">
    <h2>Register for CabLink</h2>
    <p class="error-message" style="{errorMessage ? 'opacity: 1;' : 'opacity: 0;'}">{errorMessage}</p>
    <form on:submit|preventDefault={handleRegistration} on:input={resetError}>
        <label for="phNo">Phone Number:</label>
        <input type="tel" id="phNo" bind:value={phNo} required>
        <small>Enter a 10-digit phone number.</small>

        <!-- Dropdown for selecting user type -->
        <label for="userType">User Type:</label>
        <select id="userType" bind:value={userType} required>
            <option value="">Select User Type</option>
            <option value="STUDENT">Student</option>
            <option value="STAFF">Professor</option>
            <option value="DRIVER">Driver</option>
        </select>

        <button type="submit">Register</button>
    </form>
</div>