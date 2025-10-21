<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <sec:csrfMetaTags />
        <title>Martinis - Scene Profile</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <style>
            .drag-handle {
                cursor: move;
                padding: 5px 10px;
                color: #999;
                font-size: 18px;
                display: inline-block;
            }
            .drag-handle:hover {
                color: #333;
            }
            .sortable-ghost {
                opacity: 0.4;
                background: #f5f5f5;
            }
            .sortable-drag {
                opacity: 0.8;
            }
            #table-blocks tbody tr {
                transition: background-color 0.2s;
            }
            #table-blocks tbody tr:hover {
                background-color: #f9f9f9;
            }
            .block-edit {
                padding: 10px;
                background-color: #f8f9fa;
                border-radius: 4px;
            }
            .block-edit .form-group {
                margin-bottom: 10px;
            }
            .block-edit label {
                font-weight: bold;
                margin-bottom: 5px;
            }
            .block-edit textarea.form-control {
                resize: vertical;
            }
            .block-display {
                min-height: 30px;
            }
            .save-status {
                font-size: 13px;
                min-width: 100px;
                vertical-align: middle;
            }
            .save-status i {
                font-style: italic;
            }
        </style>
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <div class="container">
            <jsp:include page="../includes/logout.jsp" />
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
                    <tr data-block-id="${block.id}" data-person-id="${block.personId}" data-scene-id="${viewModel.id}">
                        <td>
                            <span class="drag-handle" title="Drag to reorder">&#8942;&#8942;</span>
                        </td>
                        <td>
                            <div class="block-display">
                                <c:choose>
                                    <c:when test="${not empty block.personName}">
                                        <p class="mb-0 text-center">
                                            <a href="${pageContext.request.contextPath}/character/show?id=${block.personId}" class="character-name text-uppercase">${block.personName}</a>
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
                            <div class="block-edit" style="display: none;">
                                <div class="form-group">
                                    <label>Character:</label>
                                    <select class="form-control edit-person-select">
                                        <option value="">-- No Character --</option>
                                        <c:forEach items="${viewModel.persons}" var="person">
                                            <option value="${person.id}" ${person.id == block.personId ? 'selected' : ''}>${person.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>Content:</label>
                                    <textarea class="form-control edit-content-textarea" rows="3">${block.content}</textarea>
                                </div>
                                <div class="form-group">
                                    <div class="save-status"></div>
                                    <div class="text-muted small" style="margin-top: 5px;">
                                        <i>Click outside or press Escape to finish editing</i>
                                    </div>
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="nowrap">
                                <button class="btn btn-default btn-xs edit-inline-btn" role="button">edit</button>
                                <a href="${pageContext.request.contextPath}/block/delete?id=${block.id}" class="btn btn-default btn-xs" role="button">delete</a>
                                <c:if test="${not loop.last}">
                                    <a href="${pageContext.request.contextPath}/block/moveDown?id=${block.id}" class="btn btn-default btn-xs move-down" role="button">↓</a>
                                </c:if>
                                <c:if test="${not loop.first}">
                                    <a href="${pageContext.request.contextPath}/block/moveUp?id=${block.id}" class="btn btn-default btn-xs move-up" role="button">↑</a>
                                </c:if>
                                <a href="${pageContext.request.contextPath}/block/createBelow?id=${block.id}" class="btn btn-primary btn-xs create-below" role="button">+ block</a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <p>
                <a href="${pageContext.request.contextPath}/block/create?sceneId=${viewModel.id}" class="btn btn-primary" role="button">Create New Block</a> 
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
        <script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"></script>
        <script>
            var contextPath = '${pageContext.request.contextPath}';
        </script>
        <script src="${pageContext.request.contextPath}/js/block-reorder.js"></script>
        <script src="${pageContext.request.contextPath}/js/block-inline-edit.js"></script>
    </body>
</html>
