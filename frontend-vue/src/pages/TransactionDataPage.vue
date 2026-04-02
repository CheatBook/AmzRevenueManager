<script setup lang="ts">
import { ref, onMounted } from "vue";
import { get } from "aws-amplify/api";

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
const isLoading = ref(false);

onMounted(async () => {
  isLoading.value = true;
  try {
    const restOperation = get({
      apiName: "AmzRevenueApi",
      path: "/sales/summary",
    });
    const { body } = await restOperation.response;
    const data = (await body.json()) as any;
    
    if (Array.isArray(data)) {
      transactionData.value = data;
    }
  } catch (error) {
    console.error("トランザクションデータ取得エラー:", error);
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div>
    <h1>トランザクションデータ一覧</h1>
    <div v-if="isLoading">読み込み中...</div>
    <div v-else-if="transactionData.length === 0">データがありません。</div>
    <div v-else class="table-container">
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
            <td data-label="登録親番号">{{ data.settlementId || '-' }}</td>
            <td data-label="SKU">{{ data.sku || '-' }}</td>
            <td data-label="分類">{{ data.amountDescription || '-' }}</td>
            <td data-label="個数">{{ data.quantityPurchased ?? 0 }}</td>
            <td data-label="購入日">{{ data.purchaseDate || '-' }}</td>
            <td data-label="購入確定日">{{ data.postedDateTime || '-' }}</td>
            <td data-label="金額">{{ (data.amount || 0).toLocaleString() }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
