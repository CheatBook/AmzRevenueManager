import { useState, useEffect } from 'react';

// バックエンドのDTOに対応する型
interface RevenueSummary {
  totalRevenue: number;
  totalCommission: number;
  totalShipping: number;
  totalTax: number;
  grossProfit: number;
  transactionCount: number;
}

interface SkuRevenueSummary {
  sku: string;
  japaneseName: string | null; // 日本語名を追加
  totalRevenue: number;
  totalCommission: number;
  totalShipping: number;
  totalTax: number;
  grossProfit: number;
  transactionCount: number; // order_idのユニーク数
  totalQuantityPurchased: number; // quantity_purchasedの合計
}

interface HierarchicalSkuRevenueSummary {
  parentSummary: SkuRevenueSummary;
  childrenSummaries: SkuRevenueSummary[];
}


// 数値を日本円形式にフォーマットするヘルパー関数
const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY' }).format(value);
};

export default function SummaryPage() {
  const [summary, setSummary] = useState<RevenueSummary | null>(null);
  const [hierarchicalSummaries, setHierarchicalSummaries] = useState<HierarchicalSkuRevenueSummary[]>([]); // 階層的なサマリーの状態を追加
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [message, setMessage] = useState<string>('データを読み込んでいます...');
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const fetchSummaries = async () => {
    setIsLoading(true);
    try {
      const params = new URLSearchParams();
      if (startDate) params.append('startDate', startDate);
      if (endDate) params.append('endDate', endDate);

      // 全体サマリーの取得
      const summaryResponse = await fetch(`/api/sales/summary?${params.toString()}`);
      if (summaryResponse.ok) {
        const data: RevenueSummary = await summaryResponse.json();
        setSummary(data);
      } else {
        console.error('サマリーの取得に失敗しました。');
        setMessage('サマリーの取得に失敗しました。データがまだ処理されていない可能性があります。');
        setSummary(null);
      }

      // 階層的なSKUサマリーの取得
      const hierarchicalSummaryResponse = await fetch(`/api/sales/hierarchical-sku-summary?${params.toString()}`);
      if (hierarchicalSummaryResponse.ok) {
        const data: HierarchicalSkuRevenueSummary[] = await hierarchicalSummaryResponse.json();
        setHierarchicalSummaries(data);
      } else {
        console.error('階層的なSKUサマリーの取得に失敗しました。');
        setMessage(prev => prev + ' 階層的なSKUサマリーの取得に失敗しました。');
        setHierarchicalSummaries([]);
      }


      setMessage('');
    } catch (error) {
      console.error('サマリー取得エラー:', error);
      setMessage('サマリーの取得中にエラーが発生しました。');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchSummaries();
  }, []);

  if (isLoading) {
    return <div className="card"><p>{message}</p></div>;
  }

  return (
    <div className="summary-container">
      <div className="card" style={{ marginTop: '20px' }}>
        <h2>SKU別収益サマリー</h2>
        <div className="date-filter">
          <label htmlFor="startDate">開始日:</label>
          <input
            type="date"
            id="startDate"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
          <label htmlFor="endDate">終了日:</label>
          <input
            type="date"
            id="endDate"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
          <button onClick={fetchSummaries}>集計</button>
        </div>
        {hierarchicalSummaries.length > 0 ? (
          <table className="asin-summary-table">
            <thead>
              <tr>
                <th>日本語名</th>
                <th>総売上</th>
                <th>手数料・その他費用</th>
                <th>送料</th>
                <th>粗利益</th>
                <th>消費税</th>
                <th>注文数</th>
                <th>購入数量</th>
              </tr>
            </thead>
            <tbody>
              {hierarchicalSummaries.map((parentItem) => {
                const parentRow = (
                  <tr key={parentItem.parentSummary.sku} className="parent-row">
                    <td><strong>{parentItem.parentSummary.japaneseName || '未設定'}</strong></td>
                    <td><strong>{formatCurrency(parentItem.parentSummary.totalRevenue)}</strong></td>
                    <td><strong>{formatCurrency(parentItem.parentSummary.totalCommission)}</strong></td>
                    <td><strong>{formatCurrency(parentItem.parentSummary.totalShipping)}</strong></td>
                    <td><strong>{formatCurrency(parentItem.parentSummary.grossProfit)}</strong></td>
                    <td><strong>{formatCurrency(parentItem.parentSummary.totalTax)}</strong></td>
                    <td><strong>{parentItem.parentSummary.transactionCount} 件</strong></td>
                    <td><strong>{parentItem.parentSummary.totalQuantityPurchased} 個</strong></td>
                  </tr>
                );

                const childRows = parentItem.childrenSummaries.map((childItem) => (
                  <tr key={childItem.sku} className="child-row">
                    <td>{childItem.japaneseName || '未設定'}</td>
                    <td>{formatCurrency(childItem.totalRevenue)}</td>
                    <td>{formatCurrency(childItem.totalCommission)}</td>
                    <td>{formatCurrency(childItem.totalShipping)}</td>
                    <td>{formatCurrency(childItem.grossProfit)}</td>
                    <td>{formatCurrency(childItem.totalTax)}</td>
                    <td>{childItem.transactionCount} 件</td>
                    <td>{childItem.totalQuantityPurchased} 個</td>
                  </tr>
                ));

                return [parentRow, ...childRows];
              })}
            </tbody>
          </table>
        ) : (
          <p className="no-data-message">SKU別サマリーデータがありません。</p>
        )}
      </div>
      {message && <p className="info-message">{message}</p>}
    </div>
  );
}