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
                                {subIndex === 0 && <td rowSpan={monthlyData.parentSkuRevenues.length + 1}>{monthlyData.year}/{monthlyData.month}</td>}
                                <td>{item.parentSkuJapaneseName || item.parentSku}</td>
                                <td>{item.totalSales.toLocaleString()}</td>
                                <td>{item.totalFees.toLocaleString()}</td>
                                <td>{item.totalAdCost.toLocaleString()}</td>
                                <td>{item.productCost.toLocaleString()}</td>
                                <td>{item.grossProfit.toLocaleString()}</td>
                                <td>{item.orderCount.toLocaleString()}</td>
                            </tr>
                        )),
                        <tr key={`${index}-total`} style={{ fontWeight: 'bold' }}>
                            <td>合計</td>
                            <td>{monthlyData.monthlyTotal.totalSales.toLocaleString()}</td>
                            <td>{monthlyData.monthlyTotal.totalFees.toLocaleString()}</td>
                            <td>{monthlyData.monthlyTotal.totalAdCost.toLocaleString()}</td>
                            <td>{monthlyData.monthlyTotal.productCost.toLocaleString()}</td>
                            <td>{monthlyData.monthlyTotal.grossProfit.toLocaleString()}</td>
                            <td>{monthlyData.monthlyTotal.orderCount.toLocaleString()}</td>
                        </tr>
                    ])}
                </tbody>
            </table>
        </div>
    );
};

export default MonthlySummaryPage;