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

const client = axios.create()

let app = createApp(Setup);
app.config.globalProperties.$client = client
app.use(router)
    .use(PrimeVue)

    .component('Button', Button)
    .component('InputText', InputText)
    .component('Dropdown', Dropdown)

    .mount('#app')

