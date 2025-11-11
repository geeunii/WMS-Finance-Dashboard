<%-- /WEB-INF/views/outbound/member/shipmentRequestForm.jsp --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- member-header.jsp 포함 --%>

<%@ include file="../../member/member-header.jsp" %>

<div class="content-wrapper">

    <div class="container-xxl flex-grow-1 container-p-y">

        <h4 class="fw-bold py-3 mb-4">

            <span class="text-muted fw-light">출고 요청 /</span>

        </h4>

        <div class="card">

            <h5 class="card-header">출고 요청</h5>

            <div class="card-body">

                <form id="shipmentForm">

                    <h6 class="text-muted fw-light mb-3">📦 요청 품목 정보</h6>

                    <div class="table-responsive">

                        <table id="itemTable" class="table table-bordered">

                            <thead>

                            <tr class="table-dark">

                                <th style="width: 5%;">#</th>

                                <th>카테고리 <i class="bx bx-chevron-down"></i></th>

                                <th>상품 <i class="bx bx-chevron-down"></i></th>

                                <th style="width: 15%;">수량(box)</th>

                                <th style="width: 20%;">도착지 주소</th>

                                <th style="width: 15%;">출고 희망 날짜</th>

                                <th style="width: 10%;">관리</th>

                            </tr>

                            </thead>

                            <tbody>

                            <tr id="row-0">

                                <td>1</td>

                                <td>

                                    <select name="category" class="form-select form-select-sm category-select" onchange="updateProductOptions(0)" required>

                                        <option value="">선택하세요</option>

                                        <option value="아우터">아우터</option>

                                        <option value="하의">하의</option>

                                        <option value="상의">상의</option>

                                        <option value="신발">신발</option>

                                        <option value="액세서리">액세서리</option>

                                    </select>

                                </td>

                                <td>

                                    <select name="productId" class="form-select form-select-sm product-select" required>

                                        <option value="">카테고리를 먼저 선택하세요</option>

                                    </select>

                                </td>

                                <td><input type="number" name="quantity" class="form-control form-control-sm" value="1" min="1" required></td>

                                <td><input type="text" name="destinationAddress" class="form-control form-control-sm" placeholder="도착지 주소" required></td>

                                <td><input type="date" name="desiredShipmentDate" class="form-control form-control-sm" required></td>

                                <td><button type="button" class="btn btn-sm btn-outline-danger" onclick="removeItemRow(0)">제거</button></td>

                            </tr>

                            </tbody>

                        </table>

                    </div>

                    <div class="d-flex justify-content-end mb-4">

                        <button type="button" id="addItemBtn" class="btn btn-outline-secondary btn-sm me-2">

                            <i class="bx bx-plus me-1"></i> 품목 추가

                        </button>

                    </div>

                    <hr class="my-4">

                    <h6 class="text-muted fw-light mb-3">📍 도착지 주소 </h6>

                    <div class="row g-3">

                        <div class="col-md-12">

                            <label for="outboundAddress" class="form-label">도착짖 ㅜ소</label>

                            <input type="text" id="outboundAddress" name="outboundAddress" class="form-control" placeholder="수령할 주소를 입력하세요" required>

                        </div>

                    </div>

                    <div class="mt-4 pt-2 d-flex justify-content-end">

                        <button type="submit" class="btn btn-success me-3">등록</button>

                        <button type="button" class="btn btn-outline-secondary" onclick="window.history.back()">취소</button>

                    </div>

                </form>

            </div>

        </div>

    </div>

</div>

