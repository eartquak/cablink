<!-- App.svelte -->
<script>
    import { Router, Route } from 'svelte-routing';
    import EntryPage from './EntryPage.svelte';
    import EditProfile from './EditProfile.svelte';
    import Login from './Login.svelte'
    import Registration from './Registration.svelte';

    // Define routes
    const routes = {
        '/': Login,
        '/entrypage': EntryPage, // Ensure the path matches the import and route
        '/editprofile': EditProfile,
        '/registration': Registration
        

    };
</script>

<Router {routes}>
    <Route path="/" component={Login} />
    <Route path="/entrypage" component={EntryPage} />
    <Route path="/editprofile" component={EditProfile} />
    <Route path="/registration" component={Registration} />
</Router>
