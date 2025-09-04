import React, { useEffect, useState } from 'react';
import axios from 'axios';

interface TransactionData {
  settlementId: string;
  sku: string;
  amountDescription: string;
  quantityPurchased: number;
  purchaseDate: string;
  postedDateTime: string;
  amount: number;
}

const TransactionDataPage: React.FC = () => {
  const [transactionData, setTransactionData] = useState<TransactionData[]>([]);

  useEffect(() => {
    axios.get('/api/transaction-data')
      .then(response => {
        setTransactionData(response.data);
      })
      .catch(error => {
        console.error('Error fetching transaction data:', error);
      });
  }, []);

  return (
    <div>
      <h1>トランザクションデータ一覧</h1>
      <table>
        <thead>
          <tr>
            <th>登録親番号</th>
            <th>SKU</th>
            <th>分類</th>
            <th>個数</th>
            <th>購入日</th>
            <th>購入確定日</th>
            <th>金額</th>
          </tr>
        </thead>
        <tbody>
          {transactionData.map((data, index) => (
            <tr key={index}>
              <td>{data.settlementId}</td>
              <td>{data.sku}</td>
              <td>{data.amountDescription}</td>
              <td>{data.quantityPurchased}</td>
              <td>{data.purchaseDate}</td>
              <td>{data.postedDateTime}</td>
              <td>{data.amount}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TransactionDataPage;