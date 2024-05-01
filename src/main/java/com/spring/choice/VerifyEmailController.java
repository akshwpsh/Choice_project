package choiceproject.choice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VerifyEmailController {

    @GetMapping("/verify-email")
    public String showVerifyEmailPage() {
        return "verify-email";
    }
    @GetMapping("/verify-email-result")
    public String showVerifyEmailResultPage() {
        return "verify_email_result";
    }
}
	