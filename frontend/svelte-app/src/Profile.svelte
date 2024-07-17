<!-- Profile.svelte -->
<script>
    import { onMount } from 'svelte';
    import { navigate } from 'svelte-routing';
    import { writable } from 'svelte/store'; 

    export const userType = writable('');

    let user = {
        name: '',
        email: '',
        phNo: '',
        dpURL: '',
        userType: '' 
    };

    async function fetchUserProfile() {
        try {
            const response = await fetch('/api/user/me');
            if (!response.ok) {
                throw new Error('Failed to fetch user profile');
            }
            const userData = await response.json();
            user = userData.data;

            userType.set(user.userType);
        } catch (error) {
            console.error('Error fetching user profile:', error);

        }
    }

    async function handleDelete() {
        try {
            const deleteResponse = await fetch('/api/user/delete', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
            });

            if (!deleteResponse.ok) {
                throw new Error('Failed to delete user');
            }

            navigate('/'); 
        } catch (error) {
            console.error('Error deleting user:', error);

        }
    }

    onMount(fetchUserProfile);
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

    .details {
        margin-bottom: 20px;
    }

    .details p {
        margin-bottom: 10px;
        font-size: 1.2rem;
        color: #495057; 
    }

    .profile-picture {
        width: 150px; 
        height: 150px; 
        border-radius: 50%; 
        object-fit: cover; 
        margin: 0 auto 20px; 
        display: block; 
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); 
    }

    .edit-btn {
        padding: 12px 20px;
        background-color: #007bff; 
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        cursor: pointer;
        margin-right: 10px;
        transition: background-color 0.3s ease-in-out;
    }

    .edit-btn:hover {
        background-color: #0056b3; 
    }

    .delete-btn {
        padding: 12px 20px;
        background-color: #dc3545; 
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        cursor: pointer;
        transition: background-color 0.3s ease-in-out;
    }

    .delete-btn:hover {
        background-color: #c82333; 
    }
</style>

<div class="profile-container">
    <h2>Profile Details</h2>

    <!-- Display profile picture if dpURL exists -->
    {#if user.dpURL}
        <img class="profile-picture" src={user.dpURL} alt="Profile Picture">
    {:else}
        <!-- Display default profile picture or placeholder if dpURL is not available -->
        <div class="profile-picture" style="background-color: #ccc; text-align: center; line-height: 150px;">
            No Profile Picture
        </div>
    {/if}

    <div class="details">
        <p><strong>Name:</strong> {user.name}</p>
        <p><strong>Email:</strong> {user.email}</p>
        <p><strong>Phone Number:</strong> {user.phNo}</p>
        <p><strong>User Type:</strong> {user.userType}</p>
        <!-- Add other details as needed -->
    </div>

    <button class="edit-btn" on:click={() => navigate('/editprofile')}>Edit Profile</button>
    <button class="delete-btn" on:click={handleDelete}>Delete Account</button>
</div>