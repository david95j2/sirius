package com.example.sirius.plan;

import com.example.sirius.exception.BaseResponse;
import com.example.sirius.mapping.domain.PatchMappingReq;
import com.example.sirius.plan.domain.PatchMissionReq;
import com.example.sirius.plan.domain.PostMissionReq;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MissionController {
    private MissionService missionService;

    @GetMapping("api/report/maps/{map_id}/missions")
    public BaseResponse getMissions(@PathVariable Integer map_id, @RequestParam(required = false) Integer group_num) {
        return missionService.getMissions(map_id, group_num);
    }

    @GetMapping("api/report/maps/{map_id}/missions/{mission_id}")
    public BaseResponse getMissionById(@PathVariable Integer map_id, @PathVariable Integer mission_id) {
        return missionService.getMissionById(mission_id,map_id);
    }

    @PostMapping("api/report/maps/{map_id}/missions")
    public BaseResponse postMission(@PathVariable Integer map_id, @Valid @RequestBody PostMissionReq postMissionReq) {
        return missionService.postMission(postMissionReq,map_id);
    }

    @PatchMapping("api/report/maps/{map_id}/missions/{mission_id}")
    public BaseResponse patchMission(@PathVariable Integer map_id, @PathVariable Integer mission_id,
                                     @Valid @RequestBody PatchMissionReq patchMissionReq) {
        return missionService.patchMission(patchMissionReq, mission_id, map_id);
    }

    @DeleteMapping("api/report/maps/{map_id}/missions/{mission_id}")
    public BaseResponse deleteMission(@PathVariable Integer map_id, @PathVariable Integer mission_id) {
        return missionService.deleteMission(mission_id, map_id);
    }

    @PostMapping("api/report/maps/{map_id}/fitting")
    public BaseResponse startFittingProgram(@PathVariable Integer map_id) {
        return missionService.startFittingProgram(map_id);
    }
}
