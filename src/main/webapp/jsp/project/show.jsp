<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Project Profile</title>
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
                <li>${viewModel.title}</li>
            </ul></nav>
            <div >
                <h1>${viewModel.title} <small><a href="${pageContext.request.contextPath}/project/edit?id=${viewModel.id}" role="button" class="secondary" role="button">edit</a> <a href="${pageContext.request.contextPath}/project/delete?id=${viewModel.id}" role="button" class="secondary" role="button">delete</a></small></h1>
            </div>
            <div class="grid">
                <div >
                    <h2>Scenes</h2>
                    <table id="table-scenes" >
                        <c:forEach items="${viewModel.scenes}" var="scene" varStatus="loop">
                            <tr>
                                <td><a href="${pageContext.request.contextPath}/scene/show?id=${scene.id}" style="text-transform: uppercase;">${scene.name}</a></td>
                                <td>
                                    <div class="nowrap">
                                        <c:if test="${not loop.last}">
                                            <a href="${pageContext.request.contextPath}/scene/moveDown?id=${scene.id}" class="btn btn-default btn-xs move-down" role="button">↓</a>
                                        </c:if>
                                        <c:if test="${not loop.first}">
                                            <a href="${pageContext.request.contextPath}/scene/moveUp?id=${scene.id}" class="btn btn-default btn-xs move-up" role="button">↑</a>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                    <p><a href="${pageContext.request.contextPath}/scene/create?projectId=${viewModel.id}" role="button">Create New Scene</a></p>
                </div>
                <div >
                    <c:if test="${not empty viewModel.persons}">
                        <article>
                            <header>
                                <h3>Characters</h3>
                            </div>
                            <ul>
                            <c:forEach items="${viewModel.persons}" var="character">
                                <li><a href="${pageContext.request.contextPath}/character/show?id=${character.id}">${character.name}</a></li>
                            </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/character/create?projectId=${viewModel.id}" role="button">Create New Character</a>
                </div>
            </div>
        </main>
    </body>
</html>
