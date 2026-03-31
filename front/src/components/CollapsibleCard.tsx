import {
  ChevronRightIcon,
  FileCheck,
  FileClock,
  FileX,
  UserCheck,
  UserX,
} from "lucide-react";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import type { TimeSlot } from "@/types/TimeSlot";
import type { Attendance, Student } from "@/types/Student";
import { useEffect, useState, type FC } from "react";
import { Separator } from "./ui/separator";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "./ui/table";
import { Button } from "./ui/button";
import NotifyAbsencesModal from "./modal/NotifyAbsencesModal";
import {
  AttendanceType,
  getStudentAttendance,
  updateStudentAttendance,
} from "@/services/attendanceService";
import type { GetWorkSubmissionPayload } from "@/types/WorkSubmission";
import { getWorkSubmission } from "@/services/workSubmissionService";
import { WorkSubmissionBadge } from "./WorkSubmissionBadge";
import { Card, CardContent, CardTitle } from "@/components/ui/card.tsx";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Select,
  SelectContent, SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "./ui/select";

interface CollapsibleCardProps {
  tpp: TimeSlot;
  students: Student[];
  isOpen?: boolean;
  isPast?: boolean;
  onOpenChange: (isOpen: boolean) => void;
}

const CollapsibleCard: FC<CollapsibleCardProps> = ({
  tpp,
  students,
  isOpen,
  isPast,
  onOpenChange,
}) => {
  const [studentWithAttendance, setStudentWithAttendance] = useState<
      Attendance[]
  >([]);
  const [workSubmissions, setWorkSubmissions] = useState<
      GetWorkSubmissionPayload[]
  >([]);
  const [initialAttendanceState, setInitialAttendanceState] = useState<
      Attendance[]>([]);

  useEffect(() => {
    const fetchAttendance = async () => {
      const attendance = await getStudentAttendance(tpp.timeSlotId);

      const mappedAttendance = attendance.map((record) => {
        const studentInfo = students.find(
          (student) => student.studentNumber === record.studentId,
        );
        return {
          ...studentInfo!,
          attended: record.attended,
        };
      });

      setInitialAttendanceState(mappedAttendance.map((s) => ({ ...s })));
      setStudentWithAttendance(mappedAttendance.map((s) => ({ ...s })));

      if (tpp.timeSlotId === undefined) return;

      const presentStudents = mappedAttendance .filter(
          (student) => student.attended === AttendanceType.PRESENT,
      );

      presentStudents.forEach(async (student) => {
        const workSubmission = await getWorkSubmission(
          student.studentNumber,
          tpp.timeSlotId,
        );
        if (workSubmission) {
          setWorkSubmissions((prev) => [...prev, workSubmission]);
        }
      });
    };

    fetchAttendance();
  }, [students, tpp]);

  // Track modified students
  const [modifiedStudentIds, setModifiedStudentIds] = useState<Set<string>>(
    new Set(),
  );

  const handleChangeAttendance = (studentId: string, newAttendance: AttendanceType) => {
    setStudentWithAttendance((prev) =>
        prev.map((s) =>
            s.studentNumber === studentId ? { ...s, attended: newAttendance } : s
        )
    );

    setModifiedStudentIds((prev) => {
      const newSet = new Set(prev);
      newSet.add(studentId);
      return newSet;
    });
  };

  const onValidate = async () => {
    // Send only modified students
    const modifiedStudents = studentWithAttendance.filter((student) => {
      if (modifiedStudentIds.has(student.studentNumber)) {
        const initialStatus = initialAttendanceState.find(
          (s) => s.studentNumber === student.studentNumber,
        )?.attended;
        const finalStatusHasChanged = student.attended !== initialStatus;
        if (finalStatusHasChanged) {
          return student;
        }
      }
    });

    await updateStudentAttendance(tpp.timeSlotId, modifiedStudents);
    setModifiedStudentIds(new Set());
  };

  const [showDetail, setShowDetail] = useState(false);
  const numberOfStudentPresent = studentWithAttendance.filter(
    (student) => student.attended === AttendanceType.PRESENT,
  ).length;
  const numberOfStudentNotPresent = studentWithAttendance.filter(
    (student) => student.attended === AttendanceType.UNJUSTIFIED || student.attended === AttendanceType.JUSTIFIED,
  ).length;
  const numberOfValidReports = workSubmissions.filter(
    (submission) => submission.workSubmissionStatus === "VALID",
  ).length;
  const numberOfInvalidReports = workSubmissions.filter(
    (submission) => submission.workSubmissionStatus === "INVALID",
  ).length;
  const numberOfNotSubmittedReports = studentWithAttendance.filter((student) => {
    const hasSubmitted = workSubmissions.some(
      (ws) => ws.idStudent === student.studentNumber,
    );
    return (student.attended ==  AttendanceType.PRESENT) && !hasSubmitted;
  }).length;

  return (
    <Collapsible
      key={tpp.timeSlotId}
      asChild
      open={isOpen}
      onOpenChange={() => {
        onOpenChange(!isOpen);
      }}
    >
      <li className="flex flex-col w-full gap-2">
        <CollapsibleTrigger className="flex w-full items-center gap-4">
          <ChevronRightIcon className="size-4 transition-transform in-data-[state=open]:rotate-90" />
          <span className="font-medium">
            {tpp.date} - {tpp.startTime} à {tpp.endTime}
          </span>
          {/* <span className="text-sm text-muted-foreground">Salle: {tpp.room}</span> */}
        </CollapsibleTrigger>

        {!isPast || showDetail ? (
          <>
            <CollapsibleContent className="bg-gray-100 p-4 rounded-2xl">
              <div className="flex border-b border-gray-200 mb-6">
                <Tabs defaultValue="attendance">
                  <TabsList variant="line">
                    <TabsTrigger
                      value="attendance"
                      onClick={() => setShowDetail(true)}
                    >
                      Détail des présences
                    </TabsTrigger>
                    <TabsTrigger
                      value="statistics"
                      disabled={!isPast}
                      onClick={() => setShowDetail(false)}
                    >
                      Voir les statistiques
                    </TabsTrigger>
                  </TabsList>
                </Tabs>
              </div>
              <Table>
                <TableHeader>
                  <TableRow className="font-medium">
                    <TableHead className="border">Numéro</TableHead>
                    <TableHead className="border">Nom Prénom</TableHead>
                    <TableHead className="border">Email</TableHead>
                    <TableHead className="border">Présence</TableHead>
                    <TableHead className="border">Compte rendu</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {studentWithAttendance
                    ?.sort((a, b) => a.lastName.localeCompare(b.lastName))
                    .map((student) => (
                      <TableRow key={student.studentNumber}>
                        <TableCell className="border">
                          {student.studentNumber}
                        </TableCell>
                        <TableCell className="border">
                          {student.lastName
                            ? student.lastName.toUpperCase()
                            : ""}{" "}
                          {student.firstName || ""}
                        </TableCell>
                        <TableCell className="border">
                          {student.email}
                        </TableCell>
                        <TableCell className="border w-30">
                          <Select defaultValue={student.attended}
                                  value={student.attended}
                                  onValueChange={(v) => handleChangeAttendance(student.studentNumber, v as AttendanceType)}
                          >
                            <SelectTrigger className={`h-8 text-sm w-[180px] ${
                                student.attended === AttendanceType.PRESENT
                                    ? "bg-green-100 text-green-800"
                                    : student.attended === AttendanceType.JUSTIFIED
                                        ? "bg-yellow-100 text-yellow-800"
                                        : "bg-red-100 text-red-800"
                            }`}>
                              <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                              <SelectGroup>
                                <SelectItem value={AttendanceType.PRESENT}>
                                  Présent
                                </SelectItem>
                                <SelectItem value={AttendanceType.JUSTIFIED}>
                                  Absent (justifié)
                                </SelectItem>
                                <SelectItem value={AttendanceType.UNJUSTIFIED}>
                                  Absent (injustifié)
                                </SelectItem>
                              </SelectGroup>
                            </SelectContent>
                          </Select>
                        </TableCell>
                        <TableCell
                            className={`border ${
                              student.attended !== AttendanceType.PRESENT ? "bg-gray-400" : ""
                            }`}
                        >
                          {student.attended === AttendanceType.PRESENT && (
                              <WorkSubmissionBadge
                                  workSubmissions={workSubmissions}
                                  tpp={tpp}
                                  studentNumber={student.studentNumber}
                              />
                          )}
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>

              <div className="flex w-full justify-between">
                <NotifyAbsencesModal
                  absents={studentWithAttendance.filter(
                    (student) => student.attended != AttendanceType.PRESENT,
                  )}
                  timeslot={tpp}
                />
                {modifiedStudentIds.size > 0 && (
                  <Button
                    className="mt-4"
                    variant="default"
                    onClick={onValidate}
                  >
                    Valider les présences
                  </Button>
                )}
              </div>
            </CollapsibleContent>
            <Separator className="my-2" />
          </>
        ) : (
          <>
            <CollapsibleContent className="bg-gray-100 p-4 rounded-2xl">
              <div className="flex border-b border-gray-200 mb-6">
                <Tabs defaultValue="statistics">
                  <TabsList variant="line">
                    <TabsTrigger
                      value="attendance"
                      onClick={() => setShowDetail(true)}
                    >
                      Détail des présences
                    </TabsTrigger>
                    <TabsTrigger
                      value="statistics"
                      onClick={() => setShowDetail(false)}
                    >
                      Voir les statistiques
                    </TabsTrigger>
                  </TabsList>
                </Tabs>
              </div>
              <div className="flex justify-center gap-2">
                <Card className="w-full max-w-sm mt-4 pl-2 pr-2 bg-blue-200 shadow-md">
                  <CardTitle className="flex items-center justify-center gap-2">
                    <UserCheck className="w-5 h-5 text-green-500" />
                    Total de présents
                  </CardTitle>
                  <CardContent className="text-center">
                    <span>{numberOfStudentPresent}</span>
                  </CardContent>
                </Card>
                <Card className="w-full max-w-sm mt-4 pl-2 pr-2 bg-blue-200 shadow-md">
                  <CardTitle className="flex items-center justify-center gap-2">
                    <UserX className="w-5 h-5 text-red-500" />
                    Total d'absents
                  </CardTitle>
                  <CardContent className="text-center">
                    <span>{numberOfStudentNotPresent}</span>
                  </CardContent>
                </Card>
              </div>
              <div className="flex flex-col gap-1 mb-4">
                <h2 className="text-xl font-semibold mt-8 mb-2">
                  Nombre de rapports
                </h2>
                <div className="flex w-full gap-4">
                  <Card className="mx-auto w-full bg-green-200 shadow-md">
                    <CardTitle className="flex items-center justify-center gap-2">
                      <FileCheck className="w-5 h-5" />
                      Valides
                    </CardTitle>
                    <CardContent className="text-center">
                      <span>{numberOfValidReports}</span>
                    </CardContent>
                  </Card>
                  <Card className="mx-auto w-full bg-orange-200 shadow-md">
                    <CardTitle className="flex items-center justify-center gap-2">
                      <FileClock className="w-5 h-5" />
                      Invalides
                    </CardTitle>
                    <CardContent className="text-center">
                      <span>{numberOfInvalidReports}</span>
                    </CardContent>
                  </Card>
                  <Card className="mx-auto w-full bg-red-200 shadow-md">
                    <CardTitle className="flex items-center justify-center gap-2">
                      <FileX className="w-5 h-5" />
                      Non soumis
                    </CardTitle>
                    <CardContent className="text-center">
                      <span>{numberOfNotSubmittedReports}</span>
                    </CardContent>
                  </Card>
                </div>
              </div>
            </CollapsibleContent>
            <Separator className="my-2" />
          </>
        )}
      </li>
    </Collapsible>
  );
};

export default CollapsibleCard;
