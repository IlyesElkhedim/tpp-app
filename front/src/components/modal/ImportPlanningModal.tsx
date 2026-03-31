import { useState, useRef, type FC, useEffect } from "react";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../../components/ui/dialog";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../../components/ui/select";
import type { Course } from "../../types/Courses";
import { getCourses } from "../../services/courseSearchService";
import {
  importIcsByCourseId,
  importIcsFromUrl,
  importIcsFromFileUpload,
  importTimeSlotsFromExcelUpload
} from "../../services/timeSlotService";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";

const ImportPlanningModal: FC = () => {
  const [fileName, setFileName] = useState("");
  const [courses, setCourses] = useState<Course[]>([]);
  const [selectedCourse, setSelectedCourse] = useState<string | null>(null);
  const [importType, setImportType] = useState<"ade" | "excel">("ade");
  const [adeSubType, setAdeSubType] = useState<"auto" | "url" | "file">("auto");
  const [customUrl, setCustomUrl] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [errors, setErrors] = useState<{
    course?: string;
    file?: string;
    url?: string;
    general?: string;
  }>({});
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    const fetchCourses = async () => {
      const result = await getCourses();
      setCourses(result);
    };
    fetchCourses();
  }, []);

  const onValidate = async () => {
    const newErrors: {
      course?: string;
      file?: string;
      url?: string;
      general?: string;
    } = {};

    if (!selectedCourse) {
      newErrors.course = "Veuillez sélectionner une formation";
    }

    if (importType === "ade" && adeSubType === "url" && !customUrl) {
      newErrors.url = "Veuillez entrer une URL";
    }

    if (importType === "ade" && adeSubType === "file" && !fileName) {
      newErrors.file = "Veuillez choisir un fichier ICS";
    }

    if (importType === "excel" && !fileName) {
      newErrors.file = "Veuillez choisir un fichier Excel";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setErrors({});
    setSuccessMessage("");
    setIsLoading(true);

    try {
      const courseId = parseInt(selectedCourse as string);

      if (importType === "ade") {
        if (adeSubType === "auto") {
          // Automatic URL construction from ADE
          await importIcsByCourseId(courseId);
          setSuccessMessage(
            "Planning importé avec succès depuis ADE pour la formation sélectionnée"
          );
        } else if (adeSubType === "url") {
          // Custom URL
          await importIcsFromUrl(courseId, customUrl);
          setSuccessMessage(
            "Planning importé avec succès depuis l'URL personnalisée"
          );
        } else if (adeSubType === "file") {
          // File upload
          const file = fileInputRef.current?.files?.[0];
          if (file) {
            await importIcsFromFileUpload(courseId, file);
            setSuccessMessage(
              "Planning importé avec succès depuis le fichier ICS uploadé"
            );
          }
        }
      } else if (importType === "excel") {
        // Excel file import
        const file = fileInputRef.current?.files?.[0];
        if (file) {
          await importTimeSlotsFromExcelUpload(courseId, file);
          setSuccessMessage("Planning Excel importé avec succès");
        }
      }

      // Reset form after success
      setFileName("");
      setCustomUrl("");
    } catch (error) {
      setErrors({
        general:
          error instanceof Error
            ? error.message
            : "Une erreur est survenue lors de l'importation",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="default">Importer un planning</Button>
      </DialogTrigger>

      <DialogContent>
        <DialogHeader>
          <DialogTitle>Ajouter un planning pour une formation</DialogTitle>
        </DialogHeader>

        {successMessage && (
          <div className="p-3 bg-green-100 border border-green-400 text-green-700 rounded">
            {successMessage}
          </div>
        )}

        {errors.general && (
          <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded">
            {errors.general}
          </div>
        )}

        {/* Top level: Select main import type (ADE or Excel) */}
        <div className="space-y-3">
          <label className="text-sm font-medium mb-2">Méthode d'importation</label>
          <RadioGroup className="grid grid-cols-2" value={importType} onValueChange={(value) => setImportType(value as "ade" | "excel")}>
            <div className="flex items-center space-x-2">
              <RadioGroupItem value="ade" id="ade-main" />
              <label htmlFor="ade-main" className="cursor-pointer">
                Depuis ADE
              </label>
            </div>
            <div className="flex items-center space-x-2">
              <RadioGroupItem value="excel" id="excel-main" />
              <label htmlFor="excel-main" className="cursor-pointer">
                Fichier Excel
              </label>
            </div>
          </RadioGroup>
        </div>

        {/* ADE Sub-options */}
        {importType === "ade" && (
          <div className="ml-6 p-3 bg-blue-50 border border-blue-200 rounded space-y-3">
            <label className="text-sm font-medium">Source ADE</label>
            <RadioGroup value={adeSubType} onValueChange={(value) => setAdeSubType(value as "auto" | "url" | "file")}>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="auto" id="ade-auto" />
                <label htmlFor="ade-auto" className="cursor-pointer text-sm">
                  Automatique (construction URL)
                </label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="url" id="ade-url" />
                <label htmlFor="ade-url" className="cursor-pointer text-sm">
                  URL personnalisée
                </label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="file" id="ade-file" />
                <label htmlFor="ade-file" className="cursor-pointer text-sm">
                  Fichier ICS
                </label>
              </div>
            </RadioGroup>

            {/* ADE Auto */}
            {adeSubType === "auto" && (
              <div className="p-2 bg-white border border-blue-100 rounded text-xs text-gray-600 mt-2">
                Le planning sera téléchargé automatiquement depuis ADE en utilisant l'ID de la
                formation.
              </div>
            )}

            {/* ADE Custom URL */}
            {adeSubType === "url" && (
              <div className="flex flex-col gap-2 mt-2">
                <Input
                  type="url"
                  placeholder="https://exemple.com/calendar.ics"
                  value={customUrl}
                  onChange={(e) => setCustomUrl(e.target.value)}
                />
                {errors.url && (
                  <span className="text-red-500 text-sm">{errors.url}</span>
                )}
              </div>
            )}

            {/* ADE File Upload */}
            {adeSubType === "file" && (
              <div className="flex flex-col gap-2 mt-2">
                <div className="flex gap-2">
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept=".ics"
                    className="hidden"
                    onChange={(e) => {
                      const file = e.target.files?.[0];
                      if (file) setFileName(file.name);
                    }}
                  />

                  <Input
                    value={fileName}
                    placeholder="Choisissez votre fichier ICS"
                    readOnly
                    onClick={() => fileInputRef.current?.click()}
                    className="cursor-pointer flex-1"
                  />
                  <Button
                    variant="secondary"
                    onClick={() => fileInputRef.current?.click()}
                    disabled={isLoading}
                    size="sm"
                  >
                    Upload
                  </Button>
                </div>

                {errors.file && (
                  <span className="text-red-500 text-sm">{errors.file}</span>
                )}
              </div>
            )}
          </div>
        )}

        {/* Excel File Upload */}
        {importType === "excel" && (
          <div className="flex flex-col gap-2">
            <div className="flex gap-2">
              <input
                ref={fileInputRef}
                type="file"
                accept=".xlsx,.xls,.xml"
                className="hidden"
                onChange={(e) => {
                  const file = e.target.files?.[0];
                  if (file) setFileName(file.name);
                }}
              />

              <Input
                value={fileName}
                placeholder="Choisissez votre fichier Excel"
                readOnly
                onClick={() => fileInputRef.current?.click()}
                className="cursor-pointer flex-1"
              />
              <Button
                variant="secondary"
                onClick={() => fileInputRef.current?.click()}
                disabled={isLoading}
                size="sm"
              >
                Upload
              </Button>
            </div>

            {errors.file && (
              <span className="text-red-500 text-sm ml-2">{errors.file}</span>
            )}
          </div>
        )}

        {/* Select formation */}
        <div className="flex flex-col w-full">
          <label className="text-sm font-medium mb-2">Formation</label>
          <Select onValueChange={setSelectedCourse} value={selectedCourse || ""}>
            <SelectTrigger className="w-full">
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
          {errors.course && (
            <span className="text-red-500 text-sm ml-2 mt-1">{errors.course}</span>
          )}
        </div>

        <div className="mt-6 flex justify-center gap-4">
          <Button className="px-8" onClick={onValidate}>
            Valider
          </Button>
          <DialogClose asChild>
            <Button variant="outline" className="px-8">
              Annuler
            </Button>
          </DialogClose>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default ImportPlanningModal;
