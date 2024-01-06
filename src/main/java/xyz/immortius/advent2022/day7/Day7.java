package xyz.immortius.advent2022.day7;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day7 {
    public static void main(String[] args) throws IOException {
        new Day7().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day7/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        DirectoryInfo rootDirectory = new DirectoryInfo("");
        DirectoryInfo current = rootDirectory;
        for (String line : lines) {
            if (line.startsWith("$ cd ")) {
                String dirName = line.substring(5);
                if ("..".equals(dirName)) {
                    current = current.parent;
                } else if ("/".equals(dirName)) {
                    current = rootDirectory;
                } else {
                    current = current.getOrCreateSubdirectory(dirName);
                }
            } else if (line.charAt(0) >= '0' && line.charAt(0) <= '9') {
                String[] fileParts = line.split(" ");
                current.addFile(fileParts[1], Long.parseLong(fileParts[0]));
            }
        }

        long freeSpace = 70000000 - rootDirectory.size();
        long toDelete = 30000000 - freeSpace;
        System.out.println("To delete: " + toDelete);

        TotallingWalker walker = new TotallingWalker();
        rootDirectory.walk(walker);
        System.out.println(walker.total);

        SmallestExceedingWalker smallestExceedingWalker = new SmallestExceedingWalker(toDelete);
        rootDirectory.walk(smallestExceedingWalker);
        System.out.println(smallestExceedingWalker.size);
    }

    public static class DirectoryInfo {
        private String name;
        private DirectoryInfo parent;
        private Map<String, DirectoryInfo> subDirectories = new HashMap<>();
        private Map<String, Long> files = new HashMap<>();

        public DirectoryInfo(String name) {
            this.name = name;
            this.parent = this;
        }

        public DirectoryInfo getOrCreateSubdirectory(String name) {
            return subDirectories.computeIfAbsent(name, n -> {
                DirectoryInfo dir = new DirectoryInfo(n);
                dir.parent = this;
                return dir;
            });
        }

        public void addFile(String name, long size) {
            files.put(name, size);
        }

        public long size() {
            long fileSize = files.values().stream().reduce(0L, Long::sum);
            long dirSize = subDirectories.values().stream().map(DirectoryInfo::size).reduce(0L, Long::sum);
            return fileSize + dirSize;
        }

        public String toString() {
            return name;
        }

        public void walk(DirWalker walker) {
            walker.visit(this);
            subDirectories.values().forEach(dir -> dir.walk(walker));
        }
    }

    public interface DirWalker {
        void visit(DirectoryInfo dir);
    }

    public static class TotallingWalker implements DirWalker {
        long total = 0;

        public void visit(DirectoryInfo dir) {
            if (dir.size() <= 100000) {
                total += dir.size();
            }
        }
    }

    public static class SmallestExceedingWalker implements DirWalker {
        long size = Long.MAX_VALUE;
        long min;

        public SmallestExceedingWalker(long min) {
            this.min = min;
        }

        public void visit(DirectoryInfo dir) {
            if (dir.size() > min && dir.size() < size) {
                size = dir.size();
            }
        }
    }

}
