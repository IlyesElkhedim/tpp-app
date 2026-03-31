import type { Student } from "@/types/Student";
import * as XLSX from "xlsx";

export async function convertFileToJson(file: File) {
  return new Promise<Omit<Student, "currentCourse">[]>((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = (event) => {
      const data = new Uint8Array(event.target?.result as ArrayBuffer);
      const allSheet = XLSX.read(data, { type: "array" });

      const sheetName = allSheet.SheetNames[0];
      const selectedSheet = allSheet.Sheets[sheetName];

      const rows: never[][] = XLSX.utils.sheet_to_json(selectedSheet, {
        header: 1,
        defval: "",
      });

      const headerIndex = rows.findIndex(
        (row: never[]) =>
          row.some((cell: never) =>
            String(cell).toLowerCase().includes("nom"),
          ) &&
          row.some((cell: never) => String(cell).toLowerCase().includes("id")),
      );

      if (headerIndex === -1) {
        alert(
          "En-tête de colonnes invalides\n" +
            "Veuillez les renommer selon ce modèle : " +
            "ID, Prénom, Nom, email, type_contrat, date_deb_contrat",
        );
        return;
      }

      let jsonData = XLSX.utils.sheet_to_json(selectedSheet, {
        defval: "",
        range: headerIndex,
      }) as Record<string, string>[];

      // Map to Student type
      const students: Omit<Student, "currentCourse">[] = jsonData.map((row) => {
        // Rename keys
        const mapped: Record<string, string> = {};
        for (const key in row) {
          let newKey = key;
          if (key.trim().toLowerCase() === "id") newKey = "studentNumber";
          else if (key.trim().toLowerCase() === "nom") newKey = "lastName";
          else if (
            key.trim().toLowerCase() === "prénom" ||
            key.trim().toLowerCase() === "prenom"
          )
            newKey = "firstName";
          mapped[newKey] = row[key];
        }
        return {
          studentNumber: mapped.studentNumber || "",
          firstName: mapped.firstName || "",
          lastName: mapped.lastName || "",
          email: mapped.email || "",
          contractType: mapped.type_contrat
            ? (mapped.type_contrat as Student["contractType"])
            : undefined,
          contractStartDate: mapped.date_deb_contrat || undefined,
          courseIds: [],
        };
      });
      resolve(students);
    };

    reader.onerror = (err) => reject(err);
    reader.readAsArrayBuffer(file);
  });
}
