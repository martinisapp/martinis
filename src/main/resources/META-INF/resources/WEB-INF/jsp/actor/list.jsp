<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Casting</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp">
            <jsp:param name="page" value="casting" />
        </jsp:include>
        <main class="container">
            <jsp:include page="../includes/logout.jsp" />
            <nav aria-label="breadcrumb"><ul>
                <li class="active">Casting</li>
            </ul></nav>
            <hgroup>
                <h1>Casting</h1>
            </div>
            <table id="table-actors" "">
                <c:forEach items="${viewModel.actors}" var="actor">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/actor/show?id=${actor.id}">${actor.first} ${actor.last}</a></td>  
                    </tr>
                </c:forEach>
            </table>
            <a href="${pageContext.request.contextPath}/actor/create" role="button" role="button">Create New Actor</a>
        </div>
    </body>
</html>
