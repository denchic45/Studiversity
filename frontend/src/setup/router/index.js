import {createRouter, createWebHistory} from "vue-router";

import Welcome from "../views/Welcome.vue";
import DatabaseSetup from "../views/DatabaseSetup.vue";
import OrganizationSetup from "../views/OrganizationSetup.vue";
import AdminSetup from "../views/AdminSetup.vue";
import SetupCompleted from "../views/SetupCompleted.vue";

const routes = [
    {path: '/', redirect: to => 'welcome'},
    {path: '/welcome', name: 'welcome', component: Welcome},
    {path: '/setup/database', name: 'database-setup', component: DatabaseSetup},
    {path: '/setup/organization', name: 'organization-setup', component: OrganizationSetup},
    {path: '/setup/admin', name: 'admin-setup', component: AdminSetup}
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})
export default router