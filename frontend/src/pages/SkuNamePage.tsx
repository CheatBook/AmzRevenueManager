import { useState, useEffect, type ChangeEvent, type FormEvent } from 'react';

interface SkuName {
  sku: string;
  japaneseName: string;
  parentSku?: string; // 親SKUを追加
}

export default function SkuNamePage() {
  const [skuNames, setSkuNames] = useState<SkuName[]>([]);
  const [selectedSku, setSelectedSku] = useState<string>(''); // SKUをプルダウンにするため変更
  const [newJapaneseName, setNewJapaneseName] = useState<string>('');
  const [parentSkus, setParentSkus] = useState<SkuName[]>([]); // 親SKUのリスト
  const [distinctSkus, setDistinctSkus] = useState<string[]>([]); // トランザクションから取得したユニークなSKUのリスト
  const [selectedParentSku, setSelectedParentSku] = useState<string>(''); // 選択された親SKUの状態
  const [message, setMessage] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    fetchSkuNames();
    fetchParentSkus(); // 親SKUのリストも取得
    fetchDistinctSkus(); // ユニークなSKUのリストも取得
  }, []);

  const fetchParentSkus = async () => {
    try {
      const response = await fetch('/api/sku-names/parent-skus'); // 親SKUのみを取得するAPI
      if (response.ok) {
        const data: SkuName[] = await response.json();
        setParentSkus(data);
      } else {
        console.error('親SKUの取得に失敗しました。');
      }
    } catch (error) {
      console.error('親SKU取得エラー:', error);
    }
  };

  const fetchDistinctSkus = async () => {
    try {
      const response = await fetch('/api/sku-names/distinct-skus'); // トランザクションからユニークなSKUを取得するAPI
      if (response.ok) {
        const data: string[] = await response.json();
        setDistinctSkus(data);
      } else {
        console.error('ユニークなSKUの取得に失敗しました。');
      }
    } catch (error) {
      console.error('ユニークなSKU取得エラー:', error);
    }
  };

  const fetchSkuNames = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('/api/sku-names');
      if (response.ok) {
        const data: SkuName[] = await response.json();
        setSkuNames(data);
        setMessage('');
      } else {
        console.error('SKU名の取得に失敗しました。');
        setMessage('SKU名の取得に失敗しました。');
      }
    } catch (error) {
      console.error('SKU名取得エラー:', error);
      setMessage('SKU名の取得中にエラーが発生しました。');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSelectedSkuChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setSelectedSku(event.target.value);
  };

  const handleParentSkuChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setSelectedParentSku(event.target.value);
  };

  const handleJapaneseNameChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNewJapaneseName(event.target.value);
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!selectedSku || !newJapaneseName) { // newSkuをselectedSkuに変更
      setMessage('SKUと日本語名の両方を入力してください。');
      return;
    }

    setIsLoading(true);
    setMessage('SKU名を保存中です...');
    try {
      const response = await fetch('/api/sku-names', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ sku: selectedSku, japaneseName: newJapaneseName, parentSku: selectedParentSku || null }), // newSkuをselectedSkuに変更
      });

      if (response.ok) {
        setMessage('SKU名が正常に保存されました。');
        setSelectedSku(''); // newSkuをselectedSkuに変更
        setNewJapaneseName('');
        setSelectedParentSku(''); // 選択された親SKUもクリア
        fetchSkuNames(); // リストを更新
      } else {
        setMessage(`保存失敗: ${response.statusText}`);
      }
    } catch (error) {
      console.error('保存エラー:', error);
      setMessage('保存中にエラーが発生しました。');
    } finally {
      setIsLoading(false);
    }
  };


  return (
    <div className="card">
      <h2>SKU名管理</h2>
      <form onSubmit={handleSubmit} className="sku-name-form">
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="sku">SKU:</label>
            <select
              id="sku"
              value={selectedSku}
              onChange={handleSelectedSkuChange}
              disabled={isLoading}
              className="form-select"
            >
              <option value="">選択してください</option>
              {distinctSkus.map((sku, index) => (
                <option key={sku || `sku-${index}`} value={sku}>
                  {sku}
                </option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label htmlFor="japaneseName">日本語名:</label>
            <input
              type="text"
              id="japaneseName"
              value={newJapaneseName}
              onChange={handleJapaneseNameChange}
              disabled={isLoading}
            />
          </div>
          <div className="form-group">
            <label htmlFor="parentSku">親SKU:</label>
            <select
              id="parentSku"
              value={selectedParentSku}
              onChange={handleParentSkuChange}
              disabled={isLoading}
            >
              <option value="">選択してください</option>
              {parentSkus.map((parent, index) => (
                <option key={parent.sku || `parent-${index}`} value={parent.sku}>
                  {parent.japaneseName}
                </option>
              ))}
            </select>
          </div>
        </div>
        <div className="form-actions">
          <button type="submit" disabled={isLoading}>
            {isLoading ? '保存中...' : 'SKU名を保存'}
          </button>
        </div>
      </form>
      {message && <p>{message}</p>}

      <h3 style={{ marginTop: '20px' }}>登録済みSKU名一覧</h3>
      {skuNames.length > 0 ? (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>SKU</th>
                <th>日本語名</th>
                <th>親SKU</th>
              </tr>
            </thead>
            <tbody>
              {skuNames.map((item, index) => (
                <tr key={item.sku || `sku-${index}`}>
                  <td data-label="SKU">{item.sku}</td>
                  <td data-label="日本語名">{item.japaneseName}</td>
                  <td data-label="親SKU">
                    {item.parentSku
                      ? parentSkus.find(p => p.sku === item.parentSku)?.japaneseName || item.parentSku
                      : '-'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <p>登録されているSKU名はありません。</p>
      )}
    </div>
  );
}