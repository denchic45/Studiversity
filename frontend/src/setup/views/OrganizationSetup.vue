<script setup>
import {ref} from "vue"
import {useRouter} from 'vue-router'
import InputText from "primevue/inputtext"
import Button from 'primevue/button'
import Password from 'primevue/password'

const orgName = ref("")

const email = ref("")
const password = ref("")
const confirmedPassword = ref("")
const firstName = ref("")
const surname = ref("")

const router = useRouter()

function onCompleteClick() {
  router.push('/')
}
</script>

<template>
  <div class="page">
    <Panel header="Header">
    <h1 class="page__header">Сведения об организации</h1>
    <div class="page__form">

      <form class="form">
        <span class="flex flex-column gap-2">
          <label for="value">Название организации</label>
          <InputText
              v-model="orgName"
              aria-describedby="text-error"
              type="text"/>
        </span>
        <h3 class="page__subheader">Создание учетной записи</h3>
        <span class="flex flex-column gap-2">
             <label for="value">Имя</label>
        <InputText
            v-model="firstName"
            aria-describedby="text-error"
            type="text"/>
        </span>
        <span class="flex flex-column gap-2">
             <label for="value">Фамилия</label>
        <InputText
            v-model="surname"
            aria-describedby="text-error"
            type="text"/>
        </span>
        <span class="flex flex-column gap-2">
            <label for="value">Почта</label>
        <InputText
            v-model="email"
            aria-describedby="text-error"
            type="email"/>
        </span>
        <span class="flex flex-column gap-2">
          <label for="value">Пароль</label>
      <div>
          <Password
              v-model="password"
              :feedback="false"
              aria-describedby="text-error"
              toggleMask
              type="password"
              width="10px"/>
      </div>
        </span>
        <span class="flex flex-column gap-2">
           <label for="value">Повторите пароль</label>
          <div>
            <Password
                v-model="confirmedPassword"
                :feedback="false"
                aria-describedby="text-error"
                toggleMask
                type="password"/>
          </div>

          <small id="username-help">Enter your username to reset your password.</small>
        </span>

      </form>
      <Button class="submit-button" type="submit" @click="onCompleteClick">Завершить</Button>
    </div>
    </Panel>
  </div>
</template>

<style scoped>

.page {
  max-width: 568px;
  margin: 0 auto;
}

.page__header {
  margin-bottom: 28px;
  text-align: center;
}

.page__subheader {
  margin: 0;
}

.page__form {
  width: fit-content;
  margin: auto;
}

.form {
  display: grid;
  grid-column-gap: 28px;
  grid-row-gap: 18px;
  padding-bottom: 28px;
}

input {
  width: 274px;
}
</style>