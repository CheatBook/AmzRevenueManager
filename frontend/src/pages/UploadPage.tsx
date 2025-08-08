import { useState, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';

export default function UploadPage() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [message, setMessage] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigate = useNavigate();

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setSelectedFile(event.target.files[0]);
      setMessage('');
      setError('');
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setError('ファイルが選択されていません。');
      return;
    }

    setIsLoading(true);
    setMessage('ファイルをアップロード中です...');
    setError('');
    const formData = new FormData();
    formData.append('file', selectedFile);

    try {
      // このAPIパスは後でバックエンドを修正する際に有効になります
      const response = await fetch('/api/sales/upload', {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        const responseMessage = await response.text();
        setMessage(responseMessage);
        setError('');
      } else {
        const errorMessage = await response.text();
        setError(errorMessage || `アップロード失敗: ${response.statusText}`);
        setMessage('');
      }
    } catch (err) {
      console.error('アップロードエラー:', err);
      setError('アップロード中にエラーが発生しました。');
      setMessage('');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>レポートアップロード</h2>
      <p>Amazonペイメントレポート（Excel, TSV, CSV形式）をアップロードしてください。</p>
      <input type="file" accept=".csv,.tsv,.txt,.xlsx" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={isLoading}>
        {isLoading ? '処理中...' : 'アップロードして集計'}
      </button>
      {message && <p style={{ color: 'green' }}>{message}</p>}
      {error && <p style={{ color: 'red', border: '1px solid red', padding: '10px' }}>{error}</p>}
    </div>
  );
}