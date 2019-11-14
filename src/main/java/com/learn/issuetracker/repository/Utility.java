package com.learn.issuetracker.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.learn.issuetracker.model.Employee;
import com.learn.issuetracker.model.Issue;

/*
 * This class has methods for parsing the String read from the files in to corresponding Model Objects
*/
public class Utility {
	
	private Utility() {
		//Private Constructor to prevent object creation
	}

	/*
	 * parseEmployee takes a string with employee details as input parameter and parses it in to an Employee Object 
	*/
	public static Employee parseEmployee(String employeeDetail) {
		
		if(null==employeeDetail ||employeeDetail.isEmpty()) {
			return null;
		}
		
		employeeDetail=employeeDetail.concat(",");
		String[] readEmployee=employeeDetail.split(",");
		Employee employee=new Employee(Integer.parseInt(readEmployee[0]), readEmployee[1], readEmployee[2]);
		return employee;
	}

	/*
	 * parseIssue takes a string with issue details and parses it in to an Issue Object. The employee id in the 
	 * Issue details is used to search for an an Employee, using EmployeeRepository class. If the employee is found
	 * then it is set in the Issue object. If Employee is not found, employee is set as null in Issue Object  
	*/

	public static Issue parseIssue(String issueDetail) {
		if(null==issueDetail||issueDetail.isEmpty()) {
		return null;
		}
		String[] readIssue=issueDetail.split(",");
		DateTimeFormatter format=DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate created=LocalDate.parse(readIssue[2], format);
		LocalDate expResolve=LocalDate.parse(readIssue[3],format);
		Optional<Employee> employee= EmployeeRepository.getEmployee(Integer.parseInt(readIssue[6]));
		Issue issue;
		if(employee.isPresent()) {
			Employee emp=employee.get();
			issue=new Issue(readIssue[0], readIssue[1], created, expResolve, readIssue[4], readIssue[5], emp);
		}
		else {
			issue=new Issue(readIssue[0], readIssue[1], created, expResolve, readIssue[4], readIssue[5], null);
		}
		return issue;
	}
}
