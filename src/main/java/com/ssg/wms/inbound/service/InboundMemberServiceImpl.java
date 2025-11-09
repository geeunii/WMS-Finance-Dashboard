package com.ssg.wms.inbound.service;

import com.ssg.wms.inbound.domain.InboundItemVO;
import com.ssg.wms.inbound.domain.InboundVO;
import com.ssg.wms.inbound.dto.InboundDTO;
import com.ssg.wms.inbound.dto.InboundRequestDTO;
import com.ssg.wms.inbound.dto.InboundRequestItemDTO;
import com.ssg.wms.inbound.mappers.InboundMemberMapper;
import com.ssg.wms.product_ehs.dto.ProductDTO;
import com.ssg.wms.product_ehs.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Primary
public class InboundMemberServiceImpl implements InboundMemberService {

    @Autowired
    private InboundMemberMapper inboundMemberMapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    @Override
    public InboundRequestDTO createInbound(InboundRequestDTO inboundRequestDTO) {
        InboundVO inboundVO = InboundVO.builder()
                .inboundStatus("request")
                .build();
//        inboundVO.setInboundId(inboundRequestDTO.getInboundId());
//        inboundVO.setInboundStatus("request");

        inboundMemberMapper.insertInbound(inboundVO);
        int inboundId = inboundVO.getInboundId();

        if (inboundRequestDTO.getInboundRequestItems() != null &&
                !inboundRequestDTO.getInboundRequestItems().isEmpty()) {

            for (InboundRequestItemDTO inboundItemDTO : inboundRequestDTO.getInboundRequestItems()) {
                InboundItemVO inboundItemVO = InboundItemVO.builder()
                        .inboundId(inboundId)
                        .productId(inboundItemDTO.getProductId())
                        .quantity(inboundItemDTO.getQuantity())
                        .build();

                inboundMemberMapper.insertInboundItem(inboundItemVO);
            }
        }
        return inboundRequestDTO;
    }

    @Override
    public List<ProductDTO> getProductsByPartnerAndCategory(int partnerId, int categoryCd) {
        return productMapper.selectProductsByPartnerAndCategory(partnerId, categoryCd);
    }

    @Override
    public InboundDTO getInboundById(int inboundId) {
        return inboundMemberMapper.selectInboundWithItems(inboundId);
    }

}
