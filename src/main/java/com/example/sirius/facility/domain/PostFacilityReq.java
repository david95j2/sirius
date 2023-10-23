package com.example.sirius.facility.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;


@Getter @Setter
public class PostFacilityReq {
    @NotBlank(message = "장소는 필수 입력값입니다. 키가 location인지 확인해주세요.")
    private String location;
    @NotBlank(message = "이름은 필수 입력값입니다. 키가 name인지 확인해주세요.")
    private String name;
    @NotNull(message = "위도는 필수 입력값입니다. 키가 laititude인지 확인해주세요.")
    private Float latitude;
    @NotNull(message = "경도는 필수 입력값입니다. 키가 longitude인지 확인해주세요.")
    private Float longitude;

    public static PostFacilityReq fromJSONObject(JSONObject jsonObject) {
        PostFacilityReq request = new PostFacilityReq();

        if (jsonObject.get("location") != null) {
            request.setName((String) jsonObject.get("location"));
        }

        if (jsonObject.get("latitude") != null) {
            request.setLatitude(Float.parseFloat(jsonObject.get("latitude").toString()));
        }

        if (jsonObject.get("longitude") != null) {
            request.setLongitude(Float.parseFloat(jsonObject.get("longitude").toString()));
        }

        return request;
    }
}
