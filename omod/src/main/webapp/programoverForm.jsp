<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ taglib prefix="fn" uri="/WEB-INF/taglibs/fn.tld"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<openmrs:htmlInclude file="/moduleResources/programOver/jsControl.js" />
<openmrs:htmlInclude file="/moduleResources/programOver/jquery.js" />
<openmrs:htmlInclude file="/moduleResources/programOver/jquery.flot.js" />

<!--  <openmrs:require privilege="View Program Statistics Module" otherwise="/login.htm" redirect="/module/@MODULE_ID@/programoverForm" /> -->

<h3>${programName}</h3>
<b class="boxHeader"><spring:message
	code="programOver.searchbyCriteria" /> </b>
<div class="box">
<div
	style="background: #f6f6f6; border: 1px #808080 solid; padding: 0.5em; margin: 0.5em">
<div>
<form action="" method="post" name="" onsubmit="return checkform(this);">
<table>
	<tr>
		<td>
		<table valign="top">
			<tr>
				<td colspan="2"></td>
			</tr>
			<tr>
				<td>
				<table width="500" align="left" cellspacing="0">

					<tr>
						<td><select name="checkAllCriteria">
							<option value="any">Any</option>
							<option value="all">All</option>
							<option value="none">No One</option>
						</select></td>

					</tr>
					<tr>
						<td width="20%" valign="top"><spring:message
							code="programOver.criteria" />:</td>
						<td width="40%" colspan="1000"><select name="checkType" multiple="multiple"
							size="10" onchange="displayUnit(this.value)">
							<c:forEach var="criteria" items="${criteriaSet}">
								<option value="${criteria}"><spring:message
									code="programOver.${criteria}" /></option>
							</c:forEach>
						</select></td>
					</tr>
					<tr>
						<td><div id ="From"><spring:message code="programOver.inbetween" />:</div></td>
						<td><div id="StartDate"><openmrs_tag:dateField
							formFieldName="startDate" startValue="${startDate}" /></div></td>
							<td><div id="To"><spring:message
							code="programOver.and" /></div></td>
							<td><div id = "EndDate"><openmrs_tag:dateField
							formFieldName="endDate" startValue="${endDate}" /></div></td><input
							type="hidden" name="all" value="all" />
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td valign="top">
		<table
			style="background-color: #f9f9f9; -moz-border-radius: 4px; -webkit-border-radius: 4px;">
			<tr>
				<td colspan="2"></td>
			</tr>
			<tr>
				<td>
				<div id="gender"><spring:message code="programOver.gender" /></div>
				</td>
				<td>
				<div id="genderInput"><select name="gender">
					<option value="any" ${param.gender== "any" ? 'selected="selected"':''}><spring:message
						code="programOver.any" /></option>
					<option value="m" ${param.gender== "m" ? 'selected="selected"':''}><spring:message
						code="programOver.male" /></option>
					<option value="f" ${param.gender== "f" ? 'selected="selected"':''}><spring:message
						code="programOver.female" /></option>
				</select></div>
				</td>

			</tr>
			<tr>
				<td>
				<div id="age"><spring:message code="programOver.age" />:</div>
				</td>
				<td>
				<div id="ageInput"><spring:message code="programOver.between" />
				<input type="text" name="minAge" size="3" value="${param.minAge}" />
				<spring:message code="programOver.and" /> <input type="text"
					name="maxAge" size="3" value="${param.maxAge}" /> <spring:message
					code="programOver.years" /></div>
				</td>
			</tr>
			<tr>
				<td>
				<div id="numbers" style="display: none;"><spring:message
					code="programOver.NumberOfMonths" />:</div>
				</td>
				<td>
				<div id="inputNumbers" style="display: none;"><input
					type="text" name="numberOfMonths" size="3" value="" /></div></td>

			</tr>
			<tr>
				<td><div id="Graphic">Display graphic:<input type="checkbox" name="id"
					values="yes"></div></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="2"><input type="hidden" name="formSubmitted"
			value="1" /> <input type="submit"
			value="<spring:message
			code="programOver.searchSubmit"/>" /></td>
	</tr>
