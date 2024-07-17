<!-- RideForm.svelte -->
<script>
    import { writable } from 'svelte/store';
    import { navigate } from 'svelte-routing';
    import L from 'leaflet';
    import 'leaflet/dist/leaflet.css';

    const ride = writable({
        name: '',
        seats: 2,
        price: 0,
        startingPoint: '',
        destinationPoint: '',
        dateTime: new Date().toISOString().slice(0, 16), 
    });

    let map = null;
    let startingMarker = null;
    let destinationMarker = null;

    function initializeMap() {
        map = L.map('map').setView([17.385, 78.4867], 13); 

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        map.on('click', onMapClick);
        map.invalidateSize(); 
    }

    function onMapClick(e) {
        const { lat, lng } = e.latlng;

        if ($ride.startingPoint === '') {
            $ride.startingPoint = `${lat}, ${lng}`;
            updateMarker('startingPoint', lat, lng);
        } else if ($ride.destinationPoint === '') {
            $ride.destinationPoint = `${lat}, ${lng}`;
            updateMarker('destinationPoint', lat, lng);
        }
    }

    function updateMarker(type, lat, lng) {
        if (type === 'startingPoint') {
            if (startingMarker) {
                map.removeLayer(startingMarker);
            }
            startingMarker = L.marker([lat, lng]).addTo(map);
        } else if (type === 'destinationPoint') {
            if (destinationMarker) {
                map.removeLayer(destinationMarker);
            }
            destinationMarker = L.marker([lat, lng]).addTo(map);
        }
    }

    async function saveRide(event) {
        event.preventDefault();

        if ($ride.startingPoint === '' || $ride.destinationPoint === '') {
            alert('Please select both starting and destination points on the map.');
            return;
        }

        try {
            const response = await fetch('/api/ride/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: $ride.name,
                    seats: $ride.seats,
                    price: $ride.price,
                    locationStart: {
                        type: 'Point',
                        coordinates: getCoordinates($ride.startingPoint)
                    },
                    locationEnd: {
                        type: 'Point',
                        coordinates: getCoordinates($ride.destinationPoint)
                    },
                    dateTime: $ride.dateTime
                })
            });

            if (!response.ok) {
                throw new Error('Failed to create ride');
            }

            console.log('Ride created successfully.');
            alert('Ride created successfully!');
            navigate('/entrypage'); 
        } catch (error) {
            console.error('Error creating ride:', error);

        }
    }

    function clearStartingPoint() {
        $ride.startingPoint = '';
        if (startingMarker) {
            map.removeLayer(startingMarker);
            startingMarker = null;
        }
    }

    function clearDestinationPoint() {
        $ride.destinationPoint = '';
        if (destinationMarker) {
            map.removeLayer(destinationMarker);
            destinationMarker = null;
        }
    }

    function getCoordinates(location) {
        const [lat, lng] = location.split(',').map(coord => parseFloat(coord.trim()));
        return [lng, lat]; 
    }

    import { onMount, afterUpdate } from 'svelte';
    onMount(initializeMap);

    afterUpdate(() => {
        map.invalidateSize();
    });
</script>

<style>
    html,
    body {
        height: 100%;
        margin: 0;
        font-family: Arial, sans-serif;
    }

    .container {
        display: flex;
        flex-direction: column;
        align-items: stretch;
        max-width: 100%; 
        margin: 0 auto;
        padding: 20px;
        min-height: 100vh; 
        overflow-x: hidden; 
    }

    .form {
        background-color: #f8f9fa;
        border-radius: 8px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        padding: 20px;
        margin-bottom: 20px;
        overflow: hidden; 
    }

    #map {
        height: 300px; 
        width: 100%;
        border-radius: 8px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }

    .spacer {
        flex-grow: 1; 
    }

    .clear-button {
        font-size: 12px;
        padding: 3px 6px;
        margin-left: 6px;
        cursor: pointer;
    }
</style>
<div class="container">
    <div class="form">
        <h2>Make a Ride</h2>
        <form on:submit={saveRide}>
            <label for="name">Name:</label>
            <input type="text" id="name" name="name" bind:value={$ride.name} required>

            <label for="seats">Seats:</label>
            <input type="number" id="seats" name="seats" bind:value={$ride.seats} min="2" required>

            <label for="price">Total Price:</label>
            <input type="number" id="price" name="price" bind:value={$ride.price} min="0" step="1" required>

            <div class="input-container">
                <label for="startingPoint">Starting Point:</label>
                <div class="input-group">
                    <input type="text" id="startingPoint" name="startingPoint" bind:value={$ride.startingPoint} readonly>
                </div>
            </div>

            <div class="input-container">
                <label for="destinationPoint">Destination Point:</label>
                <div class="input-group">
                    <input type="text" id="destinationPoint" name="destinationPoint" bind:value={$ride.destinationPoint} readonly>
                    <button type="button" class="clear-button" on:click={clearStartingPoint}>Clear</button>
                    <button type="button" class="clear-button" on:click={clearDestinationPoint}>Clear</button>
                </div>
            </div>

            <label for="dateTime">Date and Time:</label>
            <input type="datetime-local" id="dateTime" name="dateTime" bind:value={$ride.dateTime} required>

            <button type="submit">Create Ride</button>
        </form>
    </div>

    <div id="map">
        <h2>Map</h2>
    </div>

    <div class="spacer"></div>
</div>