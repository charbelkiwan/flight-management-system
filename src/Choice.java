import java.io.InputStream;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Choice {
    private Stage primaryStage;
    private Client client;
    
    public void showChoiceScene(Stage primaryStage,Client client) {
        this.primaryStage=primaryStage;
        this.client = client;
        
        Label selectwhatyouare = new Label("Select please:");
        selectwhatyouare.setStyle("-fx-text-fill: navy;");
        Font customFont = Font.font("Times New Roman", 32);
        selectwhatyouare.setFont(customFont);

        String multiLineText = "Select Admin and then enter the password to add, delete and modify reservations."
                + " \nSelect Client to add or cancel a reservation.";

   
        Label descriptionlabel = new Label(multiLineText);
        descriptionlabel.setWrapText(true);
        descriptionlabel.setAlignment(Pos.CENTER); 
        descriptionlabel.setTextAlignment(TextAlignment.CENTER);
        descriptionlabel.setStyle("-fx-text-fill: navy;");
        
        Button clientButton = new Button("Client");
        clientButton.setPrefWidth(100); 
        clientButton.setPrefHeight(50);
        clientButton.setOnAction(e -> showClientScene(primaryStage,client));

        Button adminButton = new Button("Admin");
        adminButton.setPrefWidth(100);
        adminButton.setPrefHeight(50);
        adminButton.setOnAction(e -> {
            askPassword();
                });
        Button backButton = new Button("Back");
        backButton.setOnAction(e-> showMain());
        
        HBox hbox = new HBox(20); 
        hbox.setAlignment(Pos.CENTER);

        // Add buttons to the HBox
        hbox.getChildren().addAll(adminButton, clientButton);

  
        VBox vbox = new VBox(40); 
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));


        vbox.getChildren().addAll(selectwhatyouare,descriptionlabel, hbox,backButton);
        
        
        InputStream photoStream = Main.class.getResourceAsStream("/Files/background.jpg");
        Image backgroundImage = new Image(photoStream);
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, vbox);


        Scene scene = new Scene(stackPane, 625, 416);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Choice Scene");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    private void askPassword() {
    TextInputDialog passwordDialog = new TextInputDialog();
    passwordDialog.setTitle("Enter Password");
    passwordDialog.setHeaderText(null);
    passwordDialog.setContentText("Please enter your password: \n(Password = Admin123$)");

    Optional<String> result = passwordDialog.showAndWait();

    result.ifPresent(password -> {

        if (password.equals("Admin123$")) {
             showAdminScene();

        } else {
 
            showAlert("Incorrect Password", "Please enter the correct password.");
            showChoiceScene(primaryStage,client);
        }
    });
}
    private void showClientScene(Stage primaryStage,Client client) {
        ClientSceneView clientSceneView = new ClientSceneView(primaryStage,client);
        Scene scene = clientSceneView.createScene(primaryStage, client);
        primaryStage.setScene(scene); 
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showAdminScene() {
        AdminSceneView adminSceneView = new AdminSceneView(primaryStage,client);
        adminSceneView.showAdminScene();
    }
    private void showMain(){
        Main main = new Main();
        main.start(primaryStage);
    }
}
