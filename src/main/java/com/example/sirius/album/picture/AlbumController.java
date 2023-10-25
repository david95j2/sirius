package com.example.sirius.album.picture;

import com.example.sirius.album.picture.domain.PatchAlbumReq;
import com.example.sirius.album.picture.domain.PostAlbumReq;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.utils.SiriusUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class AlbumController {
    private AlbumService albumService;

    @GetMapping("api/report/maps/missions/{mission_id}/albums")
    public BaseResponse getAlbums(@PathVariable Integer mission_id) {
        return albumService.getAlbums(mission_id);
    }

    @GetMapping("api/report/maps/missions/{mission_id}/albums/{album_id}")
    public BaseResponse getAlbumById(@PathVariable Integer mission_id, @PathVariable Integer album_id) {
        return albumService.getAlbumById(album_id,mission_id);
    }

    @PostMapping("api/report/maps/missions/{mission_id}/albums")
    public BaseResponse postAlbum(@PathVariable Integer mission_id, @Valid @RequestBody PostAlbumReq postAlbumReq) {
        return albumService.postAlbum(postAlbumReq,mission_id);
    }

    @PatchMapping("api/report/maps/missions/{mission_id}/albums/{album_id}")
    public BaseResponse patchAlbum(@PathVariable Integer mission_id, @PathVariable Integer album_id,
                                   @Valid @RequestBody PatchAlbumReq patchAlbumReq) {
        return albumService.patchAlbum(patchAlbumReq,album_id,mission_id);
    }

    @DeleteMapping("api/report/maps/missions/{mission_id}/albums/{album_id}")
    public BaseResponse deleteAlbum(@PathVariable Integer mission_id, @PathVariable Integer album_id) {
        return albumService.deleteAlbum(album_id,mission_id);
    }

    @GetMapping("api/report/maps/missions/albums/{album_id}/pictures")
    public BaseResponse getPictures(@PathVariable Integer album_id,
    @RequestParam(required = false) String date, @RequestParam(required = false) Integer time) {
        return albumService.getPictures(album_id, date, time);
    }

    // URL 수정하자
    @PostMapping("api/report/maps/{map_id}/albums/upload")
    public BaseResponse uploadPictures(@PathVariable Integer map_id,@RequestParam("files") MultipartFile[] files) {
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename().toLowerCase();

            if (!(originalFilename.endsWith(".zip") || originalFilename.endsWith(".tar") || originalFilename.endsWith(".tgz") ||
                    contentType.equalsIgnoreCase("application/zip") || contentType.equalsIgnoreCase("application/x-tar") ||
                    contentType.equalsIgnoreCase("application/x-compressed-tar"))) {
                throw new AppException(ErrorCode.DATA_NOT_ALLOWED);
            }
        }

        return albumService.uploadPictures(files,map_id);
    }

    @GetMapping("api/report/maps/missions/albums/pictures/{picture_id}/files")
    public ResponseEntity<InputStreamResource> getPictureFileById(@PathVariable Integer picture_id) throws IOException {
        Resource file = albumService.getPictureFileById(picture_id);
        return SiriusUtils.getFile(file, false);
    }

    @GetMapping("api/report/maps/missions/albums/pictures/{picture_id}/files/thumbnails")
    public ResponseEntity<InputStreamResource> getPictureThumbnailById(@PathVariable Integer picture_id) throws IOException {
        Resource file = albumService.getPictureFileById(picture_id);
        return SiriusUtils.getFile(file, true);
    }
}
