<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Martinis - Login</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <jsp:include page="includes/nav.jsp" />
        <div class="container">
            <h1>Martinis</h1>
            <c:if test="${param.login_error == 1}">
                <p>Incorrect Username or Password.</p>
            </c:if>
            <div class="row">
                <div class="col-md-6 col-md-offset-3">
                    <form class="form-horizontal" role="form" method="POST" action="j_spring_security_check">
                        <div class="form-group">
                            <label for="j_username" class="col-md-2 control-label">Username:</label>
                            <div class="col-md-10">
                                <input type="text" class="form-control" name="j_username" placeholder="Username" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="j_password" class="col-md-2 control-label">Password:</label>
                            <div class="col-md-10">
                                <input type="password" class="form-control" name="j_password" placeholder="Password" />
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-offset-2 col-md-10">
                                <button type="submit" class="btn btn-primary">Sign In</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>   
        </div>
        <script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    </body>
</html>
