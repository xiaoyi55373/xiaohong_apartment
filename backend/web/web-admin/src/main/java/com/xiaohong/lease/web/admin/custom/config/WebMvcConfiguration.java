package com.xiaohong.lease.web.admin.custom.config;

import com.xiaohong.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
import com.xiaohong.lease.web.admin.custom.interceptor.AuthenticationInterceptor;
import com.xiaohong.lease.web.admin.custom.interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;


    @Value("${admin.auth.path-patterns.include}")
    private String[] includePathPatterns;

    @Value("${admin.auth.path-patterns.exclude}")
    private String[] excludePathPatterns;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(stringToBaseEnumConverterFactory);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor).addPathPatterns(includePathPatterns).excludePathPatterns(excludePathPatterns);
        registry.addInterceptor(this.authorizationInterceptor).addPathPatterns(includePathPatterns).excludePathPatterns(excludePathPatterns);

    }
}
