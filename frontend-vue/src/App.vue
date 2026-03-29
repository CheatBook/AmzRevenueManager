<script setup lang="ts">
// App logic here if needed
</script>

<template>
  <div class="app-container">
    <header class="app-header">
      <h1 class="app-title">Amazon収益レポート管理</h1>
      <nav class="app-nav">
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/" class="nav-link" active-class="active"
              >レポートアップロード</router-link
            >
          </li>
          <li class="nav-item">
            <router-link
              to="/monthly-summary"
              class="nav-link"
              active-class="active"
              >月別サマリー</router-link
            >
          </li>
          <li class="nav-item">
            <router-link to="/sku-names" class="nav-link" active-class="active"
              >SKU名管理</router-link
            >
          </li>
          <li class="nav-item">
            <router-link
              to="/parent-sku-names"
              class="nav-link"
              active-class="active"
              >親SKU登録</router-link
            >
          </li>
          <li class="nav-item">
            <router-link to="/purchase" class="nav-link" active-class="active"
              >仕入れ登録</router-link
            >
          </li>
          <li class="nav-item">
            <router-link
              to="/transaction-data"
              class="nav-link"
              active-class="active"
              >トランザクションデータ一覧</router-link
            >
          </li>
        </ul>
      </nav>
    </header>
    <main class="app-main">
      <router-view></router-view>
    </main>
  </div>
</template>

<style>
/* Copy styles from App.css and index.css later */
</style>
