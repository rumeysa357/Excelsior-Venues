package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.DESedeKeySpec;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCDepartmentDAO implements DepartmentDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Department> getAllDepartments() {
		ArrayList<Department> departments = new ArrayList<>();
		String sqlFindDepartmentById = "SELECT department_id, name "+
				"FROM department ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindDepartmentById);
		while(results.next()) {
			Department theDepartment = mapRowToDepartment(results);
			departments.add(theDepartment);
		}

		return departments;
	}


	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		ArrayList<Department> departments = new ArrayList<>();
		nameSearch = "%" + nameSearch + "%";
		String sql = "SELECT reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for FROM reservation " +
				"WHERE name LIKE ? AND start_id LIKE ? AND end_date LIKE ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql,nameSearch);

		while(results.next()) {
			Department theDepartment = mapRowToDepartment(results);
			departments.add(theDepartment);
		}

		return departments;
	}




//	@Override
//	public Department createDepartment(Department newDepartment) {
//		String sqlInsertDepartment ="INSERT INTO department(name) VALUES(?) RETURNING department_id;";
//		SqlRowSet rows=jdbcTemplate.queryForRowSet(sqlInsertDepartment,newDepartment.getName());
//		if(rows.next()){
//
//			int newDeptId=rows.getInt("department_id");
//			 newDepartment.setId((long) newDeptId);
//
//		}
//		return newDepartment;
//	}

	@Override
	public Department createDepartment(Department newDepartment) {
		String sqlInsertDepartment = "INSERT INTO department(department_id, name) VALUES(?, ?)";
		newDepartment.setId(getNextDepartmentId());
        jdbcTemplate.update(sqlInsertDepartment,newDepartment.getId(),newDepartment.getName());

			return newDepartment;
		}


	private long getNextDepartmentId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('department_department_id_seq')");
		if (nextIdResult.next()) {
			return nextIdResult.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new department");
		}
	}





	@Override
	public void saveDepartment(Department updatedDepartment) {

		String sql="UPDATE department set name= ? " +
				"WHERE department_id =?";

		jdbcTemplate.update(sql, updatedDepartment.getName(),updatedDepartment.getId());
	}




	@Override
	public Department getDepartmentById(Long id) {
		Department theDepartment=null;
		String sqlGetDepartmentById = "SELECT department_id, name "+
	  							   "FROM department "+
	 							   "WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetDepartmentById, id);
		    if(results.next()) {
				theDepartment = mapRowToDepartment(results);
			}
		return theDepartment;
	}



	private Department mapRowToDepartment(SqlRowSet results) {
		Department theDepartment;
		theDepartment = new Department();
		theDepartment.setId(results.getLong("department_id"));
		theDepartment.setName(results.getString("name"));

		return theDepartment;
	}

}
