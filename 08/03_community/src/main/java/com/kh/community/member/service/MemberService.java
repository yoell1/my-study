package com.kh.community.member.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.kh.community.member.model.dto.MemberDTO;

/*
 * 서비스 인터페이스
 * 
 * 컨트롤러는 무엇을 할지만 알면 되고, 어덯게 하는지(비즈니스 로직)는 몰라도 됨!
 * 인터페이스와 구현체를 분리해서 구현제가 변경되어야 할 때
 * 인터페으스는 그대로 두고 구현제만 변경
 */

public interface MemberService {
	
	// 회원 가입
	void join(MemberDTO member, MultipartFile profileImage) throws IOException;
	
	// 아이디 중복 체크
	boolean isMemberIdCheck(String memberId);
	
	// 로그인
	MemberDTO login(String memberId, String memberPwd);
	
	// 회원 탈퇴
	void withdraw(String memberId);
	
	
	

}
