package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Department;
import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Employee> getAllEmployees() {
		ArrayList<Employee> employees = new ArrayList<>();
		String sqlgetEmployee = "SELECT employee_id, department_id,first_name,last_name,birth_date,hire_date " +
				"FROM employee ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlgetEmployee);
		while (results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
		return employees;

	}
	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		ArrayList<Employee> employees = new ArrayList<>();
		firstNameSearch= "%" + firstNameSearch + "%";
		lastNameSearch= "%" + lastNameSearch + "%";

		String sql="SELECT employee_id, department_id,first_name,last_name,birth_date,hire_date " +
		"FROM employee "+
	    "WHERE  first_name  LIKE ? AND last_name LIKE ? " ;

	 	SqlRowSet results = jdbcTemplate.queryForRowSet(sql,firstNameSearch,lastNameSearch);
		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
		return employees;
	}


	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {

		ArrayList<Employee> employees = new ArrayList<>();

		String sql="SELECT employee_id, department_id,first_name,last_name,birth_date,hire_date " +
				"FROM employee "+
				"WHERE  department_id = ?" ;
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);

		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}

		return employees;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {

		ArrayList<Employee> employees = new ArrayList<>();

		String sql="SELECT employee.employee_id, employee.department_id,first_name,last_name,birth_date,hire_date " +
				"FROM employee "+
				"LEFT JOIN project_employee ON project_employee.employee_id=employee.employee_id " +
				"WHERE project_employee.employee_id IS NULL" ;
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}

		return employees;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {

		ArrayList<Employee> employees = new ArrayList<>();

		String sql="SELECT employee.employee_id, employee.department_id,first_name,last_name,birth_date,hire_date " +
				"FROM employee "+
				"JOIN project_employee ON employee.employee_id=project_employee.employee_id " +
				"WHERE  project_id = ? " ;
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql,projectId);

		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
		return employees;
	}

	@Override
	public void changeEmployeeDepartment(Long employee_id, Long departmentId) {

		String sql="UPDATE department set name= ? " +
				"WHERE department_id =?";

		jdbcTemplate.update(sql, employee_id, departmentId);
	}

	private Employee mapRowToEmployee(SqlRowSet results) {
		Employee theEmployee;
		theEmployee = new Employee();
		theEmployee.setId(results.getLong("employee_id"));
		theEmployee.setDepartmentId(results.getLong("department_id"));
		theEmployee.setFirstName(results.getString("first_name"));
		theEmployee.setLastName(results.getString("last_name"));
		theEmployee.setBirthDay(results.getDate("birth_date").toLocalDate());
		theEmployee.setHireDate(results.getDate("hire_date").toLocalDate());

		return theEmployee;
	}
	}
