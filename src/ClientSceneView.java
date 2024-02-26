import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class ClientSceneView {

    private Stage primaryStage;
    private Client client;
    private Seat seat;
    private List<String> clientReservations;

    public ClientSceneView(Stage primaryStage, Client client) {
        this.primaryStage = primaryStage;
        this.client = client;
    }

    public Scene createScene(Stage primaryStage,Client client) {
        this.primaryStage = primaryStage;
        this.client = client;
        this.clientReservations = getClientReservations(client.getFirstName(), client.getLastName());
        primaryStage.setTitle("Client Scene");
        Label welcomeLabel = new Label("Welcome " + client.getFirstName() + "!");
        Font customFont = Font.font("Times New Roman", 38);
        welcomeLabel.setFont(customFont);
        welcomeLabel.setStyle("-fx-background-color: white; -fx-padding: 10px;-fx-font-weight: bold;-fx-text-fill: navy;");
        
        Label selectLabel = new Label("Select what you want to do:");
        selectLabel.setStyle("-fx-font-size: 14px;-fx-text-fill: navy;");

        Button reserveButton = new Button("Reserve a Seat");
        reserveButton.setPrefWidth(150);
        reserveButton.setPrefHeight(50);
        reserveButton.setOnAction(e -> showReservationForm());

        Button cancelButton = new Button("Cancel Reservation");
        cancelButton.setPrefWidth(150); 
        cancelButton.setPrefHeight(50);
        cancelButton.setOnAction(e -> {
            clientReservations = getClientReservations(client.getFirstName(), client.getLastName());
            showCancelReservationForm();  
                });
        
        Label printsuccess = new Label();
        
        Button printreservationsButton = new Button("Print reservations");
        printreservationsButton.setPrefWidth(150); 
        printreservationsButton.setPrefHeight(50);
        printreservationsButton.setOnAction(event -> {
            try {
                if(getClientReservations(client.getFirstName(), client.getLastName()).isEmpty()){
                    showAlert("No reservations for " + client.getFirstName());
                    printsuccess.setText("No reservations!");
                }
                else{
                    generatePDF();
                    printsuccess.setText("Reservations printed, check your pdf file!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBackToChoiceScene());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(reserveButton, cancelButton,printreservationsButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(welcomeLabel, selectLabel, buttonBox,printsuccess,backButton);
        VBox.setMargin(buttonBox, new Insets(0, 0, 15, 0));
        
        InputStream photoStream = Main.class.getResourceAsStream("/Files/plane.jpg");
        Image backgroundImage = new Image(photoStream);
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, layout);

        return new Scene(stackPane, 625, 416);
    }
    
     private void generatePDF() throws IOException {
        if (client == null) {
            System.out.println("No client logged in.");
            return;
        }


        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.TIMES_BOLD, 20);

                String text = "Thank you for reserving a seat";
                float textWidth = PDType1Font.TIMES_BOLD.getStringWidth(text) / 1000 * 20;
                PDRectangle pageSize = page.getMediaBox();
                float startX = (pageSize.getWidth() - textWidth) / 2;
                float startY = pageSize.getHeight() - 50;

                contentStream.beginText();
                contentStream.newLineAtOffset(startX, startY);
                contentStream.showText(text);
                contentStream.endText();

                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                
                float infoStartX = 50;
                float infoStartY = startY - 50;
                contentStream.beginText();
                contentStream.newLineAtOffset(infoStartX, infoStartY);
                contentStream.showText("Client Information:");
                contentStream.newLine();
                contentStream.endText();

                float contentStartY = infoStartY - 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(infoStartX, contentStartY);
                contentStream.showText("Name: " + client.getFirstName() + " " + client.getLastName());
                contentStream.endText();

                float telephoneStartY = contentStartY - 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(infoStartX, telephoneStartY);
                contentStream.showText("Telephone: " + client.getTel());
                contentStream.endText();

                List<String> reservations = getClientReservations(client.getFirstName(), client.getLastName());
                if (!reservations.isEmpty()) {
                    float reservedSeatsStartY = telephoneStartY - 40;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(infoStartX, reservedSeatsStartY);
                    contentStream.showText("Reserved Seats:");
                    contentStream.endText();

                    float reservationStartY = reservedSeatsStartY - 20;
                    for (String reservation : reservations) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(infoStartX, reservationStartY);
                        contentStream.showText(reservation);
                        contentStream.endText();
                        reservationStartY -= 20;
                    }

                    double totalPayment = getReservationsPrice();
                    float totalPaymentStartY = reservationStartY - 40;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(infoStartX, totalPaymentStartY);
                    contentStream.showText("Total Payment: " + totalPayment + "$");
                    contentStream.endText();
                } else {
                    float noReservationStartY = telephoneStartY - 40;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(infoStartX, noReservationStartY);
                    contentStream.showText("No reservations found for " + client.getFirstName());
                    contentStream.endText();   
                }

                float safeFlightStartY = startY - 325;
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                contentStream.beginText();
                
                contentStream.newLineAtOffset(infoStartX, safeFlightStartY);
                contentStream.showText("Be safe!");
                contentStream.endText();
            }
            
            String filePath = "src/Files/" + client.getFirstName() + "_reservation.pdf";
            document.save(filePath);
            System.out.println("PDF generated successfully.");
        }
        
    }
    private double getReservationsPrice(){
        double totalReservationPrice = 0.0;
        List<String> reservations = getClientReservations(client.getFirstName(),client.getLastName());

        for (String reservation : reservations) {
        String[] reservationData = reservation.split("\\s+");;
        String resFirstName = reservationData[3];
        String resLastName = reservationData[4];
        String resPriceStr = reservationData[12];
        if (resFirstName.equalsIgnoreCase(client.getFirstName()) && resLastName.equalsIgnoreCase(client.getLastName())){
             try {
                double reservationPrice = Double.parseDouble(resPriceStr);
                totalReservationPrice += reservationPrice;
            } catch (NumberFormatException e) {
                System.out.println("Price in the wrong format!");
            }
        }
    }

    return totalReservationPrice;
    }

         
    private void showReservationForm() {

        ListView<String> flightNumbersListView = new ListView<>(FXCollections.observableArrayList());
        flightNumbersListView.setMinWidth(200);
        flightNumbersListView.setMaxWidth(200);
        flightNumbersListView.setPrefWidth(200);
        flightNumbersListView.setMinHeight(200);
        flightNumbersListView.setMaxHeight(200);
        flightNumbersListView.setPrefHeight(200);
        
        
        ListView<String> seatsListView = new ListView<>(FXCollections.observableArrayList());
        seatsListView.setMinWidth(200);
        seatsListView.setMaxWidth(200);
        seatsListView.setPrefWidth(200);
        seatsListView.setMinHeight(200);
        seatsListView.setMaxHeight(200);
        seatsListView.setPrefHeight(200);
        
        Button showflightnumbersButton = new Button("Show available flight numbers");
        showflightnumbersButton.setOnAction(e-> {
            Set<String> uniqueFlightNumbers = new HashSet<>(getUniqueFlightNumbers());
            if(uniqueFlightNumbers.isEmpty()){
                showAlert("No available flights!");
            }
            flightNumbersListView.setItems(FXCollections.observableArrayList(uniqueFlightNumbers));
        });
        
        flightNumbersListView.setOnMouseClicked(e -> {
            String flightnumber = flightNumbersListView.getSelectionModel().getSelectedItem();
            getAvailableSeatsForFlight(flightnumber);
            Set<String> availableSeats = new HashSet<>(getAvailableSeatsForFlight(flightnumber));
            seatsListView.setItems(FXCollections.observableArrayList(availableSeats));
                });
        Text seatInfoText = new Text();
        seatsListView.setOnMouseClicked(event ->{
            String message = displayFlightInfo(flightNumbersListView.getSelectionModel().getSelectedItem(),seatsListView.getSelectionModel().getSelectedItem());
            seatInfoText.setText(message);
                });
        
        seatInfoText.setTextAlignment(TextAlignment.CENTER);
        seatInfoText.setFill(Color.NAVY);
        

        Label whatToDoLabel = new Label("Select the flight number then the seat number to see its specifications:");
        whatToDoLabel.setStyle("-fx-text-fill: navy;");
        
        TextField optionsTextField = new TextField();
        optionsTextField.setPromptText("Options to selected seat");
        optionsTextField.setMinWidth(200);
        optionsTextField.setMaxWidth(200);
        optionsTextField.setPrefWidth(200);
        
        Label reservationsuccessful = new Label();
        reservationsuccessful.setStyle("-fx-text-fill: green;");

        Button reserveButton = new Button("Reserve");
        reserveButton.setOnAction(e -> {
            
           if(seatsListView.getSelectionModel().getSelectedItem() == null || seatsListView.getSelectionModel().getSelectedItem() == null){
               showAlert("Please select a flight and a seat before reserving.");
           }
           else{
               if(optionsTextField.getText().isEmpty() || optionsTextField.getText().contains(" ")){
                   showAlert("Add option to selected seat with no Spaces!");
               }
               else{
           seat = getSeat(seatsListView.getSelectionModel().getSelectedItem(),optionsTextField.getText());
           client.setSeat(seat);
           saveReservationToFile(optionsTextField.getText());
           deleteReservationfrombookaflight(seatsListView.getSelectionModel().getSelectedItem());
           deleteReservationfromseat(seatsListView.getSelectionModel().getSelectedItem());
           reservationsuccessful.setText("Reservation successful!");
           showReservationSuccessMessage(flightNumbersListView.getSelectionModel().getSelectedItem(),seat);
           clientReservations = getClientReservations(client.getFirstName(), client.getLastName());
           }
        }});

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBackToClientScene());
        
        Label flightnumbersLabel = new Label("Flight numbers:");
        flightnumbersLabel.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-font-size: 14px;");
        
        Label seatnumbersLabel = new Label("Seat numbers:");
        seatnumbersLabel.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-font-size: 14px;");
  
        VBox layout1 = new VBox(10);
        layout1.setAlignment(Pos.CENTER);
        layout1.getChildren().addAll(flightnumbersLabel,flightNumbersListView);
        
        VBox layout2 = new VBox(10);
        layout2.setAlignment(Pos.CENTER);
        layout2.getChildren().addAll(seatnumbersLabel,seatsListView);
                
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(layout1,layout2);
        
        VBox alltogether = new VBox(10);
        alltogether.setAlignment(Pos.CENTER);
        alltogether.getChildren().addAll(showflightnumbersButton,whatToDoLabel,hbox,seatInfoText,optionsTextField,reserveButton,reservationsuccessful,backButton);
        VBox.setMargin(reserveButton, new Insets(0, 0, 10, 0));
        VBox.setMargin(hbox, new Insets(0, 0, 10, 0));
        
        InputStream photoStream = getClass().getResourceAsStream("/Files/plane.jpg");
        Image backgroundImage = new Image(photoStream);
 
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, alltogether);

        Scene reservationScene = new Scene(stackPane, 625, 600);
        primaryStage.setScene(reservationScene);
    }
        private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public Set<String> getUniqueFlightNumbers() {
        Set<String> uniqueFlightNumbers = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/bookaflight.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] flightData = line.split("\\s+");
                if (flightData.length >= 10) {
                    uniqueFlightNumbers.add(flightData[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return uniqueFlightNumbers;
    }

    public Set<String> getAvailableSeatsForFlight(String flightNumber) {
        Set<String> availableSeats = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/bookaflight.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] flightData = line.split("\\s+");
                if (flightData.length >= 10 && flightData[0].equals(flightNumber)) {
                    availableSeats.add(flightData[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availableSeats;

    }
    private String displayFlightInfo(String flightnumber,String seatNumber) {
        if (flightnumber != null && seatNumber != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/bookaflight.txt")))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] flightData = line.split("\\s+");
                    if (flightData.length >= 11 && flightData[0].equals(flightnumber) && flightData[1].equals(seatNumber)) {
                        String seatInfo = "Flight: " + flightData[0] + ", Seat: " + flightData[1] +
                                ", Class: " + flightData[2] + "\nDepartureDate: " + flightData[3] + " LandingDate: " + flightData[6] +
                                "\nDepartureTime: " + flightData[4] + " LandingTime: " + flightData[7] + "\nDeparturePlace: " + flightData[5] +
                                " LandingPlace: " + flightData[8] + "\nPrice: " + flightData[10] + "$";
                        return seatInfo;
                    }
                }

                String errormessage = "Flight information not found for the selected flight and seat.";
                return errormessage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            return " ";
        
    }

    
    private void saveReservationToFile(String option) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Files/reservation.txt", true))) {
            writer.write(seat.getFlightNumber() + "  " + seat.getSeatNumber() + "  " + seat.getSeatClass() + "  " + client.getFirstName() + "  " + client.getLastName() + "   " +
                    seat.getDepartureDate() + "   " + seat.getDepartureTime() + "  " +
                    seat.getDeparturePlace() + "   " + seat.getArrivalDate() + "   " +
                    seat.getArrivalTime() + "  " + seat.getLandingPlace() + "  " + option + "  "+ seat.getPrice());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Seat getSeat(String selectedSeat,String options) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/bookaflight.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts[1].equals(selectedSeat)) {
                    String flightNo = parts[0];
                    String seatNo = parts[1];
                    String seatClassString = parts[2];
                    int index = 0; 
                    char seatClass = seatClassString.charAt(index);
                    String departuredatestring = parts[3];
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate1 = LocalDate.parse(departuredatestring, formatter);
                    LocalDate departuredate = localDate1;
                    LocalTime departureTime = LocalTime.parse(parts[4]);
                    String departureplace = parts[5];
                    String landingdatestring = parts[6];
                    LocalDate localDate2 = LocalDate.parse(landingdatestring, formatter);
                    LocalDate landingdate = localDate2;
                   
                    LocalTime landingTime = LocalTime.parse(parts[7]);
                    String landingplace = parts[8];
                    
                    Integer price = Integer.parseInt(parts[10]);
     
                    seat = new Seat(flightNo, seatNo, seatClass,departuredate,departureTime,departureplace,landingdate,landingTime,landingplace,options,price);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        return seat;
    }
    private void deleteReservationfrombookaflight(String selectedSeat) {
        List<String> linesToKeep = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/bookaflight.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String seatNumber = parts[1];
                if (!seatNumber.equals(selectedSeat)) {
                    linesToKeep.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return; 
        }

        try {
            java.nio.file.Path filePath = Paths.get("src/Files/bookaflight.txt");
            Files.write(filePath, linesToKeep);
            System.out.println("Reservation successfully deleted from bookaflight.txt!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void deleteReservationfromseat(String selectedSeat) {
        List<String> linesToKeep = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/seat.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String seatNumber = parts[1];
                if (!seatNumber.equals(selectedSeat)) {
                    linesToKeep.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            java.nio.file.Path filePath = Paths.get("src/Files/seat.txt");
            Files.write(filePath, linesToKeep);
            System.out.println("Reservation successfully deleted From seat.txt!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showReservationSuccessMessage(String flightNumber, Seat seat) {
        Label successLabel = new Label("Reservation successful!");
        successLabel.setStyle("-fx-font-size: 24px");
        Label reservationDetailsLabel = new Label("You have reserved Seat " + seat.getSeatNumber() +
                " in Class " + seat.getSeatClass() + " for Flight Number " + flightNumber + ".");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBackToClientScene());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(successLabel, reservationDetailsLabel, backButton);
        InputStream photoStream = getClass().getResourceAsStream("/Files/plane.jpg");
        Image backgroundImage = new Image(photoStream);
 
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, layout);
        
        Scene successScene = new Scene(stackPane, 625, 416);
        primaryStage.setScene(successScene);
    }
    

    private void showCancelReservationForm() {
        Label selectLabel = new Label();

        ListView<String> reservationListView = new ListView<>();
        reservationListView.setMinWidth(400);
        reservationListView.setMaxWidth(400);
        reservationListView.setPrefWidth(400);
        reservationListView.setMinHeight(250);
        reservationListView.setMaxHeight(250);
        reservationListView.setPrefHeight(250);
        reservationListView.getItems().addAll(clientReservations);
        
        if(clientReservations.isEmpty()){
            selectLabel.setText("No reservation to cancel");
        }
        else{
            selectLabel.setText("Select reservation to cancel");
        }
        

        Button cancelButton = new Button("Cancel Reservation");
        cancelButton.setOnAction(e -> {
            String selectedReservation = reservationListView.getSelectionModel().getSelectedItem();
            if (selectedReservation != null) {
                cancelReservation(selectedReservation);
                addCanceledReservationToFiles(selectedReservation);
                showCancellationSuccessMessage(selectedReservation);
            } else {
                showCancellationErrorMessage(clientReservations);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBackToClientScene());

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(selectLabel, reservationListView, cancelButton, backButton);
        
        InputStream photoStream = getClass().getResourceAsStream("/Files/plane.jpg");
        Image backgroundImage = new Image(photoStream);
 
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, layout);

        Scene cancelReservationScene = new Scene(stackPane, 625, 416);
        primaryStage.setScene(cancelReservationScene);
    }
    public static void addCanceledReservationToFiles(String selectedReservation) {
        String bookAFlightFilePath = "src/Files/bookaflight.txt";
        String seatFilePath = "src/Files/seat.txt";

        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(bookAFlightFilePath, true))) {

            String[] fields = selectedReservation.split("\\s+");

                if (fields.length >= 12) {
                    String flightNumber = fields[0];
                    String seatNumber = fields[1];
                    String seatClass = fields[2];
                    String departureDate = fields[5];
                    String departureTime = fields[6];
                    String departurePlace = fields[7];
                    String arrivalDate = fields[8];
                    String arrivalTime = fields[9];
                    String landingPlace = fields[10];
                    String options = fields[11];
                    String price = fields[12];

                    String bookAFlightLine = flightNumber + " " + seatNumber + " " + seatClass + " " +
                            departureDate + " " + departureTime + " " + departurePlace + " " +
                            arrivalDate + " " + arrivalTime + " " + landingPlace+ " " + options + " " + price;

                    writer1.write(bookAFlightLine);
                    writer1.newLine();
                }
                System.out.println("Canceled reservations added to bookaflight.txt successfully.");
            }
             catch (IOException e) {
            System.err.println("Error while processing reservations: " + e.getMessage());
        }
        try (BufferedWriter writer2 = new BufferedWriter(new FileWriter(seatFilePath, true))) {

            String[] fields = selectedReservation.split("\\s+");

                if (fields.length >= 12) {
                    String flightNumber = fields[0];
                    String seatNumber = fields[1];
                    String seatClass = fields[2];
                    String options = fields[11];
 
                    String seatLine = flightNumber + "  " + seatNumber + "   " + seatClass + "  "+ options ;

                    writer2.write(seatLine);
                    writer2.newLine();
                }
                System.out.println("Canceled reservations added to seat.txt successfully.");
            }
             catch (IOException e) {
            System.err.println("Error while processing reservations: " + e.getMessage());
        }
        
    }
    private List<String> getClientReservations(String firstName, String lastName) {
        List<String> clientReservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/reservation.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] reservationData = line.split("\\s+");
                if (reservationData.length == 13) {
                    String reservationFirstName = reservationData[3];
                    String reservationLastName = reservationData[4];

                    if (reservationFirstName.equals(firstName) && reservationLastName.equals(lastName)) {
                        clientReservations.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clientReservations;
    }

    private void cancelReservation(String reservation) {
        List<String> allReservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Files/reservation.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allReservations.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        allReservations.remove(reservation);

        try (FileWriter writer = new FileWriter("src/Files/reservation.txt")) {
            for (String updatedReservation : allReservations) {
                writer.write(updatedReservation);
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showCancellationSuccessMessage(String reservation) {
        Label successLabel = new Label("Reservation canceled successfully!");
        successLabel.setStyle("-fx-font-size: 24px;-fx-text-fill: white;");
        Label canceledlabel = new Label("Canceled reservation:");
        Label reservationDetailsLabel = new Label(reservation);
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBackToClientScene());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(successLabel,canceledlabel, reservationDetailsLabel, backButton);
        
        InputStream photoStream = getClass().getResourceAsStream("/Files/plane.jpg");
        Image backgroundImage = new Image(photoStream);
 
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, layout);

        Scene successScene = new Scene(stackPane, 625, 416);
        primaryStage.setScene(successScene);
    }

    private void showCancellationErrorMessage(List<String> clientReservations) {
        Label errorLabel = new Label();
        if(clientReservations.isEmpty()){
        errorLabel.setText("No reservation to cancel.");
        }
        else{
                errorLabel.setText("Please select a reservation to cancel.");
                }
        errorLabel.setStyle("-fx-font-size: 24px");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showCancelReservationForm());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(errorLabel, backButton);
        
        InputStream photoStream = getClass().getResourceAsStream("/Files/plane.jpg");
        Image backgroundImage = new Image(photoStream);
 
        ImageView backgroundImageView = new ImageView(backgroundImage);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        backgroundImageView.setEffect(blur);
        
        StackPane stackPane = new StackPane(backgroundImageView, layout);

        Scene errorScene = new Scene(stackPane, 625, 416);
        primaryStage.setScene(errorScene);
    }

    private void goBackToClientScene() {
        ClientSceneView clientSceneView = new ClientSceneView(primaryStage, client);
        primaryStage.setScene(clientSceneView.createScene(primaryStage,client));
    }

    private void goBackToChoiceScene() {
        Choice choiceSceneView = new Choice();
        choiceSceneView.showChoiceScene(primaryStage,client);
    }
}
