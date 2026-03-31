import { useSelector } from "react-redux";
import { Navigate } from "react-router";
import type { RootState } from "@/store/store";
import type { JSX } from "react/jsx-runtime";

export function PrivateRoute({ children, allowed }: { children: JSX.Element, allowed: ("student" | "supervisor")[] }) {
  const currentUser = useSelector((state: RootState) => state.user.currentUser);

  if (!currentUser) {
    return <Navigate to="/login" replace />;
  }

  // Détection du type
  const isStudent = "studentNumber" in currentUser;
  const isSupervisor = "id" in currentUser;

  if (isStudent && allowed.includes("student")) return children;
  if (isSupervisor && allowed.includes("supervisor")) return children;

  // Redirection si non autorisé
  return <Navigate to={isStudent ? "/student/calendar" : "/calendar"} replace />;
}