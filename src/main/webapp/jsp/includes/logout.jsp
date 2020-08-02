<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${pageContext.request.userPrincipal.name != null}">
    <p class="text-right">${pageContext.request.userPrincipal.name} | <a href="<c:url value="/j_spring_security_logout" />">logout</a></p>
</c:if>