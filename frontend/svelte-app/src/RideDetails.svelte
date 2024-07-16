<script>
    import { onMount } from 'svelte';

    let selectedRideId = '';

    let rideDetails = null;
    let currentPath = '';

    const fetchRideDetails = async () => {
        currentPath = window.location.pathname;
        console.log(currentPath)
        const selectedRideId = currentPath.substring('/ridedetails/'.length);
        console.log(selectedRideId)
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
        <p>Ride Name: {rideDetails.data.ride.name}</p>
        <p>Host's Name: {rideDetails.data.ride.host.name}</p>
        <p>Host's Phone Number: {rideDetails.data.ride.host.phNo}</p>
        <p>Start Point: {rideDetails.data.ride.locationStart}</p>
        <p>Destination: {rideDetails.data.ride.locationEnd}</p>
        <p>Date & Time: {new Date(rideDetails.data.ride.date).toLocaleString()}</p>
        </div>
{:else}
    <p>Loading ride details...</p>
{/if}
