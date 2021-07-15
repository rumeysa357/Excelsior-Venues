package com.techelevator;

import java.sql.SQLException;
import java.time.LocalDate;

import javax.sql.DataSource;

import domain.Reservation;
import model.JDBCReservationDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import static org.junit.Assert.assertNotEquals;

public abstract class DAOIntegrationTest {

	private static final String TEST_RESERVATION = "TESTRESERVATION";

	private JDBCReservationDAO dao;

	private JdbcTemplate jdbcTemplate;
	/*
	 * Using this particular implementation of DataSource so that every database
	 * interaction is part of the same database session and hence the same database
	 * transaction
	 */
	private static SingleConnectionDataSource dataSource;

	/*
	 * Before any tests are run, this method initializes the datasource for testing.
	 */
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

	@Before
	public void setup() {
		String sqlInsertReservation = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for) VALUES (5005, 21, 5, 2021-06-26, 2021-07-04, ?)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertReservation, TEST_RESERVATION);
		dao = new JDBCReservationDAO(dataSource);
	}

	private Reservation getFakeReservation( int space_id, int attendance, String reserved_for) {
		Reservation fakeReservation = new Reservation();
		fakeReservation.setSpaceId(space_id);
		fakeReservation.setNumberOfAttendees(attendance);
		fakeReservation.setReservedFor(reserved_for);
		return fakeReservation;
	}

//	@Test
//	public void saveFakeReservationAndReadBack() throws SQLException {
//		Reservation phonyReservation = getFakeReservation(21, 5, "Spontaneous Disco Party with Coach Steve");
//
//		dao.save(getFakeReservation());
//		City savedCity = dao.findCityById(theCity.getId());
//
//		assertNotEquals(null, theCity.getId());
//		assertCitiesAreEqual(theCity, savedCity);
//	}

//	@Test
//	public void save_new_city_and_read_it_back() throws SQLException {
//		City theCity = getCity("SQL Station", "South Dakota", TEST_COUNTRY, 65535);
//
//		dao.save(theCity);
//		City savedCity = dao.findCityById(theCity.getId());
//
//		assertNotEquals(null, theCity.getId());
//		assertCitiesAreEqual(theCity, savedCity);
//	}

	/*
	 * After all tests have finished running, this method will close the DataSource
	 */

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
}
