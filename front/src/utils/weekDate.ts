function getWeekDates(referenceDate: Date): Date[] {
  const monday = new Date(referenceDate);
  const day = referenceDate.getDay(); // 0 = dimanche, 1 = lundi ...
  const diff = day === 0 ? -6 : 1 - day; // si dimanche = -6, sinon 1-day
  monday.setDate(referenceDate.getDate() + diff);

  const week: Date[] = [];

  for (let i = 0; i < 5; i++) {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    week.push(d);
  }

  return week;
}

export default getWeekDates;