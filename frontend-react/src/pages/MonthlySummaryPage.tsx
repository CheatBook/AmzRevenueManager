import React, { useEffect, useState } from 'react';

interface ParentSkuRevenueForMonth {
    parentSku: string;
    parentSkuJapaneseName: string;
    totalSales: number;
    totalFees: number;
    totalAdCost: number;
    grossProfit: number;
    orderCount: number;
    productCost: number;
}

interface ParentSkuMonthlySummary {
    year: number;
    month: number;
    parentSkuRevenues: ParentSkuRevenueForMonth[];
    monthlyTotal: ParentSkuRevenueForMonth;
}

const MonthlySummaryPage: React.FC = () => {
    const [summary, setSummary] = useState<ParentSkuMonthlySummary[]>([]);

    useEffect(() => {
        fetch('/api/monthly-summary')
            .then(response => response.json())
            .then(data => setSummary(data));
    }, []);

    return (
        <div>
            <h1>月次親SKU別サマリー</h1>
            <div className="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>年月</th>
                            <th>親SKU</th>
                            <th>総売上</th>
                            <th>手数料・その他費用</th>
                            <th>広告費</th>
                            <th>商品代金</th>
                            <th>粗利益</th>
                            <th>注文数</th>
                        </tr>
                    </thead>
                    <tbody>
                        {summary.flatMap((monthlyData, index) => [
                            ...monthlyData.parentSkuRevenues.map((item, subIndex) => (
                                <tr key={`${index}-${subIndex}`}>
                                    {subIndex === 0 && <td data-label="年月" rowSpan={monthlyData.parentSkuRevenues.length + 1}>{monthlyData.year}/{monthlyData.month}</td>}
                                    <td data-label="親SKU">{item.parentSkuJapaneseName || item.parentSku}</td>
                                    <td data-label="総売上">{item.totalSales.toLocaleString()}</td>
                                    <td data-label="手数料・その他費用">{item.totalFees.toLocaleString()}</td>
                                    <td data-label="広告費">{item.totalAdCost.toLocaleString()}</td>
                                    <td data-label="商品代金">{item.productCost.toLocaleString()}</td>
                                    <td data-label="粗利益">{item.grossProfit.toLocaleString()}</td>
                                    <td data-label="注文数">{item.orderCount.toLocaleString()}</td>
                                </tr>
                            )),
                            <tr key={`${index}-total`} style={{ fontWeight: 'bold' }}>
                                <td data-label="親SKU">合計</td>
                                <td data-label="総売上">{monthlyData.monthlyTotal.totalSales.toLocaleString()}</td>
                                <td data-label="手数料・その他費用">{monthlyData.monthlyTotal.totalFees.toLocaleString()}</td>
                                <td data-label="広告費">{monthlyData.monthlyTotal.totalAdCost.toLocaleString()}</td>
                                <td data-label="商品代金">{monthlyData.monthlyTotal.productCost.toLocaleString()}</td>
                                <td data-label="粗利益">{monthlyData.monthlyTotal.grossProfit.toLocaleString()}</td>
                                <td data-label="注文数">{monthlyData.monthlyTotal.orderCount.toLocaleString()}</td>
                            </tr>
                        ])}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default MonthlySummaryPage;