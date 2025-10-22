<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="block-column block-column-handle">
    <span class="drag-handle" title="Drag to reorder">&#8942;&#8942;</span>
</div>
<div class="block-column block-column-content">
    <div class="block-display"
         hx-get="${pageContext.request.contextPath}/block/editForm?id=${block.id}"
         hx-target="closest .block-column-content"
         hx-swap="innerHTML">
        <c:choose>
            <c:when test="${not empty block.personName}">
                <p style="margin-bottom: 0; text-align: center;">
                    <a href="${pageContext.request.contextPath}/character/show?id=${block.personId}" class="character-name" style="text-transform: uppercase;" onclick="event.stopPropagation()">${block.personName}</a>
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
                class="bookmark-toggle ${block.isBookmarked ? 'bookmarked' : ''}"
                data-block-id="${block.id}"
                onclick="toggleBookmark(${block.id}, this)"
                title="${block.isBookmarked ? 'Remove bookmark' : 'Add bookmark'}">
            <span class="bookmark-icon">${block.isBookmarked ? '★' : '☆'}</span>
        </button>
        <a href="${pageContext.request.contextPath}/block/delete?id=${block.id}" role="button" class="secondary outline">delete</a>
        <c:if test="${not isLast}">
            <a href="${pageContext.request.contextPath}/block/moveDown?id=${block.id}" role="button" class="secondary outline move-down">↓</a>
        </c:if>
        <c:if test="${not isFirst}">
            <a href="${pageContext.request.contextPath}/block/moveUp?id=${block.id}" role="button" class="secondary outline move-up">↑</a>
        </c:if>
        <a hx-get="${pageContext.request.contextPath}/block/createBelowForm?id=${block.id}"
           hx-target="closest .block-row"
           hx-swap="afterend"
           role="button"
           class="create-below">+ block</a>
    </div>
</div>
