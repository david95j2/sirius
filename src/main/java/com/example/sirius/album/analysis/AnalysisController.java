package com.example.sirius.album.analysis;

import com.example.sirius.album.analysis.domain.JsonModel;
import com.example.sirius.album.analysis.domain.PatchAnalysisReq;
import com.example.sirius.album.analysis.domain.PostAnalysisReq;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.utils.SiriusUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@RestController
@AllArgsConstructor
public class AnalysisController {
    private AnalysisService analysisService;

    @GetMapping("api/report/maps/missions/albums/{album_id}/analyses")
    public BaseResponse getAnalyses(@PathVariable Integer album_id) {
        return analysisService.getAnalyses(album_id);
    }
    @GetMapping("api/report/maps/missions/albums/{album_id}/analyses/{analysis_id}")
    public BaseResponse getAnalysisById(@PathVariable Integer album_id,@PathVariable Integer analysis_id) {
        return analysisService.getAnalysisById(analysis_id, album_id);
    }

    @PostMapping("api/report/maps/missions/albums/{album_id}/analyses")
    public BaseResponse postAnalysis(@PathVariable Integer album_id, @Valid @RequestBody PostAnalysisReq postAnalysisReq) {
        return analysisService.postAnalysis(postAnalysisReq,album_id);
    }

    @PatchMapping("api/report/maps/missions/albums/{album_id}/analyses/{analysis_id}")
    public BaseResponse patchAnalysisById(@PathVariable Integer album_id,@PathVariable Integer analysis_id, @Valid @RequestBody PatchAnalysisReq patchAnalysisReq) {
        return analysisService.patchAnalysisById(patchAnalysisReq,album_id,analysis_id);
    }

    @DeleteMapping("api/report/maps/missions/albums/{album_id}/analyses/{analysis_id}")
    public BaseResponse deleteAnalysis(@PathVariable Integer album_id, @PathVariable Integer analysis_id) {
        return analysisService.deleteAnalysis(analysis_id,album_id);
    }

    @GetMapping("api/report/maps/missions/albums/analyses/{analysis_id}/segmentations")
    public BaseResponse getSegmentations(@PathVariable Integer analysis_id) {
        return analysisService.getSegmentations(analysis_id);
    }

    @GetMapping("api/report/maps/missions/albums/analyses/{analysis_id}/segmentations/{segmentation_id}")
    public BaseResponse getSegmentation(@PathVariable Integer analysis_id, @PathVariable Integer segmentation_id) {
        return analysisService.getSegmentation(analysis_id,segmentation_id);
    }

    @GetMapping("api/report/maps/missions/albums/analyses/segmentations/{picture_id}/files")
    public ResponseEntity<InputStreamResource> getSegmentationFile(@PathVariable Integer picture_id, @RequestParam(required = false) String type, @RequestParam(required = false) Boolean resize) throws Exception {
        Resource file =  analysisService.getSegmentationFile(picture_id,type);
        if (resize == null) {
            resize = false;
        }
        return SiriusUtils.resizeImageWithMetadataAndSend(file,resize);
//        return SiriusUtils.getFile(file, false, resize);
    }

    // Web GCS로부터 json 받아서 기존 json 위치에 저장
    @PostMapping("api/report/maps/missions/albums/analyses/segmentations/{picture_id}/files/json")
    public ResponseEntity<?> modifySegmentation(@PathVariable Integer picture_id,
                                           @Valid @RequestBody ArrayList<JsonModel> jsonModel) {
        analysisService.modifySegmentation(jsonModel,picture_id);
        return ResponseEntity.ok().body("Success");
    }
}
