package com.example.sirius.ftp;


import com.example.sirius.exception.BaseResponse;
import com.example.sirius.ftp.domain.PostMapURL;
import com.example.sirius.ftp.domain.PostMapURLSuccess;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class FtpController {
    private FtpService ftpService;

    @GetMapping("api/users/{login_id}/ftp")
    public BaseResponse getFtpInfo(@PathVariable String login_id) {
        return ftpService.getFtpInfo(login_id);
    }

    @PostMapping("api/users/{login_id}/maps/ftp/url")
    public BaseResponse postMapURL(@PathVariable String login_id, @Valid @RequestBody PostMapURL postMapURL) {
        return ftpService.postMapURL(postMapURL,login_id);
    }

    @PostMapping("api/users/{login_id}/maps/ftp/url/success")
    public BaseResponse postMapURLSuccess(@PathVariable String login_id, @Valid @RequestBody PostMapURLSuccess postMapURLSuccess) {
        return ftpService.postMapURLSuccess(postMapURLSuccess,login_id);

    }
}
