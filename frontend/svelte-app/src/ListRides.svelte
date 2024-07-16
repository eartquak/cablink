<script>
    import { onMount } from 'svelte';
    import { navigate } from 'svelte-routing';
    import { writable } from 'svelte/store';
    import * as turf from '@turf/turf'; // Import Turf.js functions

    // Mode state: 0 for All Rides, 1 for My Rides
    const currentMode = writable(0);

    let rides = [];
    let startPoint = '';
    let destination = '';
    let searchDateTime = ''; // New state for search datetime

    // Example coordinates for the location names
    const locationCoordinates = {
        'Campus': [78.5741606497244, 17.545012085007],
        'Airport': [78.4293210050184, 17.2369186012018],
        'Railway Station': [78.5020087381562, 17.4333820927201]
    };

    // Fetch all rides from backend API
    const fetchAllRides = async () => {
        try {
            const response = await fetch('/api/ride/all');
            if (response.ok) {
                const data = await response.json();
                console.log('Fetched all rides:', data);
                if (data && data.data) {
                    rides = data.data.map(ride => ({
                        id: ride.id,
                        name: ride.name,
                        locationStart: {
                            type: ride.locationStart.type,
                            coordinates: ride.locationStart.coordinates
                        },
                        locationEnd: {
                            type: ride.locationEnd.type,
                            coordinates: ride.locationEnd.coordinates
                        },
                        date: new Date(ride.dateTime).getTime(), // Convert date string to milliseconds
                        host: ride.host
                    }));
                }
            } else {
                console.error('Failed to fetch all rides:', response.statusText);
            }
        } catch (error) {
            console.error('Error fetching all rides:', error);
        }
    };

    // Fetch rides for the logged-in user
    const fetchMyRides = async () => {
        try {
            const response = await fetch('/api/user/rides');
            if (response.ok) {
                const data = await response.json();
                console.log('Fetched my rides:', data);
                if (data && data.data) {
                    rides = data.data.map(ride => ({
                        id: ride.id,
                        name: ride.name,
                        locationStart: {
                            type: ride.locationStart.type,
                            coordinates: ride.locationStart.coordinates
                        },
                        locationEnd: {
                            type: ride.locationEnd.type,
                            coordinates: ride.locationEnd.coordinates
                        },
                        date: new Date(ride.dateTime).getTime(), // Convert date string to milliseconds
                        host: ride.host
                    }));
                }
            } else {
                console.error('Failed to fetch my rides:', response.statusText);
            }
        } catch (error) {
            console.error('Error fetching my rides:', error);
        }
    };

    // Fetch rides based on current mode
    const fetchRides = async () => {
        const mode = $currentMode; // Get current mode from store
        rides = []; // Clear existing rides
        if (mode === 0) {
            await fetchAllRides();
        } else if (mode === 1) {
            await fetchMyRides();
        }
    };

    onMount(fetchRides); // Call fetchRides function when component mounts

    // Function to navigate to ride details page with ride ID
    const navigateToRideDetails = (rideid) => {
        navigate(`/ridedetails/${rideid}`);
    };

    // Function to format location based on type (assuming location is in GeoJSON format)
    const formatLocation = (location) => {
        switch (location.type) {
            case 'Point':
                return location.coordinates.join(', ');
            default:
                return 'Unknown Location';
        }
    };

    // Toggle between All Rides and My Rides
    const toggleMode = (mode) => {
        currentMode.set(mode);
        fetchRides(); // Fetch rides based on the selected mode
    };

    // Function to validate search inputs
    const validateInputs = () => {
        // Check if at least one search criterion is selected
        if (!startPoint && !destination && !searchDateTime) {
            alert('Please select at least one of Start Point, Destination, or Search DateTime.');
            return false;
        }
        return true;
    };

    // Function to search rides based on startPoint, destination, and time
    const searchRides = async () => {
        // Validate inputs before proceeding
        if (!validateInputs()) {
            return;
        }

        // Fetch rides based on current mode to ensure data is up to date
        await fetchRides();

        // Determine which location to use for filtering
        const startCoordinates = locationCoordinates[startPoint];
        const destCoordinates = locationCoordinates[destination];

        if (!startCoordinates || !destCoordinates) {
            console.error('Invalid start point or destination selected');
            return;
        }

        // Convert search datetime to milliseconds since epoch
        const searchTimeMS = searchDateTime ? new Date(searchDateTime).getTime() : null;

        // Filter rides based on the criteria
        const filteredRides = rides.filter(ride => {
            const start = turf.point(ride.locationStart.coordinates);
            const dest = turf.point(ride.locationEnd.coordinates);

            // Calculate distances if coordinatesToUse is defined
            let startDistance = turf.distance(turf.point(startCoordinates), start, { units: 'meters' });
            let destDistance = turf.distance(turf.point(destCoordinates), dest, { units: 'meters' });

            // Calculate time difference if searchTimeMS is defined
            let timeMatch = !searchTimeMS || Math.abs(ride.date - searchTimeMS) <= 30 * 60 * 1000;

            // Check if ride matches the criteria
            return startDistance <= 100 && destDistance <= 100 && timeMatch;
        });

        // Update the rides array with the filtered rides
        rides = filteredRides;
    };

</script>

<style>
    /* CSS styles remain the same */
    .ride-list {
        max-height: 400px;
        overflow-y: auto;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }
    .ride-box {
        background-color: #ffffff;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        padding: 12px;
        margin-bottom: 10px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }
    .ride-box:hover {
        background-color: #f5f5f5;
    }
    .ride-details {
        font-weight: bold;
        margin-bottom: 8px;
    }
    .ride-details span {
        font-weight: normal;
    }
</style>

<h1>List of Rides</h1>

<p>You can filter rides by time, distance, or both as per your preference. Enjoy!</p>

<div>
    <button on:click={() => toggleMode(0)}>All Rides</button>
    <button on:click={() => toggleMode(1)}>My Rides</button>
</div>

<div>
    <label for="startPoint">Start Point:</label>
    <select id="startPoint" bind:value={startPoint}>
        <option value="">Select Start Point</option>
        <option value="Campus">Campus</option>
        <option value="Airport">Airport</option>
        <option value="Railway Station">Railway Station</option>
    </select>
</div>

<div>
    <label for="destination">Destination:</label>
    <select id="destination" bind:value={destination}>
        <option value="">Select Destination</option>
        <option value="Campus">Campus</option>
        <option value="Airport">Airport</option>
        <option value="Railway Station">Railway Station</option>
    </select>
</div>

<div>
    <label for="searchDateTime">Search Date & Time:</label>
    <input type="datetime-local" id="searchDateTime" bind:value={searchDateTime}>
</div>

<button on:click={searchRides}>Search Rides</button>

<div class="ride-list">
    {#if rides.length === 0}
        <p>No rides available.</p>
    {:else}
        <ul>
            {#each rides as ride}
                <li class="ride-box" on:click={() => navigateToRideDetails(ride.id)}>
                    <div class="ride-details">Name: <span>{ride.name}</span></div>
                    <div class="ride-details">Start Point: <span>{formatLocation(ride.locationStart)}</span></div>
                    <div class="ride-details">Destination: <span>{formatLocation(ride.locationEnd)}</span></div>
                    <div class="ride-details">Date & Time: <span>{new Date(ride.date).toLocaleString()}</span></div>
                    <div class="ride-details">Host: <span>{ride.host ? ride.host.name : 'Unknown Host'}</span></div>
                </li>
            {/each}
        </ul>
    {/if}
</div>