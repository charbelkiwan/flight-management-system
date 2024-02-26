import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class AdminSceneView {
    private VBox adminBox;
    private Scene adminScene;
    private Stage primaryStage;
    private List<Seat> seats;
    private Seat seat;
    private HBox buttonBox;
    private Button addButton;
    private Button deleteButton;
    private Button modifyButton;
    private Button backButton;
    private Label welcomeLabel;
    private Label selectLabel;
    private Client client;


    public AdminSceneView(Stage primaryStage,Client client) {
        this.primaryStage = primaryStage;
        this.client=client;
        this.seats = readSeatsFromFile();
        createAdminScene();
    }

    private void createAdminScene() {
        addButton = new Button("Add Reservation");
        addButton.setPrefWidth(150); 
        addButton.setPrefHeight(50);
        addButton.setOnAction(e -> showAddReservationForm());

        deleteButton = new Button("Delete Reservation");
        deleteButton.setPrefWidth(150);
        deleteButton.setPrefHeight(50);
        deleteButton.setOnAction(e -> showDeleteReservationForm());

        modifyButton = new Button("Modify Reservation");
        modifyButton.setPrefWidth(150); 
        modifyButton.setPrefHeight(50);
        modifyButton.setOnAction(e -> showModifyReservationForm());

        backButton = new Button("Back");
        backButton.setOnAction(e -> backToChoice());
        

        welcomeLabel = new Label("Welcome, sir!");
        welcomeLabel.setStyle("-fx-font-weight: bold;-fx-text-fill: white;");
        Font customFont = Font.font("Times New Roman", 38);
        welcomeLabel.setFont(customFont);

        selectLabel = new Label("Select what you want to do:");
        selectLabel.setStyle("-fx-text-fill: white;");
        Font customFont1 = Font.font("Times New Roman", 14);
        selectLabel.setFont(customFont1);

        buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, deleteButton, modifyButton);
        buttonBox.setAlignment(Pos.CENTER);

        adminBox = new VBox(20);
        adminBox.getChildren().addAll(welcomeLabel, selectLabel, buttonBox, backButton);
        VBox.setMargin(buttonBox, new Insets(0, 0, 20, 0));
        adminBox.setAlignment(Pos.CENTER);
        
        InputStream photoStream = Main.class.getResourceAsStream("/Files/map.jpg");
        Image backgroundImage = new Image(photoStream);
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setOpacity(0.8);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, adminBox);

        adminScene = new Scene(stackPane, 625, 416);
    }

    public void showAdminScene() {
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Scene");
        primaryStage.show();
    }

    private void backToChoice() {
        Choice choice = new Choice();
        choice.showChoiceScene(primaryStage,client);
    }

    private void showAddReservationForm() {
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: white;");

        TextField flightNumberField = new TextField();
        flightNumberField.setMinWidth(175);
        flightNumberField.setMaxWidth(175);
        flightNumberField.setPrefWidth(175);
        flightNumberField.setPromptText("Flight Number");
        
        TextField seatNumberField = new TextField();
        seatNumberField.setMinWidth(175);
        seatNumberField.setMaxWidth(175);
        seatNumberField.setPrefWidth(175);
        seatNumberField.setPromptText("Seat Number");
        
        TextField classField = new TextField();
        classField.setMinWidth(175);
        classField.setMaxWidth(175);
        classField.setPrefWidth(175);
        classField.setPromptText("Class (Economy/Business/...)");
        
        DatePicker departureDateField = new DatePicker();
        departureDateField.setPromptText("Departure Date");
        TextField departureTimeField = new TextField();
        departureTimeField.setMinWidth(175);
        departureTimeField.setMaxWidth(175);
        departureTimeField.setPrefWidth(175);
        departureTimeField.setPromptText("Departure Time (HH:mm)");
        TextField departurePlaceField = new TextField();
        departurePlaceField.setMinWidth(175);
        departurePlaceField.setMaxWidth(175);
        departurePlaceField.setPrefWidth(175);
        departurePlaceField.setPromptText("Departure Place");
        DatePicker arrivalDateField = new DatePicker();
        arrivalDateField.setPromptText("Arrival Date");
        TextField arrivalTimeField = new TextField();
        arrivalTimeField.setMinWidth(175);
        arrivalTimeField.setMaxWidth(175);
        arrivalTimeField.setPrefWidth(175);
        arrivalTimeField.setPromptText("Arrival Time (HH:mm)");
        TextField landingPlaceField = new TextField();
        landingPlaceField.setMinWidth(175);
        landingPlaceField.setMaxWidth(175);
        landingPlaceField.setPrefWidth(175);
        landingPlaceField.setPromptText("Landing Place");
        TextField optionsField = new TextField();
        optionsField.setMinWidth(175);
        optionsField.setMaxWidth(175);
        optionsField.setPrefWidth(175);
        optionsField.setPromptText("Options (special-meal/on-board-internet/etc.)");
        TextField priceField = new TextField();
        priceField.setMinWidth(175);
        priceField.setMaxWidth(175);
        priceField.setPrefWidth(175);
        priceField.setPromptText("Price");
        
        
        
        Button backButton1 = new Button("Back");
        backButton1.setOnAction(e -> {
        adminBox.getChildren().clear();
        adminBox.getChildren().addAll(welcomeLabel, selectLabel, buttonBox, backButton);
        primaryStage.setScene(adminScene);
        });
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String flightNo;
            try {
                flightNo = flightNumberField.getText();
            } catch (NumberFormatException ex) {
                messageLabel.setText("Invalid Flight Number");
                return;
            }
            if(flightNo.isEmpty()){
                messageLabel.setText("Flight number cannot be empty");
                return;
            }

            String seatNo = seatNumberField.getText();
            if(seatNo.isEmpty()){
                messageLabel.setText("Seat number cannot be empty");
                return;
            }
            String seatClassStr = classField.getText();
            if (seatClassStr.isEmpty()) {
                messageLabel.setText("Seat Class cannot be empty");
                return;
            }
            
            char seatClasschar = seatClassStr.charAt(0);

            LocalDate departureDate = departureDateField.getValue();
            if (departureDate == null) {
                messageLabel.setText("Invalid Departure Date");
                return;
            }

            LocalTime departureTime;
            try {
                departureTime = LocalTime.parse(departureTimeField.getText());
            } catch (Exception ex) {
                messageLabel.setText("Invalid Departure Time");
                return;
            }

            String departurePlace = departurePlaceField.getText();
            if (departurePlace.isEmpty()) {
                messageLabel.setText("Price cannot be empty");
                return;
            }
            LocalDate arrivalDate = arrivalDateField.getValue();
            if (arrivalDate == null) {
                messageLabel.setText("Invalid Arrival Date");
                return;
            }
            LocalTime arrivalTime;
            try {
                arrivalTime = LocalTime.parse(arrivalTimeField.getText());
            } catch (Exception ex) {
                messageLabel.setText("Invalid Arrival Time");
                return;
            }
            String landingPlace = landingPlaceField.getText();
            if (landingPlace.isEmpty()) {
                messageLabel.setText("Price cannot be empty");
                return;
            }
            String options = optionsField.getText();
            if (options.isEmpty()) {
                messageLabel.setText("Price cannot be empty");
                return;
            }
            if (priceField.getText().isEmpty()) {
                messageLabel.setText("Price cannot be empty");
                return;
            }
            
            if(options.contains(" ")){
                showAlert("Invalid options","Options should not have spaces");
            }
            else{
            Integer price = Integer.parseInt(priceField.getText());
            
            seat = new Seat(flightNo, seatNo, seatClasschar, departureDate, departureTime, departurePlace,
                    arrivalDate, arrivalTime, landingPlace, options,price);
            
            saveSeatToBookAFlight(seat);

            if (isSeatAvailable(seat)) {
                seats.add(seat);
                saveSeatsToFile();
                messageLabel.setText("Reservation added successfully!");
            } else {
                messageLabel.setText("The seat is already reserved. \nPlease choose a different seat.");
            }
        }});
        Label flightNumberLabel = new Label("Flight Number:");
        flightNumberLabel.setStyle("-fx-text-fill: white;");
        Label seatNumberLabel = new Label("Seat Number:");
        seatNumberLabel.setStyle("-fx-text-fill: white;");
        Label seatClassLabel = new Label("Seat class:");
        seatClassLabel.setStyle("-fx-text-fill: white;");
        Label departureDateLabel = new Label("Departure Date:");
        departureDateLabel.setStyle("-fx-text-fill: white;");
        Label departureTimeLabel = new Label("Departure Time:");
        departureTimeLabel.setStyle("-fx-text-fill: white;");
        Label departurePlaceLabel = new Label("Departure Place:");
        departurePlaceLabel.setStyle("-fx-text-fill: white;");
        Label arrivalDateLabel = new Label("Arrival Date:");
        arrivalDateLabel.setStyle("-fx-text-fill: white;");
        Label arrivalTimeLabel = new Label("Arrival Time:");
        arrivalTimeLabel.setStyle("-fx-text-fill: white;");
        Label arrivalPlaceLabel = new Label("Landing Place:");
        arrivalPlaceLabel.setStyle("-fx-text-fill: white;");
        Label optionsLabel = new Label("Options:");
        optionsLabel.setStyle("-fx-text-fill: white;");
        Label priceLabel = new Label("Price:");
        priceLabel.setStyle("-fx-text-fill: white;");
        
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));

        gridPane.addRow(0, flightNumberLabel, flightNumberField);
        gridPane.addRow(1, seatNumberLabel, seatNumberField);
        gridPane.addRow(2, seatClassLabel, classField);
        gridPane.addRow(3, departureDateLabel, departureDateField);
        gridPane.addRow(4, departureTimeLabel, departureTimeField);
        gridPane.addRow(5, departurePlaceLabel, departurePlaceField);
        gridPane.addRow(6, arrivalDateLabel, arrivalDateField);
        gridPane.addRow(7, arrivalTimeLabel, arrivalTimeField);
        gridPane.addRow(8, arrivalPlaceLabel, landingPlaceField);
        gridPane.addRow(9, optionsLabel, optionsField);
        gridPane.addRow(10, priceLabel, priceField);
        gridPane.add(addButton, 1, 11, 2, 1);
        gridPane.add(messageLabel, 1, 12, 2, 1);
        gridPane.add(backButton1, 1, 13, 2, 1);
        
        InputStream photoStream = Main.class.getResourceAsStream("/Files/map.jpg");
        Image backgroundImage = new Image(photoStream);
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, gridPane);
        
        Scene addReservationScene = new Scene(stackPane, 700, 550); 
        primaryStage.setScene(addReservationScene);
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void saveSeatToBookAFlight(Seat seat) {
        String filename = "src/Files/bookaflight.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            String seatInfo = seat.getFlightNumber() + " " + seat.getSeatNumber() + " " + seat.getSeatClass() + " " + seat.getDepartureDate() + " "
                    + seat.getDepartureTime() + " " + seat.getDeparturePlace() + " " + seat.getArrivalDate() + " " + seat.getArrivalTime() + " " + seat.getLandingPlace()
                    + " " + seat.getOptions() + " " + seat.getPrice();
            writer.println(seatInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showDeleteReservationForm() {
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: white;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setMinHeight(30); 
        errorLabel.setMaxHeight(30);
        errorLabel.setPrefHeight(30);
        
        Label flightnumber = new Label("Flight number to delete:");
        flightnumber.setStyle("-fx-text-fill: white;");
        
        Label seatnumber = new Label("Seat number to delete:");
        seatnumber.setStyle("-fx-text-fill: white;");

        ComboBox<String> flightComboBox = new ComboBox<>(FXCollections.observableArrayList());
        ClientSceneView clientscene = new ClientSceneView(primaryStage,client);
        Set<String> uniqueFlightNumbers = clientscene.getUniqueFlightNumbers();
        flightComboBox.setItems(FXCollections.observableArrayList(uniqueFlightNumbers));
        flightComboBox.setPromptText("Select Flight");
        flightComboBox.setMinWidth(200);
        flightComboBox.setMaxWidth(200);
        flightComboBox.setPrefWidth(200);

        ComboBox<String> seatComboBox = new ComboBox<>();
        seatComboBox.setPromptText("Select Seat");
        seatComboBox.setMinWidth(200);
        seatComboBox.setMaxWidth(200);
        seatComboBox.setPrefWidth(200);
        
        flightComboBox.setOnAction(e -> {
        String selectedFlight = flightComboBox.getValue();
        Set<String> seats = clientscene.getAvailableSeatsForFlight(selectedFlight);
        seatComboBox.setItems(FXCollections.observableArrayList(seats));
        });
        
        Button backButton2 = new Button("Back");
        backButton2.setOnAction(e -> {
        adminBox.getChildren().clear();
        adminBox.getChildren().addAll(welcomeLabel, selectLabel, buttonBox, backButton);
        });
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(75); 
        deleteButton.setPrefHeight(30);
        deleteButton.setOnAction(e -> {
            String flightNo;
            flightNo = flightComboBox.getValue();
            String seatNo = seatComboBox.getValue();
            if(flightNo == null || seatNo == null){
                messageLabel.setVisible(false);
                errorLabel.setText("Select a flight and seat!");
                errorLabel.setVisible(true);
                return;
            }

            if (deleteReservation(flightNo, seatNo)) {
                flightComboBox.getItems().clear();
                errorLabel.setVisible(false);
                messageLabel.setVisible(true);
                messageLabel.setText("Reservation deleted successfully!");
                seats = readSeatsFromFile();
                saveSeatsToFile();

                Set<String> uniqueFlightNumbers2 = clientscene.getUniqueFlightNumbers();
                flightComboBox.setItems(FXCollections.observableArrayList(uniqueFlightNumbers2));
                String selectedFlight = flightComboBox.getValue();
                
                Set<String> availableseats = clientscene.getAvailableSeatsForFlight(selectedFlight);
                if(availableseats.isEmpty()){
                    flightComboBox.getItems().remove(flightNo);
                }
                else{
                seatComboBox.getItems().clear();
                seatComboBox.setItems(FXCollections.observableArrayList(availableseats));}
            } else {
                messageLabel.setVisible(false);
                errorLabel.setText("Failed to delete reservation. \nPlease check the flight and seat number.");
                errorLabel.setVisible(true);
                
            }
        });
        
        

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));

        gridPane.addRow(0, flightnumber, flightComboBox);
        gridPane.addRow(1, seatnumber, seatComboBox);
        gridPane.add(deleteButton, 1, 2, 2, 1);
        gridPane.add(messageLabel, 1, 3, 2, 1);
        gridPane.add(errorLabel, 1, 3, 2, 1);
        gridPane.add(backButton2, 1, 4, 2, 1);

        adminBox.getChildren().clear();
        adminBox.getChildren().add(gridPane);
    }

    
    private void showModifyReservationForm() {
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: white;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setMinHeight(30);
        errorLabel.setMaxHeight(30);
        errorLabel.setPrefHeight(30);

        Label flightnumber = new Label("Flight number to modify:");
        flightnumber.setStyle("-fx-text-fill: white;");
        
        Label seatnumber = new Label("Seat number to modify:");
        seatnumber.setStyle("-fx-text-fill: white;");
        
        Label newseat = new Label("New seat number:");
        newseat.setStyle("-fx-text-fill: white;");
        
        TextField newSeatNumberField = new TextField();
        newSeatNumberField.setPromptText("Enter new seat number");

        ComboBox<String> flightComboBox = new ComboBox<>(FXCollections.observableArrayList());
        ClientSceneView clientscene = new ClientSceneView(primaryStage,client);
        Set<String> uniqueFlightNumbers = clientscene.getUniqueFlightNumbers();
        flightComboBox.setItems(FXCollections.observableArrayList(uniqueFlightNumbers));
        flightComboBox.setPromptText("Select Flight");
        flightComboBox.setMinWidth(200);
        flightComboBox.setMaxWidth(200);
        flightComboBox.setPrefWidth(200);

        ComboBox<String> seatComboBox = new ComboBox<>();
        seatComboBox.setPromptText("Select Seat");
        seatComboBox.setMinWidth(200);
        seatComboBox.setMaxWidth(200);
        seatComboBox.setPrefWidth(200);
        
        flightComboBox.setOnAction(e -> {
        String selectedFlight = flightComboBox.getValue();
        Set<String> seats = clientscene.getAvailableSeatsForFlight(selectedFlight);
        seatComboBox.setItems(FXCollections.observableArrayList(seats));
        });
        
        Button backButton3 = new Button("Back");
        backButton3.setOnAction(e -> {
        adminBox.getChildren().clear();
        adminBox.getChildren().addAll(welcomeLabel, selectLabel, buttonBox, backButton);
        });

        Button modifyButton = new Button("Modify");
        modifyButton.setPrefWidth(75);
        modifyButton.setPrefHeight(30);
        modifyButton.setOnAction(e -> {
            String flightNo;
            try {
                flightNo = flightComboBox.getValue();
            } catch (NumberFormatException ex) {
                messageLabel.setText("Invalid Flight Number");
                return;
            }
            

            String currentSeatNo = seatComboBox.getValue();
            String newSeatNo = newSeatNumberField.getText();
            
            if(flightNo == null || currentSeatNo == null || newSeatNo.isEmpty()){
                messageLabel.setVisible(false);
                errorLabel.setText("Enter all fields!");
                errorLabel.setVisible(true);
                return;
            }

            if (modifyReservation(flightNo, currentSeatNo, newSeatNo)) {
                errorLabel.setVisible(false);
                messageLabel.setVisible(true);
                seats = readSeatsFromFile();
                messageLabel.setText("Reservation modified successfully!");
                Set<String> uniqueFlightNumbers2 = clientscene.getUniqueFlightNumbers();
                flightComboBox.getItems().clear();
                flightComboBox.setItems(FXCollections.observableArrayList(uniqueFlightNumbers2));
                String selectedFlight = flightComboBox.getValue();
                Set<String> seats = clientscene.getAvailableSeatsForFlight(selectedFlight);
                seatComboBox.getItems().clear();
                seatComboBox.setItems(FXCollections.observableArrayList(seats));
            } else {
                messageLabel.setVisible(false);
                errorLabel.setText("Failed to modify reservation. \nPlease check the flight and seat number.");
                errorLabel.setVisible(true);
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));

        gridPane.addRow(0, flightnumber, flightComboBox);
        gridPane.addRow(1, seatnumber, seatComboBox);
        gridPane.addRow(2, newseat, newSeatNumberField);
        gridPane.add(modifyButton, 1, 3, 2, 1);
        gridPane.add(messageLabel, 1, 4, 2, 1);
        gridPane.add(errorLabel, 1, 4, 2, 1);
        gridPane.add(backButton3, 1, 5, 2, 1);

        adminBox.getChildren().clear();
        adminBox.getChildren().add(gridPane);
    }

    private boolean isSeatAvailable(Seat newSeat) {
        for (Seat seat : seats) {
            if (seat.getFlightNumber().equals(newSeat.getFlightNumber()) && seat.getSeatNumber().equals(newSeat.getSeatNumber())) {
                return false; 
            }
        }
        return true; 
    }

    private void saveSeatsToFile() {
        try (FileWriter writer = new FileWriter("src/Files/seat.txt")) {
            for (Seat seat : seats) {
                writer.write(seat.toString() + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer2 = new FileWriter("src/Files/bookaflight.txt")) {
            for (Seat seat : seats) {
                writer2.write(seat.getFlightNumber() + " " + seat.getSeatNumber() + " " + seat.getSeatClass() + " " +
                    seat.getDepartureDate() + " " + seat.getDepartureTime() + " " +
                    seat.getDeparturePlace() + " " + seat.getArrivalDate() + " " +
                    seat.getArrivalTime() + " " + seat.getLandingPlace() + " " + seat.getOptions() + " " + seat.getPrice() + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Seat> readSeatsFromFile() {
    List<Seat> seats = new ArrayList<>();
    try (Scanner scanner = new Scanner(new File("src/Files/bookaflight.txt"))) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] fields = line.split("\\s+");
            
   
            if (fields.length >= 11) {
                String flightNo;
                try {
                    flightNo = fields[0];
                } catch (NumberFormatException e) {
                    System.err.println("Invalid flight number in line: " + line);
                    continue; 
                }

                String seatNo = fields[1];
                char seatClass = fields[2].charAt(0);
                String departuredatestring = fields[3];
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate1 = LocalDate.parse(departuredatestring, formatter);
                    LocalDate departuredate = localDate1;
                    LocalTime departureTime = LocalTime.parse(fields[4]);
                    String departureplace = fields[5];
                    String landingdatestring = fields[6];
                    LocalDate localDate2 = LocalDate.parse(landingdatestring, formatter);
                    LocalDate landingdate = localDate2;
                   
                    LocalTime landingTime = LocalTime.parse(fields[7]);
                    String landingplace = fields[8];
                    String options = fields[9];
                    Integer price = Integer.parseInt(fields[10]);
                    seat = new Seat(flightNo, seatNo, seatClass,departuredate,departureTime,departureplace,landingdate,landingTime,landingplace,options,price);

                seats.add(seat);
            } else {
                System.err.println("Malformed line: " + line);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return seats;
}


    private boolean deleteReservation(String flightNo, String seatNo) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getFlightNumber().equals(flightNo) && seat.getSeatNumber().equals(seatNo)) {
                seats.remove(i);
                saveSeatsToFile();
                return true;
            }
        }
        return false; 
    }

    private boolean modifyReservation(String flightNo, String currentSeatNo, String newSeatNo) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getFlightNumber().equals(flightNo) && seat.getSeatNumber().equals(currentSeatNo)) {
                seat.setSeatNumber(newSeatNo);
                saveSeatsToFile();
                return true;
            }
        }
        return false;
    }
}
