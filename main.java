/*
- Final Project
-Andrew Reyes, Ryan Demaria(me)
*/
package me;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;



public class Main {

    public static void main(String[] args) {

        System.out.println("Welcome to my Personal Management Program\n");

        Scanner scanner = new Scanner(System.in);
        InputReader reader = new InputReader(scanner);

        People people = new People();

        String input = null;
        while(true) {

            printOptions();

            int selection = reader.nextSelection();

            System.out.println("");

            if(selection == -1) {
                System.out.println("Invalid entry- please try again\n");
                continue;

            } else if(selection == 1) {

                System.out.println("Enter the faculty info:");

                String name = reader.nextLine("Name of the faculty: ");
                String id = reader.nextValidID("ID: ", people);
                String rank = reader.nextRank();
                String department = reader.nextDepartment();

                Faculty faculty = new Faculty(name, id, department, rank);
                people.addPerson(faculty);

                System.out.println("Faculty added!");

            } else if(selection == 2) {

                System.out.println("Enter the student info:");

                String name = reader.nextLine("Name of Student: ");
                String id = reader.nextValidID("ID: ", people);
                double gpa = reader.nextGPA();
                int creditHours = reader.nextCreditHours();

                Student student = new Student(name, id, gpa, creditHours);
                people.addPerson(student);

                System.out.println("Student added!");

            } else if(selection == 3) {

                String id = reader.nextLine("Enter the student's id: ");
                people.printPerson(id, "student");

            } else if(selection == 4) {

                String id = reader.nextLine("Enter the Faculty's id: ");
                people.printPerson(id, "faculty");

            } else if(selection == 5) {

                String name = reader.nextLine("Name of the staff member: ");
                String id = reader.nextValidID("Enter the id: ", people);
                String department = reader.nextDepartment();
                String status = reader.nextStatus();

                Staff staff = new Staff(name, id, department, status);
                people.addPerson(staff);

                System.out.println("Staff member added!");

            } else if(selection == 6) {

                String id = reader.nextLine("Enter the Staff's id: ");
                people.printPerson(id, "staff");

            } else if(selection == 7) {

                String id = reader.nextLine("Enter the id of the person to delete: ");
                Person removedPerson = people.removeByID(id);

                if(removedPerson != null) {
                    System.out.println("Person has been removed!");
                } else {
                    System.out.println("Sorry no such person exists.");
                }

            } else if(selection == 8) {
                break;
            }

            System.out.println("");

        }

        char reportResponse = reader.nextYesOrNo("Would you like to create the report? (Y/N): ");

        if(reportResponse == 'y') {
            int sortType = reader.nextSortType("Would you like to sort your students by descending gpa or name (1 for gpa, 2 for name): ");

            writeReport(people, sortType);
        }

        System.out.println("Goodbye!");

    }

    private static void writeReport(People people, int sortType) {

        File report = new File("report.txt");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(report);
        } catch(FileNotFoundException e) {
            System.out.println("Failed to create report!! File could not be found.");
            return;
        }

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY");
        String formattedDate = now.format(formatter);

        writer.print("Report created on " + formattedDate + "\n***********************\n\n");

        String facultyMembers = "Faculty Members\n-------------------------\n";
        String staffMembers = "Staff Members\n-------------------\n";

        int facultyCount = 1;
        int staffCount = 1;

        for(Person person : people.getList()) {
            if(person instanceof Faculty) {
                Faculty faculty = (Faculty)person;
                facultyMembers += facultyCount + ". " + faculty.getFullName() + "\n";
                facultyMembers += "ID: " + faculty.getID() + "\n";
                facultyMembers += faculty.getRank() + "," + faculty.getDepartment() + "\n\n";
                facultyCount++;
            } else if(person instanceof Staff) {
                Staff staff = (Staff)person;
                staffMembers += staffCount + ". " + staff.getFullName() + "\n";
                staffMembers += "ID: " + staff.getID() + "\n";
                staffMembers += staff.getDepartment() + ", " + staff.getStatus() + "\n\n";
                staffCount++;
            }
        }

        writer.print(facultyMembers);
        writer.print(staffMembers);

        int studentCount = 1;
        writer.print("Students\n-----------\n");
        for(Student student : people.getSortedStudents(sortType)) {
            writer.println(studentCount + ". " + student.getFullName());
            writer.println("ID: " + student.getID());
            writer.println("Gpa: " + student.getGPA());
            writer.println("Credit hours: " + student.getCreditHours() + "\n");
            studentCount++;
        }

        writer.close();

        System.out.println("Report created and saved on your hard drive!");
    }

    private static void printOptions() {
        System.out.println("Choose one of the options:\n"
                + "1. Enter the information of a faculty\n"
                + "2. Enter the information of a student\n"
                + "3. Print tuition invoice for a student\n"
                + "4. Print faculty information\n"
                + "5. Enter the information of a staff member\n"
                + "6. Print the information of a staff member\n"
                + "7. Delete a person\n"
                + "8. Exit Program\n");
    }
}

