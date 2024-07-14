<!-- EditProfile.svelte -->
<script>
    import { onMount } from 'svelte';
    import { writable } from 'svelte/store';

    let user = writable({
        name: 'John Doe',
        phoneNumber: '', // Initial empty phone number
    });

    async function saveChanges() {
        try {
            // Assuming a backend endpoint to update the user profile
            const response = await fetch('http://localhost:8080/user/profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify($user)
            });

            if (response.ok) {
                console.log('Profile updated successfully.');
                // Optionally show a success message or redirect
            } else {
                const errorData = await response.json();
                console.error('Failed to update profile:', errorData.message);
                // Handle error (show error message to user)
            }
        } catch (error) {
            console.error('Error updating profile:', error);
            // Handle network errors
        }
    }

    function handleChange(event) {
        const { name, value } = event.target;
        user.update(u => ({ ...u, [name]: value }));
    }

    onMount(async () => {
        try {
            // Fetching initial user profile data
            const response = await fetch('http://localhost:8080/user/profile');
            if (response.ok) {
                const userData = await response.json();
                user.set(userData);
            } else {
                console.error('Failed to fetch user profile:', response.statusText);
                // Handle error (show error message to user)
            }
        } catch (error) {
            console.error('Error fetching user profile:', error);
            // Handle network errors
        }
    });

    function isValidPhoneNumber(number) {
        // Validate phone number to be exactly 10 digits
        const cleaned = number.replace(/\D/g, ''); // Remove non-digit characters
        return cleaned.length === 10;
    }

    function resetError() {
        const errorElement = document.querySelector('.error-message');
        if (!isValidPhoneNumber($user.phoneNumber)) {
            errorElement.style.opacity = '1';
        } else {
            errorElement.style.opacity = '0';
        }
    }
</script>

<style>
    /* Your CSS styles for profile page */
    .profile-container {
        max-width: 600px;
        margin: 0 auto;
        padding: 20px;
        background-color: #f8f9fa; /* Light background color */
        border-radius: 8px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* Soft shadow */
    }

    h2 {
        text-align: center;
        margin-bottom: 20px;
        color: #343a40; /* Dark text color */
        font-size: 1.8rem; /* Larger font size */
    }

    form {
        display: flex;
        flex-direction: column;
    }

    label {
        margin-bottom: 6px;
        color: #495057; /* Darker text color */
        font-size: 1rem; /* Standard font size */
    }

    input {
        padding: 12px;
        margin-bottom: 16px;
        border: 1px solid #ced4da; /* Light border color */
        border-radius: 4px;
        font-size: 1rem;
        transition: border-color 0.15s ease-in-out;
    }

    input:focus {
        outline: none;
        border-color: #007bff; /* Focus color */
    }

    small {
        color: #6c757d; /* Muted text color */
        font-size: 0.9rem;
    }

    .error-message {
        color: #dc3545; /* Error text color */
        font-size: 0.9rem;
        opacity: 0;
        transition: opacity 0.3s ease-in-out;
    }

    button {
        padding: 12px 20px;
        background-color: #007bff; /* Primary button color */
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        cursor: pointer;
        transition: background-color 0.3s ease-in-out;
    }

    button:hover {
        background-color: #0056b3; /* Darker hover color */
    }
</style>

<div class="profile-container">
    <h2>Edit Profile</h2>
    <form on:submit|preventDefault={saveChanges}>
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" bind:value={$user.name} required>

        <label for="phoneNumber">Phone Number:</label>
        <input type="tel" id="phoneNumber" name="phoneNumber" bind:value={$user.phoneNumber} required on:input={resetError}>
        <small>Enter a 10-digit phone number.</small>
        <p class="error-message" style="{!isValidPhoneNumber($user.phoneNumber) ? 'opacity: 0;' : 'opacity: 1;'}">Please enter a valid 10-digit phone number.</p>

        <button type="submit">Save Changes</button>
    </form>
</div>
