import { Link, useLocation, useNavigate } from "react-router";
import {
  BookOpen,
  CalendarFold,
  ChartLine,
  ChevronUp,
  LockIcon,
  UsersRound,
} from "lucide-react";
import { Collapsible, CollapsibleTrigger } from "./ui/collapsible";
import { Button } from "./ui/button";
import { CollapsibleContent } from "@radix-ui/react-collapsible";
import Profile from "./Profile";
import { useSelector } from "react-redux";
import type { RootState } from "@/store/store";

export default function SideMenu() {
  const currentUser = useSelector((state: RootState) => state.user.currentUser);
  const isStudent = currentUser && "studentNumber" in currentUser;

  const currentNavLink = useLocation().pathname;

  const navigate = useNavigate();

  const pannelNavigation = () => {
    if (currentNavLink === "/admin") {
      navigate("/calendar");
    } else {
      navigate("/admin");
    }
  };

  return (
    <div className="flex flex-col bg-gray-100 items-center w-[20%] px-4 justify-between gap-4">
      <Profile />
      {currentNavLink !== "/admin" && (
        <>
          <div className="flex flex-1 mb-4 w-full">
            <Collapsible className="w-full" open>
              <CollapsibleTrigger asChild className="w-full p-0">
                <Button
                  variant="ghost"
                  className="flex w-full group text-gray-400 justify-between items-center hover:cursor-pointer hover:text-gray-400"
                >
                  Navigation
                  <ChevronUp className="color-gray-400 transition-transform group-data-[state=open]:rotate-180 group-data-[state=closed]:rotate-0" />
                </Button>
              </CollapsibleTrigger>

              <CollapsibleContent className="w-full px-6">
                <nav className="flex gap-1 flex-col">
                  <MenuButton
                    id="calendar"
                    selected={
                      currentNavLink === "/calendar" ||
                      currentNavLink === "/student/calendar"
                    }
                    to="/calendar"
                  >
                    <CalendarFold
                      className="inline w-5 h-5 mr-1"
                      fill={
                        currentNavLink === "/calendar" ||
                        currentNavLink === "/student/calendar"
                          ? "currentColor"
                          : "none"
                      }
                    />
                    Calendrier
                  </MenuButton>

                  {!isStudent && (
                    <MenuButton
                      id="students"
                      selected={currentNavLink === "/listStudents"}
                      to="/listStudents"
                    >
                      <UsersRound
                        className="inline w-5 h-5 mr-1"
                        fill={
                          currentNavLink === "/listStudents"
                            ? "currentColor"
                            : "none"
                        }
                      />
                      Étudiants
                    </MenuButton>
                  )}

                  {!isStudent && (
                    <MenuButton
                      id="tpp"
                      selected={currentNavLink === "/tpp"}
                      to="/tpp"
                    >
                      <BookOpen
                        className="inline w-5 h-5 mr-1"
                        fill={
                          currentNavLink === "/tpp" ? "currentColor" : "none"
                        }
                      />
                      TPP
                    </MenuButton>
                  )}

                  <MenuButton
                    id="statistics"
                    selected={
                      currentNavLink === "/statistics" ||
                      currentNavLink === "/student/statistics"
                    }
                    to={isStudent ? "/student/statistics" : "/statistics"}
                  >
                    <ChartLine
                      className="inline w-5 h-5 mr-1"
                      fill={
                        currentNavLink === "/statistics" ||
                        currentNavLink === "/student/statistics"
                          ? "currentColor"
                          : "none"
                      }
                    />
                    Statistiques
                  </MenuButton>
                </nav>
              </CollapsibleContent>
            </Collapsible>
          </div>
        </>
      )}
      {isStudent ? null : (
        <Button
          className="flex mb-6 items-center w-full"
          onClick={pannelNavigation}
        >
          {currentNavLink === "/admin" ? (
            <p className="flex items-center">
              <LockIcon className="inline w-5 h-5 mr-1" />
              Interface professeur
            </p>
          ) : (
            <p className="flex items-center">
              <LockIcon className="inline w-5 h-5 mr-1" />
              Interface admin
            </p>
          )}
        </Button>
      )}
    </div>
  );
}

interface MenuButtonProps {
  id: string;
  selected: boolean;
  to?: string;
  children: React.ReactNode;
  onClick?: () => void;
}

function MenuButton({ id, selected, to, children, onClick }: MenuButtonProps) {
  const content = <span className="flex items-center w-full">{children}</span>;
  if (to) {
    return (
      <Button
        asChild
        variant="ghost"
        className={`p-1 ${selected ? "bg-white font-bold" : "text-gray-400"}`}
        id={id}
      >
        <Link to={to} onClick={onClick}>
          {content}
        </Link>
      </Button>
    );
  }
  return (
    <Button
      variant="ghost"
      className={`p-1 ${selected ? "bg-white" : ""}`}
      id={id}
      onClick={onClick}
    >
      {content}
    </Button>
  );
}
