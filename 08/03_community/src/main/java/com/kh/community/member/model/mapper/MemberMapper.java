package com.kh.community.member.model.mapper;
import org.apache.ibatis.annotations.Mapper;
import com.kh.community.member.model.dto.MemberDTO;
@Mapper
public interface MemberMapper {

	// 회원 가입 -> 데이터를 추가
	int insertMember(MemberDTO member);

	// 아이디 중복 체크 -> 해당 아이디를 가진 회원 수 조회
	int countByMemberId(String memberId);

	// 로그인 -> 아이디로 회원 정보 조회
	MemberDTO selectMemberById(String memberId);

	// 회원 탈퇴 -> 데이터 삭제
	int deleteMember(String memberId);
}
