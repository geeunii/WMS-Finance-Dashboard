<%-- /WEB-INF/views/outbound/member/outboundList.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- member-header.jsp 포함 --%>
<%@ include file="../../member/member-header.jsp" %>

<div class="content-wrapper">
    <div class="container-xxl flex-grow-1 container-p-y">
        <h4 class="fw-bold py-3 mb-4">
            <span class="text-muted fw-light">출고 요청 /</span> 목록 조회
        </h4>

        <div class="card mb-4">
            <div class="card-body">
                <form id="searchForm" class="row g-3">
                    <div class="col-md-3">
                        <label for="customerId" class="form-label">고객아이디</label>
                        <input type="text" class="form-control" id="customerId" placeholder="고객 ID 입력">
                    </div>
                    <div class="col-md-3">
                        <label for="statusFilter" class="form-label">배송상태</label>
                        <select id="statusFilter" name="status" class="form-select">
                            <option value="">-- 전체 --</option>
                            <option value="승인대기">승인대기</option>
                            <option value="승인">승인</option>
                            <option value="반려">반려</option>
                            <option value="출고완료">출고완료</option>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary">조회</button>
                    </div>
                </form>
            </div>
        </div>
        <div class="card">
            <h5 class="card-header">출고 요청 목록</h5>
            <div class="table-responsive text-nowrap">
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th><input type="checkbox" id="selectAll"></th> <th>번호</th>
                        <th>브랜드</th>
                        <th>입고 요청자</th>
                        <th>출고 요청일</th>
                        <th>배송 상태</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody class="table-border-bottom-0">
                    <%-- Controller에서 모델에 담아준 requestList를 JSTL로 반복 출력합니다. --%>
                    <c:forEach var="req" items="${requestList}" varStatus="loop">
                        <tr>
                            <td><input type="checkbox" name="requestIds" value="${req.outboundRequestId}"></td>
                            <td>${loop.count}</td>
                            <td>${req.brandName}</td>
                            <td>${req.requestUserName}</td>
                            <td><fmt:formatDate value="${req.outboundDate}" pattern="yyyy-MM-dd" /></td>
                            <td><span class="badge bg-label-secondary me-1">${req.approvedStatus}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/user/outbound/${req.outboundRequestId}" class="btn btn-sm btn-outline-primary">
                                    <i class="bx bx-detail"></i> 상세
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="card-footer d-flex justify-content-between align-items-center">
                <a href="${pageContext.request.contextPath}/user/outbound/form" class="btn btn-success">출고 요청 등록</a>

                <%-- 페이징 영역 --%>
                <div>
                    <nav aria-label="Page navigation">
                        <ul class="pagination">
                            <li class="page-item disabled"><a class="page-link" href="#"><i class="tf-icon bx bx-chevrons-left"></i></a></li>
                            <li class="page-item active"><a class="page-link" href="#">1</a></li>
                            <li class="page-item"><a class="page-link" href="#">2</a></li>
                            <li class="page-item"><a class="page-link" href="#">3</a></li>
                            <li class="page-item"><a class="page-link" href="#"><i class="tf-icon bx bx-chevrons-right"></i></a></li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // JavaScript (필요 시 AJAX 또는 폼 제출 로직 추가)
    window.location.href = '${pageContext.request.contextPath}/user/outbound';
</script>

<%@ include file="../../member/member-footer.jsp" %>