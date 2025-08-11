// StudentGradeTrackerGUI.java
// This program builds a GUI-based student grade tracker using Java Swing.

import javax.swing.*; // Core Swing components
import java.awt.*; // Layout managers, event handling
import java.awt.event.ActionEvent; // Event handling for buttons
import java.awt.event.ActionListener; // Listener for button clicks
import java.util.ArrayList; // Used for dynamic resizing of student data
import java.util.Collections; // Used for finding min/max in ArrayList

/**
 * Main class for the Student Grade Tracker GUI application.
 * Extends JFrame to create the main application window.
 */
public class StudentGradeTrackerGUI extends JFrame {

    // ArrayList to store Student objects. Using ArrayList for dynamic size.
    private ArrayList<Student> students;

    // GUI Components
    private JTextField studentNameField; // Input for student name
    private JTextField gradeField;       // Input for grades
    private JTextArea displayArea;       // Area to display reports and messages
    private JButton addStudentButton;    // Button to add a new student
    private JButton addGradeButton;      // Button to add grades to a student
    private JButton viewStudentButton;   // Button to view a specific student's report
    private JButton viewAllSummaryButton;// Button to view summary of all students
    private JButton clearButton;         // Button to clear the display area

    /**
     * Constructor for the StudentGradeTrackerGUI.
     * Sets up the main window and all GUI components.
     */
    public StudentGradeTrackerGUI() {
        super("Student Grade Tracker"); // Set the window title
        students = new ArrayList<>(); // Initialize the student list

        // Set up the main frame properties
        setSize(800, 600); // Set initial window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(new BorderLayout(10, 10)); // Use BorderLayout for overall layout with gaps

        // --- Create Input Panel (NORTH) ---
        JPanel inputPanel = new JPanel(new GridLayout(2, 3, 10, 10)); // 2 rows, 3 columns, with gaps
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding

        inputPanel.add(new JLabel("Student Name:"));
        studentNameField = new JTextField(20);
        inputPanel.add(studentNameField);

        inputPanel.add(new JLabel("Grade (0-100):"));
        gradeField = new JTextField(10);
        inputPanel.add(gradeField);

        // --- Create Button Panel (SOUTH) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center aligned buttons with gaps
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding

        addStudentButton = new JButton("Add Student");
        addGradeButton = new JButton("Add Grade");
        viewStudentButton = new JButton("View Student Report");
        viewAllSummaryButton = new JButton("View All Summary");
        clearButton = new JButton("Clear Display");

        buttonPanel.add(addStudentButton);
        buttonPanel.add(addGradeButton);
        buttonPanel.add(viewStudentButton);
        buttonPanel.add(viewAllSummaryButton);
        buttonPanel.add(clearButton);

        // --- Create Display Area (CENTER) ---
        displayArea = new JTextArea();
        displayArea.setEditable(false); // Make the text area read-only
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Consistent font for reports
        JScrollPane scrollPane = new JScrollPane(displayArea); // Add scrollability to the display area
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reports & Messages")); // Add a title to the scroll pane

        // --- Add components to the frame ---
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Add Action Listeners to Buttons ---
        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        addGradeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addGradesToStudent();
            }
        });

        viewStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayStudentReport();
            }
        });

        viewAllSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllStudentsSummary();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayArea.setText(""); // Clear the text in the display area
            }
        });
    }

    /**
     * Adds a new student to the system based on input from the studentNameField.
     */
    private void addStudent() {
        String name = studentNameField.getText().trim(); // Get text and trim whitespace
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if student with the same name already exists
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "Student with this name already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        students.add(new Student(name)); // Create and add a new Student object
        displayArea.append(name + " added successfully!\n"); // Append message to display area
        studentNameField.setText(""); // Clear the input field
    }

    /**
     * Allows adding grades to an existing student.
     * Reads student name from studentNameField and grade from gradeField.
     */
    private void addGradesToStudent() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students added yet. Please add a student first.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String name = studentNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter student name to add grades.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student student = findStudent(name); // Find the student by name

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student '" + name + "' not found.", "Student Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String gradeText = gradeField.getText().trim();
        if (gradeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a grade.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double grade = Double.parseDouble(gradeText); // Convert text to double
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                student.addGrade(grade); // Add the valid grade
                displayArea.append("Grade " + String.format("%.2f", grade) + " added for " + student.getName() + ".\n");
                gradeField.setText(""); // Clear the grade input field
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid grade. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays a detailed report for a specific student.
     * Reads student name from studentNameField.
     */
    private void displayStudentReport() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students added yet.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String name = studentNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter student name to view report.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student student = findStudent(name);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student '" + name + "' not found.", "Student Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Clear display area before showing new report to avoid confusion
        displayArea.setText("");
        student.displayReport(displayArea); // Pass displayArea to student's displayReport method
    }

    /**
     * Displays a summary report for all students in the system.
     */
    private void displayAllStudentsSummary() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students added yet.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Clear display area before showing new report to avoid confusion
        displayArea.setText("");
        displayArea.append("\n--- All Students Summary ---\n");
        for (Student student : students) {
            student.displayReport(displayArea); // Pass displayArea to student's displayReport method
            displayArea.append("--------------------\n");
        }
        displayArea.setCaretPosition(displayArea.getDocument().getLength()); // Scroll to bottom
    }

    /**
     * Helper method to find a student by name.
     * @param name The name of the student to find.
     * @return The Student object if found, otherwise null.
     */
    private Student findStudent(String name) {
        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(name)) {
                return student;
            }
        }
        return null;
    }

    /**
     * Main method - entry point of the GUI application.
     * Ensures the GUI is created and updated on the Event Dispatch Thread (EDT).
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentGradeTrackerGUI().setVisible(true); // Create and show the GUI
            }
        });
    }
}

/**
 * Represents a Student with a name and a list of grades.
 */
class Student {
    private String name; // Student's name
    private ArrayList<Double> grades; // List of grades for the student

    /**
     * Constructor for the Student class.
     * @param name The name of the student.
     */
    public Student(String name) {
        this.name = name;
        this.grades = new ArrayList<>(); // Initialize the grades list
    }

    /**
     * Gets the name of the student.
     * @return The student's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of grades for the student.
     * @return The ArrayList of grades.
     */
    public ArrayList<Double> getGrades() {
        return grades;
    }

    /**
     * Adds a grade to the student's list of grades.
     * @param grade The grade to add.
     */
    public void addGrade(double grade) {
        grades.add(grade);
    }

    /**
     * Calculates the average grade for the student.
     * @return The average grade, or 0.0 if no grades are recorded.
     */
    public double calculateAverage() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (double grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }

    /**
     * Finds the highest grade for the student.
     * @return The highest grade, or 0.0 if no grades are recorded.
     */
    public double getHighestGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        // Collections.max finds the maximum element in the list
        return Collections.max(grades);
    }

    /**
     * Finds the lowest grade for the student.
     * @return The lowest grade, or 0.0 if no grades are recorded.
     */
    public double getLowestGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        // Collections.min finds the minimum element in the list
        return Collections.min(grades);
    }

    /**
     * Displays a report for the student, including their name,
     * all grades, average, highest, and lowest grades.
     * @param textArea The JTextArea to append the report to.
     */
    public void displayReport(JTextArea textArea) {
        textArea.append("Student Name: " + name + "\n");
        if (grades.isEmpty()) {
            textArea.append("No grades recorded yet.\n");
        } else {
            textArea.append("Number of Grades: " + grades.size() + "\n"); // Added this line for clarity
            textArea.append("Grades: " + grades + "\n");
            textArea.append(String.format("Average Grade: %.2f\n", calculateAverage()));
            textArea.append(String.format("Highest Grade: %.2f\n", getHighestGrade()));
            textArea.append(String.format("Lowest Grade: %.2f\n", getLowestGrade()));
        }
    }
}
