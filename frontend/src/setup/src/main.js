import {createApp} from 'vue'
import '../../style.css'
import router from "../router/index.js"

import PrimeVue from "primevue/config"
import 'primevue/resources/themes/lara-light-teal/theme.css'
import Button from "primevue/button"
import InputText from "primevue/inputtext"
import Dropdown from 'primevue/dropdown'
import Setup from "./Setup.vue";

import axios from 'axios'

const client = axios.create({
    baseURL: 'http://localhost:8080'
})

let app = createApp(Setup);

app.use(router)
    .use(PrimeVue)
    .component('Button', Button)
    .component('InputText', InputText)
    .component('Dropdown', Dropdown)


// app.config.globalProperties.$client = client
app.provide('client', client);
app.mount('#app')

