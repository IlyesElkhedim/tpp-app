import type { Course } from "@/types/Courses";
import { useEffect, useState, type FC } from "react";
import { Button } from "./ui/button";
import { Trash2 } from "lucide-react";
import { deleteCourse } from "@/services/courseSearchService";
import type { Supervisor } from "@/types/Supervisor";
import { getSupervisorById } from "@/services/supervisorService";

interface CourseCardProps {
  course: Course;
  reloadCourses: () => void;
}

const CourseCard: FC<CourseCardProps> = ({ course, reloadCourses }) => {
  const [supervisor, setSupervisor] = useState<Supervisor>();

  useEffect(() => {
    const fetchSupervisor = async () => {
      const res = await getSupervisorById(course.supervisorId);
      setSupervisor(res);
    };
    fetchSupervisor();
  }, [course]);

  return (
    <div className="border border-gray-300 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex justify-between">
        <h3 className="text-xl font-semibold mb-2">Parcours : {course.name}</h3>
        <h3 className="text-xl font-semibold mb-2">ID : {course.id}</h3>
      </div>

      <p className="text-gray-600">Niveau : {course.level}</p>
      <p className="text-gray-600">
        Responsable de formation : {supervisor?.firstName} {supervisor?.lastName.toUpperCase()}
      </p>
      <div className="flex justify-between">
        <p className="text-gray-600">Année : {course.years}</p>
        <div className="flex gap-1">
          {/* 
          TODO : Uncomment when update functionality is implemented
          <AddCoursesModal
            isUpdate={true}
            onCourseAdded={reloadCourses}
            defaultValues={course}
          /> */}
          <Button
            variant="ghost"
            size="sm"
            onClick={async () => {
              await deleteCourse(course.id);
              reloadCourses();
            }}
          >
            <Trash2 className="mr-2 h-4 w-4 text-red-500" />
          </Button>
        </div>
      </div>
    </div>
  );
};

export default CourseCard;
