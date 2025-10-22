<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - Register</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="includes/nav.jsp" />
        <main class="container">
            <h1>Create Account</h1>
            <c:if test="${not empty error}">
                <article style="background-color: var(--pico-del-color);">
                    ${error}
                </article>
            </c:if>
            <article>
                <form method="POST" action="${pageContext.request.contextPath}/register">
                    <sec:csrfInput />
                    <label for="username">
                        Username:
                        <input type="text" name="username" id="username"
                               placeholder="Username" value="${username}" required maxlength="20" />
                        <small>Maximum 20 characters</small>
                    </label>
                    <label for="firstName">
                        First Name:
                        <input type="text" name="firstName" id="firstName"
                               placeholder="First Name" value="${firstName}" required maxlength="30" />
                    </label>
                    <label for="lastName">
                        Last Name:
                        <input type="text" name="lastName" id="lastName"
                               placeholder="Last Name" value="${lastName}" required maxlength="30" />
                    </label>
                    <label for="password">
                        Password:
                        <input type="password" name="password" id="password"
                               placeholder="Password" required />
                    </label>
                    <label for="confirmPassword">
                        Confirm Password:
                        <input type="password" name="confirmPassword" id="confirmPassword"
                               placeholder="Confirm Password" required />
                    </label>
                    <div class="grid">
                        <button type="submit">Create Account</button>
                        <a href="${pageContext.request.contextPath}/login" role="button" class="secondary">Back to Login</a>
                    </div>
                </form>
            </article>
        </main>
    </body>
</html>
