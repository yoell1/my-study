package com.kh.community.common.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 *  WebMvcConfigurer : Spring MVC의 공통 설정 인터페이스
 *  
 *  - 업로드된 이미지 매핑
 *    : 업로드된 이미지 파일은 src/main/resources/static 폴더가 아니라
 *      별도의 경로로 저장이 될 것.
 *      --> 특정 주소(/uploads/**)로 요청했을 때 실제 파일이 저장된 경로로 연결
 */
@Configuration // @Configuration은 "이 클래스 안의 @Bean 메서드들이 만들어내는 객체들을 빈으로 등록해줘"라는 의미
public class WebConfig implements WebMvcConfigurer {
	
    @Value("${file.upload-dir}")
	private String uploadDir;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		String uploadDir = "uploads";
		
		String absoluteDir = new File(uploadDir).getAbsolutePath();
		
		registry.addResourceHandler("/uploads/**")
		        .addResourceLocations("file:"+ absoluteDir +File.separator);
	}

}
