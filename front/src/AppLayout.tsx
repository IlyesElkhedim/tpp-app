import { Outlet } from "react-router";
import SideMenu from "./components/SideMenu";

export default function AppLayout() {
  return (
    <div className="h-screen flex flex-row overflow-hidden">
      <SideMenu />

      {/* Dynamic content */}
      <main className="flex-1 overflow-y-auto">
        <Outlet />
      </main>
    </div>
  );
}
