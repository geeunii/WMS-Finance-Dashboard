# 📦 RACL - 의류 중심 창고관리 시스템 (WMS)

<div align="center">

![Project Status](https://img.shields.io/badge/status-completed-success?style=flat-square)
![Spring](https://img.shields.io/badge/Spring_MVC-5.x-6DB33F?style=flat-square&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=flat-square&logo=mysql)
![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk)

**팀명:** 빌더스 (Builders) | **팀 규모:** 6명  
**개발 기간:** 2025.11.07 ~ 2025.11.14 (8일)  
**담당 파트:** 📊 대시보드 시각화 & 💰 재무관리 시스템

</div>

---

## 📑 목차
- [프로젝트 소개](#-프로젝트-소개)
- [기술 스택](#-기술-스택)
- [나의 담당 기능](#-나의-담당-기능)
- [기술적 도전과 해결](#-기술적-도전과-해결)
- [프로젝트 구조](#-프로젝트-구조)
- [설치 및 실행](#-설치-및-실행)
- [회고](#-회고)

---

## 🎯 프로젝트 소개

**RACL**은 의류 산업에 특화된 창고관리 시스템(Warehouse Management System)입니다.  
실시간 재고 추적, 입출고 관리, 데이터 시각화를 통해 효율적인 물류 운영을 지원합니다.

### 💡 기획 배경
- 의류 업계의 복잡한 SKU 관리 및 시즌별 재고 변동 대응
- 실시간 매출/비용 분석을 통한 경영 의사결정 지원
- 직관적인 대시보드로 비전문가도 쉽게 이해할 수 있는 UI 제공

### 🎨 나의 역할
저는 **관리자 대시보드 시각화**와 **재무관리(매출/지출) 시스템**을 전담하여 개발했습니다.  
특히 복잡한 통계 데이터를 시각적으로 표현하고, RESTful API 구조로 확장 가능한 재무 CRUD를 구현하는 데 집중했습니다.

---

## 🛠 기술 스택

### Backend
- **Language:** Java 17
- **Framework:** Spring MVC 5.x
- **ORM:** MyBatis 3.x
- **Build Tool:** Gradle
- **WAS:** Apache Tomcat 9.0
- **Connection Pool:** HikariCP

### Frontend
- **Core:** HTML5, CSS3, JavaScript (ES6+)
- **Library:** jQuery 3.x, Bootstrap 5
- **Visualization:** ApexCharts.js 3.x
- **Template Engine:** JSP

### Database
- **DBMS:** MySQL 8.x
- **Design Tool:** ERD Cloud

### Collaboration
- **Version Control:** Git, GitHub
- **IDE:** IntelliJ IDEA

---

## 💼 나의 담당 기능

### 1. 📊 관리자 대시보드 (Dashboard)

창고 운영의 핵심 지표를 **ApexCharts**로 시각화하여 한눈에 파악할 수 있도록 구현했습니다.

#### 🔑 핵심 기능
| 기능 | 설명 | 기술 요소 |
|------|------|-----------|
| **연간/월간 손익 분석** | 매출·지출 데이터를 집계하여 순이익(Net Profit)과 이익률(Profit Margin) 자동 계산 | Java Stream API, MyBatis 집계 쿼리 |
| **성장률 계산** | 전월 대비(MoM), 전년 동월 대비(YoY) 성장률 산출 | Custom 비즈니스 로직, 예외 처리 |
| **실시간 물류 현황** | 당월 입고/출고 건수 실시간 카드 표시 | AJAX 비동기 통신 |
| **카테고리별 주문 통계** | 의류 카테고리(상의/하의/아우터 등)별 주문 비율 시각화 | Donut Chart |

#### 📈 시각화 차트
- **Bar Chart**: 연간 매출/지출 비교
- **Line Chart**: 월별 순이익 추이
- **Radial Bar**: MoM/YoY 성장률
- **Donut Chart**: 카테고리별 주문 분포

#### 🖥️ 구현 화면
```
[대시보드 스크린샷 자리]
- 상단: 주요 KPI 카드 (총 매출, 총 지출, 순이익, 이익률)
- 중단: 연간 매출/지출 비교 차트
- 하단: 카테고리별 주문 통계 & 최근 입출고 현황
```

#### 🔧 기술적 구현
```java
// DashboardServiceImpl.java - 월별 데이터 매핑
public List<MonthlyProfitDTO> getYearlyProfitData(int year) {
    // 1. 연간 매출/지출 데이터 일괄 조회 (단일 쿼리)
    List<SalesVO> salesList = dashboardMapper.selectYearlySales(year);
    List<ExpenseVO> expenseList = dashboardMapper.selectYearlyExpenses(year);
    
    // 2. Map으로 변환 (월 → 금액)
    Map<Integer, Long> salesMap = salesList.stream()
        .collect(Collectors.groupingBy(
            SalesVO::getMonth,
            Collectors.summingLong(SalesVO::getAmount)
        ));
    
    // 3. Stream API로 1~12월 데이터 생성 (누락 월은 0으로 채움)
    return IntStream.rangeClosed(1, 12)
        .mapToObj(month -> {
            long sales = salesMap.getOrDefault(month, 0L);
            long expenses = expenseMap.getOrDefault(month, 0L);
            long netProfit = sales - expenses;
            double margin = sales > 0 ? (double) netProfit / sales * 100 : 0;
            
            return MonthlyProfitDTO.builder()
                .month(month)
                .totalSales(sales)
                .totalExpenses(expenses)
                .netProfit(netProfit)
                .profitMargin(margin)
                .build();
        })
        .collect(Collectors.toList());
}
```

---

### 2. 💰 재무 관리 시스템 (Sales & Expense)

창고 운영에서 발생하는 매출과 비용을 체계적으로 관리하는 CRUD 시스템입니다.

#### 🔑 핵심 기능
- **RESTful API 설계**: `/api/sales`, `/api/expenses` 엔드포인트 분리
- **모달 기반 UX**: 페이지 이동 없이 등록/수정/삭제 (AJAX 통신)
- **데이터 무결성**:
  - 창고(Warehouse) 테이블 외래키 연동
  - 거래처(Partner) 리스트 자동 로딩
  - 금액 음수 입력 방지 (Validation)
- **필터링 기능**: 연도/월별 데이터 조회

#### 📋 API 엔드포인트
| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/sales?year={year}&month={month}` | 매출 목록 조회 |
| POST | `/api/sales` | 매출 등록 |
| PUT | `/api/sales/{id}` | 매출 수정 |
| DELETE | `/api/sales/{id}` | 매출 삭제 |

*(지출(Expense)도 동일한 구조)*

#### 🖥️ 구현 화면
```
[재무관리 페이지 스크린샷 자리]
- 좌측: 연도/월 필터 & 신규 등록 버튼
- 중앙: 매출/지출 테이블 (정렬, 검색 가능)
- 우측: 수정/삭제 버튼 (권한별 표시)
```

#### 🔧 기술적 구현
```javascript
// sales.js - AJAX로 매출 등록
function saveSales() {
    const formData = {
        warehouseId: $('#warehouseSelect').val(),
        partnerId: $('#partnerSelect').val(),
        amount: $('#amountInput').val(),
        salesDate: $('#dateInput').val(),
        description: $('#descInput').val()
    };
    
    $.ajax({
        url: '/api/sales',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(response) {
            alert('매출이 등록되었습니다.');
            $('#salesModal').modal('hide');
            loadSalesList(); // 목록 갱신
        },
        error: function(xhr) {
            alert('등록 실패: ' + xhr.responseJSON.message);
        }
    });
}
```

---

## 🔥 기술적 도전과 해결

### 1️⃣ 대시보드 데이터 집계 성능 최적화

#### ❌ 문제 상황
초기에는 1~12월 데이터를 각각 조회하여 총 24번의 쿼리(매출 12번 + 지출 12번)가 실행되었고,  
페이지 로딩에 2~3초가 소요되어 사용자 경험이 저하되었습니다.

#### ✅ 해결 방법
1. **쿼리 최적화**: MyBatis에서 `GROUP BY MONTH(date)` 집계 쿼리로 연간 데이터를 **단 2번**(매출 1번 + 지출 1번)에 조회
2. **Java Stream API 활용**: 조회된 데이터를 `Map<월, 금액>` 구조로 변환 후, `IntStream.rangeClosed(1, 12)`로 누락된 월을 0으로 채움
3. **캐싱 적용**: 자주 조회되는 데이터는 서버 메모리에 캐싱 (선택 사항으로 구현)

#### 📊 성능 개선 결과
- **쿼리 수**: 24회 → 2회 (**92% 감소**)
- **로딩 속도**: 2.8초 → 0.4초 (**85% 단축**)

```sql
<!-- DashboardMapper.xml - 최적화된 쿼리 -->
<select id="selectYearlySales" resultType="SalesVO">
    SELECT 
        MONTH(sales_date) as month,
        SUM(amount) as totalAmount
    FROM sales
    WHERE YEAR(sales_date) = #{year}
    GROUP BY MONTH(sales_date)
    ORDER BY month
</select>
```

---

### 2️⃣ MoM/YoY 성장률 계산 로직 안정화

#### ❌ 문제 상황
- 전월 또는 전년도 매출이 **0원**인 경우 `ArithmeticException` (division by zero) 발생
- 음수 성장률 표시 시 UI가 깨지는 현상

#### ✅ 해결 방법
1. **안전한 계산 메서드 구현**:
```java
private double calculateGrowthRate(long current, long previous) {
    if (previous == 0) {
        return current > 0 ? 100.0 : 0.0; // 전월 0원 → 100% 증가로 표시
    }
    return ((double) (current - previous) / previous) * 100;
}
```

2. **프론트엔드 처리**:
```javascript
// 음수는 빨간색, 양수는 초록색으로 표시
const growthColor = growthRate >= 0 ? '#00E396' : '#FF4560';
const growthText = growthRate >= 0 ? `+${growthRate}%` : `${growthRate}%`;
```

3. **예외 케이스 대응**:
- 전월/전년 데이터 없음 → "데이터 부족" 메시지 표시
- 극단적인 증가율(+1000% 이상) → 상한선 설정 (차트 가독성 확보)

---

### 3️⃣ RESTful API 설계 및 예외 처리

#### ✅ 구현 전략
- **공통 응답 DTO** 설계:
```java
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
```

- **@ControllerAdvice를 통한 전역 예외 처리**:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(Exception e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("데이터 무결성 위반: 존재하지 않는 창고 또는 거래처입니다."));
    }
}
```

---

## 📂 프로젝트 구조

```
src/main/java/com/ssg/wms/
├── finance/                    # 재무관리 도메인
│   ├── controller/
│   │   ├── DashboardController.java
│   │   ├── SalesController.java
│   │   └── ExpenseController.java
│   ├── service/
│   │   ├── DashboardService.java
│   │   ├── DashboardServiceImpl.java
│   │   ├── SalesService.java
│   │   └── ExpenseService.java
│   ├── domain/
│   │   ├── SalesVO.java
│   │   ├── ExpenseVO.java
│   │   └── dto/
│   │       ├── MonthlyProfitDTO.java
│   │       └── GrowthRateDTO.java
│   └── mapper/
│       ├── DashboardMapper.java
│       ├── SalesMapper.java
│       └── ExpenseMapper.java
├── inbound/                    # 입고관리 (타 팀원)
├── outbound/                   # 출고관리 (타 팀원)
└── common/                     # 공통 모듈
    ├── exception/
    │   └── GlobalExceptionHandler.java
    └── util/
        └── DateUtil.java

src/main/resources/
├── mappers/
│   ├── DashboardMapper.xml
│   ├── SalesMapper.xml
│   └── ExpenseMapper.xml
└── application.properties

src/main/webapp/
├── WEB-INF/
│   └── views/
│       └── finance/
│           ├── dashboard.jsp
│           ├── sales.jsp
│           └── expense.jsp
└── resources/
    ├── css/
    │   └── dashboard.css
    └── js/
        ├── dashboard.js
        └── sales.js
```

---

## 🚀 설치 및 실행

### 1. 사전 요구사항
- JDK 17 이상
- MySQL 8.x
- Gradle 7.x
- IntelliJ IDEA 또는 Eclipse

### 2. 데이터베이스 설정
```sql
-- 데이터베이스 생성
CREATE DATABASE wms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 테이블 생성 (ERD 참고)
SOURCE /path/to/schema.sql;
```

### 3. 프로젝트 클론 및 실행
```bash
# 저장소 클론
git clone https://github.com/SSG-2nd-2team/WMS.git
cd WMS
git checkout dev/KHG  # 내 작업 브랜치로 이동

# 의존성 설치 및 빌드
./gradlew clean build

# Tomcat 서버 실행
./gradlew bootRun

# 브라우저에서 접속
http://localhost:8080
```

---

## 🤔 회고

### 잘한 점 (Keep)
1. **성능 최적화**: Stream API를 활용한 데이터 가공으로 쿼리 횟수를 대폭 줄여 사용자 경험 개선
2. **코드 재사용성**: 공통 DTO와 예외 처리 로직을 분리하여 유지보수성 향상
3. **시각화 품질**: ApexCharts를 활용해 직관적이고 세련된 대시보드 구현

### 아쉬운 점 (Problem)
1. **테스트 코드 부족**: 단위 테스트 작성 시간 부족으로 수동 테스트에 의존
2. **반응형 디자인 미흡**: 모바일 환경에서 차트 가독성 저하
3. **실시간 업데이트**: WebSocket 기반 실시간 데이터 갱신 미구현

### 개선 방안 (Try)
1. **JUnit 5 도입**: Controller, Service 계층 단위 테스트 작성
2. **CSS Media Query**: 태블릿/모바일 반응형 레이아웃 추가
3. **Spring WebSocket**: 실시간 알림 기능 추가 (입출고 발생 시 대시보드 자동 갱신)

---

## 📬 Contact

**GitHub:** [@fsdawer](https://github.com/fsdawer)  
**Email:** [이메일 주소]  
**Portfolio:** [포트폴리오 링크]

---

<div align="center">

**⭐ 이 프로젝트가 도움이 되셨다면 Star를 눌러주세요! ⭐**

</div>
