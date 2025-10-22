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
            <nav aria-label="breadcrumb">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/project/list">Projects</a></li>
                    <li>${viewModel.title}</li>
                </ul>
            </nav>
            <hgroup>
                <h1>${viewModel.title}</h1>
                <p><a href="${pageContext.request.contextPath}/project/edit?id=${viewModel.id}" role="button" class="secondary outline">edit</a> <a href="${pageContext.request.contextPath}/project/delete?id=${viewModel.id}" role="button" class="secondary outline">delete</a></p>
            </hgroup>
            <div class="grid">
                <div>
                    <h2>Scenes</h2>
                    <figure>
                        <table id="table-scenes">
                            <tbody>
                            <c:forEach items="${viewModel.scenes}" var="scene" varStatus="loop">
                                <tr>
                                    <td><a href="${pageContext.request.contextPath}/scene/show?id=${scene.id}" style="text-transform: uppercase;">${scene.name}</a></td>
                                    <td>
                                        <div class="nowrap">
                                            <c:if test="${not loop.last}">
                                                <a href="${pageContext.request.contextPath}/scene/moveDown?id=${scene.id}" role="button" class="secondary outline move-down">↓</a>
                                            </c:if>
                                            <c:if test="${not loop.first}">
                                                <a href="${pageContext.request.contextPath}/scene/moveUp?id=${scene.id}" role="button" class="secondary outline move-up">↑</a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </figure>
                    <p><a href="${pageContext.request.contextPath}/scene/create?projectId=${viewModel.id}" role="button">Create New Scene</a></p>
                </div>
                <aside>
                    <c:if test="${not empty viewModel.persons}">
                        <article>
                            <header><strong>Characters</strong></header>
                            <ul>
                            <c:forEach items="${viewModel.persons}" var="character">
                                <li><a href="${pageContext.request.contextPath}/character/show?id=${character.id}">${character.name}</a></li>
                            </c:forEach>
                            </ul>
                        </article>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/character/create?projectId=${viewModel.id}" role="button">Create New Character</a>
                </aside>
            </div>
        </main>
    </body>
</html>
