import type { ColumnDef } from "@tanstack/react-table";
import type { StudentStatistics } from "../types/StudentStatistics.ts";
import { ArrowUpDown } from "lucide-react";
import { Button } from "../components/ui/button.tsx";
import { Progress } from "../components/ui/progress.tsx";
import { Badge } from "@/components/ui/badge.tsx";

export const StudentsStatisticsColumns: ColumnDef<StudentStatistics>[] = [
  {
    accessorKey: "studentNumber",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          ID
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
  },
  {
    accessorKey: "lastName",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Nom
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
  },
  {
    accessorKey: "firstName",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Prénom
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
  },
  {
    accessorKey: "attendanceRate",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Taux de présence
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
    cell: ({ row }) => {
      const rate = row.getValue("attendanceRate") as number;
      const getProgressColorClass = (rate: number) => {
        if (rate >= 90) return "[&>*]:bg-green-300";
        if (rate >= 70) return "[&>*]:bg-orange-300";
        return "[&>*]:bg-red-300";
      };

      return (
        <div className="flex items-center gap-2 flex-col">
          <span className={`text-sm font-medium `}>{rate}%</span>
          <Progress
            value={rate}
            className={`w-[60%] ${getProgressColorClass(rate)}`}
          />
        </div>
      );
    },
  },
  {
    accessorKey: "justifiedAbsences",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Absences justifiées
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
    cell: ({ row }) => {
      const justifiedAbsences = row.getValue("justifiedAbsences") as number;
      return (
        <div className="text-center">
          <span>{justifiedAbsences}</span>
        </div>
      );
    },
  },
  {
    accessorKey: "unjustifiedAbsences",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Absences injustifiées
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
    cell: ({ row }) => {
      const unjustifiedAbsences = row.getValue("unjustifiedAbsences") as number;
      return (
        <div className="text-center">
          <span>{unjustifiedAbsences}</span>
        </div>
      );
    },
  },
  {
    accessorKey: "validReports",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Rapports valides
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
    cell: ({ row }) => {
      const validReports = row.getValue("validReports") as number;
      return (
        <div className="text-center">
          <Badge className="bg-green-300 text-black">{validReports}</Badge>
        </div>
      );
    },
  },
  {
    accessorKey: "invalidReports",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Rapports invalides
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
    cell: ({ row }) => {
      const invalidReports = row.getValue("invalidReports") as number;
      return (
        <div className="text-center">
          <Badge className="bg-orange-300 text-black">{invalidReports}</Badge>
        </div>
      );
    },
  },
  {
    accessorKey: "notSubmittedReports",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Rapports non soumis
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
    cell: ({ row }) => {
      const notSubmittedReports = row.getValue("notSubmittedReports") as number;
      return (
        <div className="text-center">
          <Badge className="bg-red-300 text-black">{notSubmittedReports}</Badge>
        </div>
      );
    },
  },
];
