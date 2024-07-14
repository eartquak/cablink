<!-- App.svelte -->
<script>
    import { Router, Route } from 'svelte-routing';
    import EntryPage from './EntryPage.svelte';
    import EditProfile from './EditProfile.svelte';

    // Define routes
    const routes = {
        '/': EntryPage,
        '/editprofile': EditProfile // Ensure the path matches the import and route
    };
</script>

<Router {routes}>
    <Route path="/" component={EntryPage} />
    <Route path="/editprofile" component={EditProfile} />
</Router>
