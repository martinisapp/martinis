<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<tr>
    <td>
        <span class="drag-handle" title="Drag to reorder">&#8942;&#8942;</span>
    </td>
    <td>
        <div class="block-edit">
            <form hx-post="${pageContext.request.contextPath}/block/${insertAfterBlockId != null ? 'createBelowInline' : 'createInline'}"
                  hx-target="closest tr"
                  hx-swap="outerHTML"
                  hx-trigger="submit">
                <c:if test="${insertAfterBlockId != null}">
                    <input type="hidden" name="id" value="${insertAfterBlockId}">
                </c:if>
                <c:if test="${sceneId != null}">
                    <input type="hidden" name="sceneId" value="${sceneId}">
                </c:if>
                <div class="form-group">
                    <label>Character:</label>
                    <select class="form-control" name="personId">
                        <option value="">-- No Character --</option>
                        <c:forEach items="${persons}" var="person">
                            <option value="${person.id}">${person.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label>Content:</label>
                    <textarea class="form-control" name="content" rows="8" autofocus></textarea>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-sm">Save</button>
                    <button type="button"
                            class="btn btn-default btn-sm"
                            hx-get="${pageContext.request.contextPath}/block/cancelNew"
                            hx-target="closest tr"
                            hx-swap="outerHTML swap:0s">Cancel</button>
                </div>
            </form>
        </div>
    </td>
    <td></td>
</tr>
