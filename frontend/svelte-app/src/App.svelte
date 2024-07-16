<!-- App.svelte -->
<script>
    import { Router, Route } from 'svelte-routing';
    import EntryPage from './EntryPage.svelte';
    import EditProfile from './EditProfile.svelte';
    import Login from './Login.svelte';
    import Registration from './Registration.svelte';
    import RideDetails from './RideDetails.svelte';
    import { component_subscribe } from 'svelte/internal';
</script>

<Router>
    <Route path="/" component={Login} />
    <Route path="/entrypage" component={EntryPage} />
    <Route path="/editprofile" component={EditProfile} />
    <Route path="/registration" component={Registration} />
    <Route path="/ridedetails/:id" component={RideDetails} />
</Router>
