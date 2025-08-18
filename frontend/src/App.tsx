import { Routes, Route, NavLink } from 'react-router-dom';
import UploadPage from './pages/UploadPage';
import SkuNamePage from './pages/SkuNamePage'; // SkuNamePageをインポート
import ParentSkuPage from './pages/ParentSkuPage'; // ParentSkuPageをインポート
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
              <NavLink to="/" className={({ isActive }) => "nav-link" + (isActive ? " active" : "")}>レポートアップロード</NavLink>
            </li>
           <li className="nav-item">
             <NavLink to="/monthly-summary" className={({ isActive }) => "nav-link" + (isActive ? " active" : "")}>月別サマリー</NavLink>
           </li>
           <li className="nav-item">
              <NavLink to="/sku-names" className={({ isActive }) => "nav-link" + (isActive ? " active" : "")}>SKU名管理</NavLink> {/* SKU名管理へのリンクを追加 */}
           </li>
            <li className="nav-item">
              <NavLink to="/parent-sku-names" className={({ isActive }) => "nav-link" + (isActive ? " active" : "")}>親SKU登録</NavLink> {/* 親SKU登録へのリンクを追加 */}
            </li>
            <li className="nav-item">
              <NavLink to="/purchase" className={({ isActive }) => "nav-link" + (isActive ? " active" : "")}>仕入れ登録</NavLink>
            </li>
          </ul>
        </nav>
      </header>
      <main className="app-main">
        <Routes>
          <Route path="/" element={<UploadPage />} />
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
