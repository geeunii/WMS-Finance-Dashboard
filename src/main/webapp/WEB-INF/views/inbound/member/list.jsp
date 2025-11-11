<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String contextPath = request.getContextPath(); %>
<%@ include file="../../member/member-header.jsp" %>

<div class="container mt-4">
    <h4 class="fw-bold mb-3">입고 요청 목록</h4>

    <!-- 입고 요청 리스트 테이블 -->
    <table class="table table-bordered" id="inboundTable">
        <thead>
        <tr>
            <th>입고 번호</th>
            <th>브랜드</th>
            <th>요청자</th>
            <th>요청일시</th>
            <th>
                상태
                <!-- 상태 필터 드롭다운 -->
                <select id="statusFilter" class="form-select w-auto d-inline mb-3">
                    <option value="">전체</option>
                    <option value="request" ${param.status == 'request' ? 'selected' : ''}>대기</option>
                    <option value="cancelled" ${param.status == 'cancelled' ? 'selected' : ''}>취소</option>
                    <option value="approved" ${param.status == 'approved' ? 'selected' : ''}>승인</option>
                    <option value="rejected" ${param.status == 'rejected' ? 'selected' : ''}>반려</option>
                </select>
            </th>
        </tr>
        </thead>
        <tbody id="inboundTableBody">
        <c:forEach var="item" items="${list}">
            <tr style="cursor:pointer" data-inbound-id="${item.inboundId}">
                <td>${item.inboundId}</td>
                <td>${item.partnerName}</td>
                <td>${item.memberName}</td>
                <td>${item.inboundRequestedDate}</td>
                <td>${item.inboundStatusKor}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr>
                <td colspan="4" class="text-center">조회된 데이터가 없습니다.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>

<!-- 모달 JSP 인클루드 -->
<%@ include file="inboundModal.jsp" %>

<!-- axios, bootstrap, jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    $(document).ready(function() {
        const inboundModalElement = document.getElementById('inboundModal');
        const inboundModal = new bootstrap.Modal(inboundModalElement);

        // 상태 필터 변경 시 리스트 새로고침
        $("#statusFilter").change(function() {
            const status = $(this).val();
            $.ajax({
                url: "/inbound/member/list",
                type: "get",
                data: { status: status },
                success: function(data) {
                    const newBody = $(data).find("#inboundTableBody").html();
                    $("#inboundTableBody").html(newBody);
                },
                error: function() {
                    alert("리스트 조회 중 오류가 발생했습니다.");
                }
            });
        });

        // 테이블 행 클릭 시 상세 모달 띄우기
        $(document).on('click', '#inboundTableBody tr', function() {
            const inboundId = $(this).data('inbound-id');
            console.log('선택한 inboundId:', inboundId);
            if (inboundId) openInboundModal(inboundId);
        });

        // 날짜 포맷 함수
        function formatDate(dateInput) {
            if(!dateInput) return '';
            if(typeof dateInput === 'string') return dateInput.substring(0,16).replace('T',' ');
            if(dateInput instanceof Date) return dateInput.toISOString().substring(0,16).replace('T',' ');
            return String(dateInput);
        }

        // 상품 행 추가
        function addInboundItemRow(item) {
            const template = document.getElementById('inboundItemTemplate');
            if (!template) {
                console.error('템플릿을 찾을 수 없습니다.');
                return;
            }

            const clone = template.cloneNode(true);
            clone.removeAttribute('id');
            clone.style.display = '';

            // INDEX 대체 (form submit 용)
            const uniqueIndex = Date.now() + Math.floor(Math.random() * 1000);
            clone.innerHTML = clone.innerHTML.replaceAll('INDEX', uniqueIndex);

            document.getElementById('inboundItemsBody').appendChild(clone);

            const productSelect = clone.querySelector('.productSelect');
            const categorySelect = clone.querySelector('.categorySelect');
            const quantityInput = clone.querySelector('.quantity');
            const removeBtn = clone.querySelector('.removeItemBtn');

            // DB 값 존재 시 매핑
            if(item){
                if(categorySelect && item.categoryCd != null){
                    categorySelect.value = String(item.categoryCd);
                }

                if(productSelect && item.productId){
                    productSelect.value = item.productId;

                    // 상품 선택 후 카테고리 재설정
                    const selectedOption = productSelect.selectedOptions[0];
                    if(selectedOption && categorySelect){
                        const cat = selectedOption.dataset.category;
                        if(cat) categorySelect.value = String(cat);
                    }
                }

                if(quantityInput){
                    quantityInput.value = item.quantity || 1;
                }
            }


            // 상품 변경 시 카테고리 자동 설정
            if(productSelect && categorySelect){
                productSelect.addEventListener('change', () => {
                    const selected = productSelect.selectedOptions[0];
                    if(selected){
                        categorySelect.value = selected.dataset.category || '';
                    }
                });
            }

            // 삭제 버튼
            if(removeBtn){
                removeBtn.addEventListener('click', () => clone.remove());
            }
        }

        // 상품 목록 렌더링
        function renderInboundItems(items){
            const tbody = document.getElementById('inboundItemsBody');
            tbody.innerHTML = '';

            if(items && items.length > 0){
                items.forEach(item => addInboundItemRow(item));
            }
        }

        // 모달 열기
        function openInboundModal(inboundId){
            axios.get('<%=contextPath%>/inbound/member/' + inboundId)
                    .then(response => {
                    const data = response.data;
                    if(!data){
                        alert('데이터를 불러올 수 없습니다.');
                        return;
                    }

                    // 기본 정보
                    document.getElementById('inboundId').value = data.inboundId || '';
                    document.getElementById('inboundStatus').value = data.inboundStatusKor || '';
                    document.getElementById('warehouseName').value = data.warehouseName || '미지정';
                    document.getElementById('partnerName').value = data.partnerName || '';
                    document.getElementById('memberName').value = data.memberName || '';
                    document.getElementById('staffName').value = data.staffName || '미지정';
                    document.getElementById('inboundRequestedAt').value = formatDate(data.inboundRequestedAt);
                    document.getElementById('inboundAt').value = formatDate(data.inboundAt) || '미지정';

                    // 반려 사유
                    const rejectSection = document.getElementById('rejectReasonSection');
                    if(data.inboundStatus === 'rejected' && data.inboundRejectReason){
                        document.getElementById('inboundRejectReason').value = data.inboundRejectReason;
                        rejectSection.style.display = 'block';
                    } else {
                        rejectSection.style.display = 'none';
                    }

                    // 상품 목록 렌더링
                    renderInboundItems(data.inboundItems);

                    inboundModal.show();
                })
                .catch(err => {
                    console.error('입고 상세 조회 오류:', err);
                    alert('입고 상세 조회 중 오류가 발생했습니다.');
                });
        }

        // 상품 추가 버튼
        document.getElementById('addInboundItemBtn').addEventListener('click', () => addInboundItemRow(null));

        // 전역 노출
        window.openInboundModal = openInboundModal;
    });
</script>



<%@ include file="../../member/member-footer.jsp" %>
