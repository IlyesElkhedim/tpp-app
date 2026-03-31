import { useEffect, type FC } from "react";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import { Button } from "../ui/button";
import { PenLine, Plus } from "lucide-react";
import { Field, FieldGroup, FieldLabel, FieldSet } from "../ui/field";
import { Input } from "../ui/input";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { CourseLevels, CourseNames } from "@/types/Courses";
import { useForm, Controller } from "react-hook-form";
import { useState } from "react";
import { createCourse, updateCourse } from "@/services/courseSearchService";
import type { AddCourseFormValues } from "@/types/addCourseSchema";
import type { Supervisor } from "@/types/Supervisor";
import { getSupervisors } from "@/services/supervisorService";

interface CoursesModalProps {
  onCourseAdded: () => void;
  isUpdate?: boolean;
  defaultValues?: Partial<AddCourseFormValues>;
}

const CoursesModal: FC<CoursesModalProps> = ({
  onCourseAdded,
  isUpdate,
  defaultValues,
}) => {
  const [open, setOpen] = useState(false);
  const [supervisors, setSupervisors] = useState<Supervisor[]>([]);
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
    control,
  } = useForm<AddCourseFormValues>({
    defaultValues: isUpdate && defaultValues ? defaultValues : undefined,
  });

  useEffect(() => {
    if (isUpdate && defaultValues) {
      if (defaultValues.id !== undefined) setValue("id", defaultValues.id);
      if (defaultValues.name !== undefined)
        setValue("name", defaultValues.name);
      if (defaultValues.years !== undefined)
        setValue("years", defaultValues.years);
      if (defaultValues.level !== undefined)
        setValue("level", defaultValues.level);
    } else {
      reset();
    }
  }, [isUpdate, defaultValues, setValue, reset, open]);

  useEffect(() => {
    const fetchSupervisors = async () => {
      setSupervisors(await getSupervisors());
      console.log("Supervisors fetched:", supervisors);
    };

    fetchSupervisors();
  }, []);

  const onSubmit = async (data: AddCourseFormValues) => {
    console.log("Form data submitted:", data);
    if (isUpdate) {
      await updateCourse(data.id, data);
      setOpen(false);
      reset();
      onCourseAdded();
      return;
    } else {
      try {
        console.log("Creating course with data:", data);
        await createCourse({
          id: data.id,
          name: data.name,
          years: data.years,
          level: data.level,
          supervisorId: data.supervisorId,
        });
        setOpen(false);
        reset();
        onCourseAdded();
      } catch (error) {
        console.error("Erreur lors de la création du cours :", error);
      }
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {isUpdate ? (
          <Button variant="ghost" size="sm">
            <PenLine className="mr-2 h-4 w-4" />
          </Button>
        ) : (
          <Button variant="default">
            <Plus className="mr-2 h-4 w-4" />
            Ajouter une promo
          </Button>
        )}
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            {isUpdate
              ? "Modification d'une promotion"
              : "Création d'une promotion"}
          </DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)}>
          <FieldSet className="mt-4">
            <FieldGroup className="gap-4">
              <div className="flex justify-end">
                <Field
                  className={`w-1/2 ${isUpdate ? "pointer-events-none" : ""}`}
                >
                  <FieldLabel>Identifiant :</FieldLabel>
                  <Input
                    {...register("id")}
                    type="number"
                    disabled={isUpdate}
                    className={`w-full ${isUpdate ? "bg-gray-200" : ""}`}
                    defaultValue={defaultValues?.id ?? ""}
                  />
                  {errors.id && (
                    <span className="text-red-500 text-xs">
                      {errors.id.message}
                    </span>
                  )}
                </Field>
              </div>
              <Field>
                <FieldLabel>Intitulé de la formation :</FieldLabel>
                <Controller
                  name="name"
                  control={control}
                  render={({ field }) => (
                    <Select
                      value={field.value || ""}
                      onValueChange={field.onChange}
                      disabled={isUpdate}
                    >
                      <SelectTrigger
                        className={`w-full ${isUpdate ? "bg-gray-200" : ""}`}
                      >
                        <SelectValue placeholder="Choisir un cours" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectGroup>
                          {Object.values(CourseNames).map((courseName) => (
                            <SelectItem key={courseName} value={courseName}>
                              {courseName}
                            </SelectItem>
                          ))}
                        </SelectGroup>
                      </SelectContent>
                    </Select>
                  )}
                />
                {errors.name && (
                  <span className="text-red-500 text-xs">
                    {errors.name.message}
                  </span>
                )}
              </Field>
              <div className="grid grid-cols-2 gap-4">
                <Field>
                  <FieldLabel>Année :</FieldLabel>
                  <Controller
                    name="years"
                    control={control}
                    render={({ field }) => {
                      const [start, end] = (field.value ?? "").split("-").map((s: string) => s.trim());
                      const update = (newStart: string, newEnd: string) => {
                        field.onChange(`${newStart}-${newEnd}`);
                      };
                      return (
                        <div className="flex items-center gap-2">
                          <Input
                            type="number"
                            placeholder="2025"
                            value={start ?? ""}
                            onChange={(e) => update(e.target.value, end ?? "")}
                            className="w-full"
                          />
                          <span className="text-gray-500 font-medium">-</span>
                          <Input
                            type="number"
                            placeholder="2026"
                            value={end ?? ""}
                            onChange={(e) => update(start ?? "", e.target.value)}
                            className="w-full"
                          />
                        </div>
                      );
                    }}
                  />
                  {errors.years && (
                    <span className="text-red-500 text-xs">
                      {errors.years.message}
                    </span>
                  )}
                </Field>
                <Field>
                  <FieldLabel>Niveau d'étude :</FieldLabel>
                  <Controller
                    name="level"
                    control={control}
                    render={({ field }) => (
                      <Select
                        value={field.value || ""}
                        onValueChange={field.onChange}
                      >
                        <SelectTrigger className="w-full">
                          <SelectValue placeholder="Choisir un niveau" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectGroup>
                            {Object.values(CourseLevels).map((level) => (
                              <SelectItem key={level} value={level}>
                                {level}
                              </SelectItem>
                            ))}
                          </SelectGroup>
                        </SelectContent>
                      </Select>
                    )}
                  />
                  {errors.level && (
                    <span className="text-red-500 text-xs">
                      {errors.level.message}
                    </span>
                  )}
                </Field>
              </div>
              <Field>
                <FieldLabel>Résponsable de formation :</FieldLabel>
                <Controller
                  name="supervisorId"
                  control={control}
                  render={({ field }) => (
                    <Select
                      value={field.value ? String(field.value) : ""}
                      onValueChange={(val) => field.onChange(Number(val))}
                      defaultValue={
                        defaultValues?.supervisorId
                          ? String(defaultValues.supervisorId)
                          : undefined
                      }
                    >
                      <SelectTrigger className="w-full">
                        <SelectValue placeholder="Choisir un responsable" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectGroup>
                          {supervisors.map((supervisor) => (
                            <SelectItem
                              key={supervisor.id}
                              value={String(supervisor.id)}
                            >
                              {supervisor.firstName}{" "}
                              {supervisor.lastName.toUpperCase()}
                            </SelectItem>
                          ))}
                        </SelectGroup>
                      </SelectContent>
                    </Select>
                  )}
                />
                {errors.supervisorId && (
                  <span className="text-red-500 text-xs">
                    {errors.supervisorId.message}
                  </span>
                )}
              </Field>
            </FieldGroup>
          </FieldSet>
          <div className="mt-6 flex justify-center gap-4">
            <Button type="submit" className="px-8">
              Valider
            </Button>
            <DialogClose asChild>
              <Button
                variant="outline"
                className="px-8"
                type="button"
                onClick={() => setOpen(false)}
              >
                Annuler
              </Button>
            </DialogClose>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default CoursesModal;
