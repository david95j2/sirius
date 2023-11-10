package com.example.sirius.album.picture;

import com.example.sirius.album.picture.domain.PictureEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<PictureEntity, Integer> {
    @Query("select count(p) from PictureEntity p join p.albumEntity a " +
            "where a.id=:albumId")
    Integer findTotalCountByAlbumId(@Param("albumId") Integer albumId);

    @Query("select p from PictureEntity p join p.albumEntity a where a.id=:albumId")
    List<PictureEntity> findByAlbumIdWhereLimitOne(@Param("albumId") Integer albumId, Pageable pageable);

    @Transactional
    @Query("delete from PictureEntity p where p.albumEntity.id=:albumId")
    Integer deleteByAlbumId(@Param("albumId") Integer albumId);
}
