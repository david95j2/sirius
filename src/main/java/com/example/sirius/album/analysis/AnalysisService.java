package com.example.sirius.album.analysis;

import com.example.sirius.album.analysis.domain.*;
import com.example.sirius.album.picture.AlbumRepository;
import com.example.sirius.album.picture.AlbumService;
import com.example.sirius.album.picture.PictureRepository;
import com.example.sirius.album.picture.domain.AlbumEntity;
import com.example.sirius.album.picture.domain.PictureEntity;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.map.domain.MapEntity;
import com.example.sirius.utils.SiriusUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AnalysisService {
    private AnalysisRepository analysisRepository;
    private SegmentationRepository segmentationRepository;
    private AlbumRepository albumRepository;
    private PictureRepository pictureRepository;
    private AlbumService albumService;

    public BaseResponse getAnalyses(Integer albumId) {
        List<AnalysisEntity> results = analysisRepository.findAllByAlbumId(albumId);
        return new BaseResponse(ErrorCode.SUCCESS, results.stream().map(AnalysisEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getAnalysisById(Integer analysisId, Integer albumId) {
        AnalysisEntity analysisEntity = analysisRepository.findByIdAndAlbumId(analysisId,albumId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        return new BaseResponse(ErrorCode.SUCCESS,analysisEntity.toDto());
    }

    public BaseResponse postAnalysis(PostAnalysisReq postAnalysisReq, Integer albumId) {
        AlbumEntity albumEntity = albumRepository.findById(albumId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        AnalysisEntity analysisEntity = AnalysisEntity.from(postAnalysisReq,albumEntity);
        Integer createdNum = analysisRepository.save(analysisEntity).getId();

        if (postAnalysisReq.getAi_type().equals("itwin")) {
            return new BaseResponse(ErrorCode.CREATED,Integer.valueOf(albumId)+"번 앨범 분석을 시작합니다. 분석 id="+Integer.valueOf(createdNum)+","+"album_path="+albumService.getAlbumPathById(albumId));
        } else {
            return new BaseResponse(ErrorCode.CREATED,Integer.valueOf(albumId)+"번 앨범 분석을 시작합니다. 분석 id="+Integer.valueOf(createdNum));
        }
    }

    public BaseResponse patchAnalysisById(PatchAnalysisReq patchAnalysisReq, Integer albumId, Integer analysisId) {
        AnalysisEntity analysisEntity = analysisRepository.findByIdAndAlbumId(analysisId,albumId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        analysisEntity.setStatus(patchAnalysisReq.getStatus());
        analysisRepository.save(analysisEntity);
        return new BaseResponse(ErrorCode.ACCEPTED,analysisEntity.toDto());
    }


//    public BaseResponse postAnalysis(PostAnalysisReq postAnalysisReq, Integer albumId) {
//
//        AlbumEntity albumEntity = albumRepository.findById(albumId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
//        AnalysisEntity analysisEntity = AnalysisEntity.from(postAnalysisReq,albumEntity);
//        Integer createdNum = analysisRepository.save(analysisEntity).getId();
//
//        if (postAnalysisReq.getAi_type().equals("segmentation")) {
//            // segmentation 분석 시작
//            String venvPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/venv_seg/bin/python";
//            String pythonPath = "";
//
//            ExecutorService localExecutorService = Executors.newSingleThreadExecutor();
//            localExecutorService.execute(() -> {
//                ProcessBuilder processBuilder = new ProcessBuilder();
//                processBuilder.command(venvPath);
//
//                try {
//                    Process process = processBuilder.start();
//                    int exitCode = process.waitFor();
//                    log.info("[Python Program] Exited with code: " + exitCode);
//
//                } catch (IOException | InterruptedException e) {
//                    log.error("[Python Program] Error occurred while executing external process",e);
//                }
//            });
//            localExecutorService.shutdown();
//
//            // 분석 완료
//            AnalysisEntity createdAnalysisEntity = analysisRepository.findById(createdNum).orElse(null);
//            if (createdAnalysisEntity == null) {
//                log.warn("Attempted analysis for album {}, but couldn't find analysis table number {}", albumId, createdNum);
//            } else {
//                patchAnalysis(createdAnalysisEntity);
//            }
//        } else {
//            // detection 분석 시작
//            // 분석 완료
//        }
//
//        return new BaseResponse(ErrorCode.CREATED, Integer.valueOf(albumId)+"번 앨범 분석을 시작합니다. 분석 id="+Integer.valueOf(createdNum));
//    }

//    private void patchAnalysis(AnalysisEntity analysisEntity) {
//        analysisEntity.setStatus(1);
//        analysisRepository.save(analysisEntity);
//    }

    public BaseResponse deleteAnalysis(Integer analysisId, Integer albumId) {
        AnalysisEntity analysisEntity = analysisRepository.findByIdAndAlbumId(analysisId, albumId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        analysisRepository.delete(analysisEntity);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(analysisId)+"번 분석을 삭제합니다.");
    }

    public BaseResponse getSegmentations(Integer analysisId) {
        List<SegmentationEntity> results = segmentationRepository.findSegAllById(analysisId);
        return new BaseResponse(ErrorCode.SUCCESS, results.stream().map(SegmentationEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getSegmentation(Integer analysisId, Integer segmentationId) {
        SegmentationEntity segmentationEntity = segmentationRepository.findSegByIdAndSegId(analysisId, segmentationId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        return new BaseResponse(ErrorCode.SUCCESS, segmentationEntity.toDto());
    }

    public Resource getSegmentationFile(Integer pictureId, String type) {
        PictureEntity pictureEntity = pictureRepository.findById(pictureId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));

        String pattern = createSearchPattern(pictureEntity.getFilePath());
//        SegmentationEntity segmentationEntity = segmentationRepository.findByFileName(origin_file_name).orElse(null); // origin
        System.out.println(pattern);
        List<SegmentationEntity> results = segmentationRepository.findBySimilarPathPattern(pattern);
        SegmentationEntity segmentationEntity = results.get(0);
        if (segmentationEntity == null) {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }

        if (type == null) {
            return SiriusUtils.loadFileAsResource(Paths.get(segmentationEntity.getDrawFilePath()).getParent().toString(),
                    Paths.get(segmentationEntity.getDrawFilePath()).getFileName().toString());
        } else if (type.equals("mask")) {
            return SiriusUtils.loadFileAsResource(Paths.get(segmentationEntity.getMaskFilePath()).getParent().toString(),
                    Paths.get(segmentationEntity.getMaskFilePath()).getFileName().toString());
        } else if (type.equals("json")) {
            return SiriusUtils.loadFileAsResource(Paths.get(segmentationEntity.getJsonFilePath()).getParent().toString(),
                    Paths.get(segmentationEntity.getJsonFilePath()).getFileName().toString());
        }

        return null;
    }

    private String createSearchPattern(String originImageName) {
        // "origin" 부분을 "result/json"으로 변경
        String resultPathPattern = originImageName.replaceFirst("origin", "result/json");
        // 파일 확장자 전에 오는 숫자 부분을 와일드카드로 대체 (예: _45_31.json -> _%.json)
        resultPathPattern = resultPathPattern.replaceAll("_\\d+_\\d+\\.json$", "_%.json");
        // 확장자가 .JPG인 경우, 파일 이름 바로 전의 숫자들을 와일드카드로 대체
        resultPathPattern = resultPathPattern.replaceAll("\\.JPG$", "_%.json");
        return resultPathPattern;
    }

    public void modifySegmentation(ArrayList<JsonModel> jsonModel, Integer pictureId) {
        PictureEntity pictureEntity = pictureRepository.findById(pictureId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        MapEntity mapEntity = pictureEntity.getAlbumEntity().getMissionEntity().getMapGroupEntity().getMapEntities().get(0);
        String pattern = createSearchPattern(pictureEntity.getFilePath());
        List<SegmentationEntity> results = segmentationRepository.findBySimilarPathPattern(pattern);
        SegmentationEntity segmentationEntity = results.get(0);
        if (segmentationEntity == null) {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }

        String jsonFileName = segmentationEntity.getJsonFilePath();
        SiriusUtils.saveFile(jsonFileName, jsonModel);

        // c++ 분석
        ExecutorService localExecutorService = Executors.newSingleThreadExecutor();
        localExecutorService.execute(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("/home/sb/workspace/calc_dis/build/calcDistance",
                    String.valueOf(1),
                    pictureEntity.getFilePath(),
                    mapEntity.getMapPath()
                    );

            try {
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                log.info("[calcDistan Program Modify] Exited with code: " + exitCode);

            } catch (IOException | InterruptedException e) {
                log.error("[calcDistan Program Modify] Error occurred while executing external process",e);
            }
        });
        localExecutorService.shutdown();
    }
}
