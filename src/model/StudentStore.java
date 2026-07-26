package model;

import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

// for csv
public class StudentStore {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path CSV      = DATA_DIR.resolve("students.csv");

    // load from csv
    public List<Student> load() {
        List<Student> out = new ArrayList<Student>();
        try {
            if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
            if (!Files.exists(CSV)) return out;

            BufferedReader br = Files.newBufferedReader(CSV, StandardCharsets.UTF_8);
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",", -1);
                    if (p.length >= 5) {
                        String name = p[0];
                        String id   = p[1];
                        String dept = p[2];
                        String email= p[3];
                        double cgpa = parseDoubleSafe(p[4]);
                        out.add(new Student(name, id, dept, email, cgpa));
                    }
                }
            } finally {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    // Save the list to CSV
    public boolean save(ObservableList<Student> list) {
        try {
            if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
            BufferedWriter bw = Files.newBufferedWriter(
                    CSV, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            try {
                for (Student s : list) {
                    String line = String.join(",",
                            nullToEmpty(s.getName()),
                            nullToEmpty(s.getId()),
                            nullToEmpty(s.getDept()),
                            nullToEmpty(s.getEmail()),
                            String.valueOf(s.getCgpa()));
                    bw.write(line);
                    bw.newLine();
                }
            } finally {
                bw.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }
}
