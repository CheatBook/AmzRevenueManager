import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./style.css";

import { Amplify } from 'aws-amplify';
import '@aws-amplify/ui-vue/styles.css';

// Cognito & API Gateway configuration
Amplify.configure({
  Auth: {
    Cognito: {
      userPoolId: import.meta.env.VITE_USER_POOL_ID,
      userPoolClientId: import.meta.env.VITE_USER_POOL_CLIENT_ID,
    }
  },
  API: {
    REST: {
      AmzRevenueApi: {
        endpoint: import.meta.env.VITE_API_ENDPOINT,
      }
    }
  }
});

const app = createApp(App);
app.use(router);
app.mount("#app");
