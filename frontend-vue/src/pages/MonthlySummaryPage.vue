<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "axios";

interface ParentSkuRevenueForMonth {
  parentSku: string;
  parentSkuJapaneseName: string;
  totalSales: number;
  totalFees: number;
  totalAdCost: number;
  grossProfit: number;
  orderCount: number;
  productCost: number;
}

interface ParentSkuMonthlySummary {
  year: number;
  month: number;
  parentSkuRevenues: ParentSkuRevenueForMonth[];
  monthlyTotal: ParentSkuRevenueForMonth;
}

const summary = ref<ParentSkuMonthlySummary[]>([]);

onMounted(async () => {
  try {
    const response = await axios.get("/api/monthly-summary");
    summary.value = response.data;
  } catch (error) {
    console.error("Error fetching monthly summary:", error);
  }
});
</script>

<template>
  <div>
    <h1>月次親SKU別サマリー</h1>
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>年月</th>
            <th>親SKU</th>
            <th>総売上</th>
            <th>手数料・その他費用</th>
            <th>広告費</th>
            <th>商品代金</th>
            <th>粗利益</th>
            <th>注文数</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="(monthlyData, index) in summary" :key="index">
            <template
              v-for="(item, subIndex) in monthlyData.parentSkuRevenues"
              :key="`${index}-${subIndex}`"
            >
              <tr>
                <td
                  v-if="subIndex === 0"
                  data-label="年月"
                  :rowspan="monthlyData.parentSkuRevenues.length + 1"
                >
                  {{ monthlyData.year }}/{{ monthlyData.month }}
                </td>
                <td data-label="親SKU">
                  {{ item.parentSkuJapaneseName || item.parentSku }}
                </td>
                <td data-label="総売上">
                  {{ item.totalSales.toLocaleString() }}
                </td>
                <td data-label="手数料・その他費用">
                  {{ item.totalFees.toLocaleString() }}
                </td>
                <td data-label="広告費">
                  {{ item.totalAdCost.toLocaleString() }}
                </td>
                <td data-label="商品代金">
                  {{ item.productCost.toLocaleString() }}
                </td>
                <td data-label="粗利益">
                  {{ item.grossProfit.toLocaleString() }}
                </td>
                <td data-label="注文数">
                  {{ item.orderCount.toLocaleString() }}
                </td>
              </tr>
            </template>
            <tr style="font-weight: bold">
              <td data-label="親SKU">合計</td>
              <td data-label="総売上">
                {{ monthlyData.monthlyTotal.totalSales.toLocaleString() }}
              </td>
              <td data-label="手数料・その他費用">
                {{ monthlyData.monthlyTotal.totalFees.toLocaleString() }}
              </td>
              <td data-label="広告費">
                {{ monthlyData.monthlyTotal.totalAdCost.toLocaleString() }}
              </td>
              <td data-label="商品代金">
                {{ monthlyData.monthlyTotal.productCost.toLocaleString() }}
              </td>
              <td data-label="粗利益">
                {{ monthlyData.monthlyTotal.grossProfit.toLocaleString() }}
              </td>
              <td data-label="注文数">
                {{ monthlyData.monthlyTotal.orderCount.toLocaleString() }}
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>
