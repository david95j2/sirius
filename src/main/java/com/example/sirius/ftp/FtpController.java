package com.example.sirius.ftp;


import com.example.sirius.exception.BaseResponse;
import com.example.sirius.ftp.domain.FtpInfo;
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

    @GetMapping("api/users/{login_id}/maps/{map_id}/ftp/url")
    public BaseResponse getURLForDownloads(@PathVariable String login_id, @PathVariable Integer map_id) {
        return ftpService.getURLForDownloads(map_id, login_id);
    }

    @GetMapping("api/users/{login_id}/facilities/{facility_id}/ftp/url")
    public BaseResponse getURLForUpload(@PathVariable String login_id, @PathVariable Integer facility_id) {
        return ftpService.getURLForUpload(facility_id, login_id);
    }

    @PostMapping("api/users/{login_id}/facilities/{facility_id}/ftp/url")
    public BaseResponse postURLForUploadSuccess(@PathVariable String login_id, @PathVariable Integer facility_id, @Valid @RequestBody FtpInfo ftpInfo) {
        return ftpService.postURLForUploadSuccess(ftpInfo, facility_id, login_id);
    }

    @PostMapping("api/users/{login_id}/maps/ftp/url/success")
    public BaseResponse postMapURLSuccess(@PathVariable String login_id, @Valid @RequestBody PostMapURLSuccess postMapURLSuccess) {
        return ftpService.postMapURLSuccess(postMapURLSuccess,login_id);

    }
}
