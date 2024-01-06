package xyz.immortius.advent2022.day15;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {
    private Pattern linePattern = Pattern.compile("Sensor at x=(?<sx>-?[0-9]+), y=(?<sy>-?[0-9]+): closest beacon is at x=(?<bx>-?[0-9]+), y=(?<by>-?[0-9]+)");

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day15().run();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day15/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private void process(List<String> lines) throws IOException {
        List<Sensor> sensors = new ArrayList<>();

        for (String line : lines) {
            Matcher match = linePattern.matcher(line);
            if (match.matches()) {
                Vector2i sensor = new Vector2i(Integer.parseInt(match.group("sx")), Integer.parseInt(match.group("sy")));
                Vector2i beacon = new Vector2i(Integer.parseInt(match.group("bx")), Integer.parseInt(match.group("by")));

//                long distance = sensor.gridDistance(beacon);
//                SpiralIterator iterator = new SpiralIterator(sensor.x, sensor.y);
//                while (iterator.gridDistance() <= distance) {
//                    map.set(iterator.getX(), iterator.getY(), false);
//                    iterator.next();
//                }
//                map.set(beacon.x, beacon.y, false);

                sensors.add(new Sensor(sensor, beacon));
            } else {
                System.out.println("Failed to match: '" + line + "'");
            }
        }

        System.out.println("Number of sensors: " + sensors.size());

        List<Range> ranges = new ArrayList<>();
        for (int y = 0; y <= 4000000; y++) {
            ranges.add(new Range(0, 4000000, y));
            for (Sensor sensor : sensors) {
                ranges = sensor.deintersect(ranges);
            }
            if (!ranges.isEmpty()) {
                break;
            }
        }
//        for (Sensor sensor : sensors) {
//            ranges = sensor.deintersect(ranges);
//        }
        System.out.println(ranges);

        System.out.println(ranges.get(0).minX * 4000000L + ranges.get(0).y);


//        int cannotContainCount = 0;
//        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
//            Vector2i scanPos = new Vector2i(i, row);
//            for (Sensor sensor: sensors) {
//                if (!sensor.canContainBeacon(scanPos)) {
//                    cannotContainCount++;
//                    break;
//                }
//            }
//        }
//        System.out.println(cannotContainCount);

//        SpiralIterator iterator = new SpiralIterator(2000000, 2000000);
//        while (iterator.getX() > 0) {
//            boolean canContain = true;
//            for (Sensor sensor: sensors) {
//                if (!sensor.canContainBeacon(new Vector2i(iterator.getX(), iterator.getY()))) {
//                    canContain = false;
//                    break;
//                }
//            }
//            if (canContain) {
//                System.out.println(iterator.getX() * 4000000 + iterator.getY());
//            }
//            iterator.next();
//        }
//
//        for (int x = 0; x <= 2000000; x++) {
//            for (int y = 0; y < 4000000; y++) {
//                boolean canContain = true;
//                for (Sensor sensor: sensors) {
//                    if (!sensor.canContainBeacon(new Vector2i(x, y))) {
//                        canContain = false;
//                        break;
//                    }
//                }
//                if (canContain) {
//                    System.out.println(x * 4000000 + y);
//                }
//            }
//        }

    }

    private Vector2i parse(String coord) {
        String[] values = coord.split(",");
        return new Vector2i(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
    }

    private static class Range {
        int y;
        int minX;
        int maxX;

        public Range(int minX, int maxX, int y) {
            this.minX = minX;
            this.maxX = maxX;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Range{" +
                    "y=" + y +
                    ", minX=" + minX +
                    ", maxX=" + maxX +
                    '}';
        }
    }

    private static class Sensor {
        private Vector2i position;
        private Vector2i beacon;
        private long scannedDistance;


        public Sensor(Vector2ic position, Vector2ic beacon) {
            this.position = new Vector2i(position);
            this.scannedDistance = position.gridDistance(beacon);
            this.beacon = new Vector2i(beacon);
        }

        public boolean canContainBeacon(Vector2ic pos) {
            return position.gridDistance(pos) > scannedDistance && !pos.equals(beacon);
        }

        public List<Range> deintersect(List<Range> ranges) {
            List<Range> result = new ArrayList<>();

            for (Range range : ranges) {
                int yDist = Math.abs(range.y - position.y);
                if (yDist > scannedDistance) {
                    result.add(range);
                } else {
                    long inverseDist = scannedDistance - yDist;
                    int minX = (int) (position.x - inverseDist);
                    int maxX = (int) (position.x + inverseDist);
                    if (range.maxX < minX || range.minX > maxX) {
                        result.add(range);
                    } else if (range.minX >= minX && range.maxX <= maxX) {
                        // complete remove
                    } else {
                        if (range.minX < minX) {
                            result.add(new Range(range.minX, minX - 1, range.y));
                        }
                        if (range.maxX > maxX) {
                            result.add(new Range(maxX + 1, range.maxX, range.y));
                        }
                    }
                }
            }
            return result;
        }
    }

}

