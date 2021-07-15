package view;

import javax.sql.DataSource;

import domain.Space;
import domain.Venue;
import model.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class ExcelsiorCLI {
    private static final String MAIN_MENU_OPTION_LIST_VENUES = "List Venues";
    private static final String MAIN_MENU_OPTION_QUIT = "Quit";
    private static final String[] MAIN_MENU_OPTIONS = new String[]{MAIN_MENU_OPTION_LIST_VENUES,
            MAIN_MENU_OPTION_QUIT,
    };

    private static final String SUB_MENU_OPTION_VIEW_SPACES = "View Spaces";
    private static final String SUB_MENU_SEARCH_RESERVATION = "Search for Reservation";
    private static final String SUB_MENU_RETURN_PREVIOUS_SCREEN = "Return to Previous Screen";

    private static final String[] SUB_MENU_OPTIONS = new String[]{SUB_MENU_OPTION_VIEW_SPACES, SUB_MENU_SEARCH_RESERVATION, SUB_MENU_RETURN_PREVIOUS_SCREEN
    };


    private UserInterface ui;
    private VenueDAO venueDAO;
    private SpaceDAO spaceDAO;
    private ReservationDAO reservationDAO;

    public static void main(String[] args) throws ParseException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/excelsior_venues");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");

        ExcelsiorCLI application = new ExcelsiorCLI(dataSource);
        application.run();
    }

    public ExcelsiorCLI(DataSource datasource) {
        this.ui = new UserInterface(System.in, System.out);
        venueDAO = new JDBCVenueDAO(datasource);
        spaceDAO = new JDBCSpaceDAO(datasource);
        reservationDAO = new JDBCReservationDAO(datasource);
    }

    public void run() throws ParseException {
        printHeading("Main Menu");
        boolean mainMenu = true;
        boolean subMenu = true;

        
        while (mainMenu) {
            String choice = (String) ui.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (choice.equals(MAIN_MENU_OPTION_LIST_VENUES)) {
                handleVenues();
            } else if (choice.equals(MAIN_MENU_OPTION_QUIT)) {
                mainMenu = false;
                break;
            }
            while (subMenu) {
                String userChoice = handleVenueSearch();
                String answer = ui.subMenuOptions();
                if ("1".equals(answer)) {
                    listVenueSpaces(userChoice);
                    String finalMenuAnswer = ui.spaceSelectedMenu();
                    if ("1".equals(finalMenuAnswer)) {
                        LocalDate oldDate = ui.reserveASpace();
                        int days = ui.daysToReserveSpace();
                        LocalDate endDate = calculateEndDate(oldDate, days);
                        int maxpeople = ui.checkMaxOccupancy();
                        List<Venue> venues = venueDAO.getAllVenues();
                        for (Venue venue : venues) {
                            String venueIdString = userChoice;
                            if (venue.getId() == Long.parseLong(userChoice)) {
                                String venueName = venue.getName();
                                //new method
                                spaceReserve(userChoice, maxpeople,oldDate,endDate,venueName,days);

                                continue;
                            }
                        }
                        subMenu = false;
                    }
                    mainMenu = false;
                    break;
                } else if ("2".equals(answer)) {
//                    reservationSearchTypeThingy();
                } else if ("r".equalsIgnoreCase(answer)) {
                    subMenu = false;
                }
            }
        }
    }

    private void handleVenues() {

        printHeading("Which venue would you like to view?");
        List<Venue> venues = venueDAO.getAllVenues();
        int count = 1;
        for (int i = 0; i < venues.size(); i++) {
            System.out.println(count + ") " + venues.get(i).getName());
            count++;
        }
        System.out.println("R) Return to Main Menu");
    }

    private String handleVenueSearch() {
        printHeading("Choose venue");
        String id = ui.getUserInput("choose venue id");
        List<Venue> lists = venueDAO.getAllVenues();
        String venueName = lists.get((Integer.parseInt(id)) - 1).getName();
        List<Venue> Catlist = venueDAO.getVenueCategory(venueName);
        String categories = "";
        for (Venue venue : Catlist) {
            if (Catlist.size() == 1) {
                categories = venue.getCategory();
            } else {
                categories = categories + venue.getCategory() + ". ";
            }
        }
        System.out.println("\n" + lists.get((Integer.parseInt(id)) - 1).getName());
        System.out.println("Location: " + lists.get((Integer.parseInt(id)) - 1).getCityname() + ", " + lists.get((Integer.parseInt(id)) - 1).getStateAbbreviation());
        System.out.println("Categories: " + categories);
        System.out.println();
        System.out.println(lists.get((Integer.parseInt(id)) - 1).getDescription());
        if (id.equalsIgnoreCase("r")) {
            return id;
        } else {
            return String.valueOf(lists.get((Integer.parseInt(id)) - 1).getId());
        }
    }


    private void listVenueSpaces(String user) {

        List<Space> lists = spaceDAO.getByVenueId(Long.parseLong(user));

        System.out.println(String.format("%-15s %-25s %-17s %-15s %-15s %-20s", "     ", "Name", "Open", "Close", "Daily Rate", "Max. Occupancy"));
        int count = 1;
        for (Space list : lists) {
            String chars = "#";
            System.out.println(String.format("%s %-5d %-30s %-20s %-15s $%-25.2f %-20d", chars, count, list.getName(), spaceDAO.intToMonth(list.getOpenFrom()), spaceDAO.intToMonth(list.getOpenTo()), list.getDailyRate(), list.getMaxOccupancy()));
            count++;

        }
    }

    private void spaceReserve(String userChoice,int maxpeople,LocalDate oldDate,LocalDate endDate,String venueName,int days){
        List<Space> listss = reservationDAO.getTopFiveReservations(Long.parseLong(userChoice), maxpeople, oldDate, endDate);
        System.out.println(String.format("%-5s %-22s $%-20s %-15s %-25s $%-20s", "Space #", "Name", "Daily Rate", "Max Occup.", "Accessible?", "Total Cost"));

        for (Space space : listss) {
            System.out.println(String.format("%-5s %-25s $%-20.2f %-15d %-25s $%-20.2f", space.getId(), space.getName(), space.getDailyRate(), space.getMaxOccupancy(), space.isAccessible(), space.getDailyRate().multiply(BigDecimal.valueOf(days))));
        }
        System.out.println();
        String spaceSelection = ui.getUserInput("Which space would you like to reserve? ");
        Space userSelectedSpace = null;
        for (Space space : listss) {
            if (String.valueOf(space.getId()).equals(spaceSelection)) {
                userSelectedSpace = space;
            }
        }
        String person = ui.getUserInput("Who is this reservation for?\n");
        receiptGiver(venueName, userSelectedSpace.getName(), person, oldDate, endDate, userSelectedSpace.getDailyRate().multiply(BigDecimal.valueOf(days)));

    }

    private void printHeading(String headingText) {

        System.out.println("\n" + headingText);
        for (int i = 0; i < headingText.length(); i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    public LocalDate calculateEndDate(LocalDate oldDate, int days) throws ParseException {
        LocalDate newDate = oldDate.plusDays(days);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Your end date will be: " + newDate.toString());
        return newDate;
    }



    public void receiptGiver(String venueName, String spaceName, String person, LocalDate oldDate, LocalDate endDate, BigDecimal totalCost) {
        System.out.println("\nThanks for submitting your reservation! The details for your event are listed below: ");
        System.out.println("Confirmation #: " + reservationDAO.getNextReservationId());
        System.out.println("Venue: " + venueName);
        System.out.println("Space: " + spaceName);
        System.out.println("Reserved for: " + person);
        System.out.println("Arrival Date: " + oldDate);
        System.out.println("Depart Date: " + endDate);
        System.out.println("Total Cost: " + totalCost);
    }
}







