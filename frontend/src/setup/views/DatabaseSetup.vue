<script setup>
import {inject, ref} from "vue"
import {useRouter} from 'vue-router'
import InputText from "primevue/inputtext"
import Button from 'primevue/button'
import Panel from 'primevue/panel'

const dbHost = ref("localhost")
const dbPort = ref("5432")
const dbName = ref("")
const dbUser = ref("")
const dbPassword = ref("")

const errorMessage = ref("")

const router = useRouter()

const client = inject('client');

async function onNextClick() {
  console.log('onNextClick')
  try {
    await client.post(
        '/setup/database',
        {
          host: dbHost.value,
          port: dbPort.value,
          name: dbName.value,
          user: dbUser.value,
          password: dbPassword.value
        })
    await router.push({name: 'organization-setup'})
  } catch (error) {
    console.error(error)
    if (error.response) {
      const {data, status} = error.response
      switch (data.error.reason) {
        case "DATABASE_CONNECTION_FAILED":
          errorMessage.value = "Неудалось установить соединение с базой данных"
          break
        case "DATABASE_INVALID_PASSWORD":
          errorMessage.value = "Неверный пароль пользователя базы данных"
          break
        case "DATABASE_DOES_NOT_EXIST":
          errorMessage.value = "Кажется, базы данных с таким именем не существует"
          break
        default:
          errorMessage.value = "Неизвестная ошибка"
      }
      console.log("message: " + errorMessage.value + " data: " + data.error.reason)
    } else errorMessage.value = "Неизвестная ошибка"
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
            v-model="dbPort"
            aria-describedby="text-error"
            type="text"/>
        <label for="value">Порт базы данных</label>
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
        <p class="error-message">{{ errorMessage }}</p>
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

.error-message {

}

.submit-button {
  //width: 128px;
}
</style>