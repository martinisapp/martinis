<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <sec:csrfMetaTags />
        <title>Martinis - Scene Profile</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
        <script>
            // Configure HTMX to send CSRF tokens with all requests
            document.addEventListener('DOMContentLoaded', function() {
                document.body.addEventListener('htmx:configRequest', function(evt) {
                    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
                    evt.detail.headers[csrfHeader] = csrfToken;
                });
            });
        </script>
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <main class="container">
            <jsp:include page="../includes/logout.jsp" />

            <!-- Undo notification -->
            <c:if test="${blockDeleted}">
                <article style="background-color: var(--pico-ins-color);">
                    <strong>Block deleted.</strong>
                    <c:choose>
                        <c:when test="${undoCount > 1}">
                            <a href="${pageContext.request.contextPath}/block/undo" role="button" class="contrast">Undo (${undoCount} available)</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/block/undo" role="button" class="contrast">Undo</a>
                        </c:otherwise>
                    </c:choose>
                </article>
            </c:if>
            <nav aria-label="breadcrumb">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/project/list">Projects</a></li>
                    <li><a href="${pageContext.request.contextPath}/project/show?id=${viewModel.projectId}">${viewModel.projectTitle}</a></li>
                    <li style="text-transform: uppercase;">${viewModel.name}</li>
                </ul>
            </nav>
            <hgroup>
                <h1 style="text-transform: uppercase;">${viewModel.name}</h1>
                <p><a href="${pageContext.request.contextPath}/scene/edit?id=${viewModel.id}" role="button" class="secondary outline">edit</a> <a href="${pageContext.request.contextPath}/scene/delete?id=${viewModel.id}" role="button" class="secondary outline">delete</a></p>
            </hgroup>
            <div id="blocks-container">
                <div id="blocks-list">
                <c:forEach items="${viewModel.blocks}" var="block" varStatus="loop">
                    <div class="block-row" data-block-id="${block.id}">
                        <div class="block-column block-column-handle">
                            <span class="drag-handle" title="Drag to reorder">&#8942;&#8942;</span>
                        </div>
                        <div class="block-column block-column-content">
                            <div class="block-display"
                                 hx-get="${pageContext.request.contextPath}/block/editForm?id=${block.id}"
                                 hx-target="closest .block-column-content"
                                 hx-swap="innerHTML"
                                 hx-trigger="click">
                                <c:choose>
                                    <c:when test="${not empty block.personName}">
                                        <p style="margin-bottom: 0; text-align: center;">
                                            <a href="${pageContext.request.contextPath}/character/show?id=${block.personId}" class="character-name" style="text-transform: uppercase;" onclick="event.stopPropagation()">${block.personName}</a>
                                        </p>
                                        <div style="text-align: center;" class="block-content">
                                            ${block.content}
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="block-content">${block.content}</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="block-column block-column-actions">
                            <div class="nowrap">
                                <a href="${pageContext.request.contextPath}/block/delete?id=${block.id}" role="button" class="secondary outline">delete</a>
                                <c:if test="${not loop.last}">
                                    <a href="${pageContext.request.contextPath}/block/moveDown?id=${block.id}" role="button" class="secondary outline move-down">↓</a>
                                </c:if>
                                <c:if test="${not loop.first}">
                                    <a href="${pageContext.request.contextPath}/block/moveUp?id=${block.id}" role="button" class="secondary outline move-up">↑</a>
                                </c:if>
                                <a hx-get="${pageContext.request.contextPath}/block/createBelowForm?id=${block.id}"
                                   hx-target="closest .block-row"
                                   hx-swap="afterend"
                                   role="button" class="create-below">+ block</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                </div>
            </div>
            <p>
                <a href="${pageContext.request.contextPath}/scene/createBelow?id=${viewModel.id}" role="button" class="secondary">Create New Scene</a>
                <a href="${pageContext.request.contextPath}/character/create?projectId=${viewModel.projectId}" role="button" class="secondary">Create New Character</a>
            </p>
            <div class="grid">
                <c:if test="${not empty viewModel.previousSceneName}">
                    <a href="${pageContext.request.contextPath}/scene/show?id=${viewModel.previousSceneId}" title="${viewModel.previousSceneName}" role="button" class="secondary"><span aria-hidden="true">&larr;</span> Previous Scene</a>
                </c:if>
                <c:if test="${not empty viewModel.nextSceneName}">
                    <a href="${pageContext.request.contextPath}/scene/show?id=${viewModel.nextSceneId}" title="${viewModel.nextSceneName}" role="button" class="secondary">Next Scene <span aria-hidden="true">&rarr;</span></a>
                </c:if>
            </div>
        </main>
        <script src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"></script>
        <script>
            var contextPath = '${pageContext.request.contextPath}';
        </script>
        <script src="${pageContext.request.contextPath}/js/block-htmx.js"></script>
    </body>
</html>
