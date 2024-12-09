/**
 * WebConfiguration 설정
 */
package com.app.jungdreamweb.config;

import com.app.jungdreamweb.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
	
	/**
	 * 인터셉터 설정
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 로그인 세션, 로그인 아이피 인터셉터
		registry.addInterceptor(new LoginInterceptor())
			.order(1)											// 인터셉터 체인 순서
			.addPathPatterns(									// interceptor 작업이 필요한 path를 모두 추가한다
					 "/order-history"							// 기본 정보 관리
					,"/admin/*"
					,"/admins/*"
			);
	}
	
    /**
     * jsonView 설정
     * @return
     */
    @Bean(name="jsonView")
    MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
}
