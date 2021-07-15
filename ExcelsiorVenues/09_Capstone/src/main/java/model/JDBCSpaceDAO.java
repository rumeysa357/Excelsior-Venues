
package model;

import domain.Space;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public class JDBCSpaceDAO implements SpaceDAO{

    private JdbcTemplate jdbcTemplate;


    public JDBCSpaceDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public List<Space> getAllSpaces() {
        ArrayList<Space> spaces = new ArrayList<>();
        String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, CAST(daily_rate AS decimal(10,2)), max_occupancy FROM space";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Space theSpaces = mapRowToSpaces(results);
            spaces.add(theSpaces);
        }
        return spaces;
    }

    @Override
    public List<Space> getByVenueId(long id) {
        List<Space> spaces = new ArrayList<>();
        String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, CAST(daily_rate AS decimal(10,2)), max_occupancy FROM space WHERE venue_id= ? ";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        while (rows.next()) {
            Space space = mapRowToSpaces(rows);
            spaces.add(space);
        }
        return spaces;
    }

        @Override
    public List<Space> getAvailableSpaces(Date startDate, Date endDate, int maxOccupancy) {
        List<Space> spaces = new ArrayList<>();
     String sql="SELECT space.id, space.name,CAST(daily_rate AS decimal(10,2)),space.max_occupancy,space.is_accessible " +
             "FROM space " +
             "LEFT JOIN reservation on reservation.space_id=space.id " +
             "WHERE ? NOT BETWEEN reservation.start_date AND reservation.end_date " +
             "AND ? NOT BETWEEN reservation.start_date AND reservation.end_date " +
             "OR reservation.start_date IS NULL " +
             "UNION " +
             "SELECT space.id, space.name,CAST(daily_rate AS decimal(10,2)),space.max_occupancy,space.is_accessible " +
             "FROM space " +
             "LEFT JOIN reservation ON reservation.space_id=space.id " +
             "WHERE reservation.start_date IS NULL AND space.max_occupancy >= ? "+
             " ORDER BY max_occupancy DESC "+
             " LIMIT 5 ";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, startDate,endDate, maxOccupancy);
        while (rows.next()) {
            Space space = mapSpaces1(rows);
            spaces.add(space);
        }
        return spaces;
    }

    @Override
    public List<Space> getAvailableSpacesBySpaceId(Date startDate, Date endDate, int maxOccupancy, Long spaceId) {
        List<Space> spaces = new ArrayList<>();
        String sql="SELECT space.id, space.name,CAST(daily_rate AS decimal(10,2)),space.max_occupancy,space.is_accessible " +
                "FROM space " +
                "LEFT JOIN reservation on reservation.space_id=space.id " +
                "WHERE ? NOT BETWEEN reservation.start_date AND reservation.end_date " +
                "AND ? NOT BETWEEN reservation.start_date AND reservation.end_date " +
                "OR reservation.start_date IS NULL " +
                "UNION " +
                "SELECT space.id, space.name,CAST(daily_rate AS decimal(10,2)),space.max_occupancy,space.is_accessible " +
                "FROM space " +
                "LEFT JOIN reservation ON reservation.space_id=space.id " +
                "WHERE reservation.start_date IS NULL AND space.max_occupancy >= ? AND space.id = ?"+
                " ORDER BY max_occupancy DESC "+
                " LIMIT 5 ";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, startDate,endDate, maxOccupancy, spaceId);
        while (rows.next()) {
            Space space = mapSpaces1(rows);
            spaces.add(space);
        }
        return spaces;
    }

    private Space mapSpaces1(SqlRowSet results) {
        Space theSpace;
        theSpace = new Space();
        theSpace.setId(results.getLong("id"));
        theSpace.setName(results.getString("name"));
        theSpace.setDailyRate(results.getBigDecimal("daily_rate"));
        theSpace.setMaxOccupancy(results.getInt("max_occupancy"));
        theSpace.setAccessible(results.getBoolean("is_accessible"));

        return theSpace;
    }







    private Space mapRowToSpaces(SqlRowSet results) {
        Space theSpace;
        theSpace = new Space();
        theSpace.setId(results.getLong("id"));
        theSpace.setVenueId(results.getInt("venue_id"));
        theSpace.setName(results.getString("name"));
        theSpace.setAccessible(results.getBoolean("is_accessible"));
        theSpace.setOpenFrom(results.getInt("open_from"));
        theSpace.setOpenTo(results.getInt("open_to"));
        theSpace.setDailyRate(results.getBigDecimal("daily_rate"));
        theSpace.setMaxOccupancy(results.getInt("max_occupancy"));

        return theSpace;
    }

    public String intToMonth(int month) {
        String months="";
        if(month==12){
            months="Dec.";
        }  if(month==11){
            months="Nov.";
        }  if(month==12){
            months="Oct.";
        }  if(month==12){
            months="Sep.";
        }  if(month==12){
            months="Aug.";
        }  if(month==12){
            months="July.";
        }  if(month==12){
            months="Jun.";
        }  if(month==12){
            months="May.";
        }  if(month==12){
            months="Apr.";
        }  if(month==12){
            months="Mar.";
        }  if(month==12){
            months="Feb.";
        }  if(month==12){
            months="Jan.";

        }
        return months;
    }






}
