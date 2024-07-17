<!-- EntryPage.svelte -->
<script>
    import { onMount } from 'svelte';
    import { useLocation } from 'svelte-routing';
    import Profile from './Profile.svelte';
    import ListRides from './ListRides.svelte';
    import MakeRides from './MakeRides.svelte';
    import Maps from './Maps.svelte'; // Import the Maps component

    let activeTab = 'profile'; // Initially active tab is 'profile'

    // Function to handle tab change
    function changeTab(tab) {
        activeTab = tab;
        // Optional: Update URL with router if using svelte-routing
        // navigate(`/entry/${tab}`);
    }

    // Use location to detect initial tab from URL (if needed)
    const location = useLocation();
    onMount(() => {
        const path = location.pathname;
        const tab = path.split('/').pop(); // Get last segment of path
        if (['profile', 'ListRides', 'MakeRides', 'Maps'].includes(tab)) {
            activeTab = tab;
        }
    });
</script>

<style>
    /* Your CSS styles for entry page */
    .entry-page {
        height: calc(100vh - 60px); /* Adjusted height to accommodate tabs */
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: flex-start;
        background-color: #f3f6f8; /* Soft blue background */
        overflow-y: auto; /* Allow content to scroll if needed */
    }

    .tabs {
        display: flex;
        justify-content: space-around;
        width: 100%;
        position: fixed;
        bottom: 0;
        background-color: #ffffff;
        box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.1);
        z-index: 1; /* Ensure tabs are above content */
    }

    .tab {
        padding: 12px 0;
        flex: 1;
        text-align: center;
        cursor: pointer;
    }

    .tab.active {
        color: #007bff; /* Active tab color */
    }

    .tab .icon {
        font-size: 24px;
    }
</style>

<div class="entry-page">
    <!-- Content goes here -->
    {#if activeTab === 'profile'}
        <Profile />
    {/if}

    {#if activeTab === 'ListRides'}
        <ListRides />
    {/if}

    {#if activeTab === 'MakeRides'}
        <MakeRides />
    {/if}

    {#if activeTab === 'Maps'}
        <Maps />
    {/if}

    <!-- Tabs section -->
    <div class="tabs">
        <div class="tab {activeTab === 'profile' ? 'active' : ''}" on:click={() => changeTab('profile')}>
            <span class="icon">👤</span>
        </div>
        <div class="tab {activeTab === 'ListRides' ? 'active' : ''}" on:click={() => changeTab('ListRides')}>
            <span class="icon">🚕</span>
        </div>
        <div class="tab {activeTab === 'MakeRides' ? 'active' : ''}" on:click={() => changeTab('MakeRides')}>
            <span class="icon">➕</span>
        </div>
        <div class="tab {activeTab === 'Maps' ? 'active' : ''}" on:click={() => changeTab('Maps')}>
            <span class="icon">🗺️</span>
        </div>
    </div>
</div>
