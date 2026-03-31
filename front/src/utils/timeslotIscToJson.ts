export async function timeslotIcsToJson(): Promise<any> {
  const response = await fetch(
    "https://edt.univ-lyon1.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=10069&projectId=1&calType=ical&firstDate=2025-08-18&lastDate=2026-08-01",
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    },
  );

  console.log(response);

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      `Erreur lors de la conversion du timeslot ISC (${response.status}) : ${errorText}`,
    );
  }

  return response.json();
}
