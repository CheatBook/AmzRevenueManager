import { useState, useEffect, type ChangeEvent, type FormEvent } from 'react';

interface SkuName {
  sku: string;
  japaneseName: string;
}
 
export default function ParentSkuPage() {
  const [newJapaneseName, setNewJapaneseName] = useState<string>('');
  const [parentSkus, setParentSkus] = useState<SkuName[]>([]); // 親SKUのリスト
  const [message, setMessage] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    fetchParentSkus();
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

  const handleJapaneseNameChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNewJapaneseName(event.target.value);
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!newJapaneseName) {
      setMessage('日本語名を入力してください。');
      return;
    }

    setIsLoading(true);
    setMessage('親SKU名を保存中です...');
    try {
      const response = await fetch('/api/sku-names/parent-sku', { // 親SKU登録用の新しいエンドポイント
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ japaneseName: newJapaneseName }), // 日本語名のみを送信
      });

      if (response.ok) {
        setMessage('親SKU名が正常に登録されました。');
        setNewJapaneseName('');
        fetchParentSkus(); // リストを更新
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
    <>
      <div className="card">
        <h2>親SKU登録</h2>
        <form onSubmit={handleSubmit}>
          <div>
            <label htmlFor="japaneseName">日本語名:</label>
            <input
              type="text"
              id="japaneseName"
              value={newJapaneseName}
              onChange={handleJapaneseNameChange}
              disabled={isLoading}
            />
          </div>
          <button type="submit" disabled={isLoading}>
            {isLoading ? '保存中...' : '親SKU名を保存'}
          </button>
        </form>
        {message && <p>{message}</p>}
      </div>
      <h3 style={{ marginTop: '20px' }}>登録済み親SKU一覧</h3>
      {parentSkus.length > 0 ? (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>SKU</th>
                <th>日本語名</th>
              </tr>
            </thead>
            <tbody>
              {parentSkus.map((item, index) => (
                <tr key={item.sku || `parent-sku-${index}`}>
                  <td data-label="SKU">{item.sku}</td>
                  <td data-label="日本語名">{item.japaneseName}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <p>登録されている親SKU名はありません。</p>
      )}
    </>
  );
}