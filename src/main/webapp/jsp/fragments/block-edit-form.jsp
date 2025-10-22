<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="block-edit">
    <form hx-post="${pageContext.request.contextPath}/block/updateInline"
          hx-target="closest .block-column-content"
          hx-swap="innerHTML"
          hx-trigger="submit">
        <input type="hidden" name="id" value="${block.id}">
        <input type="hidden" name="sceneId" value="${block.scene.id}">
        <label>
            Character:
            <select class="edit-person-select" name="personId"
                    hx-post="${pageContext.request.contextPath}/block/updateInline"
                    hx-include="closest form"
                    hx-target="closest .block-column-content"
                    hx-swap="innerHTML"
                    hx-trigger="change">
                <option value="">-- No Character --</option>
                <c:forEach items="${persons}" var="person">
                    <option value="${person.id}" ${block.person != null && person.id == block.person.id ? 'selected' : ''}>${person.name}</option>
                </c:forEach>
            </select>
        </label>
        <label>
            Content:
            <textarea name="content" rows="8"
                      hx-post="${pageContext.request.contextPath}/block/updateInline"
                      hx-include="closest form"
                      hx-target="closest .block-column-content"
                      hx-swap="innerHTML"
                      hx-trigger="keyup changed delay:1500ms, blur">${block.content}</textarea>
        </label>
        <div>
            <div class="save-status">
                <small class="htmx-indicator">Saving...</small>
            </div>
            <small>
                <i>Click outside or press Escape to finish editing</i>
            </small>
        </div>
    </form>
</div>
