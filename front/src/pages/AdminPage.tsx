import CourseCard from "@/components/CourseCard";
import AddCoursesModal from "@/components/modal/CoursesModal";
import { getCourses } from "@/services/courseSearchService";
import { type Course } from "@/types/Courses";
import { useEffect, useState } from "react";

export const AdminPage = () => {
  const [courses, setCourses] = useState<Course[]>([]);

  const fetchCourses = async () => {
    const res = await getCourses();
    setCourses(res);
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  return (
    <div className="p-4 mt-8">
      <div className="flex w-full justify-between items-center">
        <h2 className="text-2xl font-bold">Gestion des Promotions</h2>
        <AddCoursesModal onCourseAdded={fetchCourses} />
      </div>

      <div className="mt-6 space-y-4">
        {courses.map((course, index) => (
          <CourseCard
            key={index}
            course={course}
            reloadCourses={fetchCourses}
          />
        ))}
      </div>
    </div>
  );
};
