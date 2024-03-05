package com.example.sirius.album.picture;


import com.example.sirius.album.analysis.domain.SegmentationEntity;
import com.example.sirius.album.picture.domain.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<AlbumEntity, Integer> {
    @Query("select a from AlbumEntity a join a.missionEntity m " +
            "where m.id=:missionId")
    List<AlbumEntity> findByMissionId(@Param("missionId") Integer missionId);

    @Query("select a from AlbumEntity a join a.missionEntity m " +
            "where a.id=:albumId and m.id=:missionId")
    Optional<AlbumEntity> findByIdAndMissionId(@Param("albumId") Integer albumId,@Param("missionId") Integer missionId);

    @Query("select p.filePath from PictureEntity p join p.albumEntity a where a.id=:albumId")
    List<String> findAlbumPathById(@Param("albumId") Integer albumId,Pageable pageable);

}
