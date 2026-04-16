import java.sql.*;
import java.util.Scanner;

public class QuizGui {
    public static void main(String[] args) {

        int score = 0;

        try {
            // Load driver (safe)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to MySQL
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/quizdb",
                    "root",
                    "root@123"
            );

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions");

            Scanner sc = new Scanner(System.in);

            // Loop through questions
            while (rs.next()) {

                System.out.println("\nQuestion: " + rs.getString("question"));
                System.out.println("1. " + rs.getString("option1"));
                System.out.println("2. " + rs.getString("option2"));
                System.out.println("3. " + rs.getString("option3"));
                System.out.println("4. " + rs.getString("option4"));

                System.out.print("Enter option (1-4): ");
                int choice = sc.nextInt();

                String selectedAnswer = "";

                switch (choice) {
                    case 1:
                        selectedAnswer = rs.getString("option1");
                        break;
                    case 2:
                        selectedAnswer = rs.getString("option2");
                        break;
                    case 3:
                        selectedAnswer = rs.getString("option3");
                        break;
                    case 4:
                        selectedAnswer = rs.getString("option4");
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }

                String correctAnswer = rs.getString("answer");

                if (selectedAnswer.equalsIgnoreCase(correctAnswer)) {
                    System.out.println("✅ Correct!");
                    score++;
                } else {
                    System.out.println("❌ Wrong! Correct answer: " + correctAnswer);
                }
            }

            // Final score
            System.out.println("\n🎯 Your Score: " + score);

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


