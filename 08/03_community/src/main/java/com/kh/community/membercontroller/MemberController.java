package com.kh.community.membercontroller;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.kh.community.member.model.dto.MemberDTO;
import com.kh.community.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
/*
 *  회원 관련 화면 이동, 폼 처리 등을 담당할 컨트롤러
 */
@Controller
@RequestMapping("/member")
public class MemberController {
	// MemberService 를 DI 처리 (생성자 주입방식)
	private final MemberService service;
	public MemberController(MemberService service) {  // 다형성 적용
		this.service= service;
	}


	// --- 화면 이동 요청 ---
	@GetMapping("/join")
	public String joinForm() {
		return "member/join";
	}

	@GetMapping("/login")
	public String loginForm() {
		return "member/login";
	}


	// -----------------------------------
	@PostMapping("/join")
	public String join(@ModelAttribute MemberDTO member,
			@RequestParam(required=false) MultipartFile profileImage
			) {
		System.out.println(member);
		System.out.println(profileImage);
		try {
		service.join(member, profileImage);
		}catch(IOException e){
			e.printStackTrace();

			// 예외 발생 시 회원 가입 페이지로 리다이렉트
			return "redirect:/member/join";


		}

		// 회원 가입 성공시 로그인 페이지로 리다이렉트
		return "redirect:/member/login";
	}

	// 아이디 중복 확인 (join.jsp 에서 fetch로 호출, JSON(boolean) 응답)
	// true = 이미 사용 중인 아이디, false = 사용 가능한 아이디
	@GetMapping("/idcheck")
	@ResponseBody
	public boolean idCheck(@RequestParam String memberId) {
		return service.isMemberIdCheck(memberId);
	}

	// 로그인 처리
	@PostMapping("/login")
	public String login(@RequestParam String memberId,
			@RequestParam String memberPwd,
			HttpSession session) {

		MemberDTO member = service.login(memberId, memberPwd);

		if (member == null) {
			// 로그인 실패 -> 에러 파라미터와 함께 로그인 페이지로 리다이렉트
			return "redirect:/member/login?error";
		}

		// 로그인 성공 -> 세션에 로그인 회원 정보 저장
		session.setAttribute("loginMember", member);

		return "redirect:/";
	}

	// 로그아웃
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	// 회원 탈퇴
	@PostMapping("/withdraw")
	public String withdraw(HttpSession session) {
		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

		if (loginMember != null) {
			service.withdraw(loginMember.getMemberId());
			session.invalidate();
		}

		return "redirect:/";
	}
}