<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Actor Profile</title>
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
                <li><a href="${pageContext.request.contextPath}/actor/list">Casting</a></li>
                <li>${viewModel.first} ${viewModel.last}</li>
            </ul></nav>
            <div >
                <h1>${viewModel.first} ${viewModel.last} <small><a href="${pageContext.request.contextPath}/actor/edit?id=${viewModel.id}" role="button" class="secondary" role="button">edit</a> <a href="${pageContext.request.contextPath}/actor/delete?id=${viewModel.id}" role="button" class="secondary" role="button">delete</a></small></h1>
            </div>
            <p>Phone: ${viewModel.phone}</p>
            <p>Email: ${viewModel.email}</p>
        </main>
    </body>
</html>
