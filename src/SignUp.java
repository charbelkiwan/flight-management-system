import java.io.BufferedReader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class SignUp {

    public static void showSignUp() {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter First Name");
        firstNameField.setMinWidth(200);
        firstNameField.setMaxWidth(200);
        firstNameField.setPrefWidth(200);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter Last Name");
        lastNameField.setMinWidth(200);
        lastNameField.setMaxWidth(200);
        lastNameField.setPrefWidth(200);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMinWidth(200);
        emailField.setMaxWidth(200);
        emailField.setPrefWidth(200);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMinWidth(200);
        passwordField.setMaxWidth(200);
        passwordField.setPrefWidth(200);

        TextField telephoneField = new TextField();
        telephoneField.setPromptText("Enter Telephone Number");
        telephoneField.setMinWidth(200);
        telephoneField.setMaxWidth(200);
        telephoneField.setPrefWidth(200);
        
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Visible Password");
        visiblePasswordField.setVisible(false);
        
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setStyle("-fx-text-fill: navy;");
        showPasswordCheckBox.setSelected(false); 

        Button signUpButton = new Button("Sign In");
        signUpButton.setOnAction(e -> {

            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            if(showPasswordCheckBox.isSelected()){
                passwordField.setText(visiblePasswordField.getText())
                ;
            }
            else{visiblePasswordField.setText(passwordField.getText());}
            String email = emailField.getText();
            String password = !passwordField.getText().isEmpty() ? passwordField.getText() : visiblePasswordField.getText();
            String telephone = telephoneField.getText();
            
            String errorMessage = validateFields(firstName, lastName, email, password, telephone);

            
            if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty()){
                showAlert("Enter all fields" , "Some of the fields are empty");
                return;}

            if (errorMessage != null) {
                showAlert("Invalid Input", errorMessage);
                return;
            }


            boolean signUpSuccess = saveUserDataToFile(firstName, lastName, email, password, telephone);

            if (signUpSuccess) {
                showAlertSuccessful("Sign Up Successful", "Congratulations, you have successfully signed up! \nNow log in with your email and password.");
                signUpStage.close();
            } else {
                showAlert("Error", "Failed to save user data. Please try again.");
            }
        });
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e-> signUpStage.close());
        
        showPasswordCheckBox.setOnAction(e -> {
        if (showPasswordCheckBox.isSelected()) {
 
            visiblePasswordField.setText(passwordField.getText());
            passwordField.setVisible(false);
            visiblePasswordField.setVisible(true);
        } else {

            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
        }
    });
        
        Label firstName = new Label("First Name:");
        firstName.setStyle("-fx-text-fill: navy;");
        Label lastName = new Label("Last Name:");
        lastName.setStyle("-fx-text-fill: navy;");
        Label email = new Label("Email:");
        email.setStyle("-fx-text-fill: navy;");
        Label password = new Label("Password:");
        password.setStyle("-fx-text-fill: navy;");
        Label tel = new Label("Telephone:");
        tel.setStyle("-fx-text-fill: navy;");
        

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(firstName, 0, 0);
        gridPane.add(firstNameField, 1, 0);
        gridPane.add(lastName, 0, 1);
        gridPane.add(lastNameField, 1, 1);
        gridPane.add(email, 0, 2);
        gridPane.add(emailField, 1, 2);
        gridPane.add(password, 0, 3);
        gridPane.add(passwordField, 1, 3);
        gridPane.add(visiblePasswordField, 1, 3);
        gridPane.add(showPasswordCheckBox, 2, 3);
        gridPane.add(tel, 0, 4);
        gridPane.add(telephoneField, 1, 4);
        gridPane.add(signUpButton, 1, 5);
        gridPane.add(backButton,2 ,6);
        
        InputStream photoStream = Main.class.getResourceAsStream("/Files/Airplane pic.jpg");
        Image backgroundImage = new Image(photoStream);
        ImageView backgroundImageView = new ImageView(backgroundImage);
        


        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, gridPane);

        Scene scene = new Scene(stackPane, 625, 416);
        signUpStage.setScene(scene);
        signUpStage.setResizable(false);
        signUpStage.show();
    }
    
     private static String validateFields(String firstName, String lastName, String email,
                                         String password, String telephone) {
        if (!isValidName(firstName)) {
            return "Please enter a valid first name (letters only).";
        }

        if (!isValidName(lastName)) {
            return "Please enter a valid last name (letters only).";
        }

        String passwordError = isValidPassword(password);
        if (passwordError != null) {
            return passwordError;
        }

        if (!isValidEmail(email)) {
            return "Please enter a valid email address.";
        }
        if(!isEmailUnique(email)){
            return "This email is already registered.";
        }

        if (!isValidPhoneNumber(telephone)) {
            return "Please enter a valid phone number (e.g., 70123456 or 03123456).";
        }

        return null;
    }
    private static boolean isValidName(String name) {

        return name.matches("[a-zA-Z]+");
    }

    private static String isValidPassword(String password) {
        // Password must be at least 8 characters long and contain at least one digit, one letter, and one special character
        return password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=]).*$")
                ? null : "Password must be at least 8 characters and contain digits, letters, and special characters.";
    }

    private static boolean isValidEmail(String email) {
        // Basic email format validation
         return email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"); 
           
        }
    private static boolean isEmailUnique(String email){

        try (BufferedReader br = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/Files/client.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\s+"); 

                if (fields.length >= 6) {
                String emailInFile = fields[4];
                
                if (emailInFile != null && emailInFile.equalsIgnoreCase(email)) {
                    return false; 
                }}
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; 
        }

        return true;
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{8}");
    }

    private static boolean saveUserDataToFile(String firstName, String lastName, String email,
                                              String password, String telephone) {
        String filePath = "src/Files/client.txt";
        File file = new File(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file));
             BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {

            int lastClientNumber = getLastClientNumber(br);

            // Increment the client number and format it as a 3-digit string (e.g., "001", "002", ..., "010", "011", ...)
            String clientNumber = String.format("%03d", lastClientNumber + 1);
            
            br.close();

            bw.write(clientNumber + " " + firstName + "     " + lastName + "     " + hashPassword(password) + "      " + email + "      " + telephone);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static int getLastClientNumber(BufferedReader br) throws IOException {
        int lastClientNumber = 0;
        String line;
        while ((line = br.readLine()) != null) {
            String[] fields = line.split("\\s+");
            if (fields.length > 0) {
                try {
                    int clientNumber = Integer.parseInt(fields[0]);
                    if (clientNumber > lastClientNumber) {
                        lastClientNumber = clientNumber;
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore lines with invalid client number format
                }
            }
        }
        return lastClientNumber;
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


    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private static void showAlertSuccessful(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
