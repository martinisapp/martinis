<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Martinis - Edit Actor</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <div class="container">
            <jsp:include page="../includes/logout.jsp" />
            <div class="page-header">
                <h1>Edit Actor</h1>
            </div>
            <sf:form class="form-horizontal" action="${pageContext.request.contextPath}/actor/edit" method="post" modelAttribute="commandModel">
                <sf:hidden path="id" />
                <div class="form-group">
                    <label for="first" class="col-md-2 control-label">First Name:</label>
                    <div class="col-md-10">
                        <sf:input type="text" class="form-control" path="first" />
                        <sf:errors path="first" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="last" class="col-md-2 control-label">Last Name:</label>
                    <div class="col-md-10">
                        <sf:input type="text" class="form-control" path="last" />
                        <sf:errors path="last" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="phone" class="col-md-2 control-label">Phone:</label>
                    <div class="col-md-10">
                        <sf:input type="text" class="form-control" path="phone" />
                        <sf:errors path="phone" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="email" class="col-md-2 control-label">Email:</label>
                    <div class="col-md-10">
                        <sf:input type="text" class="form-control" path="email" />
                        <sf:errors path="email" />
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <a href="${pageContext.request.contextPath}/actor/show?id=${viewModel.id}" class="btn btn-default" role="button">Cancel</a>
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                </div>
            </sf:form>
        </div>
        <script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    </body>
</html>
