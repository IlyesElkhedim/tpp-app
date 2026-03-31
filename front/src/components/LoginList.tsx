import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import { setCurrentUser } from "@/store/slice/userSlice";
import type { Student } from "@/types/Student";
import type { Supervisor } from "@/types/Supervisor";
import { ChevronDown } from "lucide-react";
import { type FC, useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router";

interface LoginListProps {
  title: string;
  users: Student[] | Supervisor[];
  isOpen: boolean;
  onToggle: () => void;
}

const LoginList: FC<LoginListProps> = ({ title, users, isOpen, onToggle }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [selectedEmail, setSelectedEmail] = useState<string>("");

  const handleLogin = () => {
    const user = users.find((u) => u.email === selectedEmail);
    if (!user) return;
    dispatch(setCurrentUser(user));
    if ("studentNumber" in user) {
      navigate("/student/calendar");
    } else {
      navigate("/calendar");
    }
  };

  return (
    <Collapsible
      open={isOpen}
      onOpenChange={onToggle}
      className={`flex flex-1 flex-col rounded-lg  ${
        isOpen ? "bg-blue-50 border-blue-300" : "bg-white border-gray-200"
      }`}
    >
      <CollapsibleTrigger className={`flex w-full px-6 py-4 cursor-pointer align-center justify-between ${isOpen ? "" : "flex-1"}`}>
        <h2
          className={`text-lg font-medium transition-colors duration-200 ${
            isOpen ? "text-blue-700" : "text-gray-800"
          }`}
        >
          {title}
        </h2>
        <ChevronDown
          className={`w-5 h-5 text-sm transition-transform duration-200 ${
            isOpen ? "rotate-180 text-blue-500" : "text-gray-400"
          }`}
        />
      </CollapsibleTrigger>
      <CollapsibleContent className="flex flex-1 ">
        <div className="flex flex-col flex-1 gap-3 px-6 pb-6 pt-2 align-center ">
          <select
            value={selectedEmail}
            onChange={(e) => setSelectedEmail(e.target.value)}
            className="w-full rounded-lg border border-blue-200 bg-white px-3 py-2 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-blue-400"
          >
            <option value="" disabled>
              Sélectionner une personne…
            </option>
            {users.map((user) => (
              <option key={user.email} value={user.email}>
                {user.lastName} {user.firstName}
              </option>
            ))}
          </select>
          <button
            onClick={handleLogin}
            disabled={!selectedEmail}
            className="w-full rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed"
          >
            Se connecter
          </button>
        </div>
      </CollapsibleContent>
    </Collapsible>
  );
};

export default LoginList;
