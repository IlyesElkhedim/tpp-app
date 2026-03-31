import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { DialogClose, DialogTrigger } from "@radix-ui/react-dialog";
import { useEffect, useRef, useState } from "react";
import { Plus } from "lucide-react";
import { convertFileToJson } from "../../utils/studentsCsvToJson.ts";
import { getCourses } from "@/services/courseSearchService.ts";
import type { Course } from "@/types/Courses.ts";
import { createMultipleStudents } from "@/services/studentService.ts";

interface ImportStudentModalProps {
  onImportSuccess: () => void;
} 

const ImportStudentModal = ({ onImportSuccess }: ImportStudentModalProps) => {
  const [fileName, setFileName] = useState("");
  const [uploadedFile, setUploadedFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [selectedCourse, setSelectedCourse] = useState<number | null>(null);
  const [courses, setCourses] = useState<Course[]>([]);

  useEffect(() => {
    const fetchCourses = async () => {
      const result = await getCourses();
      setCourses(result);
    };
    fetchCourses();
  }, []);

  async function convertToJson() {
    if (!uploadedFile) {
      alert("Aucun fichier importé");
      return;
    }
    try {
      if (!selectedCourse) return;

      const jsonData = await convertFileToJson(uploadedFile);
      await createMultipleStudents(selectedCourse, jsonData);
      onImportSuccess();
    } catch (err) {
      console.error("Erreur parsing xlsx :", err);
    }
  }

  const resetFileUpload = () => {
    setFileName("");
    setUploadedFile(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="outline">
          <Plus />
          Ajouter des étudiants
        </Button>
      </DialogTrigger>
      <DialogContent onClick={resetFileUpload}>
        <DialogHeader>
          <DialogTitle>Ajouter des étudiants</DialogTitle>
        </DialogHeader>
        <DialogDescription></DialogDescription>

        {/* Select formation */}
        <div>
          <Select
            onValueChange={(value) => setSelectedCourse(Number(value))}
            value={selectedCourse?.toString() || undefined}
          >
            <SelectTrigger>
              <SelectValue placeholder="Sélectionner une formation" />
            </SelectTrigger>
            <SelectContent>
              {courses.map((course) => (
                <SelectItem key={course.id} value={course.id.toString()}>
                  {course.level + " " + course.name + " " + course.years}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Upload file */}
        <div className="flex gap-2">
          <input
            ref={fileInputRef}
            type="file"
            accept=".xlsx,.xls,.xlsm,.ods,.csv"
            className="hidden"
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) {
                setFileName(file.name);
                setUploadedFile(file);
              }
            }}
          />

          <Input
            value={fileName}
            placeholder="Choisissez votre fichier"
            readOnly
            onClick={() => fileInputRef.current?.click()}
            className="cursor-pointer"
          />
          <Button
            variant="secondary"
            onClick={() => fileInputRef.current?.click()}
          >
            <span className="cursor-pointer">Upload</span>
          </Button>
        </div>

        <DialogFooter className="mt-6">
          <div className="w-full flex justify-center gap-4">
            <DialogClose asChild onClick={resetFileUpload}>
              <Button className="px-8" onClick={convertToJson}>
                Valider
              </Button>
            </DialogClose>
            <DialogClose asChild onClick={resetFileUpload}>
              <Button variant="outline" className="px-8">
                Annuler
              </Button>
            </DialogClose>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default ImportStudentModal;
