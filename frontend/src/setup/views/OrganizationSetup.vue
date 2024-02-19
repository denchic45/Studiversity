<script setup>
import {computed, inject, ref} from "vue"
import {useRouter} from 'vue-router'
import InputText from "primevue/inputtext"
import Panel from 'primevue/panel';
import Button from 'primevue/button'
import Password from 'primevue/password'
import SetupCompleted from "./SetupCompleted.vue";

const client = inject('client');
const router = useRouter()

const orgName = ref("")

const firstName = ref("")
const surname = ref("")
const patronymic = ref("")
const gender = ref()
const email = ref("")
const password = ref("")
const confirmedPassword = ref("")

const correctPassword = computed(() => password.value === confirmedPassword.value || confirmedPassword.value.length === 0)
const submitEnable = computed(() => correctPassword && confirmedPassword.value.length !== 0
    && firstName.value.length !== 0
    && surname.value.length !== 0
    && gender.value
    && email.value.length !== 0
    && password.value.length !== 0
)


const genders = [
  {name: "Мужской", value: "MALE"},
  {name: "Женский", value: "FEMALE"}
]

async function onSubmitClick() {
  await client.post('/setup/organization', {name: orgName.value})

  await client.post('/setup/admin', {
    firstName: firstName.value,
    surname: surname.value,
    patronymic: patronymic.value,
    gender: gender.value,
    email: email.value,
    password: password.value
  }).then(() => {
    router.addRoute({path: '/setup-completed', component: SetupCompleted})
    router.push('/setup-completed')
  })
}
</script>

<template>
  <div class="page">
    <Panel header="Сведения об организации">
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
          <div class="card">
            <Dropdown v-model="gender" :highlightOnSelect="false" :options="genders" checkmark class="w-full"
                      option-label="name"
                      option-value="value"
                      placeholder="Пол"/>
          </div>
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
                :invalid="!correctPassword"
                aria-describedby="text-error"
                toggleMask
                type="password"/>
          </div>

            <!--          <small id="username-help">Enter your username to reset your password.</small>-->
        </span>

        </form>
        <Button :disabled="!submitEnable" class="submit-button" type="submit" @click="onSubmitClick">Завершить</Button>
      </div>
    </Panel>
  </div>
</template>

<style scoped>

.page {
  max-width: 568px;
  margin: 0 auto;
}

.panel {
  max-width: 540px;
  margin: auto;
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