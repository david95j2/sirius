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

    @GetMapping("api/users/{login_id}/missions/{mission_id}/waypoints")
    public BaseResponse getMappingWaypoints(@PathVariable String login_id, @PathVariable Integer mission_id) {
        userService.getUserByLoginId(login_id);
        return mappingWaypointService.getMappingWaypoints(mission_id);
    }

    @GetMapping("api/users/{login_id}/missions/{mission_id}/waypoints/{waypoint_id}")
    public BaseResponse getMappingWaypointById(@PathVariable String login_id, @PathVariable Integer mission_id,
                                     @PathVariable Integer waypoint_id) {
        userService.getUserByLoginId(login_id);
        return mappingWaypointService.getMappingWaypointById(waypoint_id, mission_id);
    }

    @PostMapping("api/users/{login_id}/missions/{mission_id}/waypoints")
    public BaseResponse postMappingWaypoint(@PathVariable String login_id, @PathVariable Integer mission_id,
                                      @Valid @RequestBody PostMappingWaypointReq postMappingWaypointReq) {
        userService.getUserByLoginId(login_id);
        return mappingWaypointService.postMappingWaypoint(postMappingWaypointReq, mission_id);
    }

    @DeleteMapping("api/users/{login_id}/missions/{mission_id}/waypoints/{waypoint_id}")
    @ResponseBody
    public BaseResponse deleteMappingWayPoint(@PathVariable("login_id") String login_id,@PathVariable("mission_id") Integer mission_id,
                                              @PathVariable("waypoint_id") Integer waypoint_id) {
        userService.getUserByLoginId(login_id);
        return mappingWaypointService.deleteMappingWayPoint(waypoint_id,mission_id);
    }
}
