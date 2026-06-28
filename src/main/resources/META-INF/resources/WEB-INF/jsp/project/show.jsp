<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <sec:csrfMetaTags />
        <title>Martinis - Project Profile</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body class="${viewModel.cleanView ? 'clean-view' : ''}">
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
                <div><a href="${pageContext.request.contextPath}/project/edit?id=${viewModel.id}" role="button" class="secondary outline">edit</a> <form action="${pageContext.request.contextPath}/project/delete" method="post" style="display:inline;"><input type="hidden" name="id" value="${viewModel.id}"/><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><button type="submit" role="button" class="secondary outline" style="width:auto;">delete</button></form></div>
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
                                                <form action="${pageContext.request.contextPath}/scene/moveDown" method="post" style="display:inline;"><input type="hidden" name="id" value="${scene.id}"/><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><button type="submit" role="button" class="secondary outline move-down">↓</button></form>
                                            </c:if>
                                            <c:if test="${not loop.first}">
                                                <form action="${pageContext.request.contextPath}/scene/moveUp" method="post" style="display:inline;"><input type="hidden" name="id" value="${scene.id}"/><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><button type="submit" role="button" class="secondary outline move-up">↑</button></form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </figure>
                    <p><a href="${pageContext.request.contextPath}/scene/create?projectId=${viewModel.id}" role="button" class="create-new-scene">Create New Scene</a></p>
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
                    <a href="${pageContext.request.contextPath}/character/create?projectId=${viewModel.id}" role="button" class="create-new-character">Create New Character</a>
                </aside>
            </div>
        </main>

        <!-- Clean View Toggle Button -->
        <button class="clean-view-toggle" onclick="toggleCleanView()">
            ${viewModel.cleanView ? 'Exit Clean View' : 'Clean View'}
        </button>

        <script>
            function toggleCleanView() {
                var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
                fetch('${pageContext.request.contextPath}/project/toggleCleanView', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        [csrfHeader]: csrfToken
                    }
                })
                .then(response => response.text())
                .then(data => {
                    // Reload the page to apply the new view mode
                    location.reload();
                })
                .catch(error => {
                    console.error('Error toggling clean view:', error);
                });
            }
        </script>
    </body>
</html>
