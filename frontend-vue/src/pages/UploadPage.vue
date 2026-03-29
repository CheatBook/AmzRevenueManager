<script setup lang="ts">
import { ref, reactive } from "vue";
import axios from "axios";

const selectedFiles = reactive<{ [key: string]: FileList | null }>({});
const messages = reactive<{ [key: string]: string }>({});
const errors = reactive<{ [key: string]: string }>({});
const isLoading = reactive<{ [key: string]: boolean }>({});

const handleFileChange = (event: Event, uploadType: string) => {
  const target = event.target as HTMLInputElement;
  if (target.files) {
    selectedFiles[uploadType] = target.files;
    messages[uploadType] = "";
    errors[uploadType] = "";
  }
};

const handleUpload = async (
  uploadType: "payment" | "advertisement" | "salesDate",
) => {
  const selectedFile = selectedFiles[uploadType];
  if (!selectedFile) {
    errors[uploadType] = "ファイルが選択されていません。";
    return;
  }

  isLoading[uploadType] = true;
  messages[uploadType] = "ファイルをアップロード中です...";
  errors[uploadType] = "";

  const formData = new FormData();
  if (
    (uploadType === "payment" || uploadType === "salesDate") &&
    selectedFile
  ) {
    for (let i = 0; i < selectedFile.length; i++) {
      formData.append("files", selectedFile[i]);
    }
  } else if (selectedFile) {
    formData.append("file", selectedFile[0]);
  }

  let url = "";
  switch (uploadType) {
    case "payment":
      url = "/api/sales/upload";
      break;
    case "advertisement":
      url = "/api/sales/upload-advertisement";
      break;
    case "salesDate":
      url = "/api/sales/upload-sales-date";
      break;
  }

  try {
    const response = await axios.post(url, formData);
    messages[uploadType] = response.data;
    errors[uploadType] = "";
  } catch (err: any) {
    console.error("アップロードエラー:", err);
    errors[uploadType] =
      err.response?.data || "アップロード中にエラーが発生しました。";
    messages[uploadType] = "";
  } finally {
    isLoading[uploadType] = false;
  }
};
</script>

<template>
  <div>
    <div class="card">
      <h2>決済レポートアップロード</h2>
      <p>
        Amazonペイメントレポート（TSV, CSV形式）をアップロードしてください。
      </p>
      <div class="upload-area">
        <label for="payment-upload" class="file-upload-label">
          ファイルを選択
        </label>
        <input
          id="payment-upload"
          type="file"
          accept=".csv,.tsv,.txt"
          @change="handleFileChange($event, 'payment')"
          multiple
        />
        <span class="file-name">{{
          selectedFiles["payment"]
            ? `${selectedFiles["payment"].length} ファイル選択済み`
            : "ファイルが選択されていません"
        }}</span>
        <button
          @click="handleUpload('payment')"
          :disabled="isLoading['payment'] || !selectedFiles['payment']"
        >
          {{ isLoading["payment"] ? "処理中..." : "アップロードして集計" }}
        </button>
      </div>
      <p v-if="messages['payment']" style="color: green">
        {{ messages["payment"] }}
      </p>
      <p
        v-if="errors['payment']"
        style="color: red; border: 1px solid red; padding: 10px"
      >
        {{ errors["payment"] }}
      </p>
    </div>

    <div class="card">
      <h2>広告レポートアップロード</h2>
      <p>
        スポンサープロダクト広告レポート（CSV形式）をアップロードしてください。
      </p>
      <div class="upload-area">
        <label for="advertisement-upload" class="file-upload-label">
          ファイルを選択
        </label>
        <input
          id="advertisement-upload"
          type="file"
          accept=".csv"
          @change="handleFileChange($event, 'advertisement')"
        />
        <span class="file-name">{{
          selectedFiles["advertisement"]
            ? selectedFiles["advertisement"][0].name
            : "ファイルが選択されていません"
        }}</span>
        <button
          @click="handleUpload('advertisement')"
          :disabled="
            isLoading['advertisement'] || !selectedFiles['advertisement']
          "
        >
          {{ isLoading["advertisement"] ? "処理中..." : "アップロード" }}
        </button>
      </div>
      <p v-if="messages['advertisement']" style="color: green">
        {{ messages["advertisement"] }}
      </p>
      <p
        v-if="errors['advertisement']"
        style="color: red; border: 1px solid red; padding: 10px"
      >
        {{ errors["advertisement"] }}
      </p>
    </div>

    <div class="card">
      <h2>販売日管理レポートアップロード</h2>
      <p>販売日管理レポート（TXT形式）をアップロードしてください。</p>
      <div class="upload-area">
        <label for="salesDate-upload" class="file-upload-label">
          ファイルを選択
        </label>
        <input
          id="salesDate-upload"
          type="file"
          accept=".txt"
          @change="handleFileChange($event, 'salesDate')"
          multiple
        />
        <span class="file-name">{{
          selectedFiles["salesDate"]
            ? `${selectedFiles["salesDate"].length} ファイル選択済み`
            : "ファイルが選択されていません"
        }}</span>
        <button
          @click="handleUpload('salesDate')"
          :disabled="isLoading['salesDate'] || !selectedFiles['salesDate']"
        >
          {{ isLoading["salesDate"] ? "処理中..." : "アップロード" }}
        </button>
      </div>
      <p v-if="messages['salesDate']" style="color: green">
        {{ messages["salesDate"] }}
      </p>
      <p
        v-if="errors['salesDate']"
        style="color: red; border: 1px solid red; padding: 10px"
      >
        {{ errors["salesDate"] }}
      </p>
    </div>
  </div>
</template>

<style scoped>
.upload-area {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.file-upload-label {
  background-color: var(--secondary);
  color: var(--secondary-foreground);
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius);
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
}

.file-upload-label:hover {
  opacity: 0.9;
}

input[type="file"] {
  display: none;
}

.file-name {
  color: var(--muted-foreground);
}
</style>
