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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
        AlbumEntity albumEntity = albumRepository.findByIdAndMissionId(albumId, missionId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        // 총 사진 개수
        Integer total_image_count = pictureRepository.findTotalCountByAlbumId(albumId);

        // 총 균열 개수
        AtomicInteger total_crack_count = new AtomicInteger(0);
        List<SegmentationEntity> results = segmentationRepository.findSegAllByAlbumId(albumId);
        results.stream().forEach(x -> {
            String[] fileName = FilenameUtils.removeExtension(x.getJsonFilePath()).split("_");
            total_crack_count.addAndGet(Integer.parseInt(fileName[fileName.length - 1]));
        });

        // 카메라 제조 업체, 카메라 모델, 사진 크기, 만든 날짜
        PageRequest pageRequest = PageRequest.of(0, 1);
        PictureEntity pictureEntity = pictureRepository.findByAlbumIdWhereLimitOne(albumId, pageRequest).get(0);
        Map<String, String> image_metadata = SiriusUtils.extractImageMetadata(pictureEntity.getFilePath());

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

        return new BaseResponse(ErrorCode.SUCCESS, getAlbumDetailRes);
    }

    public BaseResponse patchAlbum(PatchAlbumReq patchAlbumReq, Integer albumId, Integer missionId) {
        AlbumEntity albumEntity = albumRepository.findByIdAndMissionId(albumId, missionId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        if (patchAlbumReq.getRegdate() != null) {
            albumEntity.setRegdate(LocalDateTime.parse(patchAlbumReq.getRegdate(), formatter));
        } else {
            throw new AppException(ErrorCode.METHOD_NOT_ALLOWED);
        }
        albumRepository.save(albumEntity);
        return new BaseResponse(ErrorCode.ACCEPTED, Integer.valueOf(albumId) + "번 앨범의 값이 변경되었습니다.");
    }

    @Transactional
    public BaseResponse deleteAlbum(Integer albumId, Integer missionId) {
        albumRepository.findByIdAndMissionId(albumId, missionId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        deleteAlbumLogic(albumId);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(albumId) + "번 앨범이 삭제되었습니다.");
    }

    @Transactional
    public void deleteAlbumLogic(Integer albumId) {
        // 사진 있으면 지우기
        Integer deletePictureNum = pictureRepository.deleteByAlbumId(albumId);

        // 분석 결과 있으면 지우기
        Integer deleteSegNum = segmentationRepository.deleteByAlbumId(albumId);

        // 분석 있으면 지우기
        Integer deleteAnalysesNum = analysisRepository.deleteByAlbumId(albumId);

        albumRepository.deleteById(albumId);
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
            LocalTime startTIme = LocalTime.of(time, 0);
            LocalTime endTime = LocalTime.of(23, 59, 59);
            query.where(pictureEntity.time.between(startTIme, endTime));
        }

        List<PictureEntity> results = query.fetch();
        List<GetPictureRes> new_results = results.stream().map(PictureEntity::toDto).collect(Collectors.toList());
        return new BaseResponse(ErrorCode.SUCCESS, new_results);
    }

    public Resource getPictureFileById(Integer pictureId) {
        PictureEntity pictureEntity = pictureRepository.findById(pictureId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND)
        );
        return SiriusUtils.loadFileAsResource(Paths.get(pictureEntity.getFilePath()).getParent().toString(),
                Paths.get(pictureEntity.getFilePath()).getFileName().toString());
    }

    private BaseResponse handleFile(MultipartFile file, Integer missionId, Function<InputStream, ArchiveInputStream> streamCreator) {
        MissionEntity missionEntity = missionRepository.findById(missionId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        String root_path = Paths.get(missionEntity.getMapGroupEntity().getMapEntities().get(1).getMapPath()).getParent().toString().replace("pcd", "inspection_images");
        boolean isFirstEntry = true;
        Integer albumId = null;

        try (ArchiveInputStream imageStream = streamCreator.apply(file.getInputStream())) {
            ArchiveEntry imageEntry;
            AlbumEntity albumEntity;
            AlbumEntity create_albumEntity = null;
            while ((imageEntry = imageStream.getNextEntry()) != null) {

                if (imageEntry.isDirectory()) {
                    throw new AppException(ErrorCode.ZIP_NOT_ALLOWED);
                }

                if (isFirstEntry) {
                    String temp_folder_name = FilenameUtils.removeExtension(imageEntry.getName());
                    root_path = root_path + File.separator + temp_folder_name.split("_")[0] + File.separator + "origin";
                    SiriusUtils.makeFolder(new File(root_path));

                    // albums DB insert
                    albumEntity = AlbumEntity.from(temp_folder_name.split("_")[0] + " " + temp_folder_name.split("_")[1], missionEntity);
                    create_albumEntity = albumRepository.save(albumEntity);
                    albumId = create_albumEntity.getId();
                    isFirstEntry = false;
                }

                File extractedImage = new File(root_path, imageEntry.getName());
                SiriusUtils.saveImageToFile(imageStream,extractedImage);

                // pictures DB insert
                String[] fileInfo = FilenameUtils.removeExtension(imageEntry.getName()).split("_");

                Float posX = Float.valueOf(fileInfo[2]);
                Float posY = Float.valueOf(fileInfo[3]);
                Float posZ = Float.valueOf(fileInfo[4]);

                double[] euler = SiriusUtils.quaternionToEuler(Float.valueOf(fileInfo[5]), Float.valueOf(fileInfo[6]), Float.valueOf(fileInfo[7]), Float.valueOf(fileInfo[8]));
                PictureEntity pictureEntity = PictureEntity.from(root_path+File.separator+imageEntry.getName(), fileInfo[0], fileInfo[1], posX, posY, posZ, euler[0], euler[1], euler[2], create_albumEntity);
                pictureRepository.save(pictureEntity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BaseResponse(ErrorCode.CREATED, albumId);
    }

    public BaseResponse unZip(MultipartFile compressedFile, Integer missionId) {
        return handleFile(compressedFile, missionId, ZipArchiveInputStream::new);
    }

    public BaseResponse
    unTarOrTgzFile(MultipartFile file, Integer missionId) {
        return handleFile(file, missionId, TarArchiveInputStream::new);
    }

    @Transactional
    public ResponseEntity<BaseResponse> deletePicture(Integer pictureId) {
        // 분석결과 있으면 지우기
        PictureEntity pictureEntity = pictureRepository.findById(pictureId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));

        String file_name = Paths.get(pictureEntity.getFilePath()).toString().replace(".JPG",".png");
        file_name = file_name.replace("origin","result/drawImage");

        // 분석 결과 지우기
        segmentationRepository.deleteByFileName(file_name);

        // 사진 지우기
        pictureRepository.delete(pictureEntity);

        return new ResponseEntity<>(new BaseResponse(Integer.valueOf(pictureId)+"번 사진이 삭제되었습니다."), HttpStatus.CREATED);
    }
}
