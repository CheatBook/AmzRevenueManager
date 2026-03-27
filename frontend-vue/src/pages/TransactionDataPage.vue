<template>
  <div>
    <h1>トランザクションデータ一覧</h1>
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>登録親番号</th>
            <th>SKU</th>
            <th>分類</th>
            <th>個数</th>
            <th>購入日</th>
            <th>購入確定日</th>
            <th>金額</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(data, index) in transactionData" :key="index">
            <td data-label="登録親番号">{{ data.settlementId }}</td>
            <td data-label="SKU">{{ data.sku }}</td>
            <td data-label="分類">{{ data.amountDescription }}</td>
            <td data-label="個数">{{ data.quantityPurchased }}</td>
            <td data-label="購入日">{{ data.purchaseDate }}</td>
            <td data-label="購入確定日">{{ data.postedDateTime }}</td>
            <td data-label="金額">{{ data.amount }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "axios";

interface TransactionData {
  settlementId: string;
  sku: string;
  amountDescription: string;
  quantityPurchased: number;
  purchaseDate: string;
  postedDateTime: string;
  amount: number;
}

const transactionData = ref<TransactionData[]>([]);

onMounted(async () => {
  try {
    const response = await axios.get("/api/transaction-data");
    transactionData.value = response.data;
  } catch (error) {
    console.error("Error fetching transaction data:", error);
  }
});
</script>
