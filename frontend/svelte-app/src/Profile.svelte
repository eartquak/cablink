<!-- src/Profile.svelte -->
<script>
    let user = {
        name: 'John Does',
        phoneNumber: '1234567890', // Example phone number
    };

    function handleEdit() {
        // Handle edit functionality
        console.log('Edit button clicked');
    }

    function handleDelete() {
        // Handle delete functionality
        console.log('Delete button clicked');
    }
</script>

<style>
    /* Your CSS styles for the profile section */
    .profile-details {
        max-width: 400px;
        padding: 20px;
        background-color: #ffffff;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        margin-bottom: 20px;
        text-align: center;
    }

    .profile-details h2 {
        color: #333;
        margin-bottom: 10px;
    }

    .profile-details p {
        color: #666;
        margin-bottom: 8px;
    }

    .button-row {
        display: flex;
        justify-content: center;
        margin-top: 20px;
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

<div class="profile-details">
    <h2>Profile Details</h2>
    <p><strong>Name:</strong> {user.name}</p>
    <p><strong>Phone Number:</strong> {user.phoneNumber}</p>
    <div class="button-row">
        <button on:click={handleEdit}>Edit</button>
        <button on:click={handleDelete}>Delete</button>
    </div>
</div>
