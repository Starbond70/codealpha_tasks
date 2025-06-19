import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main {
    static ArrayList<Student> studentList = new ArrayList<>();
    static boolean lastActionWasReport = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Grade Tracker");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextField nameField = new JTextField(20);
        JTextField scoreField = new JTextField(5);
        JButton addButton = new JButton("Add Student");
        JButton reportButton = new JButton("Show Report");
        JTextArea outputArea = new JTextArea(20, 40);
        outputArea.setEditable(false);

        frame.add(new JLabel("Student Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Score (0-100):"));
        frame.add(scoreField);
        nameField.addActionListener(e -> scoreField.requestFocusInWindow());
        scoreField.addActionListener(e -> addButton.doClick());

        frame.add(addButton);
        frame.add(reportButton);
        frame.add(new JScrollPane(outputArea));

        // Add student button
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

                if(lastActionWasReport){
                    outputArea.setText("Added: " + name + " - Grade: " + s.grade + "\n");
                }
                else{
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

        // Report button
        reportButton.addActionListener(e -> {
            if (studentList.isEmpty()) {
                outputArea.setText("No student data available.\n");
                return;
            }

            double total = 0, max = studentList.get(0).score, min = studentList.get(0).score;
            int passCount = studentList.size();
            Student top = studentList.get(0), low = studentList.get(0);

            StringBuilder report = new StringBuilder("====== Student Report ======\n");
            for (Student s : studentList) {
                report.append(s.name).append(" - ").append(s.score).append(" (").append(s.grade).append(")\n");
                total += s.score;
                if (s.fail) {
                    passCount--;
                }
                if (s.score > max) {
                    max = s.score;
                    top = s;
                }
                if (s.score < min) {
                    min = s.score;
                    low = s;
                }
            }

            double avg = total / studentList.size();
            double passRate = ((double) passCount / studentList.size()) * 100;

            report.append("\nAverage Score: ").append(avg)
                    .append("\nTopper: ").append(top.name).append(" - ").append(top.score)
                    .append("\nLowest: ").append(low.name).append(" - ").append(low.score)
                    .append("\nPassed: ").append(passCount).append("/").append(studentList.size())
                    .append("\nPassing Rate: ").append(String.format("%.2f", passRate)).append("%\n")
                            .append("========================");

            outputArea.setText(report.toString());
            lastActionWasReport = true;
        });

        frame.setVisible(true);
    }
}
