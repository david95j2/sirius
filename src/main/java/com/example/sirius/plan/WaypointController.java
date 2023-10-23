package com.example.sirius.plan;


import com.example.sirius.exception.BaseResponse;
import com.example.sirius.mapping.domain.PostMappingWaypointReq;
import com.example.sirius.plan.domain.PostWaypointReq;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class WaypointController {
    private WaypointService waypointService;

    @GetMapping("api/report/maps/missions/shapes/{shape_id}/waypoints")
    public BaseResponse getWaypoints(@PathVariable Integer shape_id) {
        return waypointService.getWaypoints(shape_id);
    }

    @GetMapping("api/report/maps/missions/shapes/{shape_id}/waypoints/{waypoint_id}")
    public BaseResponse getWaypointById(@PathVariable Integer shape_id,@PathVariable Integer waypoint_id) {
        return waypointService.getWaypointById(waypoint_id, shape_id);
    }

    @PostMapping("api/report/maps/missions/shapes/{shape_id}/waypoints")
    public BaseResponse postWaypoint(@PathVariable Integer shape_id,@Valid @RequestBody PostWaypointReq postWaypointReq) {
        return waypointService.postWaypoint(postWaypointReq, shape_id);
    }

    @DeleteMapping("api/report/maps/missions/shapes/{shape_id}/waypoints/{waypoint_id}")
    @ResponseBody
    public BaseResponse deleteWayPoint(@PathVariable("mission_id") Integer shape_id,@PathVariable("waypoint_id") Integer waypoint_id) {
        return waypointService.deleteWayPoint(waypoint_id,shape_id);
    }
}
