import { useState, useMemo, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { type RootState } from "@/store/store";
import ScheduleCalendar from "../components/Calendar";
import getWeekDates from "../utils/weekDate";
import { CalendarNavigation } from "../components/CalendarNavigation";
import { CourseSearch } from "../components/CourseSearch";
import ImportPlanningModal from "../components/modal/ImportPlanningModal";
import { useNavigate } from "react-router";
import { setCurrentCourse } from "@/store/slice/courseSlice";
import { getCourseById } from "@/services/courseSearchService";

export const CalendarPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [referenceDate, setReferenceDate] = useState(new Date());
  const currentCourse = useSelector(
    (state: RootState) => state.course.currentCourse,
  );
  const currentUser = useSelector((state: RootState) => state.user.currentUser);

  const currentWeek = useMemo(
    () => getWeekDates(referenceDate),
    [referenceDate],
  );
  const currentDay = useMemo(() => new Date(), []);

  if (!currentUser) {
    navigate("/login");
    return null;
  }
  const isStudent = currentUser && "studentNumber" in currentUser;
  useEffect(() => {
    const setStudentCourse = async () => {
      console.log(currentUser);
      if (isStudent) {
        const studentCourse = await getCourseById(currentUser.currentCourse);
        dispatch(setCurrentCourse(studentCourse!));
      }
    };
    setStudentCourse();
  }, [currentUser, dispatch, isStudent]);

  return (
    <>
      <div className="w-full h-full flex flex-col p-8 gap-3">
        <div className="flex justify-between">
          {!isStudent && <CourseSearch />}
          {!isStudent && <ImportPlanningModal />}
        </div>

        {(currentCourse && (
          <>
            <div className="flex justify-between items-center mb-6">
              <div className="flex gap-2">
                <span className="text-2xl font-bold">
                  {referenceDate
                    .toLocaleDateString("fr-FR", {
                      month: "long",
                    })
                    .toUpperCase()}
                </span>
                <span className="text-2xl text-gray-600">
                  {referenceDate.toLocaleDateString("fr-FR", {
                    year: "numeric",
                  })}
                </span>
              </div>

              <CalendarNavigation
                referenceDate={referenceDate}
                setReferenceDate={setReferenceDate}
              />
            </div>
            <div className="h-full flex flex-col">
              <ScheduleCalendar
                currentWeek={currentWeek}
                currentDay={currentDay}
                readOnly={isStudent}
              />
            </div>
          </>
        )) || (
          <div className="flex flex-1 justify-center items-center">
            <span className="text-gray-500 text-lg">
              Veuillez sélectionner un parcours pour afficher le calendrier.
            </span>
          </div>
        )}
      </div>
    </>
  );
};
