package com.example.sirius.plan.domain;


import com.example.sirius.map.domain.MapGroupEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "missions")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regdate;
    @Column(name = "group_num")
    private Integer groupNum;

    @PrePersist
    public void prePersist() {
        this.regdate = LocalDateTime.now();
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "map_group_id")
    private MapGroupEntity mapGroupEntity;

    public static MissionEntity from(PostMissionReq postMissionReq, MapGroupEntity mapGroupEntity) {
        return MissionEntity.builder()
                .mapGroupEntity(mapGroupEntity)
                .name(postMissionReq.getName())
                .groupNum(postMissionReq.getGroup_num())
                .build();
    }

    public GetMissionRes toDto() {
        GetMissionRes getMissionRes = new GetMissionRes();
        getMissionRes.setId(this.id);
        getMissionRes.setName(this.name);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        getMissionRes.setRegdate(this.regdate.format(formatter));
        getMissionRes.setGroup_num(this.groupNum);
        return getMissionRes;
    }
}
