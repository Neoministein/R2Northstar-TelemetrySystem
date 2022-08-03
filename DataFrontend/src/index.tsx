import React from 'react';
import ReactDOM from "react-dom/client";
import './index.css';
import MatchListPage from './routes/MatchListPage';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import MatchPage from "./routes/MatchPage";
import HeatmapListPage from "./routes/HeatmapListPage";
import HeatmapMapPage from "./routes/HeatmapMapPage";

const root = ReactDOM.createRoot(document.getElementById("root")!);

root.render(
    <BrowserRouter>
        <Routes>
            <Route path="/" element={<MatchListPage />}/>
            <Route path="/match/" element={<MatchPage />}/>
            <Route path="/heatmap/" element={<HeatmapListPage />}/>
            <Route path="/heatmap/map" element={<HeatmapMapPage/>}/>
            <Route
                path="*"
                element={
                    <main style={{ padding: "1rem" }}>
                        <p>There's nothing here!</p>
                    </main>
                }
            />
        </Routes>
    </BrowserRouter>
);


// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
