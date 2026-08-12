package com.kh.community.member.service;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.kh.community.common.util.FileUploadUtil;
import com.kh.community.common.util.SavedFile;
import com.kh.community.member.model.dto.MemberDTO;
import com.kh.community.member.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor  // 롬복이 final 필드를 매개변수로 갖는 생성자를 자동 생성 ( 생성자 주입 방식)
public class MemberServiceImpl implements MemberService {
	// FileUploadUtil 을 DI처리 (생성자 주입 방식, 롬복 사용)
	private final FileUploadUtil uploadUtil;
	// MemberMapper DI
	private final MemberMapper mapper;

	@Value("${file.upload-dir.profile}")
	private String profileUploadDir;  //  @RequiredArgsConstructor 이 있어서 final 붙이면 안됨
	@Override
	public void join(MemberDTO member, MultipartFile profileImage) throws IOException {

		// 프로필 이미지 파일을 "서버"에 저장  --> 공통 클래스로 분리
		SavedFile saved = uploadUtil.save(profileImage, profileUploadDir, "/uploads/profile");
		if(saved != null) {
			// 저장된 경로를 dto에 설정
			member.setProfile(saved.getPath());
		}
		// TB_MEMBER 테이블("DB")에 데이터 저장  --> Mapper
		mapper.insertMember(member);
	}
	@Override
	public boolean isMemberIdCheck(String memberId) {
		// 이미 사용 중인 아이디인지 확인 (true = 중복 O, 사용 불가 / false = 사용 가능)
		int count = mapper.countByMemberId(memberId);
		return count > 0;
	}
	@Override
	public MemberDTO login(String memberId, String memberPwd) {
		// 아이디로 회원 정보 조회
		MemberDTO member = mapper.selectMemberById(memberId);

		// 아이디가 존재하지 않거나, 비밀번호가 일치하지 않으면 로그인 실패
		// TODO: 현재는 비밀번호를 평문으로 비교 중. join()에서 비밀번호 암호화(PasswordEncoder)를
		//       적용하게 되면 이 부분도 encoder.matches(memberPwd, member.getMemberPwd())로 변경 필요
		if (member == null || !member.getMemberPwd().equals(memberPwd)) {
			return null;
		}
		return member;
	}
	@Override
	public void withdraw(String memberId) {
		mapper.deleteMember(memberId);
	}
}