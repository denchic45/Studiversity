<script setup>
import {ref} from "vue"
import {useRouter} from 'vue-router'
import InputText from "primevue/inputtext"
import Button from 'primevue/button'
import axios from "axios";
import Password from "primevue/password";

const firstname = ref("")
const surname = ref("")
const patronymic = ref("")
const gender = ref()
const email = ref("")
const password = ref("")
const confirmedPassword = ref("")

const genders = [
  {name: "Мужской", value: "MALE"},
  {name: "Женский", value: "FEMALE"}
]

const router = useRouter()

function onNextClick() {
  axios
  router.push({name: 'organization-setup'})
}
</script>

<template>
  <div class="page">
    <h1 class="page__header">Создайте первую учетную запись</h1>
    <div class="page__form">
      <div class="form">
      <span class="p-float-label">
        <InputText
            v-model="firstname"
            aria-describedby="text-error"
            type="text"/>
        <label for="value">Имя</label>
      </span>
        <span class="p-float-label">
        <InputText v-model="surname" aria-describedby="text-error" type="text"/>
        <label for="value">Фамилия</label>
      </span>
        <span class="p-float-label">
        <InputText v-model="patronymic" aria-describedby="text-error" type="text"/>
        <label for="value">Отчество</label>
        </span>
        <span class="p-float-label">
        <InputText v-model="email" aria-describedby="text-error" type="text"/>
        <label for="value">Почта</label>
        </span>
        <span class="p-float-label">
           <Password
               v-model="password"
               :feedback="false"
               aria-describedby="text-error"
               toggleMask
               type="password"
               width="10px"/>
        <label for="value">Пароль</label>
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
        <div class="card">
          <Dropdown v-model="gender" :highlightOnSelect="false" :options="genders" checkmark class="w-full"
                    option-label="name"
                    placeholder="Пол"/>
        </div>
      </div>
      <Button class="submit-button" type="submit" @click="onNextClick">Продолжить</Button>
    </div>
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