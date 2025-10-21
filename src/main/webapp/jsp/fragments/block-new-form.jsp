<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="block-row">
    <div class="block-column block-column-handle">
        <span class="drag-handle" title="Drag to reorder">&#8942;&#8942;</span>
    </div>
    <div class="block-column block-column-content">
        <div class="block-edit">
            <c:choose>
                <c:when test="${insertAfterBlockId != null}">
                    <form hx-post="${pageContext.request.contextPath}/block/createBelowInline"
                          hx-target="closest .block-row"
                          hx-swap="outerHTML">
                        <input type="hidden" name="id" value="${insertAfterBlockId}">
                        <input type="hidden" name="sceneId" value="${sceneId}">
                        <div class="form-group">
                            <label>Character:</label>
                            <select class="form-control" name="personId"
                                    hx-post="${pageContext.request.contextPath}/block/createBelowInline"
                                    hx-include="closest form"
                                    hx-target="closest .block-row"
                                    hx-swap="outerHTML"
                                    hx-trigger="change">
                                <option value="">-- No Character --</option>
                                <c:forEach items="${persons}" var="person">
                                    <option value="${person.id}">${person.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Content:</label>
                            <textarea class="form-control" name="content" rows="8" autofocus
                                      hx-post="${pageContext.request.contextPath}/block/createBelowInline"
                                      hx-include="closest form"
                                      hx-target="closest .block-row"
                                      hx-swap="outerHTML"
                                      hx-trigger="keyup changed delay:1500ms, blur"></textarea>
                        </div>
                        <div class="form-group">
                            <div class="save-status">
                                <span class="text-muted htmx-indicator">Saving...</span>
                            </div>
                            <div class="text-muted small">
                                <i>Auto-saves as you type.</i>
                            </div>
                        </div>
                    </form>
                </c:when>
                <c:otherwise>
                    <form hx-post="${pageContext.request.contextPath}/block/createInline"
                          hx-target="closest .block-row"
                          hx-swap="outerHTML">
                        <input type="hidden" name="sceneId" value="${sceneId}">
                        <div class="form-group">
                            <label>Character:</label>
                            <select class="form-control" name="personId"
                                    hx-post="${pageContext.request.contextPath}/block/createInline"
                                    hx-include="closest form"
                                    hx-target="closest .block-row"
                                    hx-swap="outerHTML"
                                    hx-trigger="change">
                                <option value="">-- No Character --</option>
                                <c:forEach items="${persons}" var="person">
                                    <option value="${person.id}">${person.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Content:</label>
                            <textarea class="form-control" name="content" rows="8" autofocus
                                      hx-post="${pageContext.request.contextPath}/block/createInline"
                                      hx-include="closest form"
                                      hx-target="closest .block-row"
                                      hx-swap="outerHTML"
                                      hx-trigger="keyup changed delay:1500ms, blur"></textarea>
                        </div>
                        <div class="form-group">
                            <div class="save-status">
                                <span class="text-muted htmx-indicator">Saving...</span>
                            </div>
                            <div class="text-muted small">
                                <i>Auto-saves as you type.</i>
                            </div>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <div class="block-column block-column-actions"></div>
</div>
