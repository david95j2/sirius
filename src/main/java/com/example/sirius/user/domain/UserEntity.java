package com.example.sirius.user.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "login_id")
    private String loginId;
    private String password;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regdate;

    private String authority;

    @PrePersist
    public void prePersist() {
        this.regdate = LocalDateTime.now();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public static UserEntity from(PostUserReq postUserReq) {
        return UserEntity.builder()
                .loginId(postUserReq.getLogin_id())
                .password(postUserReq.getPassword())
                .authority(postUserReq.getAuthority())
                .build();
    }
}
