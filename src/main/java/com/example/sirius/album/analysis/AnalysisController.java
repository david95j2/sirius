package com.example.sirius.album.analysis;

import com.example.sirius.album.analysis.domain.PostAnalysisReq;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.utils.SiriusUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public ResponseEntity<InputStreamResource> getSegmentationFile(@PathVariable Integer picture_id, @RequestParam(required = false) String type) throws IOException {
        Resource file =  analysisService.getSegmentationFile(picture_id,type);
        return SiriusUtils.getFile(file, false);
    }
}
