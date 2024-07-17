<script>
    import { writable } from 'svelte/store';
    import { navigate } from 'svelte-routing';

    const ride = writable({
        name: '',
        seats: 2,
        price: 0,
        startingPoint: '',
        destinationPoint: '',
        dateTime: new Date().toISOString().slice(0, 16), 
    });

    async function saveRide(event) {
        event.preventDefault();

        if ($ride.startingPoint === $ride.destinationPoint) {
            alert('Starting point and destination point cannot be the same.');
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

    function getCoordinates(location) {
        switch (location) {
            case 'Campus':
                return [78.57416064972438, 17.54501208500703]; 
            case 'Airport':
                return [78.42932100501844, 17.23691860120178]; 
            case 'Railway Station':
                return [78.50200873815618, 17.433382092720095]; 
            default:
                return [0, 0];
        }
    }
</script>

<style>

    button[type="submit"] {
        margin-top: 20px; 
    }
</style>

<div>
    <h2>Make a Ride</h2>
    <form on:submit={saveRide}>
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" bind:value={$ride.name} required>

        <label for="seats">Seats:</label>
        <input type="number" id="seats" name="seats" bind:value={$ride.seats} min="2" required>

        <label for="price">Total Price:</label>
        <input type="number" id="price" name="price" bind:value={$ride.price} min="0" step="1" required>

        <label for="startingPoint">Starting Point:</label>
        <select id="startingPoint" name="startingPoint" bind:value={$ride.startingPoint}>
            <option value="">Select starting point</option>
            <option value="Campus">Campus</option>
            <option value="Airport">Airport</option>
            <option value="Railway Station">Railway Station</option>
        </select>

        <label for="destinationPoint">Destination Point:</label>
        <select id="destinationPoint" name="destinationPoint" bind:value={$ride.destinationPoint}>
            <option value="">Select destination point</option>
            <option value="Campus">Campus</option>
            <option value="Airport">Airport</option>
            <option value="Railway Station">Railway Station</option>
        </select>

        <label for="dateTime">Date and Time:</label>
        <input type="datetime-local" id="dateTime" name="dateTime" bind:value={$ride.dateTime} required>

        <button type="submit">Create Ride</button>
    </form>
</div>