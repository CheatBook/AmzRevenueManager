import { createRouter, createWebHistory } from "vue-router";
import UploadPage from "../pages/UploadPage.vue";
import MonthlySummaryPage from "../pages/MonthlySummaryPage.vue";
import SkuNamePage from "../pages/SkuNamePage.vue";
import ParentSkuPage from "../pages/ParentSkuPage.vue";
import PurchasePage from "../pages/PurchasePage.vue";
import TransactionDataPage from "../pages/TransactionDataPage.vue";

const routes = [
  { path: "/", component: UploadPage },
  { path: "/monthly-summary", component: MonthlySummaryPage },
  { path: "/sku-names", component: SkuNamePage },
  { path: "/parent-sku-names", component: ParentSkuPage },
  { path: "/purchase", component: PurchasePage },
  { path: "/transaction-data", component: TransactionDataPage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
