<!-- EditProfile.svelte -->
<script>
    import { writable } from 'svelte/store';
    import { navigate } from 'svelte-routing';

    let user = writable({
        name: 'John Doe',
        phNo: '', 
        dpURL: ''  
    });

    async function saveChanges(event) {
        event.preventDefault(); 

        try {
            const response = await fetch('/api/user/edit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: $user.name,
                    phNo: parseInt($user.phNo), 
                    dpURL: $user.dpURL
                })
            });

            if (!response.ok) {
                throw new Error('Failed to update profile'); 
            }

            console.log('Profile updated successfully.');
            navigate('/registration'); 
        } catch (error) {
            console.error('Error updating profile:', error);

        }
    }

    function resetError() {

    }
</script>

<style>

    .profile-container {
        max-width: 600px;
        margin: 0 auto;
        padding: 20px;
        background-color: #f8f9fa; 
        border-radius: 8px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); 
    }

    h2 {
        text-align: center;
        margin-bottom: 20px;
        color: #343a40; 
        font-size: 1.8rem; 
    }

    form {
        display: flex;
        flex-direction: column;
    }

    label {
        margin-bottom: 6px;
        color: #495057; 
        font-size: 1rem; 
    }

    input {
        padding: 12px;
        margin-bottom: 16px;
        border: 1px solid #ced4da; 
        border-radius: 4px;
        font-size: 1rem;
        transition: border-color 0.15s ease-in-out;
    }

    input:focus {
        outline: none;
        border-color: #007bff; 
    }

    small {
        color: #6c757d; 
        font-size: 0.9rem;
    }

    .error-message {
        color: #dc3545; 
        font-size: 0.9rem;
        opacity: 0;
        transition: opacity 0.3s ease-in-out;
    }

    button {
        padding: 12px 20px;
        background-color: #007bff; 
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        cursor: pointer;
        transition: background-color 0.3s ease-in-out;
    }

    button:hover {
        background-color: #0056b3; 
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
        <!-- Optionally, add validation message based on backend DTO constraints -->

        <label for="dpURL">Profile Picture URL:</label>
        <input type="url" id="dpURL" name="dpURL" bind:value={$user.dpURL}>
        <small>Optional: Enter a valid URL for profile picture.</small>

        <button type="submit">Save Changes</button>
    </form>
</div>