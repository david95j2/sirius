package com.example.sirius.map;

import com.example.sirius.exception.BaseResponse;
import com.example.sirius.utils.SiriusUtils;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class MapController {
    private MapService mapService;

    @GetMapping("api/report/{login_id}/facilities/{facility_id}/maps")
    public BaseResponse getMaps(@PathVariable String login_id, @PathVariable Integer facility_id,
                                      @RequestParam(required = false) String date, @RequestParam(required = false) Integer time) {
        return mapService.getMaps(facility_id,login_id,date,time);
    }

    @DeleteMapping("api/report/{login_id}/facilities/{facility_id}/maps/{map_id}")
    public BaseResponse deleteMap(@PathVariable String login_id, @PathVariable Integer facility_id,@PathVariable Integer map_id) {
        return mapService.deleteMap(facility_id,map_id);
    }

    @GetMapping("api/report/maps/{map_id}/files")
    public ResponseEntity<InputStreamResource> getMapFileById(@PathVariable Integer map_id) throws IOException {
        Resource file =mapService.getMapFileById(map_id);
        return SiriusUtils.getFile(file, false);
    }


}
