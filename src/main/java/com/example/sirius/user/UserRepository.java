package com.example.sirius.user;

import com.example.sirius.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {


    // 비밀번호가 일치하는지 확인용
    Optional<UserEntity> findByPassword(String password);

    Optional<UserEntity> findByLoginId(String loginId);
    Optional<UserEntity> findByLoginIdAndPassword(String loginId, String password);
    Optional<UserEntity> findByAuthority(String authority);

    @Modifying
    @Query("delete from UserEntity u where u.loginId=:loginId")
    void deleteUserByLoginId(@Param("loginId") String loginId);

}
