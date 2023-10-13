package com.example.sirius.map.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "maps")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "map_path")
    private String mapPath;
    @Column(name = "map_count")
    private Integer mapCount;
    @Column(name = "map_area")
    private Float mapArea;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "map_group_id")
    private MapGroupEntity mapGroupEntity;

    public static MapEntity from(PostMapReq postMapReq, MapGroupEntity mapGroupEntity) {
        return MapEntity.builder()
                .mapGroupEntity(mapGroupEntity)
                .mapPath(postMapReq.getFile_path())
                .date(postMapReq.getDate())
                .time(postMapReq.getTime())
                .mapCount(postMapReq.getMap_count())
                .mapArea(postMapReq.getMap_area())
                .build();
    }
}