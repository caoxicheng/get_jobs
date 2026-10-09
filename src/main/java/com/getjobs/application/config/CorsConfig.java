package com.getjobs.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS 跨域配置
 *
 * 原先这里是 addAllowedOriginPattern("*") + allowCredentials(true)，等于对本机打开的
 * 任意网页开放；而 GET /api/config 会明文返回 AI 的 API_KEY，任何你在浏览器里访问的
 * 网站都能跨域把它读走。现收紧为只允许本项目自己的管理页面来源。
 *
 * 管理页面由本应用在 6866 端口提供（StartupRunner 优先打开 6866），前端 lib/api.ts
 * 固定请求 9527，所以这两个端口的 localhost / 127.0.0.1 都要放行。
 * 若直接从 9527 打开页面，则与 API 同源，不经过 CORS 检查。
 */
@Configuration
public class CorsConfig {

    /** 只放行本机管理页面的来源 */
    public static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:6866",
            "http://127.0.0.1:6866",
            "http://localhost:9527",
            "http://127.0.0.1:9527"
    );

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 只允许本机管理页面来源（不再使用通配符）
        ALLOWED_ORIGINS.forEach(config::addAllowedOrigin);

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有请求方法
        config.addAllowedMethod("*");

        // 允许携带凭证（此时 Allow-Origin 必须回精确来源，不能是 *）
        config.setAllowCredentials(true);

        // 预检请求的有效期，单位为秒
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
