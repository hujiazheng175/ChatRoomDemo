package top.hujiazheng.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.hujiazheng.interceptor.UserInterceptor;

/**
 * @Author HuXiaoMing
 * @Date 2024/11/29 10:38
 * @Description 配置拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private UserInterceptor userInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:src/main/resources/static/assets/")  // 使用绝对路径
                .setCachePeriod(3600)  // 缓存一小时
                .resourceChain(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/assets/**",  // 排除静态资源
                        "/", "/*.html", "/js/**", "/css/**"
                );
    }
}
