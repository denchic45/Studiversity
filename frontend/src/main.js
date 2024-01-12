import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import router from "./router/index.js"

import PrimeVue from "primevue/config"
import 'primevue/resources/themes/lara-light-teal/theme.css'
import Button from "primevue/button"
import InputText from "primevue/inputtext"
import Dropdown from 'primevue/dropdown'

createApp(App)
    .use(router)
    .use(PrimeVue)

    .component('Button', Button)
    .component('InputText', InputText)
    .component('Dropdown', Dropdown)

    .mount('#app')

