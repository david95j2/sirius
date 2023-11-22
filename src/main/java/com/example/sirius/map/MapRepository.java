package com.example.sirius.map;

import com.example.sirius.facility.domain.ThumbnailEntity;
import com.example.sirius.map.domain.MapEntity;
import com.example.sirius.map.domain.MapGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MapRepository extends JpaRepository<MapEntity, Integer> {
    @Query("select t from FacilityEntity f join f.thumbnailEntities t " +
            "where f.id=:facilityId order by t.thumbnailRegdate desc limit 1")
    Optional<ThumbnailEntity> findByfacilityId(@Param("facilityId") Integer facilityId);

    @Query("select mg.id as mapGroupId from MapEntity m " +
            "join m.mapGroupEntity mg " +
            "where m.id=:mapId")
    Optional<Integer> findMapGroupIdByMapId(@Param("mapId") Integer mapId);

    @Query("select mg from MapEntity m " +
            "join m.mapGroupEntity mg " +
            "where m.id=:mapId")
    Optional<MapGroupEntity> findMapGroupByMapId(@Param("mapId") Integer mapId);

    @Query("select m from MapEntity m " +
            "join m.mapGroupEntity mg join mg.facilityEntity f " +
            "where f.id=:facilityId and m.date=:date and m.time=:time")
    List<MapEntity> findByLocationIdAndDatetime(@Param("facilityId") Integer facilityId,@Param("date") LocalDate date,
                                                @Param("time") LocalTime time);

    @Modifying
    @Query("delete from MapEntity m where m.mapGroupEntity.facilityEntity.id=:facilityId")
    Integer deleteAllByFacilityId(@Param("facilityId") Integer facilityId);

    @Modifying
    @Query("delete from MapEntity m where m.id=:mapId and m.mapGroupEntity.facilityEntity.id=:facilityId")
    Integer deleteByIdAndFacilityId(@Param("mapId") Integer mapId,@Param("facilityId") Integer facilityId);

    @Modifying
    @Query("delete from MapEntity m where m.mapGroupEntity.id=:mapGroupId")
    Integer deleteAllByMapGroupId(@Param("mapGroupId") Integer mapGroupId);

    @Query("select maps from AlbumEntity a join a.missionEntity.mapGroupEntity mg join mg.mapEntities maps " +
            "where a.id=:albumId and maps.mapPath like %:fileName%")
    Optional<MapEntity> findByAlbumIdAndFileName(@Param("albumId") Integer albumId, @Param("fileName") String fileName);

}
