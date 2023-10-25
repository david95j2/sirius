package com.example.sirius.album.analysis;

import com.example.sirius.album.analysis.domain.AnalysisEntity;
import com.example.sirius.album.analysis.domain.PostAnalysisReq;
import com.example.sirius.album.analysis.domain.SegmentationEntity;
import com.example.sirius.album.picture.AlbumRepository;
import com.example.sirius.album.picture.AlbumService;
import com.example.sirius.album.picture.domain.AlbumEntity;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.utils.SiriusUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
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

        if (postAnalysisReq.getAi_type().equals("segmentation")) {
            // segmentation 분석 시작
            String venvPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/venv_seg/bin/python";
            String pythonPath = "";


            ExecutorService localExecutorService = Executors.newSingleThreadExecutor();
            localExecutorService.execute(() -> {
                // 비동기
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(venvPath);

                try {
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();
                    log.info("[Python Program] Exited with code: " + exitCode);

                } catch (IOException | InterruptedException e) {
                    log.error("[Python Program] Error occurred while executing external process",e);
                }
            });
            localExecutorService.shutdown();

            // 분석 완료
            AnalysisEntity createdAnalysisEntity = analysisRepository.findById(createdNum).orElse(null);
            if (createdAnalysisEntity == null) {
                log.warn("Attempted analysis for album {}, but couldn't find analysis table number {}", albumId, createdNum);
            } else {
                patchAnalysis(createdAnalysisEntity);
            }
        } else {
            // detection 분석 시작
            // 분석 완료
        }

        return new BaseResponse(ErrorCode.CREATED, Integer.valueOf(albumId)+"번 앨범 분석을 시작합니다. 분석 id="+Integer.valueOf(createdNum));
    }

    private void patchAnalysis(AnalysisEntity analysisEntity) {
        analysisEntity.setStatus(1);
        analysisRepository.save(analysisEntity);
    }

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

    public Resource getSegmentationFile(Integer segmentationId, String type) {
        SegmentationEntity segmentationEntity = segmentationRepository.findSegBySegId(segmentationId).orElse(null);
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
}
