package com.example.sirius.map.domain;

import com.example.sirius.facility.domain.FacilityEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "map_groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MapGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "facility_id")
    private FacilityEntity facilityEntity;

    @JsonManagedReference
    @JsonIgnore
    @OneToMany(mappedBy = "mapGroupEntity")
    private List<MapEntity> mapEntities;
}
