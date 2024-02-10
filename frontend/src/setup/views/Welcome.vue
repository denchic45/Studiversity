<script setup>
import {useRouter} from 'vue-router'
import Button from 'primevue/button'
import Panel from 'primevue/panel';
import Dropdown from 'primevue/dropdown';
import {ref} from "vue";

// const languages = ref(['Русский']);
// const selectedLang = ref(languages.value[0])

const router = useRouter()

function onNextClick() {
  router.push({name: 'database-setup'})
}

</script>

<template>
  <Panel class="panel">
  <div>
    <h1>Добро Пожаловать!</h1>
    <p>Для установки необходимо пройти следующие этапы:</p>
    <ul>
      <li>настройка базы данных</li>
      <li>указание данных об организации</li>
      <li>создание учетной записи администратора</li>
    </ul>
    <Button label="Начать" @click="onNextClick"/>
  </div>
  </Panel>
</template>

<style scoped>
.panel {
  max-width: 540px;
  margin: auto;
}
</style>