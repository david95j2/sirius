package com.example.sirius.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /*
     * 200 : 요청 성공
     */
    SUCCESS(HttpStatus.OK, "요청에 성공하였습니다."),
    /*
     * 201 : 생성 성공
     */
    CREATED(HttpStatus.CREATED,"생성되었습니다."),
    /*
     * 202 : 데이터 일부 or 전체 변경 성공
     */
    ACCEPTED(HttpStatus.ACCEPTED, "값이 변경되었습니다."),
    EXIST_ACCEPTED(HttpStatus.ACCEPTED, "장소가 이미 존재하기 때문에 위도경도만 변경되었습니다."),

    /*
     * 400 : Request 오류
     */
    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED, "인증되지 않은 요청입니다."),
    TIME_METHOD_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "허용되지 않은 요청입니다. time 값은 1~23까지의 자연수로 입력해주세요."),
    INCORRECT(HttpStatus.BAD_REQUEST, "비밀번호가 틀립니다."),
    NOT_NULL(HttpStatus.BAD_REQUEST,"빈칸을 채워주십시오."),

    /*
     * 403 FORBIDDEN: 권한이 없는 사용자의 요청
     */
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한을 확인해주세요."),
    ADMIN_CREATED_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "admin 유저는 이미 존재합니다."),

    /*
     * 404 NOT_FOUND: 리소스를 찾을 수 없음
     */
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 URL"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터가 존재하지 않습니다."),
    FTP_DATA_NOT_FOUND(HttpStatus.NOT_FOUND,"데이터를 확인할 수 없습니다. FTP URL 경로를 다시 확인해주세요."),
    FTP_INFO_NOT_FOUND(HttpStatus.NOT_FOUND,"infoMap.txt를 확인할 수 없습니다. FTP URL 경로를 다시 확인해주세요."),

    /*
     * 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
     */
    ZIP_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,"Folders are not allowed inside the zip."),
    TARTGZ_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,"Folders are not allowed inside the tar/tgz."),
    INVALID_FILENAME(HttpStatus.METHOD_NOT_ALLOWED,"파일명은 mission id 여야 합니다."),
    DATA_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,"Only ZIP, TAR, and TGZ files are allowed"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 요청입니다. 보내실 데이터를 다시 한 번 확인해주세요."),
    INVALID_INPUT(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 변수명입니다."),

    /*
     * 409 CONFLICT: 중복
     */
    DUPLICATED_DATA(HttpStatus.CONFLICT, "이미 사용중입니다."),
    DUPLICATED_THUMBNAILS_DATA(HttpStatus.CONFLICT, "해당 썸네일은 이미 존재합니다."),
    DUPLICATED_LOCATION_DATA(HttpStatus.CONFLICT, "해당 장소는 이미 존재합니다."),
    DUPLICATED_MAP_DATA(HttpStatus.CONFLICT, "해당 map은 이미 존재합니다."),

    /*
     * 500 INTERNAL_SERVER_ERROR: 내부 서버 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER ERROR"),
    INTERNAL_DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Album안에 사진이 존재하지 않습니다. 확인 예정");

    private final HttpStatus status;
    private final String message;
}
