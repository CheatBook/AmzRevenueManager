<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "axios";

interface SkuName {
  sku: string;
  japaneseName: string;
  parentSku?: string;
}

const skuNames = ref<SkuName[]>([]);
const selectedSku = ref("");
const newJapaneseName = ref("");
const parentSkus = ref<SkuName[]>([]);
const distinctSkus = ref<string[]>([]);
const selectedParentSku = ref("");
const message = ref("");
const isLoading = ref(false);

const fetchParentSkus = async () => {
  try {
    const response = await axios.get("/api/sku-names/parent-skus");
    parentSkus.value = response.data;
  } catch (error) {
    console.error("親SKU取得エラー:", error);
  }
};

const fetchDistinctSkus = async () => {
  try {
    const response = await axios.get("/api/sku-names/distinct-skus");
    distinctSkus.value = response.data;
  } catch (error) {
    console.error("ユニークなSKU取得エラー:", error);
  }
};

const fetchSkuNames = async () => {
  isLoading.value = true;
  try {
    const response = await axios.get("/api/sku-names");
    skuNames.value = response.data;
    message.value = "";
  } catch (error) {
    console.error("SKU名取得エラー:", error);
    message.value = "SKU名の取得に失敗しました。";
  } finally {
    isLoading.value = false;
  }
};

const handleSubmit = async () => {
  if (!selectedSku.value || !newJapaneseName.value) {
    message.value = "SKUと日本語名の両方を入力してください。";
    return;
  }

  isLoading.value = true;
  message.value = "SKU名を保存中です...";
  try {
    const response = await axios.post("/api/sku-names", {
      sku: selectedSku.value,
      japaneseName: newJapaneseName.value,
      parentSku: selectedParentSku.value || null,
    });

    if (response.status === 200 || response.status === 201) {
      message.value = "SKU名が正常に保存されました。";
      selectedSku.value = "";
      newJapaneseName.value = "";
      selectedParentSku.value = "";
      fetchSkuNames();
    }
  } catch (error: any) {
    console.error("保存エラー:", error);
    message.value = `保存失敗: ${error.response?.statusText || "エラーが発生しました"}`;
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchSkuNames();
  fetchParentSkus();
  fetchDistinctSkus();
});
</script>

<template>
  <div class="card">
    <h2>SKU名管理</h2>
    <form @submit.prevent="handleSubmit" class="sku-name-form">
      <div class="form-row">
        <div class="form-group">
          <label for="sku">SKU:</label>
          <select
            id="sku"
            v-model="selectedSku"
            :disabled="isLoading"
            class="form-select"
          >
            <option value="">選択してください</option>
            <option
              v-for="(sku, index) in distinctSkus"
              :key="sku || `sku-${index}`"
              :value="sku"
            >
              {{ sku }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label for="japaneseName">日本語名:</label>
          <input
            type="text"
            id="japaneseName"
            v-model="newJapaneseName"
            :disabled="isLoading"
          />
        </div>
        <div class="form-group">
          <label for="parentSku">親SKU:</label>
          <select
            id="parentSku"
            v-model="selectedParentSku"
            :disabled="isLoading"
          >
            <option value="">選択してください</option>
            <option
              v-for="(parent, index) in parentSkus"
              :key="parent.sku || `parent-${index}`"
              :value="parent.sku"
            >
              {{ parent.japaneseName }}
            </option>
          </select>
        </div>
      </div>
      <div class="form-actions">
        <button type="submit" :disabled="isLoading">
          {{ isLoading ? "保存中..." : "SKU名を保存" }}
        </button>
      </div>
    </form>
    <p v-if="message">{{ message }}</p>

    <h3 style="margin-top: 20px">登録済みSKU名一覧</h3>
    <div v-if="skuNames.length > 0" class="table-container">
      <table>
        <thead>
          <tr>
            <th>SKU</th>
            <th>日本語名</th>
            <th>親SKU</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(item, index) in skuNames"
            :key="item.sku || `sku-${index}`"
          >
            <td data-label="SKU">{{ item.sku }}</td>
            <td data-label="日本語名">{{ item.japaneseName }}</td>
            <td data-label="親SKU">
              {{
                item.parentSku
                  ? parentSkus.find((p) => p.sku === item.parentSku)
                      ?.japaneseName || item.parentSku
                  : "-"
              }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <p v-else>登録されているSKU名はありません。</p>
  </div>
</template>

<style scoped>
.sku-name-form .form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  margin-bottom: 1.5rem;
}

.sku-name-form .form-group {
  min-width: 150px;
}

.sku-name-form .form-actions {
  display: flex;
  gap: 1rem;
}
</style>
