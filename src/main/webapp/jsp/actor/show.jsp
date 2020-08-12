<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Martinis - Actor Profile</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <div class="container">
            <jsp:include page="../includes/logout.jsp" />
            <ol class="breadcrumb">
                <li><a href="${pageContext.request.contextPath}/actor/list">Casting</a></li>
                <li class="active">${viewModel.first} ${viewModel.last}</li>
            </ol>
            <div class="page-header">
                <h1>${viewModel.first} ${viewModel.last} <small><a href="${pageContext.request.contextPath}/actor/edit?id=${viewModel.id}" class="btn btn-default btn-xs" role="button">edit</a> <a href="${pageContext.request.contextPath}/actor/delete?id=${viewModel.id}" class="btn btn-default btn-xs" role="button">delete</a></small></h1>
            </div>
            <p>Phone: ${viewModel.phone}</p>
            <p>Email: ${viewModel.email}</p>
        </div>
        <script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    </body>
</html>
