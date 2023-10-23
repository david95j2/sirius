package com.example.sirius.mapping;


import com.example.sirius.exception.BaseResponse;
import com.example.sirius.mapping.domain.PostMappingWaypointReq;
import com.example.sirius.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MappingWaypointController {
    private MappingWaypointService mappingWaypointService;
    private UserService userService;

    @GetMapping("api/users/facilities/mappings/{mapping_id}/waypoints")
    public BaseResponse getMappingWaypoints(@PathVariable Integer mapping_id) {
        return mappingWaypointService.getMappingWaypoints(mapping_id);
    }

    @GetMapping("api/users/facilities/mappings/{mapping_id}/waypoints/{waypoint_id}")
    public BaseResponse getMappingWaypointById(@PathVariable Integer mapping_id, @PathVariable Integer waypoint_id) {
        return mappingWaypointService.getMappingWaypointById(waypoint_id, mapping_id);
    }

    @PostMapping("api/users/facilities/mappings/{mapping_id}/waypoints")
    public BaseResponse postMappingWaypoint(@PathVariable Integer mapping_id,
                                      @Valid @RequestBody PostMappingWaypointReq postMappingWaypointReq) {
        return mappingWaypointService.postMappingWaypoint(postMappingWaypointReq, mapping_id);
    }

    @DeleteMapping("api/users/facilities/mappings/{mapping_id}/waypoints/{waypoint_id}")
    @ResponseBody
    public BaseResponse deleteMappingWayPoint(@PathVariable("mapping_id") Integer mapping_id, @PathVariable("waypoint_id") Integer waypoint_id) {
        return mappingWaypointService.deleteMappingWayPoint(waypoint_id,mapping_id);
    }
}