class NameComparator implements Comparator<Person> {

    @Override
    public int compare(Person person1, Person person2) {
        return person1.getFullName().compareTo(person2.getFullName());
    }

}

class GPAComparator implements Comparator<Student> {

    @Override
    public int compare(Student student1, Student student2) {
        return (int)(student2.getGPA()*100 - student1.getGPA()*100);
    }

}

class People {

    private ArrayList<Person> personList;

    public People() {
        personList = new ArrayList<>();
    }

    public void addPerson(Person person) {
        personList.add(person);
    }

    public boolean containsID(String id) {
        return getByID(id) != null;
    }

    public Person getByID(String id) {
        for(Person person : personList) {
            if(person.getID().equals(id)) return person;
        }
        return null;
    }

    public Person removeByID(String id) {
        Person person = getByID(id);
        if(person != null) personList.remove(person);
        return person;
    }

    public void printPerson(String id, String type) {
        Person person = getByID(id);

        if(person != null && person.getType().equals(type)) {
            System.out.println("");
            person.print();
        } else {
            String typeParse = "";
            if(type.equals("student")) typeParse = "student";
            else if(type.equals("staff")) typeParse = "Staff member";
            else if(type.equals("faculty")) typeParse = "Faculty";

            System.out.printf("No %s matched!\n", typeParse);
        }
    }

    public ArrayList<Student> getSortedStudents(int sortType) {
        ArrayList<Student> students = new ArrayList<>();
        for(Person person : personList) {
            if(person instanceof Student) {
                students.add((Student)person);
            }
        }

        if(sortType == 1) {
            GPAComparator comparator = new GPAComparator();
            Collections.sort(students, comparator);
        } else if(sortType == 2) {
            NameComparator comparator = new NameComparator();
            Collections.sort(students, comparator);
        }

        return students;
    }

    public ArrayList<Person> getList() {
        return personList;
    }

}

class InputReader {
    private Scanner scanner;
    private ArrayList<String> validDepartments;
    private ArrayList<String> validRanks;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
        this.validDepartments = new ArrayList<>();
        this.validRanks = new ArrayList<>();

        Collections.addAll(validDepartments, "mathematics", "engineering", "english");
        Collections.addAll(validRanks, "professor", "adjunct");
    }

    public int nextSelection() {
        System.out.print("Enter your Selection: ");
        String input = scanner.nextLine();

        if(input.length() != 1) return -1;
        if(input.charAt(0) - '0' < 0 || input.charAt(0) - '0' > 8) return -1;

        return input.charAt(0) - '0';
    }

    public String nextValidID(String prompt, People people) {
        String input = "";
        while(true) {
            System.out.print(prompt);
            input = scanner.nextLine();

            boolean valid = true;
            if(input.length() != 6) valid = false;
            if(!input.matches("[a-zA-Z]{2}\\d{4}")) valid = false;
            if(valid == false) {
                System.out.println("Invalid ID format. Must be LetterLetterDigitDigitDigitDigit");
                continue;
            }

            if(/*people != null && */people.containsID(input)) {
                System.out.println("This ID is already in use.");
                continue;
            }

            break;
        }
        return input;
    }

    public String nextLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int nextCreditHours() {
        int creditHours = 0;
        String input = "";
        while(true) {
            System.out.print("Credit hours: ");
            input = scanner.nextLine();

            try {
                creditHours = Integer.parseInt(input);
            } catch(NumberFormatException e) {
                System.out.println("Invalid Decimal Format! try again!");
                continue;
            }

            break;
        }
        return creditHours;
    }

    public double nextGPA() {
        double gpa = 0.0;
        String input = "";
        while(true) {
            System.out.print("Gpa: ");
            input = scanner.nextLine();

            try {
                gpa = Double.parseDouble(input);
            } catch(NumberFormatException e) {
                System.out.println("Invalid Decimal Format! try again!");
                continue;
            }

            break;
        }

        return gpa;
    }

    public String nextRank() {
        String input = "";
        while(true) {
            System.out.print("Rank: ");
            String unfilteredInput = scanner.nextLine();
            input = (unfilteredInput).toLowerCase();

            if(!validRanks.contains(input)) {
                System.out.println("\"" + unfilteredInput + "\" is invalid");
                continue;
            }

            break;
        }

        input = Character.toUpperCase(input.charAt(0)) + input.substring(1, input.length());
        return input;
    }

    public String nextDepartment() {
        String input = "";
        while(true) {
            System.out.print("Department: ");
            input = (scanner.nextLine()).toLowerCase();

            if(!validDepartments.contains(input)) {
                System.out.println("\"" + input + "\" is invalid");
                continue;
            }

            break;
        }

        input = Character.toUpperCase(input.charAt(0)) + input.substring(1, input.length());
        return input;
    }

    public String nextStatus() {
        System.out.print("Status, Enter P for Part Time, or Enter F for Full time: ");
        String input = "";
        while(true) {
            input = (scanner.nextLine()).toLowerCase();

            if(!input.equals("f") && !input.equals("p")) {
                System.out.print("\"" + input + "\" is invalid: ");
                continue;
            }

            break;
        }

        if(input.equals("f")) input = "Full Time";
        else if(input.equals("p")) input = "Part Time";

        return input;
    }

    public char nextYesOrNo(String prompt) {
        System.out.print(prompt);
        String input = "";
        while(true) {
            input = (scanner.nextLine()).toLowerCase();

            if(!input.equals("y") && !input.equals("n")) {
                System.out.print("Invalid input, try again: ");
                continue;
            }

            break;
        }
        return input.charAt(0);
    }

    public int nextSortType(String prompt) {
        System.out.print(prompt);
        int sortType = 0;
        while(true) {
            String input = scanner.nextLine();

            try {
                sortType = Integer.parseInt(input);
            } catch(NumberFormatException e) {
                System.out.print("Invalid Decimal Format! try again: ");
                continue;
            }

            if(sortType != 0 && sortType != 1) {
                System.out.print("Invalid sort type, try again: ");
                continue;
            }

            break;
        }
        return sortType;
    }
}

