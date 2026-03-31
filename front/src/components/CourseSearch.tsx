import { Check, Search } from "lucide-react";
import { useState, useEffect, useRef, type FC } from "react";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "./ui/command";
import { cn } from "../lib/utils";
import type { Course } from "../types/Courses";
import { useSelector, useDispatch } from "react-redux";
import { type RootState } from "@/store/store";
import { setCurrentCourse } from "@/store/slice/courseSlice";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { getCourses } from "../services/courseSearchService";

export const CourseSearch: FC = () => {
  const dispatch = useDispatch();
  const currentCourse = useSelector((state: RootState) => state.course.currentCourse);
  const [open, setOpen] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [courses, setCourses] = useState<Course[]>([]);

  useEffect(() => {
    const fetchCourses = async () => {
      const data = await getCourses();
      setCourses(data);
    };
    fetchCourses();
    if (open && inputRef.current) {
      setTimeout(() => {
        inputRef.current?.focus();
      }, 0);
    }
  }, [open]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
      }
    };

    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [open]);

  return (
    <div className="w-full max-w-md relative" ref={containerRef}>
      {open ? (
        <Command className="border rounded-md">
          <CommandInput
            ref={inputRef}
            placeholder="Rechercher un parcours"
            className="h-9"
          />
          <CommandList className="absolute top-full left-0 w-full mt-1 border rounded-md bg-white shadow-lg z-50 max-h-60 overflow-auto">
            <CommandEmpty>Aucun parcours trouvé.</CommandEmpty>
            <CommandGroup>
              {courses.map((course) => {
                const concatenated =
                  course.level + " " + course.name + " " + course.years;
                return (
                  <CommandItem
                    key={course.id}
                    value={course.id.toString()}
                    onSelect={(courseId) => {
                      dispatch(
                        setCurrentCourse(
                          courseId === currentCourse?.id.toString()
                            ? null
                            : course
                        )
                      );
                      setOpen(false);
                    }}
                  >
                    {concatenated}
                    <Check
                      className={cn(
                        "ml-auto",
                        currentCourse?.id === course.id
                          ? "opacity-100"
                          : "opacity-0",
                      )}
                    />
                  </CommandItem>
                );
              })}
            </CommandGroup>
          </CommandList>
        </Command>
      ) : (
        <div className="flex items-center gap-4">
          <Button
            className="flex w-fit p-2 items-center border rounded-md hover:bg-gray-100"
            onClick={() => setOpen(true)}
            variant="ghost"
          >
            <Search className="size-4 text-gray-500" />
          </Button>
          {currentCourse && (
            <Badge className="flex h-full text-sm font-medium bg-blue-100 text-blue-800">
              {currentCourse.level} {currentCourse.name} {currentCourse.years}
            </Badge>
          )}
        </div>
      )}
    </div>
  );
};
