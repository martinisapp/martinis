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
            <c:if test="${param.registered == 'true'}">
                <article style="background-color: var(--pico-ins-color);">
                    Registration successful! Please log in with your new account.
                </article>
            </c:if>
            <c:if test="${param.login_error == 1}">
                <article style="background-color: var(--pico-del-color);">
                    Incorrect Username or Password.
                </article>
            </c:if>
            <article>
                <form method="POST" action="${pageContext.request.contextPath}/j_spring_security_check">
                    <sec:csrfInput />
                    <label for="j_username">
                        Username:
                        <input type="text" name="j_username" placeholder="Username" required />
                    </label>
                    <label for="j_password">
                        Password:
                        <input type="password" name="j_password" placeholder="Password" required />
                    </label>
                    <button type="submit">Sign In</button>
                </form>
                <div style="text-align: center; margin-top: 15px;">
                    <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one here</a></p>
                </div>
            </article>
        </main>
    </body>
</html>
