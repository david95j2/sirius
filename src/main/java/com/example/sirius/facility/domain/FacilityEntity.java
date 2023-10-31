package com.example.sirius.facility.domain;


import com.example.sirius.user.domain.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "facilities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FacilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private String location;
    @Column(name = "location_ascii")
    private String locationAscii;
    private String name;
    private Float latitude;
    private Float longitude;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @JsonManagedReference
    @JsonIgnore
    @OneToMany(mappedBy = "facilityEntity",cascade = CascadeType.REMOVE)
    private List<ThumbnailEntity> thumbnailEntities;

    public static FacilityEntity from(PostFacilityReq postFacilityReq, UserEntity userEntity) {
        return FacilityEntity.builder()
                .userEntity(userEntity)
                .location(postFacilityReq.getLocation())
                .locationAscii(postFacilityReq.getLocationAscii())
                .name(postFacilityReq.getName())
                .latitude(postFacilityReq.getLatitude())
                .longitude(postFacilityReq.getLongitude())
                .build();
    }

    public PatchFacilityRes toDto() {
        PatchFacilityRes patchFacilityRes = new PatchFacilityRes();
        patchFacilityRes.setId(this.id);
        patchFacilityRes.setLocation(this.location);
        patchFacilityRes.setName(this.name);
        patchFacilityRes.setLatitude(this.latitude);
        patchFacilityRes.setLongitude(this.longitude);
        return patchFacilityRes;
    }
}