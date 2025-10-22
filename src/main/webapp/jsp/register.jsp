<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Register</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="includes/nav.jsp" />
        <div class="container">
            <h1>Create Account</h1>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">
                    ${error}
                </div>
            </c:if>
            <div class="row">
                <div class="col-md-6 col-md-offset-3">
                    <form class="form-horizontal" role="form" method="POST" action="${pageContext.request.contextPath}/register">
                        <sec:csrfInput />
                        <div class="form-group">
                            <label for="username" class="col-md-3 control-label">Username:</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" name="username" id="username"
                                       placeholder="Username" value="${username}" required maxlength="20" />
                                <small class="help-block">Maximum 20 characters</small>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="firstName" class="col-md-3 control-label">First Name:</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" name="firstName" id="firstName"
                                       placeholder="First Name" value="${firstName}" required maxlength="30" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastName" class="col-md-3 control-label">Last Name:</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" name="lastName" id="lastName"
                                       placeholder="Last Name" value="${lastName}" required maxlength="30" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="password" class="col-md-3 control-label">Password:</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control" name="password" id="password"
                                       placeholder="Password" required />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="confirmPassword" class="col-md-3 control-label">Confirm Password:</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control" name="confirmPassword" id="confirmPassword"
                                       placeholder="Confirm Password" required />
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-offset-3 col-md-9">
                                <button type="submit" class="btn btn-primary">Create Account</button>
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-default">Back to Login</a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
