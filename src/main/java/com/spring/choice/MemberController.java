package com.spring.choice;

import com.spring.choice.Entity.MemberDTO;
import com.spring.choice.Repository.MemberRepository;
import com.spring.choice.Repository.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

// G-mail 관련 모듈
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Controller
public class MemberController {
    @Autowired
    private MemberRepository memberRepository;
    private final JavaMailSender emailSender;
    public MemberController(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    // 회원가입
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("member", new MemberDTO());
        return "signup";
    }

    // 회원가입
    @PostMapping("/signup")
    public String signUp(@ModelAttribute MemberDTO member, Model model) {
        boolean isUsernameDuplicate = memberRepository.existsByUsername(member.getUsername());
        boolean isEmailDuplicate = memberRepository.existsByEmail(member.getEmail());
        if (isUsernameDuplicate) {
            model.addAttribute("error_id", "이미 사용 중인 아이디입니다.");
        } else if (isEmailDuplicate) {
            model.addAttribute("error_email", "이미 사용 중인 이메일입니다.");
        } else {
            // SHA256 + SALT 비밀번호 암호화
            String encryptedPassword = PasswordEncoder.encode(member.getPassword());
            member.setPassword(encryptedPassword);

            member.setEmailCheck(0); // 0 = 미인증 , 1 = 인증
            memberRepository.save(member);
            // 이메일 전송
            sendVerificationEmail(member.getEmail(), member.getId());
            return "redirect:/verify-email";
        }
        return "signup";
    }

    // 이메일 전송
    private void sendVerificationEmail(String email, Long id) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("회원가입을 확인해주세요");
            String verificationLink = "http://localhost:8080/verify-email/" + id;
            String emailContent = "<p>회원가입을 완료하려면 아래 링크를 클릭하여 이메일을 확인해주세요.</p>"
                    + "<p><a href=\"" + verificationLink + "\">이메일 인증하기</a></p>";
            helper.setText(emailContent, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        emailSender.send(message);
    }

    // 이메일 확인 페이지
    @GetMapping("/verify-email/{id}")
    public String verifyEmail(@PathVariable Long id, Model model) {
        MemberDTO member = memberRepository.findById(id).orElse(null);
        if (member != null && member.getEmailCheck() == 0) {
            member.setEmailCheck(1); // 인증된 이메일로 설정
            memberRepository.save(member);
            model.addAttribute("verificationSuccess_done", "이메일 인증이 완료되었습니다.");
        } else {
            model.addAttribute("verificationSuccess_error", "이미 인증된 이메일입니다.");
        }
        return "verify_email_result";
    }

    // 로그인
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

 // 로그인 요청 처리
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        MemberDTO member = memberRepository.findByUsername(username);
        if (member != null) {
            // 저장된 비밀번호와 비교
            String savedPassword = member.getPassword();
            String[] parts = savedPassword.split("\\$");
            String encodedSalt = parts[0];
            String encodedHash = parts[1];
            String inputPasswordHash = PasswordEncoder.encodeWithSalt(password, encodedSalt);
            
            if (inputPasswordHash.equals(encodedHash)) {
                if (member.getEmailCheck() == 1) { // 이메일 인증자만 로그인 하도록 처리
                    session.setAttribute("username", username);
                    return "redirect:/index";
                } else {
                    model.addAttribute("error_email", "이메일 인증이 되지 않은 사용자입니다.");
                    return "login";
                }
            }
        }
        model.addAttribute("error_login", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return "login";
    }
    
    // 로그아웃 요청 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("username");
        return "redirect:/index";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            MemberDTO member = memberRepository.findByUsername(username);
            model.addAttribute("member", member);
            return "mypage";
        } else {
            return "redirect:/index"; 
        }
    }
    
 // 아이디 변경
    @PostMapping("/change-username")
    public String changeUsername(@RequestParam String currentUsername, @RequestParam String newUsername, HttpSession session, Model model) {
        MemberDTO currentMember = memberRepository.findByUsername(currentUsername);
        MemberDTO newMember = memberRepository.findByUsername(newUsername);

        if (currentMember != null && newMember == null) {
            currentMember.setUsername(newUsername);
            memberRepository.save(currentMember);
            session.setAttribute("username", newUsername);
            model.addAttribute("usernameChanged", "아이디가 변경되었습니다.");
        } else {
            model.addAttribute("usernameChanged_error", "오류가 발생했습니다. 다시 시도해주세요.");
        }

        return "redirect:/mypage";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentUsername, @RequestParam String currentPassword,
                                 @RequestParam String newPassword, HttpSession session, RedirectAttributes redirectAttributes) {
        MemberDTO currentMember = memberRepository.findByUsername(currentUsername);

        if (currentMember != null) {
            // 현재 비밀번호가 일치하는지 확인
            boolean passwordMatches = PasswordEncoder.matches(currentPassword, currentMember.getPassword());

            if (passwordMatches) {
                // 새로운 비밀번호로 업데이트
                String encryptedNewPassword = PasswordEncoder.encode(newPassword);
                currentMember.setPassword(encryptedNewPassword);
                memberRepository.save(currentMember); // 변경된 비밀번호를 데이터베이스에 저장
                redirectAttributes.addFlashAttribute("passwordChanged", "비밀번호가 변경되었습니다.");
            } else {
                // 현재 비밀번호가 일치하지 않는 경우
                redirectAttributes.addFlashAttribute("passwordChanged_error", "현재 비밀번호가 일치하지 않습니다.");
            }
        } else {
            // 사용자를 찾을 수 없는 경우
            redirectAttributes.addFlashAttribute("passwordChanged_error", "아이디가 일치하지 않습니다.");
        }

        return "redirect:/mypage";
    }



}
