<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Martinis - Scene Profile</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <div class="container">
            <jsp:include page="../includes/logout.jsp" />
            <ol class="breadcrumb">
                <li><a href="${pageContext.request.contextPath}/project/list">Projects</a></li>
                <li><a href="#"><a href="${pageContext.request.contextPath}/project/show?id=${viewModel.projectId}">${viewModel.projectTitle}</a></a></li>
                <li class="active">${viewModel.name}</li>
            </ol>
            <div class="page-header">
                <h1>${viewModel.name} <small><a href="${pageContext.request.contextPath}/scene/edit?id=${viewModel.id}" class="btn btn-default btn-xs" role="button">edit</a> <a href="${pageContext.request.contextPath}/scene/delete?id=${viewModel.id}" class="btn btn-default btn-xs" role="button">delete</a></small></h1>
            </div>
            <table id="table-blocks" class="table table-hover">
                <c:forEach items="${viewModel.blocks}" var="block" varStatus="loop">
                    <tr>
                        <td>
                            <c:if test="${not empty block.personName}">
                                <a href="${pageContext.request.contextPath}/character/show?id=${block.personId}" class="character-name">${block.personName}</a>
                                <br />
                            </c:if>
                            ${block.content}
                        </td>
                        <td>
                            <div class="nowrap">
                                <a href="${pageContext.request.contextPath}/block/edit?id=${block.id}" class="btn btn-default btn-xs" role="button">edit</a>
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
            </table>
            <p>
                <a href="${pageContext.request.contextPath}/block/create?sceneId=${viewModel.id}" class="btn btn-primary" role="button">Create New Block</a> 
                <a href="${pageContext.request.contextPath}/scene/create?projectId=${viewModel.projectId}" class="btn btn-default" role="button">Create New Scene</a> 
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
    </body>
</html>
