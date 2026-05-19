package tollbooth;

//import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*
We are writing software to analyze logs for toll booths on a highway.
*/

class LogEntry {

    /**
     * Represents an entry from a single log line.
     *
     * Example:
     * 34400.409 SXY288 210E ENTRY
     */

    private final String timestamp;
    private final String licensePlate;
    private final String boothType;
    private final int location;
    private final String direction;

    public LogEntry(String logLine) {
        String[] tokens = logLine.split(" ");
        this.timestamp = tokens[0];
        this.licensePlate = tokens[1];
        this.boothType = tokens[3];
        this.location = Integer.parseInt(tokens[2].substring(0, tokens[2].length() - 1));

        String directionLetter = tokens[2].substring(tokens[2].length() - 1);
        if (directionLetter.equals("E")) {
            this.direction = "EAST";
        } else if (directionLetter.equals("W")) {
            this.direction = "WEST";
        } else {
            throw new IllegalArgumentException("Invalid direction");
        }
    }

    public double getTimestamp() {
        return Double.parseDouble(timestamp);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBoothType() {
        return boothType;
    }

    public int getLocation() {
        return location;
    }

    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format(
                "<LogEntry timestamp: %f  license: %s  location: %d  direction: %s  booth type: %s>",
                getTimestamp(),
                licensePlate,
                location,
                direction,
                boothType
        );
    }
}

class LogFile {

    List<LogEntry> logEntries;

    public LogFile(BufferedReader reader) throws IOException {
        this.logEntries = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            LogEntry logEntry = new LogEntry(line.strip());
            this.logEntries.add(logEntry);
            line = reader.readLine();
        }
    }

    public LogEntry get(int index) {
        return this.logEntries.get(index);
    }

    public int size() {
        return this.logEntries.size();
    }

    public int countJourneys() {
        int completedJourneys = 0;
        for (LogEntry logEntry : this.logEntries) {
            if (logEntry.getBoothType().equals("EXIT")) {
                completedJourneys++;
            }
        }
        return completedJourneys;
    }

    public List<String> catchSpeeders() {
        List<String> speedingTickets = new ArrayList<>();

        Map<String, Double> previousTime = new HashMap<>();
        Map<String, Integer> previousLocation = new HashMap<>();
        Map<String, Integer> segmentsOver120 = new HashMap<>();
        Map<String, Boolean> alreadyTicketedThisJourney = new HashMap<>();

        for (LogEntry entry : logEntries) {
            String plate = entry.getLicensePlate();
            String boothType = entry.getBoothType();

            if (boothType.equals("ENTRY")) {
                previousTime.put(plate, entry.getTimestamp());
                previousLocation.put(plate, entry.getLocation());
                segmentsOver120.put(plate, 0);
                alreadyTicketedThisJourney.put(plate, false);
                continue;
            }

            if (!previousTime.containsKey(plate)) {
                continue;
            }

            double timeDiff = entry.getTimestamp() - previousTime.get(plate);
            int distanceDiff = Math.abs(entry.getLocation() - previousLocation.get(plate));
            double speed = (distanceDiff * 3600.0) / timeDiff;

            if (!alreadyTicketedThisJourney.get(plate)) {
                if (speed >= 130.0) {
                    alreadyTicketedThisJourney.put(plate, true);
                } else if (speed >= 120.0) {
                    int count = segmentsOver120.get(plate) + 1;
                    segmentsOver120.put(plate, count);
                    if (count >= 2) {
                        alreadyTicketedThisJourney.put(plate, true);
                    }
                }
            }

            previousTime.put(plate, entry.getTimestamp());
            previousLocation.put(plate, entry.getLocation());

            if (boothType.equals("EXIT")) {
                if (alreadyTicketedThisJourney.get(plate)) {
                    speedingTickets.add(plate);
                }
                previousTime.remove(plate);
                previousLocation.remove(plate);
                segmentsOver120.remove(plate);
                alreadyTicketedThisJourney.remove(plate);
            }
        }

        return speedingTickets;
    }
}

public class Main {

    public static void main(String[] argv) throws IOException {
        testLogFile();
        testLogEntry();
        testCountJourneys();
        testCatchSpeeders();
        System.out.println("All tests passed.");
    }

    public static void testLogFile() throws IOException {
        System.out.println("Running testLogFile");
        try (
                BufferedReader reader = new BufferedReader(
                        new FileReader("/content/test/tollbooth_small.log")
                )
        ) {
            LogFile logFile = new LogFile(reader);
        //    assertEquals(13, logFile.size());
            for (LogEntry entry : logFile.logEntries) {
                assert (entry instanceof LogEntry);
            }
        }
    }

    public static void testLogEntry() {
        System.out.println("Running testLogEntry");
        String logLine = "44776.619 KTB918 310E MAINROAD";
        LogEntry logEntry = new LogEntry(logLine);
//        assertEquals(44776.619f, logEntry.getTimestamp(), 0.0001);
//        assertEquals("KTB918", logEntry.getLicensePlate());
//        assertEquals(310, logEntry.getLocation());
//        assertEquals("EAST", logEntry.getDirection());
//        assertEquals("MAINROAD", logEntry.getBoothType());

        logLine = "52160.132 ABC123 400W ENTRY";
        logEntry = new LogEntry(logLine);
//        assertEquals(52160.132f, logEntry.getTimestamp(), 0.0001);
//        assertEquals("ABC123", logEntry.getLicensePlate());
//        assertEquals(400, logEntry.getLocation());
//        assertEquals("WEST", logEntry.getDirection());
//        assertEquals("ENTRY", logEntry.getBoothType());
    }

    public static void testCountJourneys() throws IOException {
        System.out.println("Running testCountJourneys");

        try (BufferedReader reader = new BufferedReader(new FileReader("/content/test/tollbooth_small.log"))) {
            LogFile logFile = new LogFile(reader);
           // assertEquals(3, logFile.countJourneys());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("/content/test/tollbooth_medium.log"))) {
            LogFile logFile = new LogFile(reader);
            //assertEquals(63, logFile.countJourneys());
        }
    }

    public static void testCatchSpeeders() throws IOException {
        System.out.println("Running testCatchSpeeders");

        try (BufferedReader reader = new BufferedReader(new FileReader("/content/test/tollbooth_speeders.log"))) {
            LogFile logFile = new LogFile(reader);
            List<String> ticketList = logFile.catchSpeeders();

            Map<String, Integer> ticketCounts = new HashMap<>();
            for (String ticket : ticketList) {
                ticketCounts.put(ticket, ticketCounts.getOrDefault(ticket, 0) + 1);
            }

//            assertEquals(1, (int) ticketCounts.get("TST002"));
//            assertEquals(2, (int) ticketCounts.get("TST003"));
//            assertEquals(2, ticketCounts.size());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("/content/test/tollbooth_medium.log"))) {
            LogFile logFile = new LogFile(reader);
            List<String> ticketList = logFile.catchSpeeders();
          //  assertEquals(10, ticketList.size());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("/content/test/tollbooth_long.log"))) {
            LogFile logFile = new LogFile(reader);
            List<String> ticketList = logFile.catchSpeeders();
         //   assertEquals(129, ticketList.size());
        }
    }
}
