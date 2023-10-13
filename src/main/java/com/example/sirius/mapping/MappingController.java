package com.example.sirius.mapping;


import com.example.sirius.exception.BaseResponse;
import com.example.sirius.mapping.domain.PatchMappingReq;
import com.example.sirius.mapping.domain.PostMappingReq;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MappingController {
    private MappingService mappingService;

    @GetMapping("api/users/facilities/{facility_id}/mappings")
    public BaseResponse getMappings(@PathVariable Integer facility_id) {
        return mappingService.getMappings(facility_id);
    }

    @GetMapping("api/users/facilities/{facility_id}/mappings/{mapping_id}")
    public BaseResponse getMappingById(@PathVariable Integer facility_id,@PathVariable Integer mapping_id) {
        return mappingService.getMappingByIdAndFacilityId(mapping_id,facility_id);
    }

    @PostMapping("api/users/facilities/{facility_id}/mappings")
    public BaseResponse postMapping(@PathVariable Integer facility_id,@Valid @RequestBody PostMappingReq postMappingReq) {
        return mappingService.postMapping(postMappingReq,facility_id);
    }

    @PatchMapping("api/users/facilities/{facility_id}/mappings/{mapping_id}")
    public BaseResponse patchMapping(@PathVariable Integer facility_id,@PathVariable Integer mapping_id,
                                      @Valid @RequestBody PatchMappingReq patchMappingReq) {
        return mappingService.patchMapping(patchMappingReq,mapping_id,facility_id);
    }

    @DeleteMapping("api/users/facilities/{facility_id}/mappings/{mapping_id}")
    public BaseResponse deleteMapping(@PathVariable Integer facility_id,@PathVariable Integer mapping_id) {
        return mappingService.deleteMapping(mapping_id,facility_id);
    }
}