abstract class Person {

    private String fullName;
    private String id;

    // print fee invoice for students
    // print print information for staff and faculty
    public abstract void print();

    public Person() {
        this.id = "AA0000";
        this.fullName = "UNKNOWN";
    }

    public Person(String fullName, String id) {
        this.id = id;
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getID() {
        return id;
    }

    public abstract String getType();

}

class Student extends Person {

    private static final double PER_CREDIT_HOUR = 236.45;
    private static final double ADMINISTRATIVE_FEE = 52;

    private double gpa;
    private int creditHours;

    public Student(String fullName, String id, double gpa, int creditHours) {
        super(fullName, id);
        this.gpa = gpa;
        this.creditHours = creditHours;
    }

    @Override
    public void print() {

        double tuition = calculateTuition();
        double discount = 0.0;

        if(gpa >= 3.85) {
            discount = (tuition * (1.0 / 0.75)) - tuition;
        }

        System.out.printf("Here is the tuition invoice for %s:\n\n", getFullName());
        System.out.printf("---------------------------------------------------------------------------\n");
        System.out.printf("%s \t \t \t %s\n", getFullName(), getID());
        System.out.printf("Credit Hours: %d ($%.2f/credit hour)\n", creditHours, PER_CREDIT_HOUR);
        System.out.printf("Fee: $%d\n", (int)ADMINISTRATIVE_FEE);
        System.out.printf("Total payment (after discount): $%,.2f \t ($%.2f discount applied)\n", tuition, discount);
        System.out.printf("---------------------------------------------------------------------------\n\n");
    }

    public double calculateTuition() {
        double cost = creditHours * PER_CREDIT_HOUR + ADMINISTRATIVE_FEE;

        if(gpa >= 3.85) {
            cost *= 0.75;
        }

        return cost;
    }

    public double getGPA() {
        return gpa;
    }

    public void setGPA(double gpa) {
        this.gpa = gpa;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public String getType() {
        return "student";
    }
}

abstract class Employee extends Person {

    private String department;

    public Employee() {
        department = "UNKNOWN";
    }

    public Employee(String fullName, String id, String department) {
        super(fullName, id);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}

class Staff extends Employee {

    private String status;

    public Staff(String fullName, String id, String department, String status) {
        super(fullName, id, department);
        this.status = status;
    }

    @Override
    public void print() {
        System.out.printf("---------------------------------------------------------------------------\n");
        System.out.printf("%s \t \t %s\n", getFullName(), getID());
        System.out.printf("%s, %s\n", getDepartment(), status);
        System.out.printf("---------------------------------------------------------------------------\n");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return "staff";
    }

}

class Faculty extends Employee {

    private String rank;

    public Faculty() {
        rank = "UNKNOWN";
    }

    public Faculty(String fullName, String id, String department, String rank) {
        super(fullName, id, department);
        this.rank = rank;
    }

    @Override
    public void print() {
        System.out.printf("---------------------------------------------------------------------------\n");
        System.out.printf("%s \t \t %s\n", getFullName(), getID());
        System.out.printf("%s, %s\n", getDepartment(), rank);
        System.out.printf("---------------------------------------------------------------------------\n");
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getType() {
        return "faculty";
    }

}
