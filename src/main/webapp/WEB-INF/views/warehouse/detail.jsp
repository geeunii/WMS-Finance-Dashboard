<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../admin/admin-header.jsp" %>

<!DOCTYPE html>
<html>
<head>
  <title>창고 상세 정보: ${detail.name}</title>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=8284a9e56dbc80e2ab8f41c23c1bbb0a"></script>
  <style>
    /* 기존 스타일 유지 */
    #map { width: 400px; height: 350px; margin-top: 10px; border: 1px solid #ccc; }

    /* 레이아웃 변경을 위한 스타일 */
    .detail-container { display: flex; flex-wrap: wrap; gap: 20px; }
    .info-box, .map-box, .section-list-box {
      border: 1px solid #e0e0e0;
      padding: 20px;
      border-radius: 8px;
      background-color: #fff;
    }
    .info-box { flex-basis: 55%; } /* 창고 기본 정보 (좌상) */
    .map-box { flex-basis: 40%; } /* 창고 위치 (우상) */
    .section-list-box { flex-basis: 100%; margin-top: 20px; } /* 구역 정보 (하단 전체 너비) */

    /* 기존 테이블 스타일 */
    table.info-table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }
    table.info-table th, table.info-table td { border: 1px solid #ddd; padding: 10px; text-align: left; }
    table.info-table th { background-color: #f7f7f7; width: 30%; font-weight: 600; }

    .section-header {
      background-color: #e9e9ff; /* 섹션 헤더 색상 변경 */
      color: #333;
      padding: 10px;
      margin-top: 15px;
      font-size: 1.1em;
      font-weight: bold;
      border-radius: 4px;
    }
    /* 구역별 위치 테이블 스타일 */
    .location-table th, .location-table td { font-size: 0.9em; padding: 6px; }
    .location-container { display: flex; flex-wrap: wrap; gap: 20px; margin-top: 10px;}
    .single-section { flex: 1 1 48%; min-width: 300px; border: 1px dashed #ccc; padding: 10px; border-radius: 4px; }

    /* 🌟 버튼 크기 확대 (수정된 부분) 🌟 */
    .action-buttons {
      margin-top: 20px;
      display: flex;
      gap: 10px;
    }
    .action-buttons button {
      padding: 12px 25px; /* 패딩을 늘려서 버튼 크기 확대 */
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-weight: bold;
      font-size: 16px; /* 폰트 크기도 살짝 키워서 가독성 높임 */
    }
  </style>
</head>
<body>
<h1>창고 상세 페이지</h1>

<div class="detail-container">

  <div class="info-box">
    <h2>창고 기본 정보</h2>
    <table class="info-table">
      <tr><th>창고 ID</th><td>${detail.warehouseId}</td></tr>
      <tr><th>창고 이름</th><td>${detail.name}</td></tr>
      <tr><th>담당자 ID</th><td>${detail.adminId}</td></tr>
      <tr><th>창고 주소</th><td>${detail.address}</td></tr>
      <tr><th>창고 종류</th><td>${detail.warehouseType}</td></tr>
      <tr><th>수용 용량</th><td>${detail.warehouseCapacity}</td></tr>
      <tr><th>운영 현황</th><td>${detail.warehouseStatus == 1 ? '운영 중' : '점검 중'}</td></tr>
      <tr><th>등록 일자</th><td>${detail.registrationDate}</td></tr>
      <tr><th>최종 수정</th><td>${detail.latestUpdateDate}</td></tr>
    </table>
  </div>

  <div class="map-box">
    <h2>창고 위치</h2>
    <div id="map"></div>
  </div>

  <div class="section-list-box">
    <h2>창고 정보</h2>
    <c:choose>
      <c:when test="${not empty detail.sections}">
        <div class="location-container">
          <c:forEach items="${detail.sections}" var="section">
            <div class="single-section">
              <div class="section-header">
                <span>${section.sectionName}</span> (${section.sectionType})
              </div>

              <table class="info-table">
                <tr><th>보관 목적</th><td>${section.sectionPurpose}</td></tr>
                <tr><th>보관 면적</th><td>${section.allocatedArea}</td></tr>
              </table>

              <h4>창고구역 정보</h4>
              <table class="info-table location-table">
                <thead>
                <tr>
                  <th>창고 코드</th>
                  <th>층수</th>
                  <th>창고 유형</th>
                  <th>최대 부피</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${section.locations}" var="location">
                  <tr>
                    <td>${location.locationCode}</td>
                    <td>${location.floorNum}</td>
                    <td>${location.locationTypeCode}</td>
                    <td>${location.maxVolume}</td>
                  </tr>
                </c:forEach>
                <c:if test="${empty section.locations}">
                  <tr><td colspan="4">등록된 위치가 없습니다.</td></tr>
                </c:if>
                </tbody>
              </table>
            </div>
          </c:forEach>
        </div>
      </c:when>
      <c:otherwise>
        <div class="section-header">등록된 구역 정보가 없습니다.</div>
        <p style="margin-top: 10px;">창고에 구역 정보가 없습니다. 창고 구역을 등록해주세요.</p>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<hr>

<c:if test="${userRole == 'ADMIN' || userRole == 'MANAGER'}">
  <div class="action-buttons">
    <button onclick="showDeleteModal()" style="background-color: darkred; color: white;">삭제</button>
    <button onclick="location.href='${pageContext.request.contextPath}/admin/warehouses'" style="background-color: navy; color: white;">목록으로</button>
  </div>
</c:if>

<%-- 삭제 모달 기능 --%>
<div id="deleteModal" class="modal">
  <div class="modal-content">
    <h2>정말 삭제하시겠습니까?</h2>
    <p>창고 ${detail.name} (ID: ${detail.warehouseId}) 정보를 복구할 수 없습니다.</p>
    <form action="${pageContext.request.contextPath}/${userRole == 'ADMIN' ? 'admin' : 'manager'}/warehouses/${detail.warehouseId}/delete" method="POST" style="display: inline;">
      <button type="submit" style="background-color: navy; color: white;">예</button>
    </form>
    <button onclick="hideDeleteModal()" style="background-color: darkred; color: white;">아니요</button>
  </div>
</div>

<script src="${pageContext.request.contextPath}/static/warehouse/warehouse.js"></script>
<script>
  // 마커 표시를 위한 데이터
  const detailData = {
    latitude: ${detail.latitude},
    longitude: ${detail.longitude},
    name: '${detail.name}'
  };

  // 지도 초기화
  kakao.maps.load(function() {
    if (typeof initMapForDetail === 'function') {
      initMapForDetail('map', detailData);
    } else {
      console.error("initMapForDetail 함수를 찾을 수 없습니다. warehouse.js를 확인해주세요.");
    }
  });

  // 모달 제어 함수 (유지)
  function showDeleteModal() {
    document.getElementById('deleteModal').style.display = 'block';
  }
  function hideDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
  }
</script>
</body>
</html>
<%@ include file="../admin/admin-footer.jsp" %>