import { useState, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';

export default function UploadPage() {
  const [selectedFiles, setSelectedFiles] = useState<{ [key: string]: File | null }>({});
  const [messages, setMessages] = useState<{ [key: string]: string }>({});
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [isLoading, setIsLoading] = useState<{ [key: string]: boolean }>({});
  const navigate = useNavigate();

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>, uploadType: string) => {
    if (event.target.files) {
      setSelectedFiles({ ...selectedFiles, [uploadType]: event.target.files[0] });
      setMessages({ ...messages, [uploadType]: '' });
      setErrors({ ...errors, [uploadType]: '' });
    }
  };

  const handleUpload = async (uploadType: 'payment' | 'advertisement' | 'salesDate') => {
    const selectedFile = selectedFiles[uploadType];
    if (!selectedFile) {
      setErrors({ ...errors, [uploadType]: 'ファイルが選択されていません。' });
      return;
    }

    setIsLoading({ ...isLoading, [uploadType]: true });
    setMessages({ ...messages, [uploadType]: 'ファイルをアップロード中です...' });
    setErrors({ ...errors, [uploadType]: '' });
    const formData = new FormData();
    formData.append('file', selectedFile);

    let url = '';
    switch (uploadType) {
      case 'payment':
        url = '/api/sales/upload';
        break;
      case 'advertisement':
        url = '/api/sales/upload-advertisement';
        break;
      case 'salesDate':
        url = '/api/sales/upload-sales-date';
        break;
    }

    try {
      const response = await fetch(url, {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        const responseMessage = await response.text();
        setMessages({ ...messages, [uploadType]: responseMessage });
        setErrors({ ...errors, [uploadType]: '' });
      } else {
        const errorMessage = await response.text();
        setErrors({ ...errors, [uploadType]: errorMessage || `アップロード失敗: ${response.statusText}` });
        setMessages({ ...messages, [uploadType]: '' });
      }
    } catch (err) {
      console.error('アップロードエラー:', err);
      setErrors({ ...errors, [uploadType]: 'アップロード中にエラーが発生しました。' });
      setMessages({ ...messages, [uploadType]: '' });
    } finally {
      setIsLoading({ ...isLoading, [uploadType]: false });
    }
  };

  return (
    <div>
      <div className="card">
        <h2>決済レポートアップロード</h2>
        <p>Amazonペイメントレポート（TSV, CSV形式）をアップロードしてください。</p>
        <input type="file" accept=".csv,.tsv,.txt" onChange={(e) => handleFileChange(e, 'payment')} />
        <button onClick={() => handleUpload('payment')} disabled={isLoading['payment']}>
          {isLoading['payment'] ? '処理中...' : 'アップロードして集計'}
        </button>
        {messages['payment'] && <p style={{ color: 'green' }}>{messages['payment']}</p>}
        {errors['payment'] && <p style={{ color: 'red', border: '1px solid red', padding: '10px' }}>{errors['payment']}</p>}
      </div>
      <div className="card">
        <h2>広告レポートアップロード</h2>
        <p>スポンサープロダクト広告レポート（CSV形式）をアップロードしてください。</p>
        <input type="file" accept=".csv" onChange={(e) => handleFileChange(e, 'advertisement')} />
        <button onClick={() => handleUpload('advertisement')} disabled={isLoading['advertisement']}>
          {isLoading['advertisement'] ? '処理中...' : 'アップロード'}
        </button>
        {messages['advertisement'] && <p style={{ color: 'green' }}>{messages['advertisement']}</p>}
        {errors['advertisement'] && <p style={{ color: 'red', border: '1px solid red', padding: '10px' }}>{errors['advertisement']}</p>}
      </div>
      <div className="card">
        <h2>販売日管理レポートアップロード</h2>
        <p>販売日管理レポート（TXT形式）をアップロードしてください。</p>
        <input type="file" accept=".txt" onChange={(e) => handleFileChange(e, 'salesDate')} />
        <button onClick={() => handleUpload('salesDate')} disabled={isLoading['salesDate']}>
          {isLoading['salesDate'] ? '処理中...' : 'アップロード'}
        </button>
        {messages['salesDate'] && <p style={{ color: 'green' }}>{messages['salesDate']}</p>}
        {errors['salesDate'] && <p style={{ color: 'red', border: '1px solid red', padding: '10px' }}>{errors['salesDate']}</p>}
      </div>
    </div>
  );
}