<!-- src/Registration.svelte -->
<script>
    let phNo = '';
    let errorMessage = '';
    let userType = ''; // Initialize userType variable

    async function checkUserAndRedirect() {
        try {
            const response = await fetch('api/user/me');
            
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
            // Attempt to register the phone number and userType
            const registrationResponse = await fetch('api/user/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    phNo: phNo,
                    userType: userType // Include userType in the request body
                })
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
    /* Your existing CSS styles */
    /* Ensure styles for dropdown and any related UI are added here */
</style>

<div class="registration-container">
    <h2>Register for CabLink</h2>
    <p class="error-message" style="{errorMessage ? 'opacity: 1;' : 'opacity: 0;'}">{errorMessage}</p>
    <form on:submit|preventDefault={handleRegistration} on:input={resetError}>
        <label for="phNo">Phone Number:</label>
        <input type="tel" id="phoneNumber" bind:value={phNo} required>
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
