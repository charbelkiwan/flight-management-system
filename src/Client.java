public class Client {
    private String passport_nb;
    private int ID;
    private String firstName;
    private String lastName;
    private String password;
    private String tel;
    private Seat seat;

    public Client(String passport_nb, int ID, String firstName, String lastName,
            String password, String tel, Seat seat) {
        this.passport_nb = passport_nb;
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.tel = tel;
        this.seat = seat;
    }

    public String getPassportNumber() {
        return passport_nb;
    }

    public void setPassportNumber(String passportNumber) {
        this.passport_nb = passportNumber;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "Client{" +
                "passportNumber='" + passport_nb + '\'' +
                ", ID=" + ID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", tel='" + tel + '\'' +
                ", seat=" + seat +
                '}';
    }

}