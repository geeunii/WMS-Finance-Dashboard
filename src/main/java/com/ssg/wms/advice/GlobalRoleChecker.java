package com.ssg.wms.advice;

import com.ssg.wms.common.Role;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@ControllerAdvice
public class GlobalRoleChecker {
    @ModelAttribute
    public void checkAdminAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // /admin 경로에 접근하는데 ADMIN이 아닌 경우 차단
        if (uri.contains("/admin") || uri.contains("/dashboard")) {
            Role role = (Role) session.getAttribute("role");

            if (session == null || session.getAttribute("role") == null) {
                response.sendRedirect("/login");
            } else if (role != Role.ADMIN) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자만 접근 가능합니다.");
            }
        }
    }

}
