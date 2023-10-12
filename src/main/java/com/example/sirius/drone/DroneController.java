package com.example.sirius.drone;


import com.example.sirius.drone.domain.PatchDroneReq;
import com.example.sirius.drone.domain.PostDroneReq;
import com.example.sirius.exception.BaseResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class DroneController {
    private DroneService droneService;
    @GetMapping("api/users/{login_id}/drones")
    public BaseResponse getDrones(@PathVariable String login_id) {
        return droneService.getDrones(login_id);
    }

    @GetMapping("api/users/{login_id}/drones/{drone_id}")
    public BaseResponse getDroneByLoginId(@PathVariable String login_id,@PathVariable Integer drone_id) {
        return droneService.getDroneById(drone_id,login_id);
    }

    @PostMapping("api/users/{login_id}/drones")
    public BaseResponse postDrone(@PathVariable String login_id, @Valid @RequestBody PostDroneReq postDroneReq) {
        return droneService.postDrone(postDroneReq,login_id);
    }

    @PatchMapping("api/users/{login_id}/drones/{drone_id}")
    public BaseResponse patchDroneById(@PathVariable String login_id,@PathVariable Integer drone_id,
                                  @Valid @RequestBody PatchDroneReq patchDroneReq) {
        return droneService.patchDroneById(patchDroneReq,drone_id,login_id);
    }

    @DeleteMapping("api/users/{login_id}/drones/{drone_id}")
    public BaseResponse deleteDroneById(@PathVariable String login_id,@PathVariable Integer drone_id) {
        return droneService.deleteDroneById(drone_id,login_id);
    }
}
