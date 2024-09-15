/**
 * WebConfiguration 설정
 */
package com.app.jungdreamweb.config;

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
	
	private final MessageSource messageSource;
	
	/**
	 * 인터셉터 설정
	 * @param registry
	 */
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		// 로그인 세션, 로그인 아이피 인터셉터
//		registry.addInterceptor(new AuthenticationInterceptor(messageSource))
//			.order(1)											// 인터셉터 체인 순서
//			.addPathPatterns(									// interceptor 작업이 필요한 path를 모두 추가한다
//					 "/manage/**"								// 기본 정보 관리
//					)
//			.excludePathPatterns(								// 제외할 whitelist
////					 "/**"										// 전체(테스트할 때만 임시 사용)
//					);
//	}
	
    /**
     * jsonView 설정
     * @return
     */
    @Bean(name="jsonView")
    MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
}
