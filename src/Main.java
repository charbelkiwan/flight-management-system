import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class Main extends Application {
    
    private Stage primaryStage;
    private Map<String, String> clientData;
    private Client client;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login");

        clientData = readClientData();

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMinWidth(225);
        emailField.setMaxWidth(225);
        emailField.setPrefWidth(225);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMinWidth(225);
        passwordField.setMaxWidth(225);
        passwordField.setPrefWidth(225);
        
        Label welcome = new Label("Welcome!");
        welcome.setStyle("-fx-text-fill: navy;");
        Font customFont = Font.font("Times New Roman", 38);
        welcome.setFont(customFont);
        
        Label enteremailpassword = new Label("Please enter your email and password.");
        enteremailpassword.setStyle("-fx-text-fill: navy;");
       
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Visible Password");
        visiblePasswordField.setVisible(false); 
        visiblePasswordField.setMinWidth(225);
        visiblePasswordField.setMaxWidth(225);
        visiblePasswordField.setPrefWidth(225);
        
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setStyle("-fx-text-fill: navy;");
        showPasswordCheckBox.setSelected(false);
        
        Button loginButton = new Button("Log In");
        loginButton.setOnAction(e -> {
            clientData = readClientData();
            String email = emailField.getText();
            if(showPasswordCheckBox.isSelected()){
                passwordField.setText(visiblePasswordField.getText())
                ;
            }
            else{visiblePasswordField.setText(passwordField.getText());}
            
            String password = !passwordField.getText().isEmpty() ? passwordField.getText() : visiblePasswordField.getText();
            
            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Input Error", "Please enter both email and password.");}
                else if (checkCredentials(email, password)) {
                    Client loggedInClient = getClientInfoByEmailAndPassword(email, password);
                    showAlert("Login Successful", "Welcome, " + getClientName(email) + "!");
                    this.client = loggedInClient;
                    showChoiceScene(primaryStage,client);
                
            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }
        });

        Label signUpLabel = new Label("Don't have an account?");
        signUpLabel.setStyle("-fx-text-fill: navy;");
        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> {
            SignUp.showSignUp();
            
                });
        
        InputStream photoStream = getClass().getResourceAsStream("/Files/Airplane pic.jpg");
        Image backgroundImage = new Image(photoStream);
 
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);

        showPasswordCheckBox.setOnAction(e -> {
        if (showPasswordCheckBox.isSelected()) {

            visiblePasswordField.setText(passwordField.getText());
            passwordField.setVisible(false);
            visiblePasswordField.setVisible(true);
        } else {

            passwordField.setText(visiblePasswordField.getText());
            visiblePasswordField.setVisible(false);
            passwordField.setVisible(true);
        }
    });
        StackPane passwordPane = new StackPane();
        passwordPane.getChildren().addAll(passwordField, visiblePasswordField);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(welcome,enteremailpassword,emailField, passwordPane, showPasswordCheckBox, loginButton, signUpLabel, signUpButton);
        VBox.setMargin(showPasswordCheckBox, new Insets(0, 0, 10, 0));
        VBox.setMargin(loginButton, new Insets(0, 0, 15, 0));
        
        StackPane stackPane = new StackPane(backgroundImageView, layout);
        
        Scene scene = new Scene(stackPane, 625, 416);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void showChoiceScene(Stage primaryStage,Client client) {
        Choice choice = new Choice();
        choice.showChoiceScene(primaryStage,client);
    }
    
    
    private Map<String, String> readClientData() {
        Map<String, String> data = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/client.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\s+");
                if (fields.length >= 6) {
                    String email = fields[4];
                    String hashedPassword = fields[3];
                    data.put(email, hashedPassword);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    private static Client getClientInfoByEmailAndPassword(String email, String Password) {
        String hashedPassword = hashPassword(Password);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/Files/client.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\s+");
                if (fields.length >= 6) {
                    String storedEmail = fields[4];
                    String storedHashedPassword = fields[3];

                    if (email.equals(storedEmail) && hashedPassword.equals(storedHashedPassword)) {
                        int id = Integer.parseInt(fields[0]);
                        String firstName = fields[1];
                        String lastName = fields[2];
                        String tel = fields[5];


                        return new Client(null, id, firstName, lastName, hashedPassword, tel, null);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean checkCredentials(String email, String password) {
        if (clientData.containsKey(email)) {
            String hashedPasswordFromFile = clientData.get(email);
            String hashedInputPassword = hashPassword(password);

            return hashedPasswordFromFile.equals(hashedInputPassword);
        }

        return false;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } 
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }   
    }
    

    private String getClientName(String email) {
 
        if (clientData.containsKey(email)) {

            try (BufferedReader br = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/Files/client.txt")))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split("\\s+");
                    if (fields.length >= 6 && fields[4].equals(email)) {
                        return fields[1] + " " + fields[2]; // 
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ""; 
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