</table>
</form>
</div>
</div>
</div>
<br />
<br />
<c:if test="${fn:length(patientNumbers)>0}">

	<b class="boxHeader"><spring:message
		code="programOver.numberOfPatients" /></b>
	<div class="box">

	<div style="border: 1px #f9f9f9 solid;">
	<table width="100%">
		<c:forEach var="patientNumber" items="${patientNumbers}"
			varStatus="status">
			<tr>
				<td width="70%"><spring:message
					code="programOver.${patientNumber.key}" /></td>

				<!--  <td><a
					href="${pageContext.request.contextPath}/module/programOver/patientsDetailsOverFormList.form?checkType=${programIdKey}&programId=${programId}&startDate=<openmrs:formatDate date="${startDate}"/>&endDate=<openmrs:formatDate date="${endDate}"/>">${patientNumber.value}</a></td>-->

				<td><a
					href="${pageContext.request.contextPath}/module/programOver/patientsDetailsOverFormList.form?checkType=${patientNumber.key}&programId=${programId}&lineNumber=${status.count}&minAge=${minAge}&maxAge=${maxAge}&gender=${gender}&startDate=${startDate}&endDate=${endDate}">${patientNumber.value}</a></td>
			</tr>
		</c:forEach>
	</table>
	</div>
	</div>
</c:if>
<br />
<br />
<c:if test="${fn:length(patientNumbers)>0}">
<c:if test="${fn:length(checkedValue)>0}">
	<b class="boxHeader"> <c:forEach var="patientNumber"
		items="${patientNumbers}" varStatus="status">
		<spring:message code="programOver.${patientNumber.key}" />
	</c:forEach> </b>
	<div class="box">
	
		<!-- <a href="javascript:void(printSpecial())">PRINT</a>  -->
		<div id="placeholder" style="width: 1000px; height: 300px;"></div></div>

		<script language="javascript" type="text/javascript">
$(function (){
    var listIndicators = [], yax = [];
    <c:forEach items="${listOfNumberOfPatientByYear}" var="numberOfPatientByYear">
      <c:forEach items="${numberOfPatientByYear.value}" var="patientCount">
        yax.push([${patientCount.key}, ${patientCount.value}]);
      </c:forEach> 
      listIndicators.push(yax);
      yax = [];
    </c:forEach> 

    var plot = $.plot($("#placeholder"),
           [ 
   	<c:forEach items="${listOfNumberOfPatientByYear}" var="numberOfPatientByYear" varStatus="status">
             { data: listIndicators[${status.count-1}], label: "${numberOfPatientByYear.key}" }
             <c:if test="${not status.last}">,</c:if>
    </c:forEach>             
             ], {
               series: {
                   lines: { show: true },
                   points: { show: true }
               },
               grid: { hoverable: true }
             });

    function showTooltip(x,y, contents) {
        $('<div id="tooltip">' + contents + '</div>').css( {
            position: 'absolute',
            display: 'none',
            top: y + 5,
            left: x + 5,
            border: '1px solid #fdd',
            padding: '2px',
            'background-color': '#fee',
            opacity: 0.80
        }).appendTo("body").fadeIn(200);
    }

    var previousPoint = null;
    $("#placeholder").bind("plothover", function (event, pos, item) {
        $("#x").text(pos.x);
        $("#y").text(pos.y);
         if (item) {
             if (previousPoint != item.datapoint) {
                 previousPoint = item.datapoint;
                 $("#tooltip").remove();
                 var x = item.datapoint[0],
                     y = item.datapoint[1];
                 
                 showTooltip(item.pageX, item.pageY,
                             item.series.label + " <spring:message code="SearchResults.of"/> " + x + " = " + y);
             }
         }
    });
});
</script>
</c:if>
</c:if>
<!-- down -->
<%@ include file="/WEB-INF/template/footer.jsp"%>