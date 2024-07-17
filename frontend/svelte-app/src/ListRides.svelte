<script>
    import { onMount } from 'svelte';
    import { navigate } from 'svelte-routing';
    import { writable } from 'svelte/store';
    import L from 'leaflet';
    import 'leaflet/dist/leaflet.css';
    import * as turf from '@turf/turf';
    

    // Mode state: 0 for All Rides, 1 for My Rides
    const currentMode = writable(0);

    let rides = [];
    let startPoint = '';
    let destination = '';
    let searchDateTime = ''; // New state for search datetime
    let startLatitude = '';
    let startLongitude = '';
    let destLatitude = '';
    let destLongitude = '';

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
        if (!startPoint && (!startLatitude || !startLongitude) && !destination && (!destLatitude || !destLongitude) && !searchDateTime) {
            alert('Please select at least one of Start Point, Destination, or Search DateTime.');
            return false;
        }
        return true;
    };

    // Haversine distance calculation function
    const haversineDistance = (lat1, lon1, lat2, lon2) => {
        const R = 6371; // Radius of the Earth in km
        const dLat = (lat2 - lat1) * Math.PI / 180;
        const dLon = (lon2 - lon1) * Math.PI / 180;
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        const distance = R * c; // Distance in km
        return distance * 1000; // Convert to meters
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
        let startCoordinates = null;
        let destCoordinates = null;

        if (startPoint) {
            startCoordinates = locationCoordinates[startPoint];
        } else if (startLatitude && startLongitude) {
            startCoordinates = [parseFloat(startLatitude), parseFloat(startLongitude)];
        }

        if (destination) {
            destCoordinates = locationCoordinates[destination];
        } else if (destLatitude && destLongitude) {
            destCoordinates = [parseFloat(destLatitude), parseFloat(destLongitude)];
        }

        if (!startCoordinates || !destCoordinates) {
            console.error('Invalid start point or destination selected');
            return;
        }

        // Convert search datetime to milliseconds since epoch
        const searchTimeMS = searchDateTime ? new Date(searchDateTime).getTime() : null;

        // Filter rides based on the criteria
        const filteredRides = rides.filter(ride => {

            // Calculate distances from search coordinates to ride start and destination points
            var point1 = turf.point(ride.locationStart.coordinates);
            var point2 = turf.point(ride.locationEnd.coordinates);

            var point1s = turf.point(startCoordinates);
            var point2s = turf.point(destCoordinates);

            var startDistance = turf.distance(point1, point1s);
            var destDistance = turf.distance(point2, point2s);

            // Calculate time difference if searchTimeMS is defined
            const timeMatch = !searchTimeMS || Math.abs(ride.date - searchTimeMS) <= 30 * 60 * 1000;

            console.log(startDistance)
            console.log(destDistance)
            console.log(timeMatch)

            // Check if ride matches the criteria (distance less than 1 km for start and destination)
            return (startDistance <= 1) && (destDistance <= 1) && timeMatch;
        });

        // Update the rides array with the filtered rides
        rides = filteredRides;
    };

    // Leaflet Map initialization
    let map = null;
    let startMarker = null;
    let destMarker = null;

    function initializeMap() {
        // Default center (Hyderabad)
        map = L.map('map').setView([17.385, 78.4867], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        // Add click event listener to map
        map.on('click', onMapClick);

        map.invalidateSize(); // Ensure the map size is correct initially

        // Initialize markers if coordinates are already set
        if (startLatitude && startLongitude) {
            setStartMarker([startLatitude, startLongitude]);
        }
        if (destLatitude && destLongitude) {
            setDestMarker([destLatitude, destLongitude]);
        }
    }

    // Function to handle click on the map
    // Function to handle click on the map
    function onMapClick(e) {
    const { lat, lng } = e.latlng;

    // Determine whether to update start or destination coordinates
    if (startPoint === '' && startLatitude === '' && startLongitude === '') {
        startLatitude = lat.toFixed(6);
        startLongitude = lng.toFixed(6);
        setStartMarker([startLatitude, startLongitude]);
    } else if (destination === '' && destLatitude === '' && destLongitude === '') {
        destLatitude = lat.toFixed(6);
        destLongitude = lng.toFixed(6);
        setDestMarker([destLatitude, destLongitude]);
    }
}


    // Function to set starting point marker
    function setStartMarker(coordinates) {
        if (startMarker) {
            startMarker.setLatLng(coordinates);
        } else {
            startMarker = L.marker(coordinates).addTo(map);
        }
    }

    // Function to set destination marker
    function setDestMarker(coordinates) {
        if (destMarker) {
            destMarker.setLatLng(coordinates);
        } else {
            destMarker = L.marker(coordinates).addTo(map);
        }
    }

    // Function to clear starting point coordinates
    function clearStartCoordinates() {
        startLatitude = '';
        startLongitude = '';
        if (startMarker) {
            map.removeLayer(startMarker);
            startMarker = null;
        }
    }

    // Function to clear destination coordinates
    function clearDestCoordinates() {
        destLatitude = '';
        destLongitude = '';
        if (destMarker) {
            map.removeLayer(destMarker);
            destMarker = null;
        }
    }

    // Lifecycle hook to initialize the map
    onMount(() => {
        initializeMap();
    });
</script>
<style>
    /* Grid container for layout control */
    .grid-container {
        display: grid;
        grid-template-columns: 1fr; /* Single column on mobile */
        gap: 20px; /* Gap between columns */
        height: 100vh; /* Set height of the grid container to full viewport height */
        overflow-y: auto; /* Enable vertical scrolling if content exceeds viewport height */
    }

    /* Left side content styling */
    .left-content {
        padding-right: 20px;
        padding-left: 20px;
    }

    /* Right side (ride list) styling */
    .right-content {
        padding-left: 20px;
        padding-right: 20px;
    }

    /* Your existing CSS styles */
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
    /* Style for map container */
    #map {
        height: 300px; /* Set desired height */
        width: 100%; /* Full width */
        border-radius: 8px;
        border: 1px solid #ccc;
        margin-top: 20px;
    }
    /* Additional styles for coordinate inputs */
    .coordinate-inputs {
        margin-bottom: 20px;
    }
    .coordinate-inputs > div {
        margin-bottom: 10px;
    }
    .coordinate-inputs label {
        display: block;
        margin-bottom: 5px;
    }
    .coordinate-inputs input[type="text"],
    .coordinate-inputs select {
        width: 100%;
        padding: 5px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .coordinate-clear {
        font-size: 0.8em;
        color: #007bff;
        cursor: pointer;
        margin-left: 5px;
    }
</style>

<div class="grid-container">
    <!-- Left Side Content -->
    <div class="left-content">
        <h1>List of Rides</h1>

        <p>You can filter rides by time, distance, or both as per your preference. Enjoy!</p>

        <div>
            <button on:click={() => toggleMode(0)}>All Rides</button>
            <button on:click={() => toggleMode(1)}>My Rides</button>
        </div>

        <div class="coordinate-inputs">
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
                <label for="startLatitude">Latitude:</label>
                <input type="text" id="startLatitude" bind:value={startLatitude} placeholder="Enter Latitude">
            </div>
            <div>
                <label for="startLongitude">Longitude:</label>
                <input type="text" id="startLongitude" bind:value={startLongitude} placeholder="Enter Longitude">
                <span class="coordinate-clear" on:click={clearStartCoordinates}>Clear</span>
            </div>
        </div>

        <div class="coordinate-inputs">
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
                <label for="destLatitude">Latitude:</label>
                <input type="text" id="destLatitude" bind:value={destLatitude} placeholder="Enter Latitude">
            </div>
            <div>
                <label for="destLongitude">Longitude:</label>
                <input type="text" id="destLongitude" bind:value={destLongitude} placeholder="Enter Longitude">
                <span class="coordinate-clear" on:click={clearDestCoordinates}>Clear</span>
            </div>
        </div>

        <div>
            <label for="searchDateTime">Search Date & Time:</label>
            <input type="datetime-local" id="searchDateTime" bind:value={searchDateTime}>
        </div>

        <button on:click={searchRides}>Search Rides</button>
    </div>

    <!-- Right Side (Ride List) -->
    <div class="right-content">
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
    </div>
</div>

<!-- Map container -->
<div style="width: 100%; display: grid; place-items: center; margin-top: 20px;">
    <div id="map"></div>
</div>
