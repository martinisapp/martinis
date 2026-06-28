<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="block-column block-column-handle">
    <span class="drag-handle" title="Drag to reorder">&#8942;&#8942;</span>
</div>
<div class="block-column block-column-content">
    <div class="block-display"
         hx-get="${pageContext.request.contextPath}/block/editForm?id=${block.id}"
         hx-target="closest .block-column-content"
         hx-swap="innerHTML"
         hx-trigger="click">
        <c:choose>
            <c:when test="${not empty block.person}">
                <p style="margin-bottom: 0; text-align: center;">
                    <a href="${pageContext.request.contextPath}/character/show?id=${block.person.id}" class="character-name" style="text-transform: uppercase;" onclick="event.stopPropagation()">${block.person.name}</a>
                </p>
                <div style="text-align: center;" class="block-content">
                    ${block.content}
                </div>
            </c:when>
            <c:otherwise>
                <div class="block-content">${block.content}</div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<div class="block-column block-column-actions">
    <div class="nowrap">
        <button type="button"
                class="bookmark-toggle ${block.bookmarked ? 'bookmarked' : ''}"
                data-block-id="${block.id}"
                onclick="toggleBookmark(${block.id}, this)"
                title="${block.bookmarked ? 'Remove bookmark' : 'Add bookmark'}">
            <span class="bookmark-icon">${block.bookmarked ? '★' : '☆'}</span>
        </button>
        <form action="${pageContext.request.contextPath}/block/delete" method="post" style="display:inline;">
            <input type="hidden" name="id" value="${block.id}"/>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button type="submit" role="button" class="secondary outline">delete</button>
        </form>
        <a hx-get="${pageContext.request.contextPath}/block/createBelowForm?id=${block.id}"
           hx-target="closest .block-row"
           hx-swap="afterend"
           role="button"
           class="create-below">+ block</a>
    </div>
</div>