<script>

    let rowCount = 1;   

    // 카테고리별 상품 데이터 (실제로는 서버에서 가져와야 함)

    let productData = {}; // DB에서 불러올 데이터 저장용

    // 페이지 로드 시 전체 상품 데이터 불러오기
    window.addEventListener("DOMContentLoaded", () => {
        fetch("${pageContext.request.contextPath}/user/products")
            .then(res => res.json())
            .then(data => {
                // DB 데이터(category별로 묶기)
                productData = data.reduce((acc, product) => {
                    if (!acc[product.category]) {
                        acc[product.category] = [];
                    }
                    acc[product.category].push({
                        id: product.id,
                        name: product.name
                    });
                    return acc;
                }, {});
                console.log("✅ 상품 데이터 로드 완료:", productData);
            })
            .catch(err => {
                console.error("상품 데이터를 불러오는 중 오류 발생:", err);
                alert("상품 목록을 불러올 수 없습니다.");
            });
    });




    // 카테고리 선택 시 상품 옵션 업데이트
    function updateProductOptions(rowId) {
        const row = document.getElementById(`row-${rowId}`);
        const categorySelect = row.querySelector('.category-select');
        const productSelect = row.querySelector('.product-select');
        const selectedCategory = categorySelect.value;

        productSelect.innerHTML = '<option value="">선택하세요</option>';

        if (selectedCategory && productData[selectedCategory]) {
            productData[selectedCategory].forEach(product => {
                const option = document.createElement('option');
                option.value = product.id;
                option.textContent = product.name;
                productSelect.appendChild(option);
            });
        } else {
            productSelect.innerHTML = '<option value="">카테고리를 먼저 선택하세요</option>';
        }
    }




    // 품목 행을 동적으로 추가하는 함수

    document.getElementById('addItemBtn').addEventListener('click', function() {

        const tableBody = document.querySelector('#itemTable tbody');

        const newRow = document.createElement('tr');

        const newId = rowCount++;

        newRow.id = `row-${newId}`;

        newRow.innerHTML = `

            <td>${newId + 1}</td>

            <td>

                <select name="category" class="form-select form-select-sm category-select" onchange="updateProductOptions(${newId})" required>

                    <option value="">선택하세요</option>

                    <option value="아우터">아우터</option>

                    <option value="하의">하의</option>

                    <option value="상의">상의</option>

                    <option value="신발">신발</option>

                    <option value="액세서리">액세서리</option>

                </select>

            </td>

            <td>

                <select name="productId" class="form-select form-select-sm product-select" required>

                    <option value="">카테고리를 먼저 선택하세요</option>

                </select>

            </td>

            <td><input type="number" name="quantity" class="form-control form-control-sm" value="1" min="1" required></td>

            <td><input type="text" name="destinationAddress" class="form-control form-control-sm" placeholder="도착지 주소" required></td>

            <td><input type="date" name="desiredShipmentDate" class="form-control form-control-sm" required></td>

            <td><button type="button" class="btn btn-sm btn-outline-danger" onclick="removeItemRow(${newId})">제거</button></td>

        `;

        tableBody.appendChild(newRow);

    });

    // 품목 행을 제거하는 함수

    function removeItemRow(id) {

        const row = document.getElementById(`row-${id}`);

        if (document.querySelectorAll('#itemTable tbody tr').length > 1) {

            row.remove();

        } else {

            alert('최소 1개의 품목은 등록해야 합니다.');

        }

    }

    // 폼 제출 처리 (JSON 전송)

    document.getElementById('shipmentForm').addEventListener('submit', function (e) {

        e.preventDefault();

        const itemRows = document.querySelectorAll('#itemTable tbody tr');

        const shipmentItems = [];

        itemRows.forEach(row => {

            const category = row.querySelector('.category-select').value;

            const productId = row.querySelector('.product-select').value;

            const quantity = row.querySelector('input[name="quantity"]').value;

            const destinationAddress = row.querySelector('input[name="destinationAddress"]').value;

            const desiredShipmentDate = row.querySelector('input[name="desiredShipmentDate"]').value;

            if (category && productId && quantity && destinationAddress && desiredShipmentDate) {

                shipmentItems.push({

                    category: category,

                    productId: parseInt(productId), // INT 타입으로 변환

                    quantity: parseInt(quantity),    // INT 타입으로 변환

                    destinationAddress: destinationAddress,

                    desiredShipmentDate: desiredShipmentDate

                });

            }

        });

        if (shipmentItems.length === 0) {

            alert('요청할 품목을 하나 이상 입력해 주세요.');

            return;

        }

        // userId는 세션에서 처리하므로 JSON에 포함하지 않음

        const finalData = {

            outboundAddress: document.getElementById('outboundAddress').value,

            shipmentItems: shipmentItems // 배열 형태로 전송

        };

        // 🚨 Controller의 POST /user/outbound 엔드포인트로 JSON 전송

        fetch('${pageContext.request.contextPath}/user/outbound', {

            method: 'POST',

            headers: {

                'Content-Type': 'application/json'

            },

            body: JSON.stringify(finalData)

        })

            .then(response => {

                if(response.ok) {

                    alert('출고 요청이 성공적으로 생성되었습니다.');

                    // 성공 시 목록 조회 페이지로 이동 (Controller에서 JSON 반환 시 API 호출이므로)

                    window.location.href = '${pageContext.request.contextPath}/user/outbound';

                } else if (response.status === 401) {

                    alert('로그인이 필요합니다.');

                } else if (response.status === 403) {

                    alert('권한이 없습니다.');

                } else {

                    alert('출고 요청 생성에 실패했습니다. 관리자에게 문의하세요.');

                }

            })

            .catch(error => {

                console.error('Error:', error);

                alert('서버 통신 중 오류가 발생했습니다.');

            });

    });

</script>

<%-- member-footer.jsp 포함 --%>

<%@ include file="../../member/member-footer.jsp" %>

