package com.techelevator.projects.model.jdbc;

import com.techelevator.projects.model.Department;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class JDBCDepartmentDAOTest {

    private static final String  TEST_DEPARTMENT="DUMMY DEPT"; //DUMMY COUNTRY


    /* Using this particular implementation of DataSource so that
     * every database interaction is part of the same database
     * session and hence the same database transaction */
    private static SingleConnectionDataSource dataSource;    //
    private JDBCDepartmentDAO dao;

    /* Before any tests are run, this method initializes the datasource for testing. */
    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        /* The following line disables autocommit for connections
         * returned by this DataSource. This allows us to rollback
         * any changes after each test */
        dataSource.setAutoCommit(false);
    }
    @AfterClass
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }

    @Before
    public void setup() {
        String sqlInsertDepartment = "INSERT INTO department (name) VALUES (?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sqlInsertDepartment,TEST_DEPARTMENT);
        dao = new JDBCDepartmentDAO(dataSource);
    }

    /* After each test, we rollback any changes that were made to the database so that
     * everything is clean for the next test */

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    public void create_department_test(){
        Department theDepartment= dao.createDepartment(getDepartment("DUMMY DEP"));
        Department createdDepartment=dao.createDepartment(theDepartment);

        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
        String sql="SELECT department_id,name FROM department WHERE department_id= ?";
        SqlRowSet result=jdbcTemplate.queryForRowSet(sql,createdDepartment.getName());
        if(result.next()){
            assertEquals("DUMMY DEP",result.getString("name"));
            assertEquals(createdDepartment.getId().longValue(),result.getLong("department_is"));
        }
       // assertEquals(theDepartment.getName(),newDepartment.getName());
    }
//    @Test
//    public void add_department_and_read_it_back() throws SQLException {
//        Department department=dao.createDepartment(getDepartment("DUMMY DEP"));
//        Department theDepartment = getDepartment"Sales");       //creating DUMMY DEPARTMENT  object;
//
//        dao.createDepartment(theDepartment);
//        Department createdDepartment=dao.getDepartmentById(theDepartment.getId());
//
//        assertNotEquals(null, theDepartment.getId());
//        assertDepartmentsAreEqual(theDepartment, createdDepartment);
//    }

//    @Test
//    public void return_departments_by_department_id() {
//        Department theDepartment = getDepartment(TEST_EMPLOYEE, "Engineering"); //DUMMY department;
//
//        dao.saveDepartment(theDepartment);
//
//        List<Department> results =new ArrayList<>();
//        Department gettingDepartment=dao.getDepartmentById((long) TEST_EMPLOYEE);
//        results.add(gettingDepartment);
//
//        assertNotNull(results);
//        assertEquals(1, results.size());
//        Department createdDepartment = results.get(0);
//        assertDepartmentsAreEqual(theDepartment, createdDepartment);
//
//
//        // List<City> results = dao.findCityByDistrict(testDistrict);?
//
//    }
//

//    @Test
//    public void searchDepartmentsByName() {
//    }
//
//    @Test
//    public void createDepartment() {
//    }
//
//    @Test
//    public void saveDepartment() {
//    }
//
//    @Test
//    public void getDepartmentById() {
//    }


    private Department getDepartment( String name) {  //IT POPULATES CITY OBJECT, SO,WE DONOT WRITE OVER AGAIN
        Department theDepartment = new Department();
        theDepartment.setName(name);
        return theDepartment;
    }
    private void assertDepartmentsAreEqual(Department expected, Department actual) {
        assertEquals(expected.getName(), actual.getName());
    }


}

