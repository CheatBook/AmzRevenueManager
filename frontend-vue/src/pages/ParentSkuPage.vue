<script setup lang="ts">
import { ref, onMounted } from "vue";
import { get, post } from "aws-amplify/api";

interface SkuName {
  sku: string;
  japaneseName: string;
  parentSku?: string;
}

const newJapaneseName = ref("");
const parentSkus = ref<SkuName[]>([]);
const message = ref("");
const isLoading = ref(false);

const fetchParentSkus = async () => {
  try {
    const restOperation = get({
      apiName: "AmzRevenueApi",
      path: "/sku-names",
    });
    const { body } = await restOperation.response;
    const data = (await body.json()) as any[];
    if (Array.isArray(data)) {
      parentSkus.value = data.filter((s) => !s.parentSku);
    }
  } catch (error) {
    console.error("親SKU取得エラー:", error);
  }
};

const handleSubmit = async () => {
  if (!newJapaneseName.value) {
    message.value = "日本語名を入力してください。";
    return;
  }

  isLoading.value = true;
  message.value = "親SKU名を保存中です...";
  try {
    const restOperation = post({
      apiName: "AmzRevenueApi",
      path: "/sku-names",
      options: {
        body: {
          sku: `PARENT-${Date.now()}`,
          japaneseName: newJapaneseName.value,
          parentSku: "", // undefined ではなく空文字、またはプロパティ自体を含めない
        },
      },
    });

    const { body } = await restOperation.response;
    const responseData = await body.json();
    console.log("保存成功:", responseData);

    message.value = "親SKU名が正常に登録されました。";
    newJapaneseName.value = "";
    fetchParentSkus();
  } catch (error: any) {
    console.error("保存エラー:", error);
    message.value = `保存に失敗しました: ${error.message || "不明なエラー"}`;
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchParentSkus();
});
</script>

<template>
  <div>
    <div class="card">
      <h2>親SKU登録</h2>
      <form @submit.prevent="handleSubmit" class="parent-sku-form">
        <div class="form-row">
          <div class="form-group">
            <label for="japaneseName">日本語名:</label>
            <input
              type="text"
              id="japaneseName"
              v-model="newJapaneseName"
              :disabled="isLoading"
            />
          </div>
        </div>
        <div class="form-actions">
          <button type="submit" :disabled="isLoading">
            {{ isLoading ? "保存中..." : "親SKU名を保存" }}
          </button>
        </div>
      </form>
      <p v-if="message">{{ message }}</p>
    </div>
    <h3 style="margin-top: 20px">登録済み親SKU一覧</h3>
    <div v-if="parentSkus.length > 0" class="table-container">
      <table>
        <thead>
          <tr>
            <th>SKU</th>
            <th>日本語名</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(item, index) in parentSkus"
            :key="item.sku || `parent-sku-${index}`"
          >
            <td data-label="SKU">{{ item.sku }}</td>
            <td data-label="日本語名">{{ item.japaneseName }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <p v-else>登録されている親SKU名はありません。</p>
  </div>
</template>
