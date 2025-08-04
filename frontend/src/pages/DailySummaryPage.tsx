import { useState, useEffect } from 'react';

// バックエンドのDTOに対応する型
interface ParentSkuRevenueForDaily {
  parentSkuName: string;
  totalRevenue: number;
  totalCommission: number;
  grossProfit: number;
  transactionCount: number;
}

interface DailySummaryWithParentSku {
  date: string;
  parentSkuSummaries: ParentSkuRevenueForDaily[];
  dailyTotal: ParentSkuRevenueForDaily;
}

// 数値を日本円形式にフォーマットするヘルパー関数
const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY' }).format(value);
};

export default function DailySummaryPage() {
  const [summaries, setSummaries] = useState<DailySummaryWithParentSku[]>([]);
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [message, setMessage] = useState<string>('データを読み込んでいます...');
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const fetchSummaries = async () => {
    setIsLoading(true);
    setMessage('データを読み込んでいます...');
    try {
      const params = new URLSearchParams();
      if (startDate) params.append('startDate', startDate);
      if (endDate) params.append('endDate', endDate);

      const response = await fetch(`/api/sales/parent-sku-daily-summary?${params.toString()}`);
      if (response.ok) {
        const data: DailySummaryWithParentSku[] = await response.json();
        setSummaries(data);
        if (data.length === 0) {
          setMessage('データがありません。');
        } else {
          setMessage('');
        }
      } else {
        console.error('サマリーの取得に失敗しました。');
        setMessage('サマリーの取得に失敗しました。');
        setSummaries([]);
      }
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
        <h2>親SKU別・日別収益サマリー</h2>
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
        {summaries.length > 0 ? (
          <table className="daily-summary-table">
            <thead>
              <tr>
                <th>日付</th>
                <th>親SKU名</th>
                <th>総売上</th>
                <th>手数料・その他費用</th>
                <th>粗利益</th>
                <th>注文数</th>
              </tr>
            </thead>
            <tbody>
              {summaries.flatMap((dailySummary) => [
                ...dailySummary.parentSkuSummaries.map((parentSkuSummary, index) => (
                  <tr key={`${dailySummary.date}-${parentSkuSummary.parentSkuName}`}>
                    {index === 0 && (
                      <td rowSpan={dailySummary.parentSkuSummaries.length + 1}>
                        {dailySummary.date}
                      </td>
                    )}
                    <td>{parentSkuSummary.parentSkuName}</td>
                    <td>{formatCurrency(parentSkuSummary.totalRevenue)}</td>
                    <td>{formatCurrency(parentSkuSummary.totalCommission)}</td>
                    <td>{formatCurrency(parentSkuSummary.grossProfit)}</td>
                    <td>{parentSkuSummary.transactionCount} 件</td>
                  </tr>
                )),
                <tr key={`${dailySummary.date}-total`} style={{ fontWeight: 'bold' }}>
                  <td>合計</td>
                  <td>{formatCurrency(dailySummary.dailyTotal.totalRevenue)}</td>
                  <td>{formatCurrency(dailySummary.dailyTotal.totalCommission)}</td>
                  <td>{formatCurrency(dailySummary.dailyTotal.grossProfit)}</td>
                  <td>{dailySummary.dailyTotal.transactionCount} 件</td>
                </tr>
              ])}
            </tbody>
          </table>
        ) : (
          <p className="no-data-message">データがありません。</p>
        )}
      </div>
      {message && <p className="info-message">{message}</p>}
    </div>
  );
}