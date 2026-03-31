import { Avatar } from "./ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from "./ui/dropdown-menu";
import { useNavigate } from "react-router";
import { useDispatch, useSelector } from "react-redux";
import type { RootState } from "@/store/store";
import { clearCurrentUser } from "@/store/slice/userSlice";
import { ChevronDown, ChevronUp } from "lucide-react";

const Profile = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const currentUser = useSelector((state: RootState) => state.user.currentUser);

  const title = currentUser
    ? "studentNumber" in currentUser
      ? "Étudiant"
      : "Professeur"
    : "Invité";

  const displayName = currentUser
    ? `${currentUser.firstName} ${currentUser.lastName}`
    : "";

  const email = currentUser?.email ?? "";

  const handleLogout = () => {
    dispatch(clearCurrentUser());
    navigate("/login");
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <div className="flex items-center gap-2 cursor-pointer p-2 mt-8 bg-white w-full rounded-xl justify-between">
          <div className="flex items-center gap-2">
            <Avatar className="w-8 h-8 rounded-full">
              <img src="/logo.png" alt="Logo" />
            </Avatar>
            <div className="flex flex-col">
              <span className="text-sm font-medium">
                {displayName || title}
              </span>
              <span className="text-xs text-gray-500">{email}</span>
            </div>
          </div>
          <div className="flex flex-col">
            <ChevronUp className="w-4 h-4 text-gray-400" />
            <ChevronDown className="w-4 h-4 text-gray-400" />
          </div>
        </div>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-48">
        <div className="px-4 py-2 border-b">
          <p className="text-sm font-medium">{displayName}</p>
          <p className="text-xs text-gray-500">{title}</p>
          <p className="text-xs text-gray-400">{email}</p>
        </div>
        <p
          className="px-4 py-2 text-sm text-red-600 cursor-pointer hover:bg-gray-100"
          onClick={handleLogout}
        >
          Déconnexion
        </p>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default Profile;
