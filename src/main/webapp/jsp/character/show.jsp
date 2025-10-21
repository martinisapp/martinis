<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Character Profile</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <main class="container">
            <jsp:include page="../includes/logout.jsp" />
            <nav aria-label="breadcrumb"><ul>
                <li><a href="${pageContext.request.contextPath}/project/list">Projects</a></li>
                <li><a href="#"><a href="${pageContext.request.contextPath}/project/show?id=${viewModel.projectId}">${viewModel.projectTitle}</a></a></li>
                <li>${viewModel.name}</li>
            </ul></nav>
            <div >
                <h1>${viewModel.name} <small><a href="${pageContext.request.contextPath}/character/edit?id=${viewModel.id}" role="button" class="secondary" role="button">edit</a> <a href="${pageContext.request.contextPath}/character/delete?id=${viewModel.id}" role="button" class="secondary" role="button">delete</a></small></h1>
            </div>
            <p>Full Name: ${viewModel.fullName}</p>
            <c:if test="${not empty viewModel.actorName}">
                <p>Actor: <a href="${pageContext.request.contextPath}/actor/show?id=${viewModel.actorId}">${viewModel.actorName}</a></p>
            </c:if>
        </main>
    </body>
</html>
