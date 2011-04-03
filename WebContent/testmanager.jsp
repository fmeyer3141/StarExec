<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="data.*, data.to.*, manage.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Manager Tester</title>
</head>

<%!
	Jobject job;	// Where the job is stored
	SolverLink lnk;	// A subjob (1 solver and 1 or more benchmarks)
%>

<body>
	<h1>Manager Tester!</h1>
	<p>This will run the manager with default solver and benchmark. Woo!</p>
	<p>I'm adding solver ID 1 and associating benchmark ID 1 with it, then passing the jobject to the JobManager.</p>
	<% 
	lnk = job.addSolver(1L);
	lnk.addBenchmark(1L);
	
	JobManager.doJob(job);
	
	%>
</body>
</html>