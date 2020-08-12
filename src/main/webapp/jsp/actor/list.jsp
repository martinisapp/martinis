<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Martinis - Actors</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp">
            <jsp:param name="page" value="actors" />
        </jsp:include>
        <div class="container">
            <jsp:include page="../includes/logout.jsp" />
            <ol class="breadcrumb">
                <li class="active">Actors</li>
            </ol>
            <div class="page-header">
                <h1>Actors</h1>
            </div>
            <table id="table-actors" class="table table-hover">
                <c:forEach items="${viewModel.actors}" var="actor">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/actor/show?id=${actor.id}">${actor.first} ${actor.last}</a></td>  
                    </tr>
                </c:forEach>
            </table>
            <a href="${pageContext.request.contextPath}/actor/create" class="btn btn-primary" role="button">Create New Actor</a>
        </div>
        <script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    </body>
</html>
