<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="dispatchFormContent">

  <script>
    console.log("dispatchForm loaded. contextPath =", contextPath);
  </script>

  <form id="dispatchForm">
    <input type="hidden" name="approvedOrderID" value="${dispatch.approvedOrderID}">

    <table class="table table-bordered text-center align-middle">
      <thead class="table-light">
      <tr>
        <th>출고지시서번호</th>
        <th>기사이름</th>
        <th>차량번호</th>
        <th>차량종류</th>
        <th>출고박스</th>
        <th>창고</th> <!-- ⭐추가 -->
        <th>최대적재</th>
        <th>배차상태</th>
        <th>요청상태</th>
      </tr>
      </thead>

      <tbody>
      <tr>
        <td>${dispatch.approvedOrderID}</td>

        <!-- 기사 선택 -->
        <td>
          <select id="driverSelect" name="driverName" class="form-select" required>
            <option value="">-- 기사 선택 --</option>
          </select>
        </td>

        <td><input type="text" name="vehicleNumber" class="form-control" readonly></td>
        <td><input type="text" name="vehicleType" class="form-control" readonly></td>

        <td><input type="number" name="boxCount" class="form-control" required></td>

        <!-- ⭐ 창고 선택 -->
        <td>
          <select id="warehouseSelect" name="warehouseId" class="form-select" required>
            <option value="">-- 창고 선택 --</option>
          </select>
        </td>

        <td><input type="number" name="vehicleCapacity" class="form-control" required></td>

        <td>
          <select name="dispatchStatus" class="form-select" required>
            <option value="대기">대기</option>
            <option value="완료">완료</option>
          </select>
        </td>

        <td>
          <select name="approvalStatus" class="form-select" required>
            <option value="승인">승인</option>
            <option value="반려">반려</option>
          </select>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="text-end mt-3">
      <button type="button" class="btn btn-primary" id="submitDispatchBtn">등록</button>
      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
    </div>
  </form>

  <script>
    $(document).ready(function () {

      var isExistingDispatch = ${dispatch.carId != null ? 'true' : 'false'};

      // ------------------------
      // 1) 기사 목록 불러오기
      // ------------------------
      $.ajax({
        url: contextPath + "/admin/dispatches/drivers",
        type: "GET",
        success: function (response) {

          var select = $("#driverSelect");
          select.empty();
          select.append('<option value="">-- 기사 선택 --</option>');

          var seenCarIds = new Set();

          response.forEach(function (d) {
            if (!d.carId || seenCarIds.has(d.carId)) return;
            seenCarIds.add(d.carId);

            select.append(`
                    <option value="${d.driverName}"
                            data-car="${d.carId}"
                            data-type="${d.carType}">
                        ${d.driverName} (${d.carId})
                    </option>
                `);
          });

          if (isExistingDispatch) setExistingValues();
        }
      });

      // ------------------------
      // 2) 창고 목록 불러오기 ⭐
      // ------------------------
      $.ajax({
        url: contextPath + "/admin/dispatches/warehouses",
        type: "GET",
        success: function (response) {

          var select = $("#warehouseSelect");
          select.empty();
          select.append('<option value="">-- 창고 선택 --</option>');

          response.forEach(function (w) {
            select.append(`
                    <option value="${w.warehouseId}">
                        ${w.name} (${w.address})
                    </option>
                `);
          });

          if (isExistingDispatch && "${dispatch.warehouseId}") {
            $("#warehouseSelect").val("${dispatch.warehouseId}");
          }
        }
      });

      // ------------------------
      // 기존 배차 값 세팅
      // ------------------------
      function setExistingValues() {
        $("input[name='vehicleNumber']").val("${dispatch.carId}");
        $("input[name='vehicleType']").val("${dispatch.carType}");
        $("input[name='boxCount']").val("${dispatch.loadedBox}");
        $("input[name='vehicleCapacity']").val("${dispatch.maximumBOX}");
        $("select[name='dispatchStatus']").val("${dispatch.dispatchStatus}");
        $("select[name='approvalStatus']").val("${dispatch.approvedStatus}");
        $("#driverSelect").val("${dispatch.driverName}");
        $("#warehouseSelect").val("${dispatch.warehouseId}");
      }

      // ------------------------
      // 기사 변경 → 차량 자동 입력
      // ------------------------
      $("#driverSelect").on("change", function () {
        var option = $(this).find("option:selected");
        $("input[name='vehicleNumber']").val(option.data("car"));
        $("input[name='vehicleType']").val(option.data("type"));
      });

      // ------------------------
      // 4) 등록 버튼
      // ------------------------
      $("#submitDispatchBtn").on("click", function () {

        var data = {
          approvedOrderID: Number("${dispatch.approvedOrderID}"),
          carId: $("input[name='vehicleNumber']").val(),
          carType: $("input[name='vehicleType']").val(),
          driverName: $("#driverSelect").val(),
          loadedBox: Number($("input[name='boxCount']").val()),
          warehouseId: Number($("#warehouseSelect").val()),   // ⭐ 추가됨
          maximumBOX: Number($("input[name='vehicleCapacity']").val()),
          dispatchStatus: $("select[name='dispatchStatus']").val(),
          approvedStatus: $("select[name='approvalStatus']").val()
        };

        $.ajax({
          url: contextPath + "/admin/outbound/" + data.approvedOrderID + "/register",
          type: "POST",
          contentType: "application/json",
          data: JSON.stringify(data),
          success: function () {
            alert("🚚 배차 등록 완료!");
            $("#dispatchModal").modal("hide");
            location.reload();
          },
          error: function () {
            alert("등록 실패");
          }
        });
      });

    });
  </script>

</div>
