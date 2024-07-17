<script>
    import { onMount } from 'svelte';
    import { navigate } from 'svelte-routing';
    import { writable } from 'svelte/store';
    import L from 'leaflet';
    import 'leaflet/dist/leaflet.css';
    import * as turf from '@turf/turf';

    const currentMode = writable(0);

    let rides = [];
    let startPoint = '';
    let destination = '';
    let searchDateTime = ''; 
    let startLatitude = '';
    let startLongitude = '';
    let destLatitude = '';
    let destLongitude = '';

    const locationCoordinates = {
        'Campus': [78.5741606497244, 17.545012085007],
        'Airport': [78.4293210050184, 17.2369186012018],
        'Railway Station': [78.5020087381562, 17.4333820927201]
    };

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
                        date: new Date(ride.dateTime).getTime(), 
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
                        date: new Date(ride.dateTime).getTime(), 
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

    const fetchRides = async () => {
        const mode = $currentMode; 
        rides = []; 
        if (mode === 0) {
            await fetchAllRides();
        } else if (mode === 1) {
            await fetchMyRides();
        }
    };

    onMount(fetchRides); 

    const navigateToRideDetails = (rideid) => {
        navigate(`/ridedetails/${rideid}`);
    };

    const formatLocation = (location) => {
        switch (location.type) {
            case 'Point':
                return location.coordinates.join(', ');
            default:
                return 'Unknown Location';
        }
    };

    const toggleMode = (mode) => {
        currentMode.set(mode);
        fetchRides(); 
    };

    const validateInputs = () => {

        if (!startPoint && (!startLatitude || !startLongitude) && !destination && (!destLatitude || !destLongitude) && !searchDateTime) {
            alert('Please select at least one of Start Point, Destination, or Search DateTime.');
            return false;
        }
        return true;
    };

    const haversineDistance = (lat1, lon1, lat2, lon2) => {
        const R = 6371; 
        const dLat = (lat2 - lat1) * Math.PI / 180;
        const dLon = (lon2 - lon1) * Math.PI / 180;
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        const distance = R * c; 
        return distance * 1000; 
    };

    const searchRides = async () => {

        if (!validateInputs()) {
            return;
        }

        await fetchRides();

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

        const searchTimeMS = searchDateTime ? new Date(searchDateTime).getTime() : null;

        const filteredRides = rides.filter(ride => {

            var point1 = turf.point(ride.locationStart.coordinates);
            var point2 = turf.point(ride.locationEnd.coordinates);

            var point1s = turf.point(startCoordinates);
            var point2s = turf.point(destCoordinates);

            var startDistance = turf.distance(point1, point1s);
            var destDistance = turf.distance(point2, point2s);

            const timeMatch = !searchTimeMS || Math.abs(ride.date - searchTimeMS) <= 30 * 60 * 1000;

            console.log(startDistance)
            console.log(destDistance)
            console.log(timeMatch)

            return (startDistance <= 1) && (destDistance <= 1) && timeMatch;
        });

        rides = filteredRides;
    };

    let map = null;
    let startMarker = null;
    let destMarker = null;

    function initializeMap() {

        map = L.map('map').setView([17.385, 78.4867], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        map.on('click', onMapClick);

        map.invalidateSize(); 

        if (startLatitude && startLongitude) {
            setStartMarker([startLatitude, startLongitude]);
        }
        if (destLatitude && destLongitude) {
            setDestMarker([destLatitude, destLongitude]);
        }
    }

    function onMapClick(e) {
    const { lat, lng } = e.latlng;

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

    function setStartMarker(coordinates) {
        if (startMarker) {
            startMarker.setLatLng(coordinates);
        } else {
            startMarker = L.marker(coordinates).addTo(map);
        }
    }

    function setDestMarker(coordinates) {
        if (destMarker) {
            destMarker.setLatLng(coordinates);
        } else {
            destMarker = L.marker(coordinates).addTo(map);
        }
    }

    function clearStartCoordinates() {
        startLatitude = '';
        startLongitude = '';
        if (startMarker) {
            map.removeLayer(startMarker);
            startMarker = null;
        }
    }

    function clearDestCoordinates() {
        destLatitude = '';
        destLongitude = '';
        if (destMarker) {
            map.removeLayer(destMarker);
            destMarker = null;
        }
    }

    onMount(() => {
        initializeMap();
    });
</script>
<style>

    .grid-container {
        display: grid;
        grid-template-columns: 1fr; 
        gap: 20px; 
        height: 100vh; 
        overflow-y: auto; 
    }

    .left-content {
        padding-right: 20px;
        padding-left: 20px;
    }

    .right-content {
        padding-left: 20px;
        padding-right: 20px;
    }

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

    #map {
        height: 300px; 
        width: 100%; 
        border-radius: 8px;
        border: 1px solid #ccc;
        margin-top: 20px;
    }

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