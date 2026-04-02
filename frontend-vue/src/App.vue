<script setup lang="ts">
import { Authenticator } from '@aws-amplify/ui-vue';
</script>

<template>
  <authenticator>
    <template v-slot="{ user, signOut }">
      <div class="app-container">
        <header class="app-header">
          <div class="header-top">
            <h1 class="app-title">Amazon収益レポート管理</h1>
            <div class="user-info">
              <span class="user-email">{{ user?.signInDetails?.loginId }}</span>
              <button @click="signOut" class="signout-button">サインアウト</button>
            </div>
          </div>
          <nav class="app-nav">
            <ul class="nav-list">
              <li class="nav-item">
                <router-link to="/" class="nav-link" active-class="active">レポートアップロード</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/monthly-summary" class="nav-link" active-class="active">月別サマリー</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/sku-names" class="nav-link" active-class="active">SKU名管理</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/parent-sku-names" class="nav-link" active-class="active">親SKU登録</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/purchase" class="nav-link" active-class="active">仕入れ登録</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/transaction-data" class="nav-link" active-class="active">トランザクションデータ一覧</router-link>
              </li>
            </ul>
          </nav>
        </header>
        <main class="app-main">
          <router-view></router-view>
        </main>
      </div>
    </template>
  </authenticator>
</template>

<style>
/* 既存のスタイルを維持しつつ、ログイン情報のスタイルを追加 */
.app-container {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.app-header {
  background-color: #232f3e;
  color: white;
  padding: 1rem 2rem;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.app-title {
  margin: 0;
  font-size: 1.5rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.9rem;
}

.signout-button {
  padding: 0.4rem 0.8rem;
  background-color: #f0c14b;
  border: 1px solid #a88734;
  border-radius: 3px;
  cursor: pointer;
  color: #111;
}

.signout-button:hover {
  background-color: #ddb347;
}

.nav-list {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
  gap: 1.5rem;
  overflow-x: auto;
}

.nav-link {
  color: #ddd;
  text-decoration: none;
  white-space: nowrap;
  padding-bottom: 0.2rem;
}

.nav-link:hover {
  color: white;
}

.nav-link.active {
  color: white;
  border-bottom: 2px solid #f0c14b;
}

.app-main {
  padding: 2rem;
}
</style>
