<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/moduleResources/programOver/jquery.js" />

<openmrs:htmlInclude
	file="/moduleResources/programOver/jquery.dataTables.js" />

<openmrs:htmlInclude file="/moduleResources/programOver/demo_page.css" />

<openmrs:htmlInclude file="/moduleResources/programOver/demo_table.css" />

<script type="text/javascript" charset="utf-8">
	$(document).ready( function() {
		$('#example').dataTable( {
			"sPaginationType" :"full_numbers"
		});
	});
</script>


<openmrs:require privilege="View Patient Details in Program Statistics Module" otherwise="/login.htm" redirect="/module/@MODULE_ID@/programoverForm" />


<a href="${pageContext.request.contextPath}/admin/index.htm">Admin</a>
|
<a
	href="${pageContext.request.contextPath}/module/programOver/patientDawnloadController.form?checkType=${checkType}&lineNumber=${lineNumber}">Export</a>

<c:forEach var="patientNumber" items="${patientNumbers}"
	varStatus="status">

	<h3 align="center"><spring:message
		code="programOver.${patientNumber.key}" /></h3>

</c:forEach>
<div id="dt_example">
<div id="container">
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="example">
	<thead>
		<tr>
			<th><b>Number</b></th>
			<th><b>Given Name</b></th>
			<th><b>Family Name</b></th>
			<th><b>Age</b></th>
			<th><b>Gender</b></th>			
			<th><b>View Patient Dashboard</b></th>
			<th><b>${eventDate}</b></th>
			<th>${eventDate1}<b></b></th>	
			<th>${consultationDateTitle}</th>		
			
		</tr>
	</thead>
	<tbody>
		<c:forEach var="list" items="${patients}" varStatus="status">
			<tr>
				<td>${status.count}</td>
				<td>${list[0].givenName}</td>
				<td>${list[0].familyName}</td>
				<td>${list[0].age}</td>

				<td><img
					src="${pageContext.request.contextPath}/images/${list[0].gender == 'M' ? 'male' : 'female'}.gif" /></td>				
				<td><a
					href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${list[0].patientId}">View
				Dashboard</a></td>
				<td>${list[1]}</td>
				<td>${list[3]}</td>	
				<td>${list[5]}</td>				
				
			</tr>
		</c:forEach>
	</tbody>
</table>
</div>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>