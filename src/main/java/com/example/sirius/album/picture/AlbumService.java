package com.example.sirius.album.picture;

import com.example.sirius.album.analysis.AnalysisRepository;
import com.example.sirius.album.analysis.SegmentationRepository;
import com.example.sirius.album.analysis.domain.SegmentationEntity;
import com.example.sirius.album.picture.domain.*;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.plan.MissionRepository;
import com.example.sirius.plan.domain.MissionEntity;
import com.example.sirius.utils.SiriusUtils;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.example.sirius.album.picture.domain.QPictureEntity.pictureEntity;

@Service
@Slf4j
@AllArgsConstructor
public class AlbumService {
    private JPAQueryFactory queryFactory;
    private MissionRepository missionRepository;
    private AlbumRepository albumRepository;
    private PictureRepository pictureRepository;
    private AnalysisRepository analysisRepository;
    private SegmentationRepository segmentationRepository;

    public BaseResponse getAlbums(Integer missionId) {
        List<AlbumEntity> results = albumRepository.findByMissionId(missionId);
        return new BaseResponse(ErrorCode.SUCCESS, results.stream().map(AlbumEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getAlbumById(Integer albumId, Integer missionId) {
        AlbumEntity albumEntity = albumRepository.findByIdAndMissionId(albumId, missionId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        // 총 사진 개수
        Integer total_image_count = pictureRepository.findTotalCountByAlbumId(albumId);

        // 총 균열 개수
        AtomicInteger total_crack_count = new AtomicInteger(0);
        List<SegmentationEntity> results = segmentationRepository.findSegAllByAlbumId(albumId);
        results.stream().forEach( x -> {
            String[] fileName = FilenameUtils.removeExtension(x.getJsonFilePath()).split("_");
            total_crack_count.addAndGet(Integer.parseInt(fileName[fileName.length - 1]));
        });

        // 카메라 제조 업체, 카메라 모델, 사진 크기, 만든 날짜
        PageRequest pageRequest = PageRequest.of(0, 1);
        PictureEntity pictureEntity = pictureRepository.findByAlbumIdWhereLimitOne(albumId,pageRequest).get(0);
        Map<String,String> image_metadata = SiriusUtils.extractImageMetadata(pictureEntity.getFilePath());

        GetAlbumDetailRes getAlbumDetailRes = new GetAlbumDetailRes();
        getAlbumDetailRes.setId(albumEntity.getId());
        getAlbumDetailRes.setTotalImageCount(total_image_count);
        getAlbumDetailRes.setTotalCrackCount(total_crack_count.get());
        getAlbumDetailRes.setMaker(image_metadata.get("Make"));
        getAlbumDetailRes.setModel(image_metadata.get("Model"));
        getAlbumDetailRes.setImageHeight(Integer.valueOf(image_metadata.get("Image Height").toString().split(" ")[0]));
        getAlbumDetailRes.setImageWidth(Integer.valueOf(image_metadata.get("Image Width").toString().split(" ")[0]));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        getAlbumDetailRes.setRegdate(albumEntity.getRegdate().format(formatter));

        return new BaseResponse(ErrorCode.SUCCESS,getAlbumDetailRes);
    }

    public BaseResponse postAlbum(PostAlbumReq postAlbumReq, Integer missionId) {
        MissionEntity missionEntity = missionRepository.findById(missionId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        AlbumEntity albumEntity = AlbumEntity.from(postAlbumReq,missionEntity);
        Integer createdNum = albumRepository.save(albumEntity).getId();
        return new BaseResponse(ErrorCode.CREATED, Integer.valueOf(createdNum)+"번 앨범이 생성되었습니다.");
    }

    public BaseResponse patchAlbum(PatchAlbumReq patchAlbumReq, Integer albumId, Integer missionId) {
        AlbumEntity albumEntity = albumRepository.findByIdAndMissionId(albumId, missionId).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        if (patchAlbumReq.getRegdate() != null) {
            albumEntity.setRegdate(LocalDateTime.parse(patchAlbumReq.getRegdate(),formatter));
        } else {
            throw new AppException(ErrorCode.METHOD_NOT_ALLOWED);
        }
        albumRepository.save(albumEntity);
        return new BaseResponse(ErrorCode.ACCEPTED,Integer.valueOf(albumId)+"번 앨범의 값이 변경되었습니다.");
    }

    @Transactional
    public BaseResponse deleteAlbum(Integer albumId, Integer missionId) {
        AlbumEntity albumEntity = albumRepository.findByIdAndMissionId(albumId, missionId).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));
        albumRepository.delete(albumEntity);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(albumId)+"번 앨범이 삭제되었습니다.");
    }

    public BaseResponse getPictures(Integer albumId, String date, Integer time) {
        JPAQuery<PictureEntity> query = queryFactory.selectFrom(pictureEntity)
                .where(pictureEntity.albumEntity.id.eq(albumId));

        if (date != null) {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
            query.where(pictureEntity.date.eq(parsedDate));
        }

        if (time != null) {
            if (time >= 24) {
                throw new AppException(ErrorCode.DATA_NOT_FOUND);
            }
            LocalTime startTIme = LocalTime.of(time,0);
            LocalTime endTime = LocalTime.of(23,59,59);
            query.where(pictureEntity.time.between(startTIme,endTime));
        }

        List<PictureEntity> results = query.fetch();
        List<GetPictureRes> new_results = results.stream().map(PictureEntity::toDto).collect(Collectors.toList());
        return new BaseResponse(ErrorCode.SUCCESS,new_results);
    }

    public Resource getPictureFileById(Integer pictureId) {
        PictureEntity pictureEntity = pictureRepository.findById(pictureId).orElseThrow(
                ()-> new AppException(ErrorCode.DATA_NOT_FOUND)
        );
        return SiriusUtils.loadFileAsResource(Paths.get(pictureEntity.getFilePath()).getParent().toString(),
                Paths.get(pictureEntity.getFilePath()).getFileName().toString());
    }

    public BaseResponse uploadPictures(MultipartFile[] files) {
        List<String> fileNames = new ArrayList<>();
        Arrays.asList(files).stream().forEach(file -> {
            try {
                String originalFileName = file.getOriginalFilename();
                Path filePath = Paths.get("" + File.separator + originalFileName);
                Files.write(filePath, file.getBytes());

                fileNames.add(originalFileName);

                // DB 업데이트

            } catch (IOException e) {
                e.printStackTrace();
                throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        });
        return new BaseResponse(ErrorCode.CREATED);
    }
}
