package com.ssg.wms.warehouse.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@Component
public class KakaoApiUtil {

    // 1. API Key를 설정 파일(application.properties/yml)에서 주입받도록 수정
    private final String KAKAO_REST_API_KEY;
    private final String API_URL = "https://dapi.kakao.com/v2/local/search/address.json?query=";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 생성자 주입을 통해 @Value 값을 초기화합니다.
    public KakaoApiUtil(@Value("${kakao.api.rest-key}") String kakaoRestApiKey) {
        this.KAKAO_REST_API_KEY = kakaoRestApiKey;
    }

    /**
     * 주소를 카카오 API를 통해 위도(latitude)와 경도(longitude)로 변환합니다.
     * @param address 변환할 주소 문자열
     * @return [경도(longitude), 위도(latitude)] 배열
     * @throws Exception API 호출 실패 또는 좌표 추출 실패 시
     */
    public Double[] getCoordinates(String address) throws Exception {

        String encodedAddress;

        // 1. 주소 인코딩
        try {
            encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            log.error("주소 인코딩 실패: {}", address, e);
            throw new Exception("주소 인코딩 중 오류가 발생했습니다.", e);
        }

        // 2. HTTP 헤더 설정 (인증 키 포함)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;

        // 3. API 호출 및 통신 예외 처리 강화
        try {
            response = restTemplate.exchange(
                    API_URL + encodedAddress,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            // 4xx 에러 (Bad Request, Unauthorized 등) 처리
            log.error("카카오 API 호출 실패 (HTTP Status {}). 주소: {}", e.getStatusCode(), address, e);
            throw new Exception("카카오 API 호출 실패: HTTP 상태 코드 " + e.getStatusCode() + " 확인 필요.", e);
        } catch (ResourceAccessException e) {
            // 네트워크, DNS 문제, 타임아웃 등 처리
            log.error("카카오 API 통신 중 연결 오류 발생. 주소: {}", address, e);
            throw new Exception("카카오 API 통신 오류(네트워크/타임아웃).", e);
        } catch (Exception e) {
            // 기타 예상치 못한 통신 오류 처리
            log.error("카카오 API 호출 중 알 수 없는 오류 발생. 주소: {}", address, e);
            throw new Exception("카카오 API 통신 중 알 수 없는 오류.", e);
        }


        // 4. 응답 Status Code 확인
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("카카오 API 호출 실패. 응답 상태: {}, 응답 본문: {}", response.getStatusCode(), response.getBody());
            throw new Exception("카카오 API 호출 실패: 비정상적인 응답 상태.");
        }

        // 5. JSON 파싱 및 좌표 추출
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            if (documents.isArray() && documents.size() > 0) {
                JsonNode firstDocument = documents.get(0);
                // 카카오 API 응답: x=경도(Longitude), y=위도(Latitude)
                double longitude = firstDocument.path("x").asDouble();
                double latitude = firstDocument.path("y").asDouble();

                // [경도(longitude), 위도(latitude)] 배열로 반환
                return new Double[]{longitude, latitude};
            }
        } catch (Exception e) {
            log.error("카카오 API 응답 파싱 실패. 응답: {}", response.getBody(), e);
            throw new Exception("카카오 API 응답 파싱 중 오류가 발생했습니다.", e);
        }


        // 6. 주소 검색 결과가 0건일 경우 (가장 유력한 기존 오류 원인)
        throw new Exception("주소에 해당하는 유효한 좌표를 찾을 수 없습니다. (입력 주소: " + address + ")");
    }
}