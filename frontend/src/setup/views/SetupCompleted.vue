<script setup>
import Panel from 'primevue/panel';
</script>

<template>
  <Panel class="panel">
    <h1 class="page__header">Готово!</h1>
    <p>Настройка сервера завершена.</p>
    <!--      <div class="page__form">-->
    <!--        <Button class="submit-button" type="submit" @click="onNextClick">Продолжить</Button>-->
    <!--        <p class="error-message">{{ errorMessage }}</p>-->
    <!--      </div>-->
  </Panel>
</template>

<style scoped>
.panel {
  max-width: 540px;
  margin: auto;
}

.page__header {
  //margin-bottom: 28px;
}

.page__form {
  width: fit-content;
  margin: auto;
}

.submit-button {
  //width: 128px;
}
</style>