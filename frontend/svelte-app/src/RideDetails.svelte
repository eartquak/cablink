<!-- RideDetails.svelte -->
<script>
    import { onMount } from 'svelte';
    import { derived } from 'svelte/store';

    // Assuming selectedRideId is passed from ListRides.svelte
    export let selectedRideId = '';

    let rideDetails = null;

    const fetchRideDetails = async () => {
        try {
            const response = await fetch(`/api/ride/${selectedRideId}`); // Fetch ride details from backend API
            if (response.ok) {
                rideDetails = await response.json(); // Parse response JSON
            } else {
                console.error('Failed to fetch ride details:', response.statusText); // Log error if fetch fails
            }
        } catch (error) {
            console.error('Error fetching ride details:', error); // Log error if fetch throws an exception
        }
    };

    // Fetch ride details when component mounts
    onMount(fetchRideDetails);
</script>

<h1>Ride Details</h1>

{#if rideDetails}
    <div>
        <p>Name: {rideDetails.name}</p>
        <p>Start Point: {formatLocation(rideDetails.locationStart)}</p>
        <p>Destination: {formatLocation(rideDetails.locationEnd)}</p>
        <p>Date & Time: {new Date(rideDetails.date).toLocaleString()}</p>
        <p>Host: {rideDetails.host ? rideDetails.host.name : 'Unknown Host'}</p>
    </div>
{:else}
    <p>Loading ride details...</p>
{/if}
