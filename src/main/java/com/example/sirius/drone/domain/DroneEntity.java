package com.example.sirius.drone.domain;


import com.example.sirius.user.domain.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drones")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DroneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "drone_voltage_min")
    private Float droneVoltageMin;
    @Column(name = "drone_voltage_max")
    private Float droneVoltageMax;
    @Column(name = "drone_type")
    private String droneType;
    @Column(name = "x_dimension")
    private Integer xDimension;
    @Column(name = "y_dimension")
    private Integer yDimension;
    @Column(name = "z_dimension")
    private Integer zDimension;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    public PatchDroneRes toDto() {
        PatchDroneRes patchDroneRes = new PatchDroneRes();
        patchDroneRes.setId(this.id);
        patchDroneRes.setMin(this.droneVoltageMin);
        patchDroneRes.setMax(this.droneVoltageMax);
        patchDroneRes.setName(this.droneType);
        patchDroneRes.setX_dimension(this.xDimension);
        patchDroneRes.setY_dimension(this.yDimension);
        patchDroneRes.setZ_dimension(this.zDimension);
        return patchDroneRes;
    }

    public static DroneEntity from(PostDroneReq postDroneReq, UserEntity userEntity) {
        return DroneEntity.builder()
                .userEntity(userEntity)
                .droneVoltageMin(postDroneReq.getMin())
                .droneVoltageMax(postDroneReq.getMax())
                .droneType(postDroneReq.getName())
                .xDimension(postDroneReq.getX_dimension())
                .yDimension(postDroneReq.getY_dimension())
                .zDimension(postDroneReq.getZ_dimension())
                .build();
    }
}
