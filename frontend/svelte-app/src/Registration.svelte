<!-- src/Registration.svelte -->
<script>
    let phNo = '';
    let errorMessage = '';

    async function checkUserAndRedirect() {
    try {
        const response = await fetch('http://localhost:8000/api/user/me');
        
        if (response.status === 200) {
            console.log('User exists, redirecting to entrypage');
            window.location = '/entrypage';
        } else {
            console.log('User does not exist or error fetching data');
            // Handle the case where user does not exist or other error scenarios
        }
    } catch (error) {
        console.error('Error checking user:', error);
        // Handle network errors or other exceptions
    }
}

// Call the function to initiate the check and redirect
checkUserAndRedirect();

    async function handleRegistration() {
    // Validate phone number format
    if (!isValidPhoneNumber(phNo)) {
        errorMessage = 'Please enter a valid 10-digit phone number.';
        blinkError();
        return;
    }

    try {

        // Attempt to register the phone number
        const registrationResponse = await fetch('http://localhost:8000/api/user/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({phNo: phNo})
        });

        if (registrationResponse.status === 201 || registrationResponse.status === 409) {
            // If created or conflict (phone number already exists), redirect to entrypage
            window.location = '/entrypage'; // Replace with your actual route
            // Alternatively, if using a router like svelte-routing:
            // import { goto } from '$app/navigation';
            // goto('/entrypage');
        } else {
            const errorData = await registrationResponse.json();
            errorMessage = errorData.message || 'Registration failed.';
            console.error('Registration failed:', errorMessage);
            // Handle other failure cases (show error message, reset form, etc.)
        }

    } catch (error) {
        console.error('Error during registration:', error);
        errorMessage = 'Error during registration.';
    }
}



    // Function to validate phone number format
    function isValidPhoneNumber(number) {
        // Remove all non-digit characters
        const cleaned = number.replace(/\D/g, '');
        // Check if it's exactly 10 digits
        return cleaned.length === 10;
    }

    // Function to make error message blink
    function blinkError() {
        const errorElement = document.querySelector('.error-message');
        errorElement.classList.add('blink');

        setTimeout(() => {
            errorElement.classList.remove('blink');
        }, 1000); // Adjust blinking duration (milliseconds)
    }

    // Function to reset error message when input is valid
    function resetError() {
        if (isValidPhoneNumber(phNo)) {
            errorMessage = '';
        }
    }
</script>

<style>
    /* Your CSS styles for registration page */
    .registration-container {
        max-width: 400px;
        margin: 0 auto;
        padding: 20px;
        background-color: #f0f0f0;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }

    .registration-container h2 {
        text-align: center;
        margin-bottom: 20px;
        color: #333;
    }

    .error-message {
        color: red;
        margin-top: 8px;
        font-size: 0.9rem;
        opacity: 0;
        transition: opacity 0.3s ease-in-out;
    }

    .blink {
        animation: blinkAnimation 0.6s infinite alternate;
    }

    @keyframes blinkAnimation {
        from {
            opacity: 0;
        }
        to {
            opacity: 1;
        }
    }

    form {
        display: flex;
        flex-direction: column;
    }

    label {
        margin-bottom: 6px;
        color: #666;
    }

    input {
        padding: 10px;
        margin-bottom: 12px;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 1rem;
    }

    small {
        color: #999;
        font-size: 0.8rem;
        margin-bottom: 10px;
    }

    button {
        padding: 12px;
        background-color: #007bff;
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        cursor: pointer;
    }

    button:hover {
        background-color: #0056b3;
    }
</style>

<div class="registration-container">
    <h2>Register for CabLink</h2>
    <p class="error-message" style="{errorMessage ? 'opacity: 1;' : 'opacity: 0;'}">{errorMessage}</p>
    <form on:submit|preventDefault={handleRegistration} on:input={resetError}>
        <label for="phNo">Phone Number:</label>
        <input type="tel" id="phoneNumber" bind:value={phNo} required>
        <small>Enter a 10-digit phone number.</small>

        <button type="submit">Register</button>
    </form>
</div>
