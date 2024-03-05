package com.example.sirius.album.analysis.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PatchAnalysisReq {
    private Integer status;
}
