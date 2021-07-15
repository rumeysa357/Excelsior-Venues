package model;

import domain.Venue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JDBCVenueDAO implements VenueDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCVenueDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Venue> getAllVenues() {
        ArrayList<Venue> venues = new ArrayList<>();
        //String sql="SELECT veid, name, city_id, description FROM venue ";
        String sql = "SELECT  venue.id AS venue_id, venue.city_id AS city_id, venue.name AS venue_name, city.name AS city_name, city.state_abbreviation , venue.description  " +
                "FROM venue " +
                "JOIN city ON city.id=venue.city_id  " +
                "ORDER BY venue_name ASC";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Venue theVenues = mapRowToVenue(results);
            venues.add(theVenues);
        }
        return venues;
    }

    private Venue mapRowToVenue(SqlRowSet results) {
        Venue theVenue;
        theVenue = new Venue();
        theVenue.setId(results.getLong("venue_id"));
        theVenue.setCityId(results.getInt("city_id"));
        theVenue.setName(results.getString("venue_name"));
        theVenue.setCityname(results.getString("city_name"));
        theVenue.setStateAbbreviation(results.getString("state_abbreviation"));
        theVenue.setDescription(results.getString("description"));
        return theVenue;
    }

    public Venue mapCategory(SqlRowSet results) {
        Venue selectedVenue;
        selectedVenue = new Venue();
        selectedVenue.setCategory(results.getString("category_name"));
        return selectedVenue;
    }

    public List<Venue> getVenueCategory(String venueName) {
        ArrayList<Venue> venueCat = new ArrayList<>();
        String sql = "SELECT category.name AS category_name " +
        "FROM venue " +
        "JOIN category_venue on category_venue.venue_id=venue.id " +
        "JOIN category ON category.id=category_venue.category_id " +
        "WHERE venue.name= ? ";
               SqlRowSet results = jdbcTemplate.queryForRowSet(sql,venueName);
        while (results.next()) {
            Venue venueCategory = mapCategory(results);
            venueCat.add(venueCategory);
        }
        return venueCat;
    }
}

