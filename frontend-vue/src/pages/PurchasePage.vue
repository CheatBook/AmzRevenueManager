<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "axios";

interface SkuName {
  sku: string;
  japaneseName: string;
  parentSku: string | null;
}

interface Purchase {
  parentSku: string;
  purchaseDate: string;
  quantity: number;
  amount: number;
  tariff: number;
  unitPrice: number;
}

const parentSkus = ref<SkuName[]>([]);
const purchases = ref<Purchase[]>([]);
const selectedParentSku = ref("");
const purchaseDate = ref(new Date().toISOString().split("T")[0]);
const quantity = ref(0);
const amount = ref(0);
const tariff = ref(0);
const message = ref("");
const error = ref("");
const isLoading = ref(false);
const editingPurchase = ref<Purchase | null>(null);

const formatNumber = (num: number) => {
  return Math.round(num).toLocaleString();
};

const getJapaneseName = (sku: string) => {
  const foundSku = parentSkus.value.find((s) => s.sku === sku);
  return foundSku ? foundSku.japaneseName : sku;
};

const fetchPurchases = async () => {
  try {
    const response = await axios.get("/api/purchases");
    purchases.value = response.data;
  } catch (err) {
    error.value = "仕入れ情報の取得中にエラーが発生しました。";
  }
};

const fetchParentSkus = async () => {
  try {
    const response = await axios.get("/api/sku-names/parent-skus");
    parentSkus.value = response.data;
    if (response.data.length > 0 && !selectedParentSku.value) {
      selectedParentSku.value = response.data[0].sku;
    }
  } catch (err) {
    error.value = "親SKUの取得中にエラーが発生しました。";
  }
};

const handleEdit = (purchase: Purchase) => {
  editingPurchase.value = purchase;
  selectedParentSku.value = purchase.parentSku;
  purchaseDate.value = purchase.purchaseDate;
  quantity.value = purchase.quantity;
  amount.value = purchase.amount;
  tariff.value = purchase.tariff;
};

const handleClearForm = () => {
  editingPurchase.value = null;
  selectedParentSku.value =
    parentSkus.value.length > 0 ? parentSkus.value[0].sku : "";
  purchaseDate.value = new Date().toISOString().split("T")[0];
  quantity.value = 0;
  amount.value = 0;
  tariff.value = 0;
};

const handleSubmit = async () => {
  if (
    !selectedParentSku.value ||
    !purchaseDate.value ||
    quantity.value <= 0 ||
    amount.value <= 0
  ) {
    error.value = "すべての必須項目を入力してください。";
    return;
  }

  isLoading.value = true;
  message.value = editingPurchase.value
    ? "仕入れ情報を更新中です..."
    : "仕入れ情報を登録中です...";
  error.value = "";

  const purchaseData = {
    parentSku: selectedParentSku.value,
    purchaseDate: purchaseDate.value,
    quantity: quantity.value,
    amount: amount.value,
    tariff: tariff.value,
  };

  try {
    const url = editingPurchase.value
      ? `/api/purchases/${editingPurchase.value.parentSku}/${editingPurchase.value.purchaseDate}`
      : "/api/purchases";
    const method = editingPurchase.value ? "put" : "post";

    await axios({
      method: method,
      url: url,
      data: purchaseData,
    });

    message.value = editingPurchase.value
      ? "仕入れ情報を更新しました。"
      : "仕入れ情報を登録しました。";
    handleClearForm();
    fetchPurchases();
  } catch (err: any) {
    console.error("処理エラー:", err);
    error.value = err.response?.data || "処理中にエラーが発生しました。";
    message.value = "";
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchParentSkus();
  fetchPurchases();
});
</script>

<template>
  <div class="card">
    <h2>仕入れ登録</h2>
    <form @submit.prevent="handleSubmit" class="purchase-form">
      <div class="form-row">
        <div class="form-group">
          <label>仕入れ対象 (親SKU):</label>
          <select v-model="selectedParentSku" required>
            <option v-for="sku in parentSkus" :key="sku.sku" :value="sku.sku">
              {{ sku.japaneseName }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>仕入れ日:</label>
          <input type="date" v-model="purchaseDate" required />
        </div>
        <div class="form-group">
          <label>数量:</label>
          <input type="number" v-model.number="quantity" required min="1" />
        </div>
        <div class="form-group">
          <label>金額:</label>
          <input type="number" v-model.number="amount" required min="1" />
        </div>
        <div class="form-group">
          <label>関税:</label>
          <input type="number" v-model.number="tariff" />
        </div>
      </div>
      <div class="form-actions">
        <button type="submit" :disabled="isLoading">
          {{
            isLoading
              ? editingPurchase
                ? "更新中..."
                : "登録中..."
              : editingPurchase
                ? "更新"
                : "登録"
          }}
        </button>
        <button v-if="editingPurchase" type="button" @click="handleClearForm">
          クリア
        </button>
      </div>
    </form>
    <p v-if="message" style="color: green">{{ message }}</p>
    <p v-if="error" style="color: red; border: 1px solid red; padding: 10px">
      {{ error }}
    </p>

    <div class="card" style="margin-top: 2rem">
      <h2>仕入れ一覧</h2>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>仕入れ日</th>
              <th>親SKU</th>
              <th>数量</th>
              <th>金額</th>
              <th>関税</th>
              <th>単価</th>
              <th>編集</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="purchase in purchases"
              :key="`${purchase.parentSku}-${purchase.purchaseDate}`"
            >
              <td data-label="仕入れ日">{{ purchase.purchaseDate }}</td>
              <td data-label="親SKU">
                {{ getJapaneseName(purchase.parentSku) }}
              </td>
              <td data-label="数量">{{ formatNumber(purchase.quantity) }}</td>
              <td data-label="金額">{{ formatNumber(purchase.amount) }}</td>
              <td data-label="関税">{{ formatNumber(purchase.tariff) }}</td>
              <td data-label="単価">{{ formatNumber(purchase.unitPrice) }}</td>
              <td data-label="編集">
                <button @click="handleEdit(purchase)">編集</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
