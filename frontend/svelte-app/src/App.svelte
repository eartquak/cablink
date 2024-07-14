<!-- App.svelte -->
<script>
    import { Router, Route } from 'svelte-routing';
    import EntryPage from './EntryPage.svelte';
    import EditProfile from './EditProfile.svelte';
    import Login from './Login.svelte'

    // Define routes
    const routes = {
        '/': EntryPage,
        '/entrypage': EntryPage // Ensure the path matches the import and route
    };
</script>

<Router {routes}>
    <Route path="/" component={Login} />
    <Route path="/entrypage" component={EntryPage} />
    <Route path="/editprofile" component={EditProfile} />
</Router>
