import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    static ArrayList<Student> studentList = new ArrayList<>(); // Store student data
    static boolean lastActionWasReport = false; // Track if last action was 'Report'

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Grade Tracker"); // Main application window
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Input fields and UI components
        JTextField nameField = new JTextField(20);
        JTextField scoreField = new JTextField(5);
        JButton addButton = new JButton("Add Student");
        JButton reportButton = new JButton("Show Report");
        JButton exportButton = new JButton("Export Students");
        JButton importButton = new JButton("Import Students");
        JTextArea outputArea = new JTextArea(20, 45);
        outputArea.setEditable(false);

        // Add components to window
        frame.add(new JLabel("Student Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Score (0-100):"));
        frame.add(scoreField);
        frame.add(addButton);
        frame.add(reportButton);
        frame.add(importButton);
        frame.add(exportButton);
        frame.add(new JScrollPane(outputArea));

        // Move focus from name to score on Enter
        nameField.addActionListener(e -> scoreField.requestFocusInWindow());
        scoreField.addActionListener(e -> addButton.doClick());

        // Focus and select score when it gains focus
        scoreField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                scoreField.selectAll();
            }
        });

        // Add Student Button Logic
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String scoreText = scoreField.getText().trim();

            try {
                double score = Double.parseDouble(scoreText);

                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(frame, "Score must be between 0 and 100.");
                    return;
                }

                Student s = new Student(name, score);
                studentList.add(s);

                // Clear or append message based on previous action
                if (lastActionWasReport) {
                    outputArea.setText("Added: " + name + " - Grade: " + s.grade + "\n");
                } else {
                    outputArea.append("Added: " + name + " - Grade: " + s.grade + "\n");
                }
                lastActionWasReport = false;

                nameField.setText("");
                scoreField.setText("");
                nameField.requestFocus();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid score.");
            }
        });

        // Report Button Logic
        reportButton.addActionListener(e -> {
            if (studentList.isEmpty()) {
                outputArea.setText("No student data available.\n");
                return;
            }

            double total = 0;
            double max = studentList.get(0).score;
            double min = studentList.get(0).score;
            int passCount = 0;
            Student top = studentList.get(0), low = studentList.get(0);

            StringBuilder report = new StringBuilder("====== Student Report ======\n");
            for (Student s : studentList) {
                report.append(s.name).append(" - ").append(s.score).append(" (").append(s.grade).append(")\n");
                total += s.score;
                if (s.pass) passCount++;
                if (s.score > max) { max = s.score; top = s; }
                if (s.score < min) { min = s.score; low = s; }
            }

            double avg = total / studentList.size();
            double passRate = ((double) passCount / studentList.size()) * 100;

            report.append("\nAverage Score: ").append(avg)
                    .append("\nTopper: ").append(top.name).append(" - ").append(top.score)
                    .append("\nLowest: ").append(low.name).append(" - ").append(low.score)
                    .append("\nPassed: ").append(passCount).append("/").append(studentList.size())
                    .append("\nPassing Rate: ").append(String.format("%.2f", passRate)).append("%\n");

            outputArea.setText(report.toString());
            lastActionWasReport = true; // set flag
        });

        // Export Button Logic
        exportButton.addActionListener(e -> {
            if (studentList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Student list is empty. Please add some students before exporting.");
                return;
            }

            try {
                // Generate timestamp-based filename
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                File file = new File("students_" + timestamp + ".csv");

                // Ensure unique filename by appending counter if file exists
                int counter = 1;
                while (file.exists()) {
                    file = new File("students_" + timestamp + "_" + counter + ".csv");
                    counter++;
                }

                PrintWriter writer = new PrintWriter(file);
                writer.println("Name,Score,Grade,Pass"); // CSV header
                for (Student s : studentList) {
                    // Escape commas in names to prevent CSV corruption
                    String name = s.name.replace(",", "\\,");
                    writer.println(name + "," + s.score + "," + s.grade + "," + s.pass);
                }
                writer.close();
                JOptionPane.showMessageDialog(frame, "Student data exported to " + file.getName());
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(frame, "Cannot write to file: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error exporting student data: " + ex.getMessage());
            }
        });

        // Import Button Logic
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(); // File open dialog
            int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    Scanner fileScanner = new Scanner(file);

                    // Check if file is empty
                    if (!fileScanner.hasNextLine()) {
                        fileScanner.close();
                        JOptionPane.showMessageDialog(frame, "Error: File is empty.");
                        return;
                    }

                    // Validate file format (check header and data rows)
                    String header = fileScanner.nextLine().trim();
                    if (!header.equals("Name,Score,Grade,Pass")) {
                        fileScanner.close();
                        JOptionPane.showMessageDialog(frame, "Error: Invalid CSV header. Expected 'Name,Score,Grade,Pass'.");
                        return;
                    }

                    // Pre-validate all rows before importing
                    ArrayList<Student> tempList = new ArrayList<>();
                    int lineNumber = 1;
                    while (fileScanner.hasNextLine()) {
                        lineNumber++;
                        String line = fileScanner.nextLine().trim();
                        if (line.isEmpty()) continue; // Skip empty lines
                        String[] parts = line.split(",", -1); // -1 to include empty fields
                        if (parts.length < 2 || parts[0].trim().isEmpty()) {
                            fileScanner.close();
                            JOptionPane.showMessageDialog(frame, "Error: Missing name or score at line " + lineNumber);
                            return;
                        }
                        try {
                            double score = Double.parseDouble(parts[1]);
                            if (score < 0 || score > 100) {
                                fileScanner.close();
                                JOptionPane.showMessageDialog(frame, "Error: Invalid score at line " + lineNumber);
                                return;
                            }
                            tempList.add(new Student(parts[0], score)); // Grade and pass recalculated
                        } catch (NumberFormatException ex) {
                            fileScanner.close();
                            JOptionPane.showMessageDialog(frame, "Error: Invalid score format at line " + lineNumber);
                            return;
                        }
                    }
                    fileScanner.close();

                    // If validation passes, update studentList
                    studentList.clear();
                    studentList.addAll(tempList);
                    outputArea.setText("Successfully imported " + studentList.size() + " students.\n");
                    lastActionWasReport = false;

                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: File not found.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error importing student data: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true); // Show the GUI
    }
}