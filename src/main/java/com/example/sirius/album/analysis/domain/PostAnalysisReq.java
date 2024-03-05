package com.example.sirius.album.analysis.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostAnalysisReq {
    @NotBlank(message = "ai_type은 필수 입력값입니다.")
    @Pattern(regexp = "^(segmentation|detection|itwin)$", message = "분석 가능한 종류는 다음과 같습니다. [segmentation/detection/itwin]")
    private String ai_type;
}
