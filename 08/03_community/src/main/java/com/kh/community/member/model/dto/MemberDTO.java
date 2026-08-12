package com.kh.community.member.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class MemberDTO {
	// TB_MEMBER 테이블을 기준으로 필드를 정의
		/*
		MEMBER_ID    VARCHAR2(50)   NOT NULL,
		MEMBER_PWD   VARCHAR2(200)  NOT NULL,
		MEMBER_NAME  VARCHAR2(50)   NOT NULL,
		NICKNAME     VARCHAR2(50)   NOT NULL,
		EMAIL        VARCHAR2(100),
		PROFILE      VARCHAR2(300),
		CREATE_AT    DATE           DEFAULT SYSDATE NOT NULL,
		*/
		
		private String memberId;
		private String memberPwd;
		private String memberName;
		private String nickname;
		private String email;
		private String profile;
		private LocalDateTime createAt;
		
		private String CreateAtStr;
		// 화면 표시용 문자열 변수
		// (JSP에서는 Date만 형식을 사용할 수 있음.. LocalDateTime을 사용하려고 하면 코드가 지저분해질 수 있음)
}
