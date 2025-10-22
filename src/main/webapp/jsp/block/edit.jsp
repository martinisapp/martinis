<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Edit Block</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <div class="container">
            <jsp:include page="../includes/logout.jsp" />
            <div class="page-header">
                <h1>Edit Block</h1>
            </div>
            <sf:form class="form-horizontal" action="${pageContext.request.contextPath}/block/edit" method="post" modelAttribute="commandModel">
                <sf:hidden path="id" />
                <sf:hidden path="sceneId" />
                <div class="form-group">
                    <label for="content" class="col-md-2 control-label">Content:</label>
                    <div class="col-md-10">
                        <sf:textarea class="form-control" spellcheck="true" rows="5" cols="30" path="content" />
                        <sf:errors path="content" />
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-10">
                        <sf:select class="form-control" path="personId">
                            <sf:option value="" label="No character" />
                            <sf:options items="${viewModel.persons}" itemValue="id" itemLabel="name" />
                        </sf:select>
                        <sf:errors path="personId" />
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <a href="${pageContext.request.contextPath}/scene/show?id=${viewModel.sceneId}" class="btn btn-default" role="button">Cancel</a>
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                </div>
            </sf:form>
        </div>
    </body>
</html>
