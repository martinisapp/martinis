<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Login</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="includes/nav.jsp" />
        <main class="container">
            <h1>Martinis</h1>
            <c:if test="${param.login_error == 1}">
                <p>Incorrect Username or Password.</p>
            </c:if>
            <div class="grid">
                <div >
                    <form  role="form" method="POST" action="${pageContext.request.contextPath}/j_spring_security_check">
                        <sec:csrfInput />
                        <div >
                            <label for="j_username" >Username:</label>
                            <div >
                                <input type="text"  name="j_username" placeholder="Username" />
                            </div>
                        </div>
                        <div >
                            <label for="j_password" >Password:</label>
                            <div >
                                <input type="password"  name="j_password" placeholder="Password" />
                            </div>
                        </div>
                        <div >
                            <div >
                                <button type="submit" role="button">Sign In</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </body>
</html>
