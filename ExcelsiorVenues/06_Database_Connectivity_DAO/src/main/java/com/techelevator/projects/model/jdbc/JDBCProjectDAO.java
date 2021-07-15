package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		ArrayList<Project> projects = new ArrayList<>();
		String sqlgetProjects = "SELECT project_id, name,from_date,to_date " +
				"FROM project ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlgetProjects);
		while (results.next()) {
			Project theProject = mapRowToProject(results);
			projects.add(theProject);
		}
		return projects;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {

			String sql = "DELETE FROM project_employee WHERE employee_id = ? AND project_id = ? ";
			jdbcTemplate.update(sql, projectId, employeeId);
		}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {

			String sqlAddEmployee = "INSERT INTO project_employee(project_id, employee_id) " +
					"VALUES(?, ?)";
			jdbcTemplate.update(sqlAddEmployee, projectId, employeeId);
	}

	private Project mapRowToProject(SqlRowSet results) {
		Project theProject;
		theProject = new Project();
		theProject.setId(results.getLong("project_id"));
		theProject.setName(results.getString("name"));
		if (results.getDate("from_date")!= null) {
			theProject.setStartDate(results.getDate("from_date").toLocalDate());
		}
		if (results.getDate("to_date")!= null) {
			theProject.setEndDate(results.getDate("to_date").toLocalDate());
		}
		return theProject;
	}
}
