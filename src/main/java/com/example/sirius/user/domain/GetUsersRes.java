package com.example.sirius.user.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetUsersRes {
    private Integer id;
    private String login_id;
    private String authority;
    private String regdate;
}
