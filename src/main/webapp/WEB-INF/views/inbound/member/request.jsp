<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../includes/header.jsp" %>

<div class="container mt-5">
    <h3>입고 요청</h3>

    <form id="inboundRequestForm" method="post" action="/inbound/member/request">
        <input type="hidden" name="memberId" value="${memberId}"/>

        <h5>입고 품목</h5>
        <table class="table table-bordered" id="inboundItemsTable">
            <thead>
            <tr>
                <th>카테고리</th>
                <th>상품명</th>
                <th>수량</th>
                <th>삭제</th>
            </tr>
            </thead>
            <tbody>
            <tr class="inboundItemRow">
                <td>
                    <select class="form-select categorySelect" required>
                        <option value="" disabled selected>카테고리</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.categoryCd}">${category.categoryName}</option>
                        </c:forEach>
                    </select>
                </td>
                <td>
                    <select name="inboundRequestItems[0].productId" class="form-select productSelect" required>
                        <option value="" disabled selected>상품 선택</option>
                    </select>
                </td>
                <td>
                    <input type="number" name="inboundRequestItems[0].quantity" class="form-control" min="1" value="1" required>
                </td>
                <td>
                    <button type="button" class="btn btn-danger btn-sm removeItemBtn">삭제</button>
                </td>
            </tr>
            </tbody>
        </table>

        <button type="button" id="addItemBtn" class="btn btn-secondary btn-sm mb-3">품목 추가</button>
        <br/>
        <button type="submit" class="btn btn-primary">입고 요청</button>
    </form>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    let itemIndex = 1;

    // 카테고리 옵션 HTML
    const categoryOptions = (() => {
        const opts = [];
        opts.push($('<option>').val('').prop('disabled', true).prop('selected', true).text('카테고리'));
        <c:forEach var="category" items="${categories}">
        opts.push($('<option>').val('${category.categoryCd}').text('${category.categoryName}'));
        </c:forEach>
        return opts;
    })();

    // 인덱스 재정렬
    function updateIndices() {
        $('#inboundItemsTable tbody tr').each(function(index) {
            $(this).find('select.productSelect').attr('name', `inboundRequestItems[${index}].productId`);
            $(this).find('input[type=number]').attr('name', `inboundRequestItems[${index}].quantity`);
        });
    }

    // row 추가
    $('#addItemBtn').click(function() {
        let newRow = $('<tr>').addClass('inboundItemRow');

        // 카테고리
        let categoryCell = $('<td>');
        let categorySelect = $('<select>').addClass('form-select categorySelect').attr('required', true);
        categoryOptions.forEach(opt => categorySelect.append(opt.clone()));
        categoryCell.append(categorySelect);
        newRow.append(categoryCell);

        // 상품
        let productCell = $('<td>');
        let productSelect = $('<select>')
            .addClass('form-select productSelect')
            .attr('name', `inboundRequestItems[${itemIndex}].productId`)
            .attr('required', true)
            .append($('<option>').val('').prop('disabled', true).prop('selected', true).text('상품 선택'));
        productCell.append(productSelect);
        newRow.append(productCell);

        // 수량
        let quantityCell = $('<td>');
        quantityCell.append(
            $('<input>').attr('type', 'number')
                .addClass('form-control')
                .attr('name', `inboundRequestItems[${itemIndex}].quantity`)
                .attr('min', 1)
                .val(1)
                .prop('required', true)
        );
        newRow.append(quantityCell);

        // 삭제 버튼
        let removeCell = $('<td>');
        removeCell.append($('<button>').attr('type', 'button').addClass('btn btn-danger btn-sm removeItemBtn').text('삭제'));
        newRow.append(removeCell);

        $('#inboundItemsTable tbody').append(newRow);
        itemIndex++;
    });

    // row 삭제
    $(document).on('click', '.removeItemBtn', function() {
        $(this).closest('tr').remove();
        updateIndices();
        itemIndex = $('#inboundItemsTable tbody tr').length; // 다음 인덱스 재설정
    });

    // 카테고리 선택 시 상품 불러오기
    $(document).on('change', '.categorySelect', function() {
        let categoryCd = $(this).val();
        let $productSelect = $(this).closest('tr').find('.productSelect');

        $.ajax({
            url: '/inbound/member/products/byCategory',
            type: 'GET',
            data: { categoryCd: categoryCd },
            success: function(products) {
                $productSelect.empty();
                $productSelect.append($('<option>').val('').prop('disabled', true).prop('selected', true).text('상품 선택'));
                products.forEach(p => {
                    $productSelect.append($('<option>').val(p.productId).text(p.productName));
                });
            },
            error: function() {
                alert('상품을 불러오지 못했습니다.');
            }
        });
    });

    // submit 직전에 인덱스 재정렬
    $('#inboundRequestForm').submit(function() {
        updateIndices();
    });
</script>

<%@ include file="../../includes/footer.jsp" %>
