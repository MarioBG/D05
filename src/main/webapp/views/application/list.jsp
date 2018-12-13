<%--
 * list.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<style type="text/css">

.pending{background-color: white;}
.pendingPeriodPassed{background-color: grey;}
.accepted{background-color: green;}
.accepted a{color: white;}
.rejected{background-color: orange;}

</style>

<!-- Listing grid -->

<security:authorize access="hasRole('EXPLORER')">
<spring:message code="application.filterByStatus"/>
<%-- <form action="application/explorer/list.do">
<select name="statusSelection">
<jstl:forEach items="${statusSet}" var="status">
<option label="${status}" value="${status}"/>
</jstl:forEach>
</select>
<input type="submit"/>
</form> --%>
<a href="application/explorer/list.do"><spring:message code="application.seeAll"/></a>
</security:authorize>
<display:table pagesize="5" class="displaytag" keepStatus="true"
	name="applications"  requestURI="${requestURI}" id="row">
	
	<!-- Cambiamos el color de fondo según el estado -->
	
	<!-- Calculamos el número de días que faltan para que termine la reparación. Si es menor a
	0 se debe aplicar otro estilo -->
	
	<jsp:useBean id="now" class="java.util.Date"/>
	<fmt:parseNumber var="dateDifference"
    value="${(now.time - row.trip.endDate.time) / (1000*60*60*24) }"
    integerOnly="true" />
	
	<jstl:choose>
	
		<jstl:when test="${row.status eq 'PENDING' and dateDifference lt 1}">
			<jstl:set value="pendingPeriodPassed" var="style"/>
		</jstl:when>
		
		<jstl:when test="${row.status eq 'PENDING'}">
			<jstl:set value="pending" var="style"/>
		</jstl:when>
		
		<jstl:when test="${row.status eq 'ACCEPTED'}">
			<jstl:set value="accepted" var="style"/>
		</jstl:when>
		
		<jstl:when test="${row.status eq 'REJECTED'}">
			<jstl:set value="rejected" var="style"/>
		</jstl:when>
		
	</jstl:choose>

	<spring:message code="application.creationMoment" var="creationMomentHeader" />
	<spring:message code="master.page.date.format" var="dateFormat" />
	<display:column property="creationMoment" title="${creationMomentHeader}"
		 format="{0,date,${dateFormat}}" />

	
	<spring:message code="application.status" var="status" />
	<display:column class="${style}" title="${status}">  
	
	<jstl:out value="${row.status}"/>
	
	<security:authorize access="hasRole('CUSTOMER')">
	<jstl:if test="${row.status eq 'PENDING'}">
	[<a href="application/customer/edit.do?applicationId=${row.id}"><spring:message
						code="application.changeStatus" /></a>]<br/>
	</jstl:if>
	</security:authorize>
	
	<security:authorize access="hasRole('CUSTOMER')">
	<jstl:if test="${(row.status eq 'ACCEPTED' or row.status eq 'PENDING') and dateValue gt now}">
	[<a href="application/customer/cancel.do?applicationId=${row.id}"><spring:message
						code="application.cancel" /></a>]<br/>
	</jstl:if>
	</security:authorize>
	</display:column>
	
	<spring:message code="application.fixUpTask" var="fixUpTask" />
	<display:column property="fixUpTask.description" title="${fixUpTask}" />

	<spring:message code="application.creditCard"
		var="creditCard" />
	<display:column title="${creditCard}">
	
	 	<jstl:choose>

			<jstl:when test="${empty row.creditCard and row.status == 'DUE'}">
			<security:authorize access="hasRole('EXPLORER')">
				<a href="application/edit.do?applicationId=${row.id}"><spring:message
						code="application.enterCard" /></a>
			</security:authorize>
			</jstl:when>
			
			<jstl:otherwise>
			${row.creditCard.brandName}
			</jstl:otherwise>
			
		</jstl:choose> 
	</display:column>

	<spring:message code="application.comment" var="comment" />
	<display:column property="comments" title="${comment}"  />
	
</display:table>

<security:authorize access="hasRole('HANDYWORKER')">
<a href="application/explorer/create.do"><spring:message code="application.create"/></a>
 </security:authorize>
