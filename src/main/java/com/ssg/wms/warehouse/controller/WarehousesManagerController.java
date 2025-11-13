package com.ssg.wms.warehouse.controller;

import com.ssg.wms.warehouse.dto.*;
import com.ssg.wms.warehouse.service.WarehouseManagerService;
import com.ssg.wms.warehouse.service.WarehouseMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import javax.validation.Valid;
import java.util.List;
import javax.servlet.http.HttpSession; // HttpSession import 추가
import com.ssg.wms.common.Role; // Role import 추가

@Controller
// Tomcat Manager 충돌 방지를 위해 경로를 /mgr/warehouses로 변경
@RequestMapping("/mgr/warehouses")
public class WarehousesManagerController {

    private final WarehouseManagerService warehouseManagerService;
    private final WarehouseMemberService memberService;
    private static final Long MOCK_ADMIN_ID = 1L; // 이 값은 세션에서 가져온 ID로 대체되어야 합니다.

    @Autowired
    public WarehousesManagerController(
            WarehouseManagerService warehouseManagerService,
            @Qualifier("warehousesMemberServiceImpl")
            WarehouseMemberService memberService) {
        this.warehouseManagerService = warehouseManagerService;
        this.memberService = memberService;
    }

    //화면
    @GetMapping({"", "/location"})
    public String managerListIndex(@ModelAttribute("searchForm") WarehouseSearchDTO searchForm, Model model) {
        List<WarehouseListDTO> list = memberService.findWarehouses(searchForm);
        model.addAttribute("warehouseList", list);
        model.addAttribute("userRole", "MANAGER");
        return "warehouse/list";
    }

    @GetMapping("/register")
    public String getWarehouseRegisterView(Model model) {
        model.addAttribute("saveDTO", new WarehouseSaveDTO());
        return "warehouse/register";
    }

    @PostMapping("/register")
    public String registerNewWarehouse(@Valid @ModelAttribute("saveDTO") WarehouseSaveDTO saveDTO,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session) { // HttpSession 추가
        if (bindingResult.hasErrors()) {
            return "warehouse/register";
        }

        // **[세션/권한 적용]**
        String loginId = (String) session.getAttribute("loginId");
        Object roleObj = session.getAttribute("role");

        if (loginId == null || roleObj == null || !roleObj.equals(Role.MANAGER)) {
            redirectAttributes.addFlashAttribute("error", "로그인 정보가 유효하지 않거나 권한이 없습니다.");
            return "redirect:/login";
        }

        try {
            // Service 계층으로 전달 전 Admin ID 설정
            // 실제 구현: Long adminId = warehouseManagerService.findManagerIdByLoginId(loginId);
            // saveDTO.setAdminId(adminId); // 세션에서 가져온 ID로 설정 (현재 DTO에 setAdminId가 필요)

            Long newWarehouseId = warehouseManagerService.saveWarehouse(saveDTO);

            redirectAttributes.addFlashAttribute("message", newWarehouseId + "번 창고 등록이 완료되었습니다.");

            return "redirect:/mgr/warehouses";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("name", "name.duplicate", e.getMessage());
            return "warehouse/register";
        } catch (Exception e) {
            bindingResult.reject("globalError", "등록 실패: " + e.getMessage());
            return "warehouse/register";
        }
    }

    @GetMapping("/{whid}")
    public String getManagerDetailView(@PathVariable("whid") Long warehouseId, Model model, RedirectAttributes redirectAttributes) {
        try {
            WarehouseDetailDTO detail = memberService.findWarehouseDetailById(warehouseId);
            model.addAttribute("detail", detail);
            model.addAttribute("userRole", "MANAGER");
            return "warehouse/detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "조회하려는 창고 정보를 찾을 수 없습니다.");
            //  리다이렉트 경로 수정
            return "redirect:/mgr/warehouses";
        }
    }

    @GetMapping("/{whid}/modify")
    public String getModifyForm(@PathVariable("whid") Long warehouseId, Model model, RedirectAttributes redirectAttributes) {
        try {
            WarehouseDetailDTO detailDTO = memberService.findWarehouseDetailById(warehouseId);
            model.addAttribute("detailDTO", detailDTO);
            if (!model.containsAttribute("updateDTO")) { model.addAttribute("updateDTO", new WarehouseUpdateDTO()); }
            model.addAttribute("userRole", "MANAGER");
            return "warehouse/modify";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "수정하려는 창고 정보를 찾을 수 없습니다.");
            //  리다이렉트 경로 수정
            return "redirect:/mgr/warehouses";
        }
    }

    @PostMapping("/{whid}")
    public String updateWarehouse(@PathVariable("whid") Long warehouseId,
                                  @Valid @ModelAttribute("updateDTO") WarehouseUpdateDTO updateDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) { // HttpSession 추가

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateDTO", bindingResult);
            redirectAttributes.addFlashAttribute("updateDTO", updateDTO);

            return "redirect:/mgr/warehouses/" + warehouseId + "/modify";
        }

        // **[세션/권한 적용]**
        String loginId = (String) session.getAttribute("loginId");
        Object roleObj = session.getAttribute("role");

        if (loginId == null || roleObj == null || !roleObj.equals(Role.MANAGER)) {
            redirectAttributes.addFlashAttribute("error", "로그인 정보가 유효하지 않거나 권한이 없습니다.");
            return "redirect:/login";
        }

        try {
            // 실제 구현: updateDTO.setAdminId(warehouseManagerService.findManagerIdByLoginId(loginId));

            warehouseManagerService.updateWarehouse(warehouseId, updateDTO);
            redirectAttributes.addFlashAttribute("message", warehouseId + "번 창고 수정이 완료되었습니다.");

            return "redirect:/mgr/warehouses/" + warehouseId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "수정 실패: " + e.getMessage());

            return "redirect:/mgr/warehouses/" + warehouseId;
        }
    }

    @PostMapping("/{whid}/delete")
    public String deleteWarehouse(@PathVariable("whid") Long warehouseId, RedirectAttributes redirectAttributes) {
        try {
            warehouseManagerService.deleteWarehouse(warehouseId);
            redirectAttributes.addFlashAttribute("message", "창고(" + warehouseId + ")가 삭제되었습니다.");

            return "redirect:/mgr/warehouses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 실패: " + e.getMessage());

            return "redirect:/mgr/warehouses/" + warehouseId;
        }
    }

    @GetMapping("/api/check/name")
    @ResponseBody
    public Boolean checkNameDuplication(@RequestParam String name) {
        return warehouseManagerService.checkNameDuplication(name);
    }
}