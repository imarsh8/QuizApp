import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class QuizAppGUI extends JFrame implements ActionListener {

    static String username;

    JLabel titleLabel, questionLabel, qNoLabel;
    JRadioButton opt1, opt2, opt3, opt4;
    JButton nextBtn;
    ButtonGroup bg;

    Connection con;
    Statement stmt;
    ResultSet rs;

    int score = 0;
    int qNo = 1;
    int totalQuestions = 0;

    public QuizAppGUI() {
        setTitle("Quiz Application");
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(null);

        titleLabel = new JLabel("Welcome " + username);
        titleLabel.setBounds(180, 10, 300, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);

        qNoLabel = new JLabel("Question 1");
        qNoLabel.setBounds(30, 50, 200, 30);
        add(qNoLabel);

        questionLabel = new JLabel();
        questionLabel.setBounds(30, 80, 500, 30);
        add(questionLabel);

        opt1 = new JRadioButton();
        opt1.setBounds(30, 120, 400, 30);

        opt2 = new JRadioButton();
        opt2.setBounds(30, 150, 400, 30);

        opt3 = new JRadioButton();
        opt3.setBounds(30, 180, 400, 30);

        opt4 = new JRadioButton();
        opt4.setBounds(30, 210, 400, 30);

        bg = new ButtonGroup();
        bg.add(opt1);
        bg.add(opt2);
        bg.add(opt3);
        bg.add(opt4);

        add(opt1);
        add(opt2);
        add(opt3);
        add(opt4);

        nextBtn = new JButton("Next");
        nextBtn.setBounds(220, 270, 100, 40);
        add(nextBtn);

        nextBtn.addActionListener(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        connectDB();
        loadQuestion();
    }

    // DATABASE CONNECTION
    void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/quizdb",
                    "root",
                    "root@123"
            );
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM questions");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOAD QUESTIONS
    void loadQuestion() {
        try {
            if (rs.next()) {
                totalQuestions++;

                qNoLabel.setText("Question " + qNo);
                questionLabel.setText(rs.getString("question"));
                opt1.setText(rs.getString("option1"));
                opt2.setText(rs.getString("option2"));
                opt3.setText(rs.getString("option3"));
                opt4.setText(rs.getString("option4"));

                bg.clearSelection();
                qNo++;
            } else {
                showResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // BUTTON CLICK
    public void actionPerformed(ActionEvent e) {
        try {
            String selected = null;

            if (opt1.isSelected()) selected = opt1.getText();
            if (opt2.isSelected()) selected = opt2.getText();
            if (opt3.isSelected()) selected = opt3.getText();
            if (opt4.isSelected()) selected = opt4.getText();

            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an answer!");
                return;
            }

            String correct = rs.getString("answer");

            if (selected.equalsIgnoreCase(correct)) {
                score++;
            }

            loadQuestion();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // SAVE RESULT TO DATABASE
    void saveResult(double percentage) {
        try {
            String query = "INSERT INTO results (username, score, total, percentage) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setInt(2, score);
            ps.setInt(3, totalQuestions);
            ps.setDouble(4, percentage);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SHOW RESULT
    void showResult() {
        double percentage = ((double) score / totalQuestions) * 100;

        // SAVE TO DB
        saveResult(percentage);

        String result;
        if (percentage >= 80) result = "Excellent 🎉";
        else if (percentage >= 50) result = "Good 👍";
        else result = "Try Again ❗";

        JOptionPane.showMessageDialog(this,
                "User: " + username + "\n\n" +
                        "Score: " + score + "/" + totalQuestions + "\n" +
                        "Percentage: " + percentage + "%\n" +
                        result,
                "Result",
                JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }

    // MAIN (LOGIN)
    public static void main(String[] args) {

        username = JOptionPane.showInputDialog("Enter your name:");

        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty!");
            return;
        }

        new QuizAppGUI();
    }
}