import { useState, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';

export default function UploadPage() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [message, setMessage] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigate = useNavigate();

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setSelectedFile(event.target.files[0]);
      setMessage('');
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setMessage('ファイルが選択されていません。');
      return;
    }

    setIsLoading(true);
    setMessage('ファイルをアップロード中です...');
    const formData = new FormData();
    formData.append('file', selectedFile);

    try {
      // このAPIパスは後でバックエンドを修正する際に有効になります
      const response = await fetch('/api/sales/upload', {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        setMessage('アップロード成功！結果ページに移動します。');
        // 成功したらサマリーページに遷移
        navigate('/summary');
      } else {
        setMessage(`アップロード失敗: ${response.statusText}`);
      }
    } catch (error) {
      console.error('アップロードエラー:', error);
      setMessage('アップロード中にエラーが発生しました。');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>レポートアップロード</h2>
      <p>Amazonペイメントレポート（TSV形式 or CSV形式）をアップロードしてください。</p>
      <input type="file" accept=".csv,.tsv,.txt" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={isLoading}>
        {isLoading ? '処理中...' : 'アップロードして集計'}
      </button>
      {message && <p>{message}</p>}
    </div>
  );
}