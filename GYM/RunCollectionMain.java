package runcollection;

import java.util.*;

class Course {
    /* Data about a particular course. */
    public String title;        // The name of the obstacle course
    public int obstacleCount;   // The number of obstacles in the course

    public Course(String courseTitle, int obstacles) {
        title = courseTitle;
        obstacleCount = obstacles;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Course)) {
            return false;
        }
        Course c = (Course) o;
        return Objects.equals(c.title, this.title) && c.obstacleCount == this.obstacleCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, obstacleCount);
    }
}

class Run {
    /* Data and methods about a single run of the obstacle course */
    public Course course;             // The Course object this run is for
    public boolean complete;          // true if the run is a full run of the course
    public List<Integer> obstacleTimes; // The times it took to complete each obstacle

    public Run(Course runCourse) {
        course = runCourse;
        complete = false;
        obstacleTimes = new ArrayList<>();
    }

    public void addObstacleTime(int obstacleTime) {
        if (this.complete) {
            throw new IllegalStateException("run is full");
        }

        obstacleTimes.add(obstacleTime);

        if (obstacleTimes.size() == course.obstacleCount) {
            this.complete = true;
        }
    }

    public int getRunTime() {
        // Returns the total time this run has taken.
        // If the run is not complete, it returns the time taken so far.
        return obstacleTimes.stream().mapToInt(Integer::intValue).sum();
    }
}

class RunCollection {
    public Course course;   // the Course this RunCollection is for
    public List<Run> runs;  // the Run objects for this particular course

    public RunCollection(Course collectionCourse) {
        course = collectionCourse;
        runs = new ArrayList<>();
    }

    public int getNumRuns() {
        // Returns the number of runs in this collection
        return runs.size();
    }

    public void addRun(Run run) {
        // Adds a run to this collection
        if (!run.course.equals(course)) {
            throw new IllegalArgumentException("run's Course is not the same as the RunCollection's");
        }
        runs.add(run);
    }

    public int personalBest() {
        // Only complete runs count as a personal best
        return runs.stream()
                .filter(r -> r.complete)
                .mapToInt(Run::getRunTime)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    public int bestOfBests() {
        int bestOfBest = 0;

        for (int i = 0; i < course.obstacleCount; i++) {
            int obstacleMin = Integer.MAX_VALUE;

            for (Run run : runs) {
                if (run.obstacleTimes.size() > i) {
                    obstacleMin = Math.min(obstacleMin, run.obstacleTimes.get(i));
                }
            }

            if (obstacleMin == Integer.MAX_VALUE) {
                throw new IllegalStateException("No recorded time for obstacle " + (i + 1));
            }

            bestOfBest += obstacleMin;
        }

        return bestOfBest;
    }

    public double chanceOfPersonalBest(Run inprogress) {
        int personalBest = personalBest();

        if (personalBest == Integer.MAX_VALUE) {
            return 0.0; // no complete runs to compare against
        }

        int totalObstacles = course.obstacleCount;
        List<List<Integer>> historical = new ArrayList<>();

        for (int i = 0; i < totalObstacles; i++) {
            historical.add(new ArrayList<>());
        }

        for (Run run : runs) {
            for (int i = 0; i < run.obstacleTimes.size(); i++) {
                historical.get(i).add(run.obstacleTimes.get(i));
            }
        }

        int currentTotal = inprogress.getRunTime();
        int completedObstacles = inprogress.obstacleTimes.size();

        if (currentTotal > personalBest) {
            return 0.0;
        }

        for (int j = completedObstacles; j < totalObstacles; j++) {
            if (historical.get(j).isEmpty()) {
                return 0.0;
            }
        }

        int trials = 10000;
        int success = 0;
        Random random = new Random();

        for (int i = 0; i < trials; i++) {
            int newRuntime = currentTotal;

            for (int j = completedObstacles; j < totalObstacles; j++) {
                List<Integer> obstacleHistory = historical.get(j);
                newRuntime += obstacleHistory.get(random.nextInt(obstacleHistory.size()));

                if (newRuntime > personalBest) {
                    break;
                }
            }

            if (newRuntime <= personalBest) {
                success++;
            }
        }

        return success / (double) trials;
    }
}

public class Main {
    public static void main(String[] argv) {
        testRun();
        testRunCollection();
        testChanceOfPersonalBest();
        System.out.println("All tests passed.");
    }

    // This is not a complete test suite, but tests some basic functionality of the above code.
    public static void testRun() {
        System.out.println("Running testRun");

        Course testCourse = new Course("Test course", 2);
        Run testRun = new Run(testCourse);

        testRun.addObstacleTime(3);
        assert !testRun.complete : "Test run should not be complete";

        testRun.addObstacleTime(5);
        assert testRun.complete : "Test run should be complete";

        assert testRun.obstacleTimes.equals(Arrays.asList(3, 5))
                : "obstacleTimes should be [3, 5], was " + testRun.obstacleTimes;

        assert testRun.getRunTime() == 8
                : "getRunTime should return 8, returned " + testRun.getRunTime();

        try {
            testRun.addObstacleTime(4);
            assert false : "adding obstacle time to complete run should throw";
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public static RunCollection makeRunCollection(Course course, int[][] obstacleData) {
        // Create a new RunCollection for test purposes.
        // obstacleData: each int[] represents obstacle times for a single run.
        RunCollection runCollection = new RunCollection(course);

        for (int[] runData : obstacleData) {
            Run run = new Run(course);
            for (int obstacleTime : runData) {
                run.addObstacleTime(obstacleTime);
            }
            runCollection.addRun(run);
        }

        return runCollection;
    }

    public static void testRunCollection() {
        // Obstacles: O1  O2  O3  O4
        // Run 1:      3   4   5   6    (total: 18 seconds)
        // Run 2:      4   4   4   5    (total: 17 seconds)
        // Run 3:      4   5   4   6    (total: 19 seconds)
        // Run 4:      5   5   3        (13 seconds, but run is incomplete)
        System.out.println("Running testRunCollection");

        int[][] obstacleData = new int[][] {
                {3, 4, 5, 6},
                {4, 4, 4, 5},
                {4, 5, 4, 6},
                {5, 5, 3}
        };

        Course testCourse = new Course("Test course", 4);
        RunCollection runCollection = makeRunCollection(testCourse, obstacleData);

        int numRuns = obstacleData.length;
        assert runCollection.getNumRuns() == numRuns
                : "number of runs should be " + numRuns + ", was " + runCollection.getNumRuns();

        assert runCollection.personalBest() == 17
                : "personalBest should be 17, was " + runCollection.personalBest();

        assert runCollection.bestOfBests() == 15
                : "bestOfBests should be 15, was " + runCollection.bestOfBests();
    }

    public static void testChanceOfPersonalBest() {
        System.out.println("Running chanceOfPersonalBest");

        int[][] obstacleData = new int[][] {
                {32, 37},                                // incomplete
                {31, 29, 34, 25, 25, 39},                // incomplete
                {25, 34, 38, 24, 26, 39, 33},            // incomplete
                {39, 21, 39, 34, 39, 29, 31, 22, 28, 20},// complete
                {23, 22, 35, 33, 36, 21, 29, 37, 24, 34},// complete
                {28, 34, 28, 22, 40, 28, 31, 33, 25, 20},// complete
                {20, 38, 40, 28, 34, 22},                // incomplete
                {36, 39, 20, 32, 38, 24, 22},            // incomplete
                {40, 20, 21, 37, 32, 30, 40, 25, 37, 30},// complete
                {21, 35, 30, 37, 32, 40, 26, 29, 29}     // incomplete
        };

        Course testCourse = new Course("Test course", 10);
        RunCollection runCollection = makeRunCollection(testCourse, obstacleData);

        Run inProgress = new Run(testCourse);
        inProgress.addObstacleTime(19);
        inProgress.addObstacleTime(19);
        inProgress.addObstacleTime(19);

        double chance = runCollection.chanceOfPersonalBest(inProgress);
        assert 0.92813 <= chance && chance <= 0.96813
                : "chanceOfPersonalBest out of expected range: " + chance;
    }
}
