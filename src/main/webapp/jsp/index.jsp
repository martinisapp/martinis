<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Martinis</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <jsp:include page="includes/nav.jsp">
            <jsp:param name="page" value="index" />
        </jsp:include>
        <div class="container">
            <c:if test="${pageContext.request.userPrincipal.name == null}">
                <p><a href="${pageContext.request.contextPath}/login" class="btn btn-primary" role="button">Login</a></p>
            </c:if>
        </div>
        <script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    </body>
</html>
