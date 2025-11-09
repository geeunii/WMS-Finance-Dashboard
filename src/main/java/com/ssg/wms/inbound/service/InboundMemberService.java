package com.ssg.wms.inbound.service;

import com.ssg.wms.inbound.dto.InboundDTO;
import com.ssg.wms.inbound.dto.InboundRequestDTO;
import org.springframework.transaction.annotation.Transactional;

public interface InboundMemberService {

    @Transactional
    InboundRequestDTO createInbound(InboundRequestDTO inboundRequestDTO);

    InboundDTO getInboundById(int inboundId);
}
