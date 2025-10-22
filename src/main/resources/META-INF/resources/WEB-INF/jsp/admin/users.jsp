<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Martinis - User Approvals</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
        <link href="${pageContext.request.contextPath}/css/martinis.css" rel="stylesheet">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon">
        <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    </head>
    <body>
        <jsp:include page="../includes/nav.jsp" />
        <main class="container">
            <h1>Pending User Approvals</h1>

            <c:if test="${not empty successMessage}">
                <article style="background-color: var(--pico-ins-color);">
                    ${successMessage}
                </article>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <article style="background-color: var(--pico-del-color);">
                    ${errorMessage}
                </article>
            </c:if>

            <c:choose>
                <c:when test="${empty pendingUsers}">
                    <article>
                        <p>No users pending approval.</p>
                    </article>
                </c:when>
                <c:otherwise>
                    <article>
                        <figure>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Username</th>
                                        <th>First Name</th>
                                        <th>Last Name</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="user" items="${pendingUsers}">
                                        <tr>
                                            <td>${user.username}</td>
                                            <td>${user.first_name}</td>
                                            <td>${user.last_name}</td>
                                            <td>
                                                <form method="POST" action="${pageContext.request.contextPath}/admin/users/approve" style="display: inline;">
                                                    <sec:csrfInput />
                                                    <input type="hidden" name="username" value="${user.username}" />
                                                    <button type="submit" class="contrast">Approve</button>
                                                </form>
                                                <form method="POST" action="${pageContext.request.contextPath}/admin/users/reject" style="display: inline;">
                                                    <sec:csrfInput />
                                                    <input type="hidden" name="username" value="${user.username}" />
                                                    <button type="submit" class="secondary" onclick="return confirm('Are you sure you want to reject and delete this user?');">Reject</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </figure>
                    </article>
                </c:otherwise>
            </c:choose>

            <a href="${pageContext.request.contextPath}/projects" role="button">Back to Projects</a>
        </main>
    </body>
</html>
