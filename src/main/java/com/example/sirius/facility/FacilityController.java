package com.example.sirius.facility;

import com.example.sirius.exception.BaseResponse;
import com.example.sirius.facility.domain.PatchFacilityReq;
import com.example.sirius.facility.domain.PostFacilityReq;
import com.example.sirius.map.MapService;
import com.example.sirius.utils.SiriusUtils;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class FacilityController {
    private FacilityService facilityService;
    private MapService mapService;

    @GetMapping("api/users/{login_id}/facilities")
    private BaseResponse getFacilities(@PathVariable String login_id) {
        return facilityService.getFacilities(login_id);
    }

    @GetMapping("api/users/{login_id}/facilities/{facility_id}")
    private BaseResponse getFacilityById(@PathVariable String login_id, @PathVariable Integer facility_id) {
        return facilityService.getFacilityByIdAndLoginId(facility_id,login_id);
    }

    @PostMapping("api/users/{login_id}/facilities")
    private BaseResponse postFacility(@PathVariable String login_id, @Valid @RequestBody PostFacilityReq postFacilityReq) {
        return facilityService.postFacility(postFacilityReq, login_id);
    }

    @PatchMapping("api/users/{login_id}/facilities/{facility_id}")
    private BaseResponse patchFacility(@PathVariable String login_id, @PathVariable Integer facility_id,
                                       @Valid @RequestBody PatchFacilityReq patchFacilityReq) {
        return facilityService.patchFacility(patchFacilityReq,facility_id,login_id,false);
    }

    @DeleteMapping("api/users/{login_id}/facilities/{facility_id}")
    private BaseResponse deleteFacility(@PathVariable String login_id, @PathVariable Integer facility_id) {
        return facilityService.deleteFacility(facility_id, login_id);
    }

    @GetMapping("api/report/{login_id}/facilities/{facility_id}/thumbnails")
    public ResponseEntity<InputStreamResource> getLocationThumbnail(@PathVariable("login_id") String login_id, @PathVariable("facility_id") Integer facility_id) throws IOException {
        Resource file = mapService.getLocationThumbnail(facility_id);
        return SiriusUtils.getFile(file, true, false);
    }
}
