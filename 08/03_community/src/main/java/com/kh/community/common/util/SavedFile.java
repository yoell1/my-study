package com.kh.community.common.util;

/*
 *  저장된 파일 정보를 당믈 객체
 *  - 회원 프로필 이미지 : 저장된 위치 
 *  - 게시글의 이미지 : 원본 파일명, 저장된 파일명 위치  
 */
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
//@AllargsConstructor
@RequiredArgsConstructor  // final 이 붙어야 함 / 생성시 변경을 안할 때 쓰는 어노테이션
public class SavedFile {
	private final String orignalName;  // 원본 파일명
	private final String saveName;     // 저장된 파일명
	private final String path;         // 저장된 위치
	
	/**
	 *  MultipartFile을 매개변수로 받아 파일을 생성(저장) 하고,
	 *  target 정보(저장 위치)로 파일을 저장한다
	 *  
	 *  @param multipartFile 업로드된 파일
	 *  @param target 저장할 위치 (디렉토리 경로)
	 *  @thrwos IOEException 파일 저장 중 오류가 발생한 경우
	 */
	
	

}
