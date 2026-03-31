import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { type RootState } from "@/store/store";
import { Search } from "lucide-react";
import type { Student } from "../types/Student";
import { Contracts } from "../types/Contracts";
import { MultiSelect } from "../components/ui/multiSelect";
import ImportStudentModal from "../components/modal/ImportStudentModal";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../components/ui/table";
import { CourseSearch } from "@/components/CourseSearch.tsx";
import { getAllCoursesStudents } from "@/services/studentService";

export const StudentsPage = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [filterContracts, setFilterContracts] = useState<string[]>([]);
  const [search, setSearch] = useState("");
  const [isContractFilterOpen, setIsContractFilterOpen] = useState(false);
  const currentCourse = useSelector(
    (state: RootState) => state.course.currentCourse,
  );

  // Fetch students function to be reused
  const fetchStudents = async () => {
    if (!currentCourse) return;
    const res = await getAllCoursesStudents(currentCourse.id);
    setStudents(res);
  };

  useEffect(() => {
    fetchStudents();
  }, [currentCourse]);

  const contractTypeOptions = Object.values(Contracts).map((value) => ({
    value,
    label: value.replace(/([A-Z])/g, " $1").trim(),
  }));

  const filteredStudents = students.filter((student) => {
    const searchValue = search.toLowerCase();
    const matchesSearch =
      student.studentNumber.toLowerCase().includes(searchValue) ||
      student.firstName.toLowerCase().includes(searchValue) ||
      student.lastName.toLowerCase().includes(searchValue);

    const matchesContract =
      filterContracts.length === 0
        ? true
        : student.contractType !== undefined &&
          filterContracts.includes(student.contractType);

    return matchesSearch && matchesContract;
  });

  return (
    <div className="flex h-full flex-col px-4 py-8">
      <div className="flex w-full justify-between items-center">
        <h2 className="text-2xl font-bold">Gestion des étudiants</h2>
      </div>

      <div className="flex justify-between items-center mt-4">
        <CourseSearch />
        <ImportStudentModal onImportSuccess={fetchStudents} />
      </div>

      {!currentCourse ? (
        <div className="mt-8 text-center text-gray-500 text-lg">
          Sélectionner une promo
        </div>
      ) : (
        <>
          <div className="flex justify-end mb-4 mt-4 gap-4">
            <div className="relative">
              <Search className="absolute left-2 top-3.5 w-4 h-4 text-gray-400" />
              <input
                type="text"
                placeholder="Rechercher un étudiant..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-8 pr-3 py-2 border rounded w-72 focus:outline-none focus:ring-2 focus:ring-gray-400"
              />
            </div>
          </div>

          <div className="overflow-y-auto border rounded">
            <Table className="border border-gray-300 w-full">
              <TableHeader>
                <TableRow className="bg-gray-100">
                  <TableHead className="border px-4 py-2 font-bold text-center">
                    {" "}
                    Numéros étudiants
                  </TableHead>
                  <TableHead className="border px-4 py-2 font-bold text-center">
                    {" "}
                    Noms{" "}
                  </TableHead>
                  <TableHead className="border px-4 py-2 font-bold text-center">
                    {" "}
                    Prénoms{" "}
                  </TableHead>
                  <TableHead className="border px-4 py-2 font-bold text-center">
                    {" "}
                    Emails{" "}
                  </TableHead>
                  <TableHead className="border px-4 py-2 font-bold">
                    <div className="flex items-center justify-center">
                      Types de contrats
                      <MultiSelect
                        options={contractTypeOptions}
                        selected={filterContracts}
                        onChange={setFilterContracts}
                        open={isContractFilterOpen}
                        onOpenChange={setIsContractFilterOpen}
                      />
                    </div>
                  </TableHead>
                  <TableHead className="border px-4 py-2 font-bold text-center">
                    {" "}
                    Dates de début de contrats
                  </TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {filteredStudents.length === 0 ? (
                  <TableRow>
                    <TableCell
                      colSpan={7}
                      className="text-center py-8 text-gray-500"
                    >
                      Aucun étudiant dans cette promo.
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredStudents.map((student) => (
                    <TableRow
                      key={student.studentNumber}
                      className="hover:bg-gray-100"
                    >
                      <TableCell className="border px-4 py-2">
                        {student.studentNumber}
                      </TableCell>
                      <TableCell className="border px-4 py-2">
                        {student.lastName}
                      </TableCell>
                      <TableCell className="border px-4 py-2">
                        {student.firstName
                          ? student.firstName.charAt(0).toUpperCase() +
                            student.firstName.slice(1).toLowerCase()
                          : ""}
                      </TableCell>
                      <TableCell className="border px-4 py-2">
                        {student.email}
                      </TableCell>
                      <TableCell className="border px-4 py-2">
                        {student.contractType}
                      </TableCell>
                      <TableCell className="border px-4 py-2">
                        {student.contractStartDate}
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </div>
        </>
      )}
    </div>
  );
};
