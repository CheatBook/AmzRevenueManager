import { useState, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';

export default function UploadPage() {
  const [selectedFiles, setSelectedFiles] = useState<{ [key: string]: FileList | null }>({});
  const [messages, setMessages] = useState<{ [key: string]: string }>({});
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [isLoading, setIsLoading] = useState<{ [key: string]: boolean }>({});
  const navigate = useNavigate();

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>, uploadType: string) => {
    if (event.target.files) {
      setSelectedFiles({ ...selectedFiles, [uploadType]: event.target.files });
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
    if (uploadType === 'payment' && selectedFile) {
      for (let i = 0; i < selectedFile.length; i++) {
        formData.append('files', selectedFile[i]);
      }
    } else if (selectedFile) {
      formData.append('file', selectedFile[0]);
    }

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
        <div className="upload-area">
          <label htmlFor="payment-upload" className="file-upload-label">
            ファイルを選択
          </label>
          <input id="payment-upload" type="file" accept=".csv,.tsv,.txt" onChange={(e) => handleFileChange(e, 'payment')} multiple />
          <span className="file-name">{selectedFiles['payment'] ? `${selectedFiles['payment'].length} ファイル選択済み` : 'ファイルが選択されていません'}</span>
          <button onClick={() => handleUpload('payment')} disabled={isLoading['payment'] || !selectedFiles['payment']}>
            {isLoading['payment'] ? '処理中...' : 'アップロードして集計'}
          </button>
        </div>
        {messages['payment'] && <p style={{ color: 'green' }}>{messages['payment']}</p>}
        {errors['payment'] && <p style={{ color: 'red', border: '1px solid red', padding: '10px' }}>{errors['payment']}</p>}
      </div>
      <div className="card">
        <h2>広告レポートアップロード</h2>
        <p>スポンサープロダクト広告レポート（CSV形式）をアップロードしてください。</p>
        <div className="upload-area">
          <label htmlFor="advertisement-upload" className="file-upload-label">
            ファイルを選択
          </label>
          <input id="advertisement-upload" type="file" accept=".csv" onChange={(e) => handleFileChange(e, 'advertisement')} />
          <span className="file-name">{selectedFiles['advertisement'] ? selectedFiles['advertisement'][0].name : 'ファイルが選択されていません'}</span>
          <button onClick={() => handleUpload('advertisement')} disabled={isLoading['advertisement'] || !selectedFiles['advertisement']}>
            {isLoading['advertisement'] ? '処理中...' : 'アップロード'}
          </button>
        </div>
        {messages['advertisement'] && <p style={{ color: 'green' }}>{messages['advertisement']}</p>}
        {errors['advertisement'] && <p style={{ color: 'red', border: '1px solid red', padding: '10px' }}>{errors['advertisement']}</p>}
      </div>
      <div className="card">
        <h2>販売日管理レポートアップロード</h2>
        <p>販売日管理レポート（TXT形式）をアップロードしてください。</p>
        <div className="upload-area">
          <label htmlFor="salesDate-upload" className="file-upload-label">
            ファイルを選択
          </label>
          <input id="salesDate-upload" type="file" accept=".txt" onChange={(e) => handleFileChange(e, 'salesDate')} />
          <span className="file-name">{selectedFiles['salesDate'] ? selectedFiles['salesDate'][0].name : 'ファイルが選択されていません'}</span>
          <button onClick={() => handleUpload('salesDate')} disabled={isLoading['salesDate'] || !selectedFiles['salesDate']}>
            {isLoading['salesDate'] ? '処理中...' : 'アップロード'}
          </button>
        </div>
        {messages['salesDate'] && <p style={{ color: 'green' }}>{messages['salesDate']}</p>}
        {errors['salesDate'] && <p style={{ color: 'red', border: '1px solid red', padding: '10px' }}>{errors['salesDate']}</p>}
      </div>
    </div>
  );
}