<!-- src/rideDetails.svelte -->
<script>
    import { onMount } from 'svelte';
    import { navigate } from 'svelte-routing';

    let rideDetails = null;
    let currentPath = '';
    let selectedRideId = '';
    let eta = null;
    let api_key = '5b3ce3597851110001cf62489adf2d05add3418aacf88d3df1f3e551';
    let locationS = '';
    let locationE = '';

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
            const url = 'https://api.openrouteservice.org/v2/directions/driving-car?api_key=' + api_key + 
                        '&start='+ rideDetails.data.ride.locationStart.coordinates[0] + ',' + rideDetails.data.ride.locationStart.coordinates[1] + 
                        '&end=' + rideDetails.data.ride.locationEnd.coordinates[0] + ',' + rideDetails.data.ride.locationEnd.coordinates[1]
            const etaResponse = await fetch(url);
            if (etaResponse.ok) {
                const data = await etaResponse.json();
                eta = data.features[0].properties.summary.duration
                console.log(eta)
            }
            const urlG = 'https://photon.komoot.io/reverse?lon='+rideDetails.data.ride.locationStart.coordinates[0]+'&lat='+rideDetails.data.ride.locationStart.coordinates[1]
            const locSResp = await fetch(urlG);
            const ls = await locSResp.json();
            locationS = ls.features[0].properties.name + ',' + ls.features[0].properties.street + ',' + ls.features[0].properties.postcode
            const urlGE = 'https://photon.komoot.io/reverse?lon='+rideDetails.data.ride.locationEnd.coordinates[0]+'&lat='+rideDetails.data.ride.locationEnd.coordinates[1]
            const locEResp = await fetch(urlGE);
            const lsE = await locEResp.json();
            locationE = lsE.features[0].properties.name + ',' + lsE.features[0].properties.street + ',' + lsE.features[0].properties.postcode
            console.log(lsE);
        } catch (error) {
            console.error('Error fetching ride details:', error);
        }
    };

    const joinRide = async () => {
        try {
            const response = await fetch(`/api/ride/add/${selectedRideId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {

                await fetchRideDetails();
                alert('Joined ride successfully!');
                navigate('/registration');
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
                }
            });
            if (response.ok) {

                await fetchRideDetails();
                alert('Left ride successfully!');
                navigate('/registration');
            } else {
                console.error('Failed to leave ride:', response.statusText);
            }
        } catch (error) {
            console.error('Error leaving ride:', error);
        }
    };

    onMount(fetchRideDetails);

    const formatLocation = (location) => {
        switch (location.type) {
            case 'Point':

                if (location.coordinates[0] === 78.57416064972438 && location.coordinates[1] === 17.54501208500703) {
                    return 'Campus';
                } else if (location.coordinates[0] === 78.42932100501844 && location.coordinates[1] === 17.23691860120178) {
                    return 'Airport';
                } else if (location.coordinates[0] === 78.50200873815618 && location.coordinates[1] === 17.433382092720095) {
                    return 'Railway Station';
                } else {
                    return `${location.coordinates[1]}, ${location.coordinates[0]}`; 
                }
            default:
                return 'Unknown Location'; 
        }
    };

    const navigateToRegistration = () => {
        navigate('/registration');
    };
</script>

<style>
    .ride-details-container {
        max-width: 600px;
        margin: auto;
        padding: 20px;
        background-color: #f9f9f9;
        border: 1px solid #ddd;
        border-radius: 8px;
        text-align: center; 
    }

    .ride-details-item {
        margin-bottom: 12px;
        text-align: left; 
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

    .back-button {
        margin-top: 20px;
        padding: 8px 16px;
        background-color: #6c757d;
        color: #fff;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }

    .back-button:hover {
        background-color: #495057;
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
            <span class="ride-details-label">Total Price:</span> {rideDetails.data.ride.price}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Time(secs):</span> {eta}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Total Number of seats:</span> {rideDetails.data.ride.seats}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Seats Filled:</span> {rideDetails.data.ride.seatsFilled}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Current Price Per Person:</span> {rideDetails.data.ride.price / rideDetails.data.ride.seatsFilled}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Host's Phone Number:</span> {rideDetails.data.ride.host.phNo}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Current Riders:</span>
            <span>
                {#each rideDetails.data.ride.riders as rider, index}
                    {rider.name}{index !== rideDetails.data.ride.riders.length - 1 ? ', ' : ''}
                {/each}
            </span>
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Start Point:</span> {locationS}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Destination:</span> {locationE}
        </div>
        <div class="ride-details-item">
            <span class="ride-details-label">Date & Time:</span> {new Date(rideDetails.data.ride.dateTime).toLocaleString()}
        </div>

        {#if rideDetails.data.ride.otp !== 0}
            <div class="ride-details-item">
                <span class="ride-details-label">OTP:</span> {rideDetails.data.ride.otp}
            </div>
        {/if}

        {#if !rideDetails.data.userInRide}
            <button class="action-button" on:click={joinRide}>Join Ride</button>
        {:else}
            {#if rideDetails.data.userHost}
                <button class="action-button" on:click={leaveRide}>Delete</button>
            {:else}
                <button class="action-button" on:click={leaveRide}>Leave</button>
            {/if}
        {/if}
    {:else}
        <p class="loading-text">Loading ride details...</p>
    {/if}

    <button class="back-button" on:click={navigateToRegistration}>Back to Home Page</button>
</div>
