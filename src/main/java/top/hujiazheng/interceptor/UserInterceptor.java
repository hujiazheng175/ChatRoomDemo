package top.hujiazheng.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.hujiazheng.Util.JwtUtil;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/29 10:37
 * @Description 拦截用户请求
 */
@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 在请求处理之前执行
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: No token provided");
            return false;
        }

        try {
            // 验证token
            String token = authHeader.substring(7);
            jwtUtil.getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid token");
            return false;
        }
    }

}
