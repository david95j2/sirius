package com.example.sirius.ftp;


import com.example.sirius.configuration.WebConfiguration;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.facility.FacilityRepository;
import com.example.sirius.facility.FacilityService;
import com.example.sirius.facility.domain.FacilityEntity;
import com.example.sirius.facility.domain.PostThumbnails;
import com.example.sirius.ftp.domain.FtpInfo;
import com.example.sirius.ftp.domain.PostMapURL;
import com.example.sirius.ftp.domain.PostMapURLSuccess;
import com.example.sirius.map.MapService;
import com.example.sirius.map.domain.PostMapReq;
import com.example.sirius.utils.SiriusUtils;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;


@Service
@AllArgsConstructor
public class FtpService {
    private WebConfiguration webConfiguration;
    private MapService mapService;
    private FacilityService facilityService;
    private FacilityRepository facilityRepository;

    public BaseResponse getFtpInfo(String loginId) {
        // loginId 임시 제거
        FtpInfo ftpInfo = new FtpInfo();
        ftpInfo.setFtpIp(webConfiguration.getFtpIp());
        ftpInfo.setFtpPort(webConfiguration.getFtpPort());
        ftpInfo.setFtpId(webConfiguration.getFtpId());
        ftpInfo.setFtpPassword(webConfiguration.getFtpPassword());
        return new BaseResponse(ErrorCode.SUCCESS, ftpInfo);
    }

    public BaseResponse postMapURL(PostMapURL postMapURL, String loginId) {
        return new BaseResponse(ErrorCode.CREATED, loginId + "/" + SiriusUtils.stringToUnicode(postMapURL.getLocation()).replace("\\","") + "/" + postMapURL.getRegdate().split("_")[0] + "/" + postMapURL.getRegdate().split("_")[1]);
    }

    public BaseResponse postMapURLSuccess(PostMapURLSuccess postMapURLSuccess, String loginId) {
        /* 사용자가 제공한 정보와 실제 파일이 있는지 검사 */
        String os_path = Paths.get("/hdd_ext/part6", "sirius").toString();
        Path root_path = Paths.get(os_path, loginId, SiriusUtils.stringToUnicode(postMapURLSuccess.getLocation()).replace("\\",""),
                postMapURLSuccess.getRegdate().split("_")[0],
                postMapURLSuccess.getRegdate().split("_")[1], "pcd");
        Integer location_id = null;
        Integer thumbnail_num = 0;
        Integer map_num = 0;

        JSONObject infoMap = SiriusUtils.convertTxtToJson(Paths.get(root_path.toString(), "infoMap.txt").toString());

        // thumbnails 파일 있는지 확인
        File thumbnails_folder = new File(Paths.get(root_path.toString(), "sample").toString());
        if (thumbnails_folder.isDirectory()) {
            File[] files = thumbnails_folder.listFiles();
            if (files.length >= 1) {
                /* location & thumbnails post */
                // facility 조회
//                PatchFacilityRes patchFacilityRes = (PatchFacilityRes) facilityService.postFacility(PostFacilityReq.fromJSONObject(infoMap), loginId).getResult();
                FacilityEntity facilityEntity = facilityRepository.findByLocation(postMapURLSuccess.getLocation()).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
                location_id = facilityEntity.getId();
                for (File file : files) {
                    // thumbnails 생성
                    PostThumbnails postThumbnails = new PostThumbnails();
                    postThumbnails.setFile_path(file.getPath());
                    String thumb_datetime = file.getName().split("_")[0] + "_" + file.getName().split("_")[1];
                    postThumbnails.setRegdate(thumb_datetime);
                    facilityService.postFacilityThumbnails(postThumbnails, location_id);
                    thumbnail_num += 1;
                }
            }
        }

        // map 파일 3개 있는지 확인(SurfMap, CornerMap, GlobalMap)
        File folder = new File(root_path.toString());
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            List<String> fileNameList = Arrays.asList("GlobalMap.pcd", "CornerMap.pcd", "SurfMap.pcd");
            Boolean postGroup = true;
            Integer mapGroupId = null;

            // map 뒤져서 해당 날짜에 있는지 확인하고 없으면 map_group 생성
            Boolean exist = mapService.getMapsByLocationIdAndDate(location_id, postMapURLSuccess.getRegdate());

            for (File file : files) {
                if (!file.isDirectory() && fileNameList.contains(file.getName())) {
                    /* map_groups & maps post */
                    if (exist) {
                        // 해당날짜에 이미 맵이 있다...?
                        throw new AppException(ErrorCode.DUPLICATED_MAP_DATA);
                    } else { // 해당날짜는 처음 들어온 값이다.
                        if (postGroup) {
                            // map_groups 생성
                            mapGroupId = mapService.postMapGroup(location_id);
                            postGroup = false;
                        }
                        // maps 생성
                        PostMapReq postMapReq = new PostMapReq();
                        postMapReq.setFile_path(file.getPath());
                        postMapReq.setDate(LocalDate.parse(postMapURLSuccess.getRegdate().split("_")[0], DateTimeFormatter.ofPattern("yyyyMMdd")));
                        postMapReq.setTime(LocalTime.parse(postMapURLSuccess.getRegdate().split("_")[1], DateTimeFormatter.ofPattern("HHmmss")));
                        postMapReq.setMap_count((Integer) infoMap.get("number of pointcloud"));
                        postMapReq.setMap_area((Float) infoMap.get("map size"));
                        mapService.postMaps(postMapReq, mapGroupId, location_id);
                        map_num += 1;
                    }
                }
            }
        }

        return new BaseResponse(ErrorCode.CREATED,Integer.valueOf(thumbnail_num)+"개의 썸네일 사진과 "+Integer.valueOf(map_num)
        +"개의 맵파일을 생성하였습니다.");
    }
}
