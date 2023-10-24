package com.example.sirius.album.analysis;

import com.example.sirius.album.analysis.domain.AnalysisEntity;
import com.example.sirius.album.analysis.domain.SegmentationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity,Integer> {
    @Query("select a from AnalysisEntity a join a.albumEntity ab " +
            "where ab.id=:albumId")
    List<AnalysisEntity> findAllByAlbumId(@Param("albumId") Integer albumId);

    @Query("select a from AnalysisEntity a join a.albumEntity ab " +
            "where a.id=:analysisId and ab.id=:albumId")
    Optional<AnalysisEntity> findByIdAndAlbumId(@Param("analysisId") Integer analysisId,@Param("albumId") Integer albumId);

    @Query("select s from SegmentationEntity s join s.analysisEntity.albumEntity a where a.id=:albumId")
    List<SegmentationEntity> findSegAllByAlbumId(@Param("albumId") Integer albumId);

    @Query("select s from SegmentationEntity s join s.analysisEntity a where a.id=:analysisId")
    List<SegmentationEntity> findSegAllById(@Param("analysisId") Integer analysisId);

    @Query("select s from SegmentationEntity s join s.analysisEntity a where a.id=:analysisId and s.id=:segmentationId")
    Optional<SegmentationEntity> findSegByIdAndSegId(@Param("analysisId") Integer analysisId,@Param("segmentationId") Integer segmentationId);

    @Query("select s from SegmentationEntity s where s.id=:segmentationId")
    Optional<SegmentationEntity> findSegBySegId(@Param("segmentationId") Integer segmentationId);

    @Query("select a from AnalysisEntity a join a.albumEntity ab where ab.id=:albumId")
    Optional<AnalysisEntity> findByAlbumId(@Param("albumId") int albumId);
}
