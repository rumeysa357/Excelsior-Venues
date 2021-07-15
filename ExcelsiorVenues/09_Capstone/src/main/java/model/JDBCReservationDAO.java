package model;

import domain.Reservation;
import domain.Space;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JDBCReservationDAO implements ReservationDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCReservationDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public List<Space> getTopFiveReservations(Long selectedVenue, int numberOfPeople, LocalDate userStartDate, LocalDate userleaveDate) {
        List<Space> newReservationList = new ArrayList<>();
        String sql = "SELECT space.id, space.venue_id, space.name, space.is_accessible, space.open_from, space.open_to, CAST(space.daily_rate AS decimal), space.max_occupancy FROM space " +
                "JOIN venue ON venue.id = space.venue_id " +
                "WHERE venue_id = ? " +
                "AND max_occupancy >= ? " +
                "AND NOT EXISTS (SELECT * FROM reservation " +
                "WHERE (CAST(? AS DATE) BETWEEN reservation.start_date AND reservation.end_date " +
                "OR CAST(? AS DATE) BETWEEN reservation.start_date AND reservation.end_date) " +
                "AND reservation.space_id = space.id) " +
                "AND ((EXTRACT(MONTH from CAST(? AS DATE)) BETWEEN space.open_from AND space.open_to) OR space.open_from IS NULL AND space.open_to IS NULL) " +
                "AND ((EXTRACT(MONTH from CAST(? AS DATE)) BETWEEN space.open_from AND space.open_to) OR space.open_from IS NULL AND space.open_to IS NULL) " +
                "GROUP BY space.id " +
                "ORDER BY space.daily_rate ASC " +
                "LIMIT 5";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, selectedVenue, numberOfPeople, userStartDate, userleaveDate, userStartDate, userleaveDate);
        while (results.next()) {
            Space reservationSpace = mapRowToSpaceReservation(results);  //space object, looking for available spaces
            newReservationList.add(reservationSpace);
        }
        return newReservationList;
    }

    private Space mapRowToSpaceReservation(SqlRowSet results) {
        Space theSpaceReservation = new Space();
        theSpaceReservation.setId(results.getLong("id"));
        theSpaceReservation.setName(results.getString("name"));
        theSpaceReservation.setAccessible(results.getBoolean("is_accessible"));
        theSpaceReservation.setOpenFrom(results.getInt("open_from"));
        theSpaceReservation.setOpenTo(results.getInt("open_to"));
        theSpaceReservation.setDailyRate(results.getBigDecimal("daily_rate"));
        theSpaceReservation.setMaxOccupancy(results.getInt("max_occupancy"));
        return theSpaceReservation;
    }

    @Override
    public long getNextReservationId() {
        SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('reservation_reservation_id_seq')");
        if(nextIdResult.next()) {
            return nextIdResult.getLong(1);
        } else {
            throw new RuntimeException("Something went wrong while getting an id for the new reservation");
        }
    }
    public void addReservation(Reservation newReservation) {
        List<Reservation> newReservationList = new ArrayList<>();
        //SELECT reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for FROM public.reservation;
        String sql = "INSERT INTO reservation(reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for)" +
                "VALUES(?,?,?,?,?,?)";
        newReservation.setReservationId(getNextReservationId());
        jdbcTemplate.update(sql,newReservation.getReservationId(),newReservation.getSpaceId(),newReservation.getNumberOfAttendees(),newReservation.getStartDate(),newReservation.getEndDate(),newReservation.getReservedFor());


    }


    @Override
    public List<Reservation> getExistingReservations() {
        ArrayList<Reservation> reservations = new ArrayList<>();
        String sqlFindReservationById = "SELECT space.name, reservation.start_date, reservation.end_date,space.max_occupancy " +
                "FROM space " +
                "LEFT JOIN reservation on reservation.space_id=space.id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindReservationById);
        while(results.next()) {
            Reservation theReservation = mapRowToReservation(results);
            reservations.add(theReservation);
        }
        return reservations;
    }

    @Override
    public List<Reservation> searchReservations(String nameSearch, Date start_date, Date end_date) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        nameSearch = "%" + nameSearch + "%";
        String sql = "SELECT reservation_id, name FROM reservation WHERE name LIKE ? AND start_date = ? AND end_date = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,nameSearch);

        while(results.next()) {
            Reservation theReservation = mapRowToReservation(results);
            reservations.add(theReservation);
        }

        return reservations;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation theReservation;
        theReservation = new Reservation();
        theReservation.setReservationId(results.getLong("reservation_id"));
        theReservation.setSpaceId(results.getInt("space_id"));
        theReservation.setNumberOfAttendees(results.getInt("number_of_attendees"));
        if (results.getDate("start_date") != null) {
            theReservation.setStartDate(results.getDate("start_date"));
        }
        if (results.getDate("end_date") != null) {
            theReservation.setEndDate(results.getDate("end_date"));
        }
        theReservation.setReservedFor(results.getString("reserved_for"));
        return theReservation;
    }



}
//(Has all Seven tables)
//        SELECT venue.name, city.name, state.abbreviation, category_venue.venue_id, category.name, space.daily_rate, reservation.space_id
//        FROM venue
//        JOIN city on venue.city_id = city.id
//        JOIN state on city.state_abbreviation = state.abbreviation
//        JOIN category_venue on venue.id = category_venue.venue_id
//        JOIN category on category_venue.category_id = category.id
//        JOIN space on space.venue_id = venue.id
//        JOIN reservation on reservation.space_id = space.id
