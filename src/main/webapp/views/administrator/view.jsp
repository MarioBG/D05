<%--
 * action-2.jsp
 *
 * Copyright (C) 2018 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<table class="ui celled table <jstl:if test='${actor.suspicious}'>red</jstl:if>">
	<thead>
		<tr>
			<th colspan="2">
				<h4 class="ui image header">
					<img src="${actor.photo}" class="ui mini rounded image">
					<div class="content">
						${actor.name}
						<div class="sub header">${actor.middleName}${actor.surname}</div>
					</div>
				</h4>
			</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td data-label="email">Email</td>
			<td data-label="email">${actor.email}</td>
		</tr>
		<tr>
			<td data-label="phoneNumber">Phone Number</td>
			<td data-label="phoneNumber">${actor.phoneNumber}</td>
		</tr>
		<tr>
			<td data-label="address">Address</td>
			<td data-label="address">${actor.address}</td>
		</tr>
	</tbody>
</table>