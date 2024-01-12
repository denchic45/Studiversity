import {createRouter, createWebHistory} from "vue-router";
import Home from "../views/Home.vue";
import Welcome from "../views/Welcome.vue";
import DatabaseConfig from "../views/config/DatabaseConfig.vue";
import OrganizationConfig from "../views/config/OrganizationConfig.vue";

const routes = [
    {path: '/welcome', name: 'welcome', component: Welcome},
    {path: '/db-config', name: 'db-config', component: DatabaseConfig},
    {path: '/org-config', name: 'org-config', component: OrganizationConfig},

    {path: '/', name: 'Home', component: Home},
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})
export default router