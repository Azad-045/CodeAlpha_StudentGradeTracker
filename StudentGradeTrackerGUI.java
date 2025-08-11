import javax.swing.*; 
import java.awt.*; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.util.ArrayList; 
import java.util.Collections; 


public class StudentGradeTrackerGUI extends JFrame {


    private ArrayList<Student> students;


    private JTextField studentNameField; 
    private JTextField gradeField;       
    private JTextArea displayArea;       
    private JButton addStudentButton;    
    private JButton addGradeButton;      
    private JButton viewStudentButton;   
    private JButton viewAllSummaryButton;
    private JButton clearButton;         


    public StudentGradeTrackerGUI() {
        super("Student Grade Tracker"); 
        students = new ArrayList<>(); 


        setSize(800, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(10, 10)); 


        JPanel inputPanel = new JPanel(new GridLayout(2, 3, 10, 10)); 
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); 

        inputPanel.add(new JLabel("Student Name:"));
        studentNameField = new JTextField(20);
        inputPanel.add(studentNameField);

        inputPanel.add(new JLabel("Grade (0-100):"));
        gradeField = new JTextField(10);
        inputPanel.add(gradeField);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 

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


        displayArea = new JTextArea();
        displayArea.setEditable(false); 
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        JScrollPane scrollPane = new JScrollPane(displayArea); 
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reports & Messages")); 


        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);


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
                displayArea.setText(""); 
            }
        });
    }


    private void addStudent() {
        String name = studentNameField.getText().trim(); 
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "Student with this name already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        students.add(new Student(name)); 
        displayArea.append(name + " added successfully!\n"); 
        studentNameField.setText(""); 
    }


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

        Student student = findStudent(name); 

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
            double grade = Double.parseDouble(gradeText); 
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                student.addGrade(grade); 
                displayArea.append("Grade " + String.format("%.2f", grade) + " added for " + student.getName() + ".\n");
                gradeField.setText(""); 
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid grade. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }


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


        displayArea.setText("");
        student.displayReport(displayArea); 
    }


    private void displayAllStudentsSummary() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students added yet.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        displayArea.setText("");
        displayArea.append("\n--- All Students Summary ---\n");
        for (Student student : students) {
            student.displayReport(displayArea); 
            displayArea.append("--------------------\n");
        }
        displayArea.setCaretPosition(displayArea.getDocument().getLength()); 
    }


    private Student findStudent(String name) {
        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(name)) {
                return student;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentGradeTrackerGUI().setVisible(true); 
            }
        });
    }
}


class Student {
    private String name; 
    private ArrayList<Double> grades; 


    public Student(String name) {
        this.name = name;
        this.grades = new ArrayList<>(); 
    }


    public String getName() {
        return name;
    }


    public ArrayList<Double> getGrades() {
        return grades;
    }


    public void addGrade(double grade) {
        grades.add(grade);
    }


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


    public double getHighestGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        return Collections.max(grades);
    }


    public double getLowestGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        return Collections.min(grades);
    }


    public void displayReport(JTextArea textArea) {
        textArea.append("Student Name: " + name + "\n");
        if (grades.isEmpty()) {
            textArea.append("No grades recorded yet.\n");
        } else {
            textArea.append("Number of Grades: " + grades.size() + "\n"); 
            textArea.append("Grades: " + grades + "\n");
            textArea.append(String.format("Average Grade: %.2f\n", calculateAverage()));
            textArea.append(String.format("Highest Grade: %.2f\n", getHighestGrade()));
            textArea.append(String.format("Lowest Grade: %.2f\n", getLowestGrade()));
        }
    }
}
