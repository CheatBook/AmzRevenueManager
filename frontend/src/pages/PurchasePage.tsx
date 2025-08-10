import { useState, useEffect, type ChangeEvent, type FormEvent } from 'react';
import DatePicker, { registerLocale } from 'react-datepicker';
import { ja } from 'date-fns/locale/ja';
import 'react-datepicker/dist/react-datepicker.css';

registerLocale('ja', ja);

interface SkuName {
  sku: string;
  japaneseName: string;
  parentSku: string | null;
}

interface Purchase {
  parentSku: string;
  purchaseDate: string;
  quantity: number;
  amount: number;
  tariff: number;
  unitPrice: number;
}

export default function PurchasePage() {
  const [parentSkus, setParentSkus] = useState<SkuName[]>([]);
  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [selectedParentSku, setSelectedParentSku] = useState<string>('');
  const [purchaseDate, setPurchaseDate] = useState<Date | null>(new Date());
  const [quantity, setQuantity] = useState<number>(0);
  const [amount, setAmount] = useState<number>(0);
  const [tariff, setTariff] = useState<number>(0);
  const [message, setMessage] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [editingPurchase, setEditingPurchase] = useState<Purchase | null>(null);

  const formatNumber = (num: number) => {
    return Math.round(num).toLocaleString();
  };

  const getJapaneseName = (sku: string) => {
    const foundSku = parentSkus.find((s) => s.sku === sku);
    return foundSku ? foundSku.japaneseName : sku;
  };

  const fetchPurchases = async () => {
    try {
      const response = await fetch('/api/purchases');
      if (response.ok) {
        const data: Purchase[] = await response.json();
        setPurchases(data);
      } else {
        setError('仕入れ情報の取得に失敗しました。');
      }
    } catch (err) {
      setError('仕入れ情報の取得中にエラーが発生しました。');
    }
  };

  useEffect(() => {
    const fetchParentSkus = async () => {
      try {
        const response = await fetch('/api/sku-names/parent-skus');
        if (response.ok) {
          const data: SkuName[] = await response.json();
          setParentSkus(data);
          if (data.length > 0) {
            setSelectedParentSku(data[0].sku);
          }
        } else {
          setError('親SKUの取得に失敗しました。');
        }
      } catch (err) {
        setError('親SKUの取得中にエラーが発生しました。');
      }
    };

    fetchParentSkus();
    fetchPurchases();
  }, []);

  const handleEdit = (purchase: Purchase) => {
    setEditingPurchase(purchase);
    setSelectedParentSku(purchase.parentSku);
    setPurchaseDate(new Date(purchase.purchaseDate));
    setQuantity(purchase.quantity);
    setAmount(purchase.amount);
    setTariff(purchase.tariff);
  };

  const handleClearForm = () => {
    setEditingPurchase(null);
    setSelectedParentSku(parentSkus.length > 0 ? parentSkus[0].sku : '');
    setPurchaseDate(new Date());
    setQuantity(0);
    setAmount(0);
    setTariff(0);
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!selectedParentSku || !purchaseDate || quantity <= 0 || amount <= 0) {
      setError('すべての必須項目を入力してください。');
      return;
    }

    setIsLoading(true);
    setMessage(editingPurchase ? '仕入れ情報を更新中です...' : '仕入れ情報を登録中です...');
    setError('');

    const purchaseData = {
      parentSku: selectedParentSku,
      purchaseDate: purchaseDate ? purchaseDate.toISOString().split('T')[0] : '',
      quantity,
      amount,
      tariff,
    };

    try {
      const url = editingPurchase
        ? `/api/purchases/${editingPurchase.parentSku}/${editingPurchase.purchaseDate}`
        : '/api/purchases';
      const method = editingPurchase ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(purchaseData),
      });

      if (response.ok) {
        setMessage(editingPurchase ? '仕入れ情報を更新しました。' : '仕入れ情報を登録しました。');
        handleClearForm();
        fetchPurchases();
      } else {
        const errorMessage = await response.text();
        setError(errorMessage || `処理失敗: ${response.statusText}`);
        setMessage('');
      }
    } catch (err) {
      console.error('処理エラー:', err);
      setError('処理中にエラーが発生しました。');
      setMessage('');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>仕入れ登録</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>
            仕入れ対象 (親SKU):
            <select
              value={selectedParentSku}
              onChange={(e: ChangeEvent<HTMLSelectElement>) => setSelectedParentSku(e.target.value)}
              required
            >
              {parentSkus.map((sku) => (
                <option key={sku.sku} value={sku.sku}>
                  {sku.japaneseName}
                </option>
              ))}
            </select>
          </label>
        </div>
        <div className="form-group">
          <label>
            仕入れ日:
            <DatePicker
              selected={purchaseDate}
              onChange={(date: Date | null) => setPurchaseDate(date)}
              dateFormat="yyyy-MM-dd"
              showYearDropdown
              showMonthDropdown
              dropdownMode="select"
              locale="ja"
              popperPlacement="bottom-start"
              required
            />
          </label>
        </div>
        <div className="form-group">
          <label>
            数量:
            <input
              type="number"
              value={quantity}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setQuantity(Number(e.target.value))}
              required
              min="1"
            />
          </label>
        </div>
        <div className="form-group">
          <label>
            金額:
            <input
              type="number"
              value={amount}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setAmount(Number(e.target.value))}
              required
              min="1"
            />
          </label>
        </div>
        <div className="form-group">
          <label>
            関税:
            <input
              type="number"
              value={tariff}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setTariff(Number(e.target.value))}
            />
          </label>
        </div>
        <button type="submit" disabled={isLoading}>
          {isLoading ? (editingPurchase ? '更新中...' : '登録中...') : (editingPurchase ? '更新' : '登録')}
        </button>
        {editingPurchase && (
          <button type="button" onClick={handleClearForm} style={{ marginLeft: '10px' }}>
            クリア
          </button>
        )}
      </form>
      {message && <p style={{ color: 'green' }}>{message}</p>}
      {error && <p style={{ color: 'red', border: '1px solid red', padding: '10px' }}>{error}</p>}

      <div className="card">
        <h2>仕入れ一覧</h2>
        <table>
          <thead>
            <tr>
              <th>仕入れ日</th>
              <th>親SKU</th>
              <th>数量</th>
              <th>金額</th>
              <th>関税</th>
              <th>単価</th>
              <th>編集</th>
            </tr>
          </thead>
          <tbody>
            {purchases.map((purchase) => (
              <tr key={`${purchase.parentSku}-${purchase.purchaseDate}`}>
                <td>{purchase.purchaseDate}</td>
                <td>{getJapaneseName(purchase.parentSku)}</td>
                <td>{formatNumber(purchase.quantity)}</td>
                <td>{formatNumber(purchase.amount)}</td>
                <td>{formatNumber(purchase.tariff)}</td>
                <td>{formatNumber(purchase.unitPrice)}</td>
                <td>
                  <button onClick={() => handleEdit(purchase)}>編集</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}