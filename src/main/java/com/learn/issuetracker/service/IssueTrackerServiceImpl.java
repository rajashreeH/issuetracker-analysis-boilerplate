package com.learn.issuetracker.service;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;



import com.learn.issuetracker.exceptions.IssueNotFoundException;
import com.learn.issuetracker.model.Employee;
import com.learn.issuetracker.model.Issue;
import com.learn.issuetracker.repository.IssueRepository;



/*
 * This class contains functionalities for searching and analyzing Issues data Which is stored in a collection
 * Use JAVA8 STREAMS API to do the analysis
 * 
*/
public class IssueTrackerServiceImpl implements IssueTrackerService {

	/*
	 * CURRENT_DATE contains the date which is considered as todays date for this
	 * application Any logic which uses current date in this application, should
	 * consider this date as current date
	 */
	private static final String CURRENT_DATE = "2019-05-01";

	/*
	 * The issueDao should be used to get the List of Issues, populated from the
	 * file
	 */
	private IssueRepository issueDao;
	private LocalDate today;

	/*
	 * Initialize the member variables Variable today should be initialized with the
	 * value in CURRENT_DATE variable
	 */
	public IssueTrackerServiceImpl(IssueRepository issueDao) {
		
		this.issueDao=issueDao;
		this.today=LocalDate.parse(CURRENT_DATE);
	}

	/*
	 * In all the below methods, the list of issues should be obtained by used
	 * appropriate getter method of issueDao.
	 */
	/*
	 * The below method should return the count of issues which are closed.
	 */
	@Override
	public long getClosedIssueCount() {
		
		long countOfClosed=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equals("CLOSED"))
				.count();
		return countOfClosed;
	}

	/*
	 * The below method should return the Issue details given a issueId. If the
	 * issue is not found, method should throw IssueNotFoundException
	 */

	@Override
	public Issue getIssueById(String issueId) throws IssueNotFoundException {
		
		if(null==issueId||issueId.isEmpty()) {
			return null;
		}
		Optional<Issue> issue= issueDao.getIssues().stream()
		.filter(i->i.getIssueId().equals(issueId))
		.findAny();
		
		if(issue.isPresent()) {
			Issue issue2=issue.get();
			return issue2;
		}
		else {
			
			throw new IssueNotFoundException();
		}
	}

	/*
	 * The below method should return the Employee Assigned to the issue given a
	 * issueId. It should return the employee in an Optional. If the issue is not
	 * assigned to any employee or the issue Id is incorrect the method should
	 * return empty optional
	 */
	@Override
	public Optional<Employee> getIssueAssignedTo(String issueId) {
		
		Optional<Employee> issueAssignedEmployee=Optional.empty();
		
		issueAssignedEmployee=issueDao.getIssues().stream()
				.filter(i->i.getIssueId().equalsIgnoreCase(issueId))
				.map(Issue::getAssignedTo)
				.findAny();
		
		
		return issueAssignedEmployee;
	}

	/*
	 * The below method should return the list of Issues given the status. The
	 * status can contain values OPEN / CLOSED
	 */
	@Override
	public List<Issue> getIssuesByStatus(String status) {
		
		List<Issue> issuesByStatus=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equals(status))
				.collect(Collectors.toList());
		return issuesByStatus;
	}

	/*
	 * The below method should return a LinkedHashSet containing issueid's of open
	 * issues in the ascending order of expected resolution date
	 */
	@Override
	public Set<String> getOpenIssuesInExpectedResolutionOrder() {
		
		LinkedHashSet<String> openIssues=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equalsIgnoreCase("OPEN"))
				.sorted((i1,i2)->i1.getExpectedResolutionOn().compareTo(i2.getExpectedResolutionOn()))
				.collect(Collectors.mapping(Issue::getIssueId, Collectors.toCollection(LinkedHashSet::new)));
		return openIssues;
	}

	/*
	 * The below method should return a List of open Issues in the descending order
	 * of Priority and ascending order of expected resolution date within a priority
	 */
	@Override
	public List<Issue> getOpenIssuesOrderedByPriorityAndResolutionDate() {
		
		List<Issue> issues=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equalsIgnoreCase("OPEN"))
				.sorted(Comparator.comparing(Issue::getPriority).reversed()
						.thenComparing(Comparator.comparing(Issue::getExpectedResolutionOn)))
				.collect(Collectors.toList());
				
		return issues;
	}

	/*
	 * The below method should return a List of 'unique' employee names who have
	 * issues not closed even after 7 days of Expected Resolution date. Consider the
	 * current date as 2019-05-01
	 */
	@Override
	public List<String> getOpenIssuesDelayedbyEmployees() {
		
		List<String> issues=issueDao.getIssues().stream()
				.filter(i->!i.getStatus().equalsIgnoreCase("closed"))
				.filter(i->ChronoUnit.DAYS.between(i.getExpectedResolutionOn(), today)>7)
				.distinct()
				.map(i->i.getAssignedTo().getName())
				.collect(Collectors.toList());
				
		return issues;
	}

	/*
	 * The below method should return a map with key as issueId and value as
	 * assigned employee Id. THe Map should contain details of open issues having
	 * HIGH priority
	 */
	@Override
	public Map<String, Integer> getHighPriorityOpenIssueAssignedTo() {
		
		Map<String, Integer> issueWithAssignee=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equalsIgnoreCase("open"))
				.filter(i->i.getPriority().equalsIgnoreCase("HIGH"))
				.collect(Collectors.toMap(Issue::getIssueId, i->i.getAssignedTo().getEmplId()));
		return issueWithAssignee;
	}

	/*
	 * The below method should return open issues grouped by priority in a map. The
	 * map should have key as issue priority and value as list of open Issues
	 */
	@Override
	public Map<String, List<Issue>> getOpenIssuesGroupedbyPriority() {
		
		Map<String, List<Issue>> issue=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equalsIgnoreCase("open"))
				.collect(Collectors.groupingBy(Issue::getPriority,Collectors.toList()));
		return issue;
	}

	/*
	 * The below method should return count of open issues grouped by priority in a map. 
	 * The map should have key as issue priority and value as count of open issues 
	 */
	@Override
	public Map<String, Long> getOpenIssuesCountGroupedbyPriority() {
		
		Map<String,Long> issue=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equalsIgnoreCase("OPEN"))
				.collect(Collectors.groupingBy(Issue::getPriority,Collectors.counting()));
		return issue;
	}
	
	/*
	 * The below method should provide List of issue id's(open), grouped by location
	 * of the assigned employee. It should return a map with key as location and
	 * value as List of issue Id's of open issues
	 */
	@Override
	public Map<String, List<String>> getOpenIssueIdGroupedbyLocation() {
		
		Map<String, List<String>> issues=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equalsIgnoreCase("open"))
				.collect(Collectors.groupingBy(i->i.getAssignedTo().getLocation(),
						Collectors.mapping(Issue::getIssueId, Collectors.toList())));
						
		return issues;
	}
	
	/*
	 * The below method should provide the number of days, since the issue has been
	 * created, for all high/medium priority open issues. It should return a map
	 * with issueId as key and number of days as value. Consider the current date as
	 * 2019-05-01
	 */
	@Override
	public Map<String, Long> getHighMediumOpenIssueDuration() {
		
		Map<String, Long> issues=issueDao.getIssues().stream()
				.filter(i->i.getStatus().equalsIgnoreCase("OPEN"))
				.filter(i->!i.getPriority().equalsIgnoreCase("low"))
				.collect(Collectors.toMap(Issue::getIssueId, i-> ChronoUnit.DAYS.between(i.getCreatedOn(), today)));
		return issues;
	}
}