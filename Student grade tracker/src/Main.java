import javax.swing.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Student> studentlist = new ArrayList<>();

        System.out.println("==================================================================================");
        System.out.println("==========================Student Grade tracker manager!==========================");
        System.out.println("==================================================================================");
        System.out.println();

        while(true){
            System.out.println("Select from the following option:");
            System.out.println("1. Enter Data of Students.");
            System.out.println("2. Get the Summary Report.");
            System.out.print("3. Exit the manager. \n >>");

            try {
                int choice = sc.nextInt();
                sc.nextLine();
                if(choice == 1){
                    System.out.print("How many student Do you want to enter? \n >>");
                    int count = sc.nextInt();
                    sc.nextLine();
                    for (int i = 0 ; i < count ; i++){
                        System.out.println("Enter the name of the Student#"+ (i + 1));
                        String name = sc.nextLine();

                        double score = -1 ;
                        while (true) {
                            System.out.print("Enter score for " + name + " (0 to 100): ");
                            try {
                                score = Double.parseDouble(sc.nextLine());
                                if (score < 0 || score > 100) {
                                    System.out.println("Invalid score! Must be between 0 and 100.");
                                } else {
                                    break; // valid score entered
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid Score.");
                            }
                        }
                        Student s = new Student(name , score);
                        studentlist.add(s);
                        System.out.println("Added: "+name+ " whose grade is "+ s.grade);

                    }
                } else if (choice == 2) {
                    if(studentlist.isEmpty()){
                        System.out.println("No student data available. ");
                        break;
                    }

                    double total = 0 ;
                    double max = studentlist.get(0).score;
                    double min = studentlist.get(0).score;
                    Student topStudent = studentlist.get(0);
                    Student lowStudent = studentlist.get(0);
                    int no_of_pass_Students = studentlist.size();

                    System.out.println("======-Student-Report-======");
                    for (Student s : studentlist){
                        System.out.println(s.name + " - " + s.score + " (" + s.grade + " )");
                        total = total+s.score;
                        if (s.fail){
                            no_of_pass_Students--;
                        }
                        if (s.score<min){
                            min = s.score;
                            lowStudent = s;
                        }
                        if (s.score>max){
                            max = s.score;
                            topStudent = s;
                        }
                    }
                    double passingPercent =((double) no_of_pass_Students /studentlist.size())*100;
                    double avgScore = total / studentlist.size();
                    System.out.println("Average Score of Students is "+avgScore);
                    System.out.println(topStudent.name + " is has scored the highest with " + topStudent.score);
                    System.out.println(lowStudent.name + " is has scored the lowest with " + lowStudent.score);
                    System.out.println(no_of_pass_Students + " out of " + studentlist.size() + " students has passed");
                    System.out.println("Passing rate of students is"+ passingPercent + "%");
                } else if (choice == 3) {
                    System.out.println("Thank you for Suing Student tracker manager! See you soon !!!");
                    return;
                }
                else {
                    System.out.println("Select form the given options(1 ,2 or 3)");
                }
            } catch (InputMismatchException e) {
                System.out.println("Select form the given options(1 ,2 or 3)");
                sc.nextLine();
            }
            finally {
                System.out.println("==================================================================================");
            }


        }
    }
}