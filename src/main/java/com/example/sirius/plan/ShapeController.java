package com.example.sirius.plan;

import com.example.sirius.exception.BaseResponse;
import com.example.sirius.plan.domain.PatchShapeAndPropertyReq;
import com.example.sirius.plan.domain.PostShapeAndPropertyReq;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ShapeController {
    private ShapeService shapeService;

    @GetMapping("api/report/maps/missions/{mission_id}/shapes")
    public BaseResponse getShapes(@PathVariable Integer mission_id) {
        return shapeService.getShapes(mission_id);
    }

    @GetMapping("api/report/maps/missions/{mission_id}/shapes/{shape_id}")
    public BaseResponse getShapeById(@PathVariable Integer mission_id,@PathVariable Integer shape_id) {
        return shapeService.getShapeById(shape_id, mission_id);
    }

    @PostMapping("api/report/maps/missions/{mission_id}/shapes")
    public BaseResponse postShape(@PathVariable Integer mission_id, @Valid @RequestBody PostShapeAndPropertyReq postShapeAndPropertyReq) {
        return shapeService.postShape(postShapeAndPropertyReq, mission_id);
    }

    @PatchMapping("api/report/maps/missions/{mission_id}/shapes/{shape_id}")
    public BaseResponse patchShape(@PathVariable Integer mission_id, @PathVariable Integer shape_id,
                                   @Valid @RequestBody PatchShapeAndPropertyReq patchShapeAndPropertyReq) {
        return shapeService.patchShape(patchShapeAndPropertyReq, shape_id, mission_id);
    }

    @DeleteMapping("api/report/maps/missions/{mission_id}/shapes/{shape_id}")
    public BaseResponse deleteShape(@PathVariable Integer mission_id, @PathVariable Integer shape_id) {
        return shapeService.deleteShape(shape_id, mission_id);
    }
}
