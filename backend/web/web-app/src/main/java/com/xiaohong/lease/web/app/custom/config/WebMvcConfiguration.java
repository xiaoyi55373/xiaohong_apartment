package com.xiaohong.lease.web.app.custom.config;


import com.xiaohong.lease.web.app.custom.interceptor.AuthenticationInterceptor;
import com.xiaohong.lease.web.app.custom.interceptor.AuthorizationInterceptor;
import com.xiaohong.lease.web.app.custom.interceptor.BehaviorLogPersistenceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${app.auth.path-patterns.include}")
    private String[] includePathPatterns;

    @Value("${app.auth.path-patterns.exclude}")
    private String[] excludePathPatterns;

    @Value("${app.cors.allowed-origins:*}")
    private String[] allowedOrigins;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    @Autowired
    private BehaviorLogPersistenceInterceptor behaviorLogPersistenceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.authenticationInterceptor).addPathPatterns(includePathPatterns).excludePathPatterns(excludePathPatterns);
        registry.addInterceptor(this.authorizationInterceptor).addPathPatterns(includePathPatterns).excludePathPatterns(excludePathPatterns);
        registry.addInterceptor(this.behaviorLogPersistenceInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}