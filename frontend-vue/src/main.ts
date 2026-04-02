import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./style.css";

import { Amplify } from 'aws-amplify';
import '@aws-amplify/ui-vue/styles.css';

// Cognito & API Gateway configuration
// These values should be replaced with the outputs from 'sam deploy'
Amplify.configure({
  Auth: {
    Cognito: {
      userPoolId: (import.meta as any).env.VITE_USER_POOL_ID || 'ap-northeast-1_KrUFLXcvh',
      userPoolClientId: (import.meta as any).env.VITE_USER_POOL_CLIENT_ID || '6ifn8edg1n1967bol8aimntdoj',
    }
  },
  API: {
    REST: {
      AmzRevenueApi: {
        endpoint: (import.meta as any).env.VITE_API_ENDPOINT || 'https://pd1qwbm17e.execute-api.ap-northeast-1.amazonaws.com',
      }
    }
  }
});

const app = createApp(App);
app.use(router);
app.mount("#app");
