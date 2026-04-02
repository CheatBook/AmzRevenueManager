<script setup lang="ts">
import { Authenticator } from '@aws-amplify/ui-vue';
</script>

<template>
  <authenticator>
    <template v-slot="{ user, signOut }">
      <div id="app-container">
        <header>
          <h1>Amazon Revenue Manager</h1>
          <div class="user-info">
            <span>{{ user?.signInDetails?.loginId }}</span>
            <button @click="signOut">Sign Out</button>
          </div>
        </header>
        <main>
          <router-view />
        </main>
      </div>
    </template>
  </authenticator>
</template>

<style>
#app-container {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #232f3e;
  color: white;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

button {
  padding: 0.5rem 1rem;
  cursor: pointer;
}

main {
  padding: 2rem;
}
</style>
