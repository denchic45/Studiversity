<script setup>
import {useRouter} from 'vue-router'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown';
import {ref} from "vue";

const languages = ref(['Русский']);
const selectedLang = ref(languages.value[0])

const router = useRouter()

function onNextClick() {
  router.push({name: 'db-config'})
}

</script>

<template>
  <div>
    <h1>Добро Пожаловать!</h1>
    <p>Для начала установки выберите язык</p>
    <div class="lang-selector">
      <Dropdown v-model="selectedLang" :options="languages" class="w-full md:w-14rem"
                placeholder="Выберите язык"/>
      <Button label="Начать" @click="onNextClick"/>
    </div>

  </div>
</template>

<style scoped>
.lang-selector {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>