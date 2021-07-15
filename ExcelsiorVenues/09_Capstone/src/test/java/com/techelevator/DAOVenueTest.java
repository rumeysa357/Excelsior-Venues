package com.techelevator;

import domain.Venue;
import model.JDBCReservationDAO;
import model.JDBCVenueDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DAOVenueTest {
    private JDBCVenueDAO venueDAO;
    private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/excelsior_venues");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        /*
         * The following line disables autocommit for connections returned by this
         * DataSource. This allows us to rollback any changes after each test
         */
        dataSource.setAutoCommit(false);
    }
//    jdbctemplate = new jdbctemplate(dataSource);
//    venueDAO = new JDBCVenueDAO;

//    @Before
//    public void setup() {
//        jdbcTemplate = new jdbcTemplate(dataSource);
//        dao = new jdbcVenueDAO(dataSource);
//    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    /*
     * This method provides access to the DataSource for subclasses so that they can
     * instantiate a DAO for testing
     */

    @AfterClass
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }

    /*
     * After each test, we rollback any changes that were made to the database so
     * that everything is clean for the next test
     */
    protected DataSource getDataSource() {
        return dataSource;
    }



@Test
  public void testListAllVenue(){
        List<Venue> testVenueList1=new ArrayList<>();
        testVenueList1=venueDAO.getAllVenues();
        int listSizeFirst=testVenueList1.size();
        
        
        String sql="INSERT INTO venue (id,name,city_id,description) "+
                "VALUES(DEFAULT,'Beauty Village',1,'Peaceful place for vacation.') RETURNING name";

    SqlRowSet testResult=jdbcTemplate.queryForRowSet(sql);

    List<Venue> testVenueList2= venueDAO.getAllVenues();

    if(testResult.next()){
        String testname=testResult.getString("name");

        assertEquals(listSizeFirst+ 1, testVenueList2.size());
        assertEquals("Beauty Village",testname);
    }
}

public void getCategoryList() {
    String sql = "INSERT INTO venue (id,name,city_id,description) " +
            "VALUES(DEFAULT,'Beauty Village',1,'Peaceful place for vacation.') RETURNING id";

    SqlRowSet venueResult = jdbcTemplate.queryForRowSet(sql);
    long newVenueId = 0;
    if (venueResult.next()) {
        newVenueId = venueResult.getLong("id");
    }


}
}