<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- 입고 상세 모달 -->
<div class="modal fade" id="inboundModal" tabindex="-1" aria-labelledby="inboundModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold" id="inboundModalLabel">
                    <i class="bi bi-box-seam me-2"></i>입고 상세 정보
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <!-- 입고 기본 정보 생략 (기존과 동일) -->
                <!-- 입고 기본 정보 -->
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white">
                        <strong>입고 기본 정보</strong>
                    </div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <label class="form-label">입고 번호</label>
                                <input type="text" id="inboundId" class="form-control" readonly>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">상태</label>
                                <input type="text" id="inboundStatus" class="form-control" readonly>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">창고명</label>
                                <input type="text" id="warehouseName" class="form-control" readonly>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">거래처명</label>
                                <input type="text" id="partnerName" class="form-control" readonly>
                            </div>
                        </div>

                        <div class="row g-3 mt-2">
                            <div class="col-md-3">
                                <label class="form-label">요청자</label>
                                <input type="text" id="memberName" class="form-control" readonly>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">담당자</label>
                                <input type="text" id="staffName" class="form-control" readonly>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">요청일시</label>
                                <input type="text" id="inboundRequestedAt" class="form-control" readonly>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">입고일시</label>
                                <input type="text" id="inboundAt" class="form-control" readonly>
                            </div>
                        </div>

                        <!-- 반려 사유 (상태가 rejected일 때만 노출) -->
                        <div class="mt-3" id="rejectReasonSection" style="display:none;">
                            <label class="form-label text-danger">반려 사유</label>
                            <textarea id="inboundRejectReason" class="form-control" rows="2" readonly></textarea>
                        </div>
                    </div>
                </div>


                <!-- 상품 리스트 -->
                <div class="card">
                    <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                        <strong>입고 상품 내역</strong>
                        <button type="button" class="btn btn-sm btn-light" id="addInboundItemBtn">
                            <i class="bi bi-plus-circle"></i> 상품 추가
                        </button>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover table-bordered mb-0">
                                <thead class="table-light">
                                <tr>
                                    <th width="20%">카테고리</th>
                                    <th width="40%">상품명</th>
                                    <th width="15%" class="text-end">수량</th>
                                    <th width="10%">삭제</th>
                                </tr>
                                </thead>
                                <tbody id="inboundItemsBody">
                                <!-- JS에서 clone할 template row는 여기서 숨겨놓음 -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- 숨겨진 row template -->
                <table style="display:none;">
                    <tbody>
                    <tr id="inboundItemTemplate">
                        <td>
                            <select name="inboundRequestItems[INDEX].categoryCd" class="form-select categorySelect" required>
                                <option value="" disabled selected>카테고리</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryCd}">${category.categoryName}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td>
                            <select name="inboundRequestItems[INDEX].productId" class="form-select productSelect" required>
                                <option value="" disabled selected>상품 선택</option>
                                <c:forEach var="product" items="${products}">
                                    <option value="${product.productId}" data-category="${product.categoryCd}" data-name="${product.productName}">
                                            ${product.productName}
                                    </option>
                                </c:forEach>
                            </select>
                        </td>
                        <td>
                            <input type="number" name="inboundRequestItems[INDEX].quantity" class="form-control quantity" min="1" required>
                        </td>
                        <td>
                            <button type="button" class="btn btn-sm btn-danger removeItemBtn">
                                <i class="bi bi-trash"></i>삭제
                            </button>
                        </td>
                    </tr>
                    </tbody>
                </table>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-circle me-1"></i>닫기
                </button>
            </div>
        </div>
    </div>
</div>
