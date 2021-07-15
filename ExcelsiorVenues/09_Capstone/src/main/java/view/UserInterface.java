package view;

import domain.Venue;


import javax.xml.transform.sax.SAXTransformerFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class UserInterface {


    private PrintWriter out;
    private Scanner in;

    public UserInterface(InputStream input, OutputStream output) {
        this.out = new PrintWriter(output);
        this.in = new Scanner(input);
    }

    public Object getChoiceFromOptions(Object[] options) {
        Object choice = null;
        while (choice == null) {
            displayMenuOptions(options);
            choice = getChoiceFromUserInput(options);
        }
        return choice;
    }

    public String getUserInput(String prompt) {
        System.out.print(prompt + " >>> ");
        return new Scanner(System.in).nextLine();
    }

    public String getVenueSelection(String venueSelection) {
        String selection = in.nextLine();
        return selection;
    }

    public Object getChoiceFromUserInput(Object[] options) {
        Object choice = null;
        String userInput = in.nextLine();
        try {
            int selectedOption = Integer.valueOf(userInput);
            if (selectedOption <= options.length) {
                choice = options[selectedOption - 1];
            }
        } catch (NumberFormatException e) {
            // eat the exception, an error message will be displayed below since choice will be null
        }
        if (choice == null) {
            out.println("\n*** " + userInput + " is not a valid option ***\n");
        }
        return choice;
    }
    private void displayMenuOptions(Object[] options) {
        out.println();
        for (int i = 0; i < options.length; i++) {
            int optionNum = i + 1;
            out.println(optionNum + ") " + options[i]);
        }
        out.print("\nPlease choose an option >>> ");
        out.flush();
    }

    public String subMenuOptions(){
        System.out.println("\nWhat would you like to do next? ");
        System.out.println("1) View Spaces");
        System.out.println("2) Search for Reservation");
        System.out.println("R) Return to Previous Screen\n");

        String userSpaceResponse=in.nextLine();
        return userSpaceResponse;
    }

    public String spaceSelectedMenu() {

            System.out.println("\nWhat would you like to do next? ");
            System.out.println("1) Make a reservation");
            System.out.println("R) Return to Previous Screen\n");

            String userSpaceResponse=in.nextLine();
            return userSpaceResponse;
    }
    public LocalDate reserveASpace() throws ParseException {
        System.out.println("When do you need the space?(yyyy-mm-dd) ");
        LocalDate startDate = LocalDate.parse(in.next());
       // Date dateUser= new SimpleDateFormat("yyyy-MM-dd").parse(in.nextLine());
        return startDate;
    }

    public int daysToReserveSpace() {
        System.out.println("How many days will you need the space? ");
        int days=Integer.parseInt(in.next());
        return days;
    }
    public int spaceIdForReservation(){
        System.out.println("Which space would you like to reserve(enter 0 to cancel)?");
        int days=Integer.parseInt(in.nextLine());
        return days;
    }

    public String personName(){
        System.out.println("Who is this reservation for?");
        String name=in.next();
        return name;
    }

    public int checkMaxOccupancy(){
        System.out.println("How many people will be in the attendance?");
        return in.nextInt();

    }

    public void getReservationSearchInput(String name, Date start_date, Date end_date) throws ParseException {
        String nameInput= in.nextLine();
        Date start_dateInput= new SimpleDateFormat("yyyy-MM-dd").parse(in.nextLine());
        Date end_dateInput= new SimpleDateFormat("yyyy-MM-dd").parse(in.nextLine());
    }

}

