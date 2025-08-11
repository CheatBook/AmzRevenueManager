import { Routes, Route, Link } from 'react-router-dom';
import UploadPage from './pages/UploadPage';
import SkuNamePage from './pages/SkuNamePage'; // SkuNamePageをインポート
import ParentSkuPage from './pages/ParentSkuPage'; // ParentSkuPageをインポート
import DailySummaryPage from './pages/DailySummaryPage'; // DailySummaryPageをインポート
import MonthlySummaryPage from './pages/MonthlySummaryPage';
import PurchasePage from './pages/PurchasePage'; // PurchasePageをインポート
import './App.css';

function App() {
  return (
    <div className="app-container">
      <header className="app-header">
        <h1 className="app-title">Amazon収益レポート管理</h1>
        <nav className="app-nav">
          <ul className="nav-list">
            <li className="nav-item">
              <Link to="/" className="nav-link">レポートアップロード</Link>
            </li>
           <li className="nav-item">
             <Link to="/daily-summary" className="nav-link">日別サマリー</Link>
           </li>
           <li className="nav-item">
             <Link to="/monthly-summary" className="nav-link">月別サマリー</Link>
           </li>
           <li className="nav-item">
              <Link to="/sku-names" className="nav-link">SKU名管理</Link> {/* SKU名管理へのリンクを追加 */}
           </li>
            <li className="nav-item">
              <Link to="/parent-sku-names" className="nav-link">親SKU登録</Link> {/* 親SKU登録へのリンクを追加 */}
            </li>
            <li className="nav-item">
              <Link to="/purchase" className="nav-link">仕入れ登録</Link>
            </li>
          </ul>
        </nav>
      </header>
      <main className="app-main">
        <Routes>
          <Route path="/" element={<UploadPage />} />
          <Route path="/daily-summary" element={<DailySummaryPage />} />
          <Route path="/monthly-summary" element={<MonthlySummaryPage />} />
          <Route path="/sku-names" element={<SkuNamePage />} /> {/* SKU名管理のルーティングを追加 */}
          <Route path="/parent-sku-names" element={<ParentSkuPage />} /> {/* 親SKU登録のルーティングを追加 */}
          <Route path="/purchase" element={<PurchasePage />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
