<!-- EditProfile.svelte -->
<script>
    import { writable } from 'svelte/store';
    import { navigate } from 'svelte-routing';

    let user = writable({
        name: 'John Doe',
        phNo: '', // Initial empty phone number
    });

    async function saveChanges(event) {
        event.preventDefault(); // Prevent default form submission

        try {
            const response = await fetch('/api/user/edit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: $user.name,
                    phNo: $user.phNo
                })
            });

            if (!response.ok) {
                throw new Error('Failed to update profile'); // Handle non-200 status
            }

            console.log('Profile updated successfully.');
            navigate('/entrypage'); // Redirect to entry page or another appropriate route
        } catch (error) {
            console.error('Error updating profile:', error);
            // Handle network errors or specific error responses
        }
    }

    function isValidPhoneNumber(number) {
        const cleaned = number.replace(/\D/g, ''); // Remove non-digit characters
        return cleaned.length === 10; // Ensure exactly 10 digits
    }

    function resetError() {
        const errorElement = document.querySelector('.error-message');
        if (!isValidPhoneNumber($user.phNo)) {
            errorElement.style.opacity = '1';
        } else {
            errorElement.style.opacity = '0';
        }
    }
</script>

<style>
    /* Your CSS styles remain the same */
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
    <form on:submit={saveChanges}>
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" bind:value={$user.name} required>

        <label for="phNo">Phone Number:</label>
        <input type="tel" id="phNo" name="phNo" bind:value={$user.phNo} required minlength="10" maxlength="10" on:input={resetError}>
        <small>Enter a 10-digit phone number.</small>
        <p class="error-message" style="{isValidPhoneNumber($user.phNo) ? 'opacity: 0;' : 'opacity: 1;'}">Please enter a valid 10-digit phone number.</p>

        <button type="submit">Save Changes</button>
    </form>
</div>
