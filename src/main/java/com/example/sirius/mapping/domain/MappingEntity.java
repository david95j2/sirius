package com.example.sirius.mapping.domain;


import com.example.sirius.facility.domain.FacilityEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "mappings")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "facility_id")
    private FacilityEntity facilityEntity;

    @JsonManagedReference
    @JsonIgnore
    @OneToMany(mappedBy = "mappingEntity",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MappingWayPointEntity> globalWayPointEntities;

    public PatchMappingRes toDto() {
        PatchMappingRes patchMappingRes = new PatchMappingRes();
        patchMappingRes.setId(this.id);
        patchMappingRes.setMission_name(this.name);
        return patchMappingRes;
    }

    public static MappingEntity from(PostMappingReq postMappingReq, FacilityEntity facilityEntity) {
        return MappingEntity.builder()
                .facilityEntity(facilityEntity)
                .name(postMappingReq.getName())
                .build();
    }
}
