package com.example.sirius.album.analysis;

import com.example.sirius.album.analysis.domain.AnalysisEntity;
import com.example.sirius.album.analysis.domain.SegmentationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity,Integer> {
    @Query("select a from AnalysisEntity a join a.albumEntity ab " +
            "where ab.id=:albumId")
    List<AnalysisEntity> findAllByAlbumId(@Param("albumId") Integer albumId);

    @Query("select a from AnalysisEntity a join a.albumEntity ab " +
            "where a.id=:analysisId and ab.id=:albumId")
    Optional<AnalysisEntity> findByIdAndAlbumId(@Param("analysisId") Integer analysisId,@Param("albumId") Integer albumId);

    @Query("select a from AnalysisEntity a join a.albumEntity ab where ab.id=:albumId")
    Optional<AnalysisEntity> findByAlbumId(@Param("albumId") int albumId);

    @Transactional
    @Query("delete from AnalysisEntity a where a.albumEntity.id=:albumId")
    Integer deleteByAlbumId(@Param("albumId") Integer albumId);
}
