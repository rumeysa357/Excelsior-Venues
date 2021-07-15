package model;

import domain.Reservation;
import domain.Space;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ReservationDAO {

    List<Space> getTopFiveReservations(Long selectedVenue, int numberOfPeople, LocalDate userStartDate, LocalDate userleaveDate);

    long getNextReservationId();

    public List<Reservation> getExistingReservations();
    public List<Reservation> searchReservations(String nameSearch, Date start_date, Date end_date);

}
