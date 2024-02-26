import java.time.LocalDate;
import java.time.LocalTime;

public class Seat {
    private String flight_no;
    private String seat_no;
    private char seat_class;
    private LocalDate departure_date;
    private LocalTime departure_time;
    private String departure_place;
    private LocalDate arrival_date;
    private LocalTime arrival_time;
    private String landing_place;
    private String options;
    private int price;

    public Seat(String flight_no, String seat_no, char seat_class,
            LocalDate departure_date, LocalTime departure_time, String departure_place,
            LocalDate arrival_date, LocalTime arrival_time, String landing_place, String options, Integer price) {
        this.flight_no = flight_no;
        this.seat_no = seat_no;
        this.seat_class = seat_class;
        this.departure_date = departure_date;
        this.departure_time = departure_time;
        this.departure_place = departure_place;
        this.arrival_date = arrival_date;
        this.arrival_time = arrival_time;
        this.landing_place = landing_place;
        this.options = options;
        this.price = price;
    }

    public String getFlightNumber() {
        return flight_no;
    }

    public void setFlightNumber(String flightNumber) {
        this.flight_no = flightNumber;
    }

    public String getSeatNumber() {
        return seat_no;
    }

    public void setSeatNumber(String seatNumber) {
        this.seat_no = seatNumber;
    }

    public char getSeatClass() {
        return seat_class;
    }

    public void setSeatClass(char seatClass) {
        this.seat_class = seatClass;
    }

    public LocalDate getDepartureDate() {
        return departure_date;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departure_date = departureDate;
    }

    public LocalTime getDepartureTime() {
        return departure_time;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departure_time = departureTime;
    }

    public String getDeparturePlace() {
        return departure_place;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departure_place = departurePlace;
    }

    public LocalDate getArrivalDate() {
        return arrival_date;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrival_date = arrivalDate;
    }

    public LocalTime getArrivalTime() {
        return arrival_time;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrival_time = arrivalTime;
    }

    public String getLandingPlace() {
        return landing_place;
    }

    public void setLandingPlace(String landingPlace) {
        this.landing_place = landingPlace;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
    public void setPrice(Integer price){
        this.price = price;
    }
    public Integer getPrice(){
        return price;
    }

    @Override
    public String toString() {
        return  flight_no +
                "    " + seat_no +
                "    " + seat_class +
                "    " + options;
    }
}