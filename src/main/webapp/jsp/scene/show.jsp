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
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
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
        <div class="container">
            <jsp:include page="../includes/logout.jsp" />

            <!-- Undo notification -->
            <c:if test="${blockDeleted}">
                <div id="undo-notification" class="alert alert-success" role="alert">
                    <strong>Block deleted.</strong>
                    <c:choose>
                        <c:when test="${undoCount > 1}">
                            <a href="${pageContext.request.contextPath}/block/undo" class="btn btn-sm btn-warning">Undo (${undoCount} available)</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/block/undo" class="btn btn-sm btn-warning">Undo</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
            <ol class="breadcrumb">
                <li><a href="${pageContext.request.contextPath}/project/list">Projects</a></li>
                <li><a href="#"><a href="${pageContext.request.contextPath}/project/show?id=${viewModel.projectId}">${viewModel.projectTitle}</a></a></li>
                <li class="active text-uppercase">${viewModel.name}</li>
            </ol>
            <div class="page-header">
                <h1 class="text-uppercase">${viewModel.name} <small><a href="${pageContext.request.contextPath}/scene/edit?id=${viewModel.id}" class="btn btn-default btn-xs" role="button">edit</a> <a href="${pageContext.request.contextPath}/scene/delete?id=${viewModel.id}" class="btn btn-default btn-xs" role="button">delete</a></small></h1>
            </div>
            <table id="table-blocks" class="table table-hover">
                <tbody>
                <c:forEach items="${viewModel.blocks}" var="block" varStatus="loop">
                    <tr data-block-id="${block.id}">
                        <td>
                            <span class="drag-handle" title="Drag to reorder">&#8942;&#8942;</span>
                        </td>
                        <td>
                            <div class="block-display"
                                 hx-get="${pageContext.request.contextPath}/block/editForm?id=${block.id}"
                                 hx-target="closest td"
                                 hx-swap="innerHTML">
                                <c:choose>
                                    <c:when test="${not empty block.personName}">
                                        <p class="mb-0 text-center">
                                            <a href="${pageContext.request.contextPath}/character/show?id=${block.personId}" class="character-name text-uppercase" onclick="event.stopPropagation()">${block.personName}</a>
                                        </p>
                                        <div class="text-center block-content">
                                            ${block.content}
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="block-content">${block.content}</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </td>
                        <td>
                            <div class="nowrap">
                                <a href="${pageContext.request.contextPath}/block/delete?id=${block.id}" class="btn btn-default btn-xs" role="button">delete</a>
                                <c:if test="${not loop.last}">
                                    <a href="${pageContext.request.contextPath}/block/moveDown?id=${block.id}" class="btn btn-default btn-xs move-down" role="button">↓</a>
                                </c:if>
                                <c:if test="${not loop.first}">
                                    <a href="${pageContext.request.contextPath}/block/moveUp?id=${block.id}" class="btn btn-default btn-xs move-up" role="button">↑</a>
                                </c:if>
                                <a hx-get="${pageContext.request.contextPath}/block/createBelowForm?id=${block.id}"
                                   hx-target="closest tr"
                                   hx-swap="afterend"
                                   class="btn btn-primary btn-xs create-below"
                                   role="button">+ block</a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <p>
                <a href="${pageContext.request.contextPath}/scene/createBelow?id=${viewModel.id}" class="btn btn-default" role="button">Create New Scene</a>
                <a href="${pageContext.request.contextPath}/character/create?projectId=${viewModel.projectId}" class="btn btn-default" role="button">Create New Character</a>
            </p>
            <nav aria-label="...">
                <ul class="pager">
                    <c:if test="${not empty viewModel.previousSceneName}">
                        <li class="previous"><a href="${pageContext.request.contextPath}/scene/show?id=${viewModel.previousSceneId}" title="${viewModel.previousSceneName}"><span aria-hidden="true">&larr;</span> Previous Scene</a></li>
                    </c:if>
                    <c:if test="${not empty viewModel.nextSceneName}">
                        <li class="next"><a href="${pageContext.request.contextPath}/scene/show?id=${viewModel.nextSceneId}" title="${viewModel.nextSceneName}">Next Scene <span aria-hidden="true">&rarr;</span></a></li>
                    </c:if>
                </ul>
            </nav>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"></script>
        <script>
            var contextPath = '${pageContext.request.contextPath}';
        </script>
        <script src="${pageContext.request.contextPath}/js/block-htmx.js"></script>
    </body>
</html>
