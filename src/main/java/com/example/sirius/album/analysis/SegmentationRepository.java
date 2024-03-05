package com.example.sirius.album.analysis;

import com.example.sirius.album.analysis.domain.SegmentationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SegmentationRepository extends JpaRepository<SegmentationEntity,Integer> {
    @Query("select s from SegmentationEntity s join s.analysisEntity.albumEntity a where a.id=:albumId")
    List<SegmentationEntity> findSegAllByAlbumId(@Param("albumId") Integer albumId);

    @Query("select s from SegmentationEntity s join s.analysisEntity a where a.id=:analysisId")
    List<SegmentationEntity> findSegAllById(@Param("analysisId") Integer analysisId);

    @Query("select s from SegmentationEntity s join s.analysisEntity a where a.id=:analysisId and s.id=:segmentationId")
    Optional<SegmentationEntity> findSegByIdAndSegId(@Param("analysisId") Integer analysisId, @Param("segmentationId") Integer segmentationId);

    @Query("select s from SegmentationEntity s where s.id=:segmentationId")
    Optional<SegmentationEntity> findSegBySegId(@Param("segmentationId") Integer segmentationId);

    @Modifying
    @Query("delete from SegmentationEntity s where s.analysisEntity.albumEntity.id=:albumId")
    Integer deleteByAlbumId(@Param("albumId") Integer albumId);

    @Modifying
    @Query("delete from SegmentationEntity s where s.drawFilePath=:fileName")
    Integer deleteByFileName(@Param("fileName") String fileName);

    // origin
    @Query("select s from SegmentationEntity s where s.drawFilePath=:originFileName")
    Optional<SegmentationEntity> findByFileName(@Param("originFileName") String originFileName);

    @Query("select s from SegmentationEntity s where s.drawFilePath like %:originFileName%")
    List<SegmentationEntity> findPartByFileName(@Param("originFileName") String originFileName);

    // tesmp
    @Query("select s from SegmentationEntity s where s.drawFilePath=:originFileName")
    List<SegmentationEntity> findByFileNameList(@Param("originFileName") String originFileName);
}
