import "./index.css";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { createRoot } from "react-dom/client";
import AppLayout from "./AppLayout";
import { CalendarPage } from "./pages/CalendarPage";
import { StudentsPage } from "./pages/StudentsPage";
import { TppPage } from "./pages/TimeSlotPage";
import { AdminPage } from "./pages/AdminPage";
import { Provider } from "react-redux";
import { store } from "@/store/store";
import { PrivateRoute } from "./components/PrivateRoute";
import { LoginPage } from "./pages/LoginPage";
import WorkSubmissionPage from "./pages/WorkSubmissionPage";
import {StatisticsPage} from "@/pages/StatisticsPage.tsx";

createRoot(document.getElementById("root")!).render(
  <Provider store={store}>
    <BrowserRouter>
      <Routes>
        {/* Student routes */}
        <Route
          path="/student"
          element={
            <PrivateRoute allowed={["student"]}>
                <AppLayout />
            </PrivateRoute>
          }
        >
          <Route path="calendar" element={<CalendarPage />} />
          <Route
            path="work-submission/:tppId"
            element={<WorkSubmissionPage />}
          />
          <Route path="statistics" element={<StatisticsPage />} />
        </Route>

        {/* Supervisor routes */}
        <Route
          element={
            <PrivateRoute allowed={["supervisor"]}>
              <AppLayout />
            </PrivateRoute>
          }
        >
          <Route path="/calendar" element={<CalendarPage />} />
          <Route path="/listStudents" element={<StudentsPage />} />
          <Route path="/tpp" element={<TppPage />} />
          <Route path="/statistics" element={<StatisticsPage />} />
          <Route path="/admin" element={<AdminPage />} />
        </Route>

        {/* Login page */}
        <Route path="/login" element={<LoginPage />} />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/calendar" replace />} />
      </Routes>
    </BrowserRouter>
  </Provider>,
);
