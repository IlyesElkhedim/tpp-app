import {
  Card,
  CardContent,
  CardTitle,
} from "../components/ui/card"
import {DataTable} from "../components/ui/data-table";
import {CourseSearch} from "../components/CourseSearch";
import {StudentsStatisticsColumns} from "../components/StudentsStatisticsColumns";
import type {RootState} from "@/store/store.ts";
import {useEffect, useState} from "react";
import {useSelector} from "react-redux";
import type {Student} from "@/types/Student.ts";
import type {StudentStatistics} from "@/types/StudentStatistics";
import type {TimeSlot} from "@/types/TimeSlot.ts";
import {AttendanceType, getStudentAttendance} from "@/services/attendanceService";
import {getAllCoursesStudents} from "@/services/studentService";
import {getTimeSlotsBetweenDates} from "@/services/timeSlotService";
import {getWorkSubmission} from "@/services/workSubmissionService.ts";
import {FileCheck, FileClock, FileX} from "lucide-react";


export const StatisticsPage = () => {

  const [students, setStudents] = useState<Student[]>([]);
  const [tppList, setTppList] = useState<TimeSlot[]>([]);
  const [studentStatistics, setStudentStatistics] = useState<StudentStatistics[]>([]);
  const [fullAttendanceSessions, setFullAttendanceSessions] = useState(0);

  const currentUser = useSelector((state: RootState) => state.user.currentUser);
  const isStudent = currentUser != null && "studentNumber" in currentUser;
  const currentStudent = isStudent ? currentUser : null;
  const currentCourse = useSelector(
      (state: RootState) => state.course.currentCourse,
  );

  const averageAttendanceStudents = studentStatistics.length > 0
    ? studentStatistics.reduce((acc, curr) => acc + curr.attendanceRate, 0) / studentStatistics.length
    : 0;
  const averageJustifiedAbsencesStudents = studentStatistics.length > 0
    ? studentStatistics.reduce((acc, curr) => acc + curr.justifiedAbsences, 0) / studentStatistics.length
    : 0;
  const averageUnjustifiedAbsencesStudents = studentStatistics.length > 0
    ? studentStatistics.reduce((acc, curr) => acc + curr.unjustifiedAbsences, 0) / studentStatistics.length
    : 0;

  const averageAttendance = studentStatistics.find(
      (s) => s.studentNumber === currentStudent?.studentNumber
  )?.attendanceRate ?? 0;
  const averageJustifiedAbsences = studentStatistics.find(
      (s) => s.studentNumber === currentStudent?.studentNumber
  )?.justifiedAbsences ?? 0;
  const averageUnjustifiedAbsences = studentStatistics.find(
      (s) => s.studentNumber === currentStudent?.studentNumber
  )?.unjustifiedAbsences ?? 0;
  const studentPresentCount = studentStatistics.find(
      (s) => s.studentNumber === currentStudent?.studentNumber
  )? Math.round((averageAttendance / 100) * tppList.length)
      : 0;

  useEffect(() => {
    if (!currentCourse) {
      console.log("currentCourse", currentCourse)
      return;
    }

    const fetchStudents = async () => {
      const res = await getAllCoursesStudents(currentCourse!.id);
      setStudents(res);
    };
    fetchStudents();

    const fetchTimeSlots = async () => {
      const [startYear, endYear] = currentCourse.years.split("-");
      const startDate = `${startYear}-09-01`;
      const endDate = `${endYear}-08-31`;
      const tpps = await getTimeSlotsBetweenDates(
          startDate,
          endDate,
          currentCourse.id,
      );
      setTppList(tpps);
    };
    fetchTimeSlots();
  }, [currentCourse]);

  useEffect(() => {
    const fetchAttendance = async () => {
      if (students.length === 0 || tppList.length === 0) return;

      const statsMap: Record<string, StudentStatistics> = {};
      students.forEach(s => {
        statsMap[s.studentNumber] = {
          studentNumber: s.studentNumber,
          firstName: s.firstName,
          lastName: s.lastName,
          email: s.email,
          attendanceRate: 0,
          justifiedAbsences: 0,
          unjustifiedAbsences: 0,
          validReports: 0,
          invalidReports: 0,
          notSubmittedReports: 0,
        };
      });
      let sessionsWithFullAttendance = 0;

      for (const tpp of tppList) {
        const attendance = await getStudentAttendance(tpp.timeSlotId);

        let presentCount = 0;

        for (const res of attendance) {
          const studentStat = statsMap[res.studentId];
          if (!studentStat) return;

          if (res.attended === AttendanceType.PRESENT) {
            studentStat.attendanceRate += 1;
            presentCount += 1;

            try {
              const submission = await getWorkSubmission(res.studentId, tpp.timeSlotId);
              if (submission.workSubmissionStatus === "VALID") {
                studentStat.validReports += 1;
              } else if (submission.workSubmissionStatus === "INVALID") {
                studentStat.invalidReports += 1;
              }
            } catch {
              studentStat.notSubmittedReports += 1;
            }
          } else if (res.attended === AttendanceType.JUSTIFIED) {
            studentStat.justifiedAbsences += 1;
          } else {
            studentStat.unjustifiedAbsences += 1;
          }
        }
        if (presentCount === students.length) {
          sessionsWithFullAttendance += 1;
        }
      }

      Object.values(statsMap).forEach((stat) => {
        stat.attendanceRate = tppList.length > 0
          ? Math.round((stat.attendanceRate / tppList.length) * 100)
          : 0;
      });

      setStudentStatistics(Object.values(statsMap));
      setFullAttendanceSessions(sessionsWithFullAttendance);
    };

    fetchAttendance();
  }, [students, tppList]);

  return (
      <div className="p-4 mt-8 pb-20">
        <div className="flex w-full justify-between items-center">
          <h2 className="text-2xl font-bold">Statistiques</h2>
        </div>

        {!isStudent && (
          <CourseSearch/>
        )}

        <div className="grid grid-cols-4 gap-2 max-w-4xl mx-auto mt-2">
          <Card className="mx-auto w-full max-w-sm mt-8 pl-2 pr-2 bg-blue-200 shadow-md">
            {!isStudent ? (
                <>
                  <CardTitle className="text-center">Taux moyen de présence des alternants</CardTitle>
                  <CardContent className="text-center">
                    <div>{averageAttendanceStudents.toFixed(2)} %</div>
                  </CardContent>
                </>
            ) : (
                <>
                  <CardTitle className="text-center">Taux de présence en cours de TPP</CardTitle>
                  <CardContent className="text-center">
                    <div>{averageAttendance.toFixed(2)} %</div>
                  </CardContent>
                </>
            )}
          </Card>

          <Card className="mx-auto w-full max-w-sm mt-8 pl-2 pr-2 bg-blue-200 shadow-md">
            {!isStudent ? (
                <>
                  <CardTitle className="text-center">Nombre moyen d'absences justifiées</CardTitle>
                  <CardContent className="text-center">
                    <div>{averageJustifiedAbsencesStudents.toFixed(0)}</div>
                  </CardContent>
                </>
            ) : (
                <>
                  <CardTitle className="text-center">Nombre d'absences justifiées</CardTitle>
                  <CardContent className="text-center">
                    <div>{averageJustifiedAbsences.toFixed(0)}</div>
                  </CardContent>
                </>
            )}
          </Card>

          <Card className="mx-auto w-full max-w-sm mt-8 pl-2 pr-2 bg-blue-200 shadow-md">
            {!isStudent ? (
                <>
                  <CardTitle className="text-center">Nombre moyen d'absences injustifiées</CardTitle>
                  <CardContent className="text-center">
                    <div>{averageUnjustifiedAbsencesStudents.toFixed(0)}</div>
                  </CardContent>
                </>
            ) : (
                <>
                  <CardTitle className="text-center">Nombre d'absences injustifiées</CardTitle>
                  <CardContent className="text-center">
                    <div>{averageUnjustifiedAbsences.toFixed(0)}</div>
                  </CardContent>
                </>
            )}
          </Card>

          <Card className="mx-auto w-full max-w-sm mt-8 pl-2 pr-2 bg-blue-200 shadow-md">
            {!isStudent ? (
                <>
                  <CardTitle className="text-center">Nombre de séances de TPP avec effectif complet </CardTitle>
                  <CardContent className="text-center">
                    <span>{fullAttendanceSessions} / {tppList.length}</span>
                  </CardContent>
                </>
            ) : (
                <>
                  <CardTitle className="text-center">Nombre de séances de TPP effectuées</CardTitle>
                  <CardContent className="text-center">
                    <span>{studentPresentCount} / {tppList.length}</span>
                  </CardContent>
                </>
            )}
          </Card>
        </div>

        {!isStudent ? (
          <div>
            <>
              <div className="container mx-auto py-10">
                <DataTable columns={StudentsStatisticsColumns} data={studentStatistics}/>
              </div>
            </>
          </div>
        ) : (
            <>
            <div className="flex flex-col gap-1 mb-4">
              <h2 className="text-xl font-semibold mt-16 mb-2 align-center mx-auto">
                Nombre de rapports
              </h2>
              <div className="flex w-full gap-3 mt-4 max-w-2xl align-center mx-auto">
                <Card className="mx-auto w-full bg-green-200 shadow-md">
                  <CardTitle className="flex items-center justify-center gap-2">
                    <FileCheck className="w-5 h-5"/>
                    Valides
                  </CardTitle>
                  <CardContent className="text-center">
                    <span>{studentStatistics.find(s => s.studentNumber === currentStudent?.studentNumber)?.validReports ?? 0}</span>
                  </CardContent>
                </Card>
                <Card className="mx-auto w-full bg-orange-200 shadow-md">
                  <CardTitle className="flex items-center justify-center gap-2">
                    <FileClock className="w-5 h-5"/>
                    Invalides
                  </CardTitle>
                  <CardContent className="text-center">
                    <span>{studentStatistics.find(s => s.studentNumber === currentStudent?.studentNumber)?.invalidReports ?? 0}</span>
                  </CardContent>
                </Card>
                <Card className="mx-auto w-full bg-red-200 shadow-md">
                  <CardTitle className="flex items-center justify-center gap-2">
                    <FileX className="w-5 h-5"/>
                    Non soumis
                  </CardTitle>
                  <CardContent className="text-center">
                    <span>{studentStatistics.find(s => s.studentNumber === currentStudent?.studentNumber)?.notSubmittedReports ?? 0}</span>
                  </CardContent>
                </Card>
              </div>
            </div>
            </>
        )}
      </div>
  );
};