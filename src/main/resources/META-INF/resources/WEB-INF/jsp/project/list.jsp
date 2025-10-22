<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Projects</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp">
            <jsp:param name="page" value="projects" />
        </jsp:include>
        <main class="container">
            <jsp:include page="../includes/logout.jsp" />
            <nav aria-label="breadcrumb"><ul>
                <li class="active">Projects</li>
            </ul></nav>
            <hgroup>
                <h1>Projects</h1>
            </hgroup>
            <figure>
                <table id="table-projects">
                    <tbody>
                    <c:forEach items="${viewModel.projects}" var="project">
                        <tr>
                            <td><a href="${pageContext.request.contextPath}/project/show?id=${project.id}">${project.title}</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </figure>
            <p>
                <a href="${pageContext.request.contextPath}/project/create" role="button">Create New Project</a>
                <a href="${pageContext.request.contextPath}/import/word" role="button" class="secondary">Import Word Document</a>
            </p>
        </main>
    </body>
</html>
