<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="block-display"
     hx-get="${pageContext.request.contextPath}/block/editForm?id=${block.id}"
     hx-target="closest td"
     hx-swap="innerHTML">
    <c:choose>
        <c:when test="${not empty block.person}">
            <p class="mb-0 text-center">
                <a href="${pageContext.request.contextPath}/character/show?id=${block.person.id}" class="character-name text-uppercase" onclick="event.stopPropagation()">${block.person.name}</a>
            </p>
            <div class="text-center block-content">
                ${block.content}
            </div>
        </c:when>
        <c:otherwise>
            <div class="block-content">${block.content}</div>
        </c:otherwise>
    </c:choose>
</div>
