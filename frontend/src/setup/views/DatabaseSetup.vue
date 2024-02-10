<script setup>
import {ref} from "vue"
import {useRouter} from 'vue-router'
import InputText from "primevue/inputtext"
import Button from 'primevue/button'
import Panel from 'primevue/panel'

const dbHost = ref("")
const dbName = ref("")
const dbUser = ref("")
const dbPassword = ref("")

const router = useRouter()

async function onNextClick() {
  try {
    const response = await this.$client.post('/setup/database')
    await router.push({name: 'org-config'})
  } catch (error) {
    console.error(error)
    if (error.response) {
      const { data, status } = error.response
      
    }
  }
}
</script>

<template>
  <div class="page">
    <Panel class="panel">
      <h1 class="page__header">Настройка базы данных</h1>
      <div class="page__form">
        <div class="form">
      <span class="p-float-label">
        <InputText
            v-model="dbHost"
            aria-describedby="text-error"
            type="text"/>
        <label for="value">Хост базы данных</label>
      </span>
          <span class="p-float-label">
        <InputText
            v-model="dbName"
            aria-describedby="text-error"
            type="text"/>
        <label for="value">Имя базы данных</label>
      </span>
          <span class="p-float-label">
        <InputText
            v-model="dbUser"
            aria-describedby="text-error"
            type="text"/>
        <label for="value">Пользователь</label>
      </span>
          <span class="p-float-label">
        <InputText
            v-model="dbPassword"
            aria-describedby="text-error"
            type="text"/>
        <label for="value">Пароль</label>
      </span>
        </div>
        <Button class="submit-button" type="submit" @click="onNextClick">Продолжить</Button>
      </div>
    </Panel>
  </div>
</template>

<style scoped>

.panel {
  max-width: 540px;
  margin: auto;
}

.page {
  //max-width: 568px;
  //margin: 0 auto;
}

.page__header {
  margin-bottom: 28px;
  text-align: center;
}

.page__form {
  width: fit-content;
  margin: auto;
}

.form {
  display: grid;
  grid-column-gap: 28px;
  grid-row-gap: 28px;
  padding-bottom: 28px;
}

.submit-button {
  //width: 128px;
}
</style>