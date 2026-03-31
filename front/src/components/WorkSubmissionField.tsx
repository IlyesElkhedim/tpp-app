import { Field, FieldLabel } from "./ui/field";
import type { FC } from "react";
import { Input } from "./ui/input";

interface WorkSubmissionFieldProps {
  register: any;
  //   errors: any;
  idx: number;
  remove: (idx: number) => void;
  defaultValues?: any;
}

export const WorkSubmissionField: FC<WorkSubmissionFieldProps> = ({
  register,
  //   errors,
  idx,
  remove,
  defaultValues,
}) => {
  return (
    <div
      className="border rounded p-4 flex flex-col gap-4"
      key={defaultValues.id}
    >
      <div className="flex gap-2 items-center">
        <Field orientation="horizontal">
          <FieldLabel className="w-fit">Sujet :</FieldLabel>
          <Input
            placeholder="Ex: MIF03"
            {...register(`works.${idx}.subject` as const)}
            defaultValue={defaultValues?.subject ?? ""}
          />
          {/* {errors.subject && (
          <span className="text-red-500 text-xs">{errors.subject.message}</span>
        )} */}
        </Field>
        <Field orientation="horizontal">
          <FieldLabel className="w-fit">Durée (min) :</FieldLabel>
          <Input
            type="number"
            min={1}
            {...register(`works.${idx}.timeSpent` as const)}
            defaultValue={defaultValues?.timeSpent ?? ""}
          />
          {/* {errors.timeSpent && (
            <span className="text-red-500 text-xs">
              {errors.timeSpent.message}
            </span>
          )} */}
        </Field>
        <button
          type="button"
          className="flex w-fit text-red-500"
          onClick={() => remove(idx)}
          disabled={defaultValues.length === 1}
          title="Supprimer ce travail"
        >
          ✕
        </button>
      </div>
      <Field orientation="vertical" className="gap-1">
        <FieldLabel className="w-fit">Description :</FieldLabel>
        <Input
          placeholder="Ex: Préparation du projet tuteuré"
          {...register(`works.${idx}.description` as const)}
          defaultValue={defaultValues?.description ?? ""}
        />
        {/* {errors.description && (
          <span className="text-red-500 text-xs">
            {errors.description.message}
          </span>
        )} */}
      </Field>
    </div>
  );
};
