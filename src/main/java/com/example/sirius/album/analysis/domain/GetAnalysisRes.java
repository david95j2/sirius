package com.example.sirius.album.analysis.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetAnalysisRes {
    private Integer id;
    private String regdate;
    private String status;
    private String aiType;
}
