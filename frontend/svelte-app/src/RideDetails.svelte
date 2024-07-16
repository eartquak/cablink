<script>
    import { onMount } from 'svelte';

    let rideDetails = null;
    let currentPath = '';
    let selectedRideId = '';

    const fetchRideDetails = async () => {
        currentPath = window.location.pathname;
        selectedRideId = currentPath.substring('/ridedetails/'.length);
        
        try {
            const response = await fetch(`/api/ride/${selectedRideId}`);
            if (response.ok) {
                rideDetails = await response.json();
            } else {
                console.error('Failed to fetch ride details:', response.statusText);
            }
        } catch (error) {
            console.error('Error fetching ride details:', error);
        }
    };

    const joinRide = async () => {
        try {
            const response = await fetch(`/api/ride/add/${selectedRideId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ userId: rideDetails.data.host.id }) // Adjust as per your API requirements
            });
            if (response.ok) {
                // Optionally handle success, such as updating UI or fetching ride details again
                await fetchRideDetails();
            } else {
                console.error('Failed to join ride:', response.statusText);
            }
        } catch (error) {
            console.error('Error joining ride:', error);
        }
    };

    const leaveRide = async () => {
        try {
            const response = await fetch(`/api/ride/delete/${selectedRideId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ userId: rideDetails.data.host.id }) // Adjust as per your API requirements
            });
            if (response.ok) {
                // Optionally handle success, such as updating UI or fetching ride details again
                await fetchRideDetails();
            } else {
                console.error('Failed to leave ride:', response.statusText);
            }
        } catch (error) {
            console.error('Error leaving ride:', error);
        }
    };

    // Fetch ride details when component mounts
    onMount(fetchRideDetails);
</script>

<style>
    .ride-details-container {
        max-width: 600px;
        margin: auto;
        padding: 20px;
        background-color: #f9f9f9;
        border: 1px solid #ddd;
        border-radius: 8px;
        text-align: center; /* Center align the content */
    }

    .ride-details-item {
        margin-bottom: 12px;
        text-align: left; /* Ensure text is left-aligned within each item */
    }

    .ride-details-label {
        font-weight: bold;
    }

    .loading-text {
        font-style: italic;
        color: #888;
    }

    .action-button {
        margin-top: 12px;
        padding: 8px 16px;
        background-color: #007bff;
        color: #fff;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }

    .action-button:hover {
        background-color: #0056b3;
    }
</style>

<div class="ride-details-container">
    <h1>Ride Details</h1>

    {#if rideDetails}
        <div class="ride-details-item">
            <span class="ride-details-label">Ride Name:</span> {rideDetails.data.ride.name}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Host's Name:</span> {rideDetails.data.ride.host.name}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Host's Phone Number:</span> {rideDetails.data.ride.host.phNo}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Start Point:</span> {rideDetails.data.ride.locationStart.coordinates[1]}, {rideDetails.data.ride.locationStart.coordinates[0]}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Destination:</span> {rideDetails.data.ride.locationEnd.coordinates[1]}, {rideDetails.data.ride.locationEnd.coordinates[0]}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Date & Time:</span> {new Date(rideDetails.data.ride.dateTime).toLocaleString()}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Name of Riders:</span>
            {#each rideDetails.data.ride.riders as rider, index}
                {rider.name}
                {#if index < rideDetails.data.ride.riders.length - 1}, {/if}
            {/each}
        </div>
        
        {#if !rideDetails.data.userInRide}
            <button class="action-button" on:click={joinRide}>Join Ride</button>
        {:else}
            <button class="action-button" on:click={leaveRide}>Leave</button>
        {/if}
    {:else}
        <p class="loading-text">Loading ride details...</p>
    {/if}
</div>
