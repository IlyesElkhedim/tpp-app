package back.projet.tpp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class IcsTimeSlotParser {
    private IcsTimeSlotParser() {}

    /**
     * Parses a single VEVENT block from an ICS file.
     * Extracts start date/time, end date/time, summary, and location.
     * Only returns events with summary matching "Travaux Personnels et Projets".
     * @param reader BufferedReader positioned after BEGIN:VEVENT line
     * @return IcsEvent object with parsed data, or null if event doesn't match criteria
     * @throws IOException if reading from the reader fails
     */
    public static IcsEvent parseEvent(BufferedReader reader) throws IOException {
        String tpp = "Travaux Personnels et Projets";

        IcsEvent event = new IcsEvent();
        String line = reader.readLine();

        while (!line.startsWith("END:VEVENT")) {

            if (line.startsWith("DTSTART:")) {
                event.startDateTime = parseIcsDateTime(line.substring("DTSTART:".length()));
            } else if (line.startsWith("DTEND:")) {
                event.endDateTime = parseIcsDateTime(line.substring("DTEND:".length()));
            } else if (line.startsWith("SUMMARY:")) {
                event.summary = line.substring("SUMMARY:".length()).trim();
                if (!event.summary.equals(tpp)) break;
            } else if (line.startsWith("LOCATION:")) {
                StringBuilder location = new StringBuilder(line);
                line = reader.readLine();
                while(!line.startsWith("DESCRIPTION:")) {
                    location.append(line);
                }
                event.location = parseLocationField(location.toString());
            }

            line = reader.readLine();

        }

        return (
                event.startDateTime != null &&
                        event.endDateTime != null &&
                        event.summary.equals(tpp)
        ) ? event : null;
    }

    /**
     * Parses an ICS datetime string in the format yyyyMMdd'T'HHmmss[Z].
     * Assumes UTC timezone if the Z suffix is present, then converts to Europe/Paris timezone.
     * @param dateTimeStr The raw ICS datetime string
     * @return LocalDateTime converted to the system's default (Europe/Paris) timezone
     */
    private static LocalDateTime parseIcsDateTime(String dateTimeStr) {
        if (dateTimeStr.endsWith("Z")) {
            dateTimeStr = dateTimeStr.substring(0, dateTimeStr.length() - 1);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        LocalDateTime utcDateTime = LocalDateTime.parse(dateTimeStr, formatter);

        return utcDateTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Paris"))
                .toLocalDateTime();
    }

    /**
     * Parses the LOCATION field from an ICS event.
     * Handles ICS line folding and removes escaped characters.
     * @param locationLine The raw LOCATION line from the ICS event
     * @return The parsed and cleaned location string
     */
    private static String parseLocationField(String locationLine) {
        String location = locationLine.substring("LOCATION:".length()).trim();
        // Remove escaped characters
        location = location.replace("\\,", ",").replace("\\n", "\n");
        return location;
    }

    /**
     * Inner class to hold parsed ICS event data.
     */
    public static class IcsEvent {
        public LocalDateTime startDateTime;
        public LocalDateTime endDateTime;
        public String summary;
        public String location;
    }
}
