package com.ssg.wms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.wms.outbound.domain.dto.OutboundDTO;
import com.ssg.wms.outbound.mappers.OutboundMapper;
import com.ssg.wms.outbound.service.OutboundService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.time.LocalDate;

public class outboundRequestTest {

    @Test
    public void testInsertOutboundRequest() throws Exception {

        // ✅ MyBatis 설정 파일 로드 (경로 주의!)
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // ✅ 자동 커밋 true (insert 후 DB 반영)
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            OutboundMapper mapper = session.getMapper(OutboundMapper.class);

            // ✅ 테스트 데이터 준비
            OutboundDTO dto = new OutboundDTO();
            dto.setOutboundAddress("서울특별시 강남구");
            dto.setApprovedStatus("승인대기");
            dto.setOutboundDate(LocalDate.now());
            dto.setRequestedDeliveryDate(LocalDate.of(2025, 11, 20));
            dto.setMemberId(1L);       // 실제 존재하는 ID로 설정
            dto.setWarehouseId(1L);    // 실제 존재하는 ID로 설정
            dto.setStaffId(1L);        // 실제 존재하는 ID로 설정

            // ✅ insert 실행
            mapper.insertOutboundRequest(dto);

            // ✅ 결과 확인
            System.out.println("✅ 생성된 ID = " + dto.getOutboundRequestId());
        }
    }
}