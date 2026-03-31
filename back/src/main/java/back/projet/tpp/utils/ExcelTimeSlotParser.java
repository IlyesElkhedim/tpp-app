package back.projet.tpp.utils;

import org.apache.poi.ss.usermodel.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class ExcelTimeSlotParser {

    private ExcelTimeSlotParser() {}

    /**
     * Extracts the room/location from the "Type salle" column in the Excel sheet.
     * Searches for the "Type salle" header and returns the first non-blank value in the following cells.
     * @param sheet The Excel Sheet to search
     * @param formatter DataFormatter for parsing cell values
     * @return The room/location string, or null if "Type salle" column is not found
     */
    public static String extractRoom(Sheet sheet, DataFormatter formatter) {
        for (Row row : sheet) {
            for (Cell cell : row) {

                String value = formatter.formatCellValue(cell).trim();

                if ("Type salle".equalsIgnoreCase(value)) {
                    int startColumn = cell.getColumnIndex() + 1;

                    // Go through all the following columns in the line
                    for (int col = startColumn; col < row.getLastCellNum(); col++) {

                        Cell candidate = row.getCell(col);
                        if (candidate == null) continue;

                        String room = formatter.formatCellValue(candidate).trim();

                        if (!room.isBlank()) {
                            return room;
                        }
                    }

                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Parses a date string from Excel in the format "Day HH/MM" (e.g., "Lun. 15/12").
     * Automatically determines the correct year based on the current date and school year logic.
     * @param rawDate The raw date string from Excel
     * @return LocalDate parsed from the input string
     * @throws IllegalArgumentException if the date format is invalid
     */
    public static LocalDate parseDate(String rawDate) throws IllegalArgumentException {
        // Example : "Lun. 15/12"
        String[] parts = rawDate.split(" ");

        if (parts.length < 2) {
            throw new IllegalArgumentException("Date format invalid: " + rawDate);
        }

        String dayMonthPart = parts[1]; // "15/12"

        String[] dm = dayMonthPart.split("/");
        int day = Integer.parseInt(dm[0]);
        int month = Integer.parseInt(dm[1]);

        int year = computeSchoolYear(month);

        return LocalDate.of(year, month, day);
    }

    /**
     * Determines the correct school year based on a given month.
     * School year logic: September to December of year Y, January to August of year Y+1.
     * If current month is January to August, school year started in the previous year.
     * If current month is September to December, school year started in the current year.
     * @param month The month to determine school year for
     * @return The year the school year started
     */
    private static int computeSchoolYear(int month) {

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        if (currentMonth <= 8) {
            if (month >= 9) {
                return currentYear - 1;
            } else {
                return currentYear;
            }
        }

        else {
            if (month >= 9) {
                return currentYear;
            } else {
                return currentYear + 1;
            }
        }
    }

    /**
     * Parses a time range string in the format "HHhMM-HHhMM" (e.g., "9h45-11h15").
     * @param rawTime The raw time range string from Excel
     * @return Array of two LocalTime objects [startTime, endTime]
     * @throws IllegalArgumentException if the time format is invalid or doesn't contain a dash separator
     */
    public static LocalTime[] parseTimeRange(String rawTime) throws IllegalArgumentException {
        // "9h45-11h15"
        String[] parts = rawTime.split("-");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Time format invalid: " + rawTime);
        }

        return new LocalTime[] {
                parseSingleTime(parts[0]),
                parseSingleTime(parts[1])
        };
    }

    /**
     * Parses a single time string (e.g., "9h45" or "11h15").
     * @param raw The raw time string
     * @return LocalTime parsed from the input string
     * @throws IllegalArgumentException if the time format is invalid or values are out of range
     */
    private static LocalTime parseSingleTime(String raw) throws IllegalArgumentException {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Time empty");
        }

        String cleaned = raw.trim()
                .toLowerCase()
                .replaceAll("\\s+", "")  // supprime espaces
                .replace("h", ":");

        String[] parts = cleaned.split(":");

        int hour = Integer.parseInt(parts[0]);
        int minute = 0;

        if (parts.length > 1 && !parts[1].isBlank()) {
            minute = Integer.parseInt(parts[1]);
        }

        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Time invalid: " + hour);
        }

        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Minute invalid: " + minute);
        }

        return LocalTime.of(hour, minute);
    }

}
