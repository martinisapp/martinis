<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Not Found - Martinis</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp">
            <jsp:param name="page" value="error" />
        </jsp:include>
        <main class="container">
            <h1>Not Found</h1>
            <p><c:out value="${errorMessage}" default="The requested resource could not be found." /></p>
            <p><a href="${pageContext.request.contextPath}/project/list" role="button">Back to Projects</a></p>
        </main>
    </body>
</html>
