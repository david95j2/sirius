package com.example.sirius.websocket;


import com.example.sirius.album.analysis.AnalysisRepository;
import com.example.sirius.album.analysis.SegmentationRepository;
import com.example.sirius.album.analysis.domain.AnalysisEntity;
import com.example.sirius.album.analysis.domain.SegmentationEntity;
import com.example.sirius.album.picture.AlbumRepository;
import com.example.sirius.album.picture.PictureRepository;
import com.example.sirius.album.picture.domain.AlbumEntity;
import com.example.sirius.album.picture.domain.PictureEntity;
import com.example.sirius.map.MapRepository;
import com.example.sirius.map.domain.MapEntity;
import com.example.sirius.utils.SiriusUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component("segmentationWebSocketHandler")
public class SegmentationWebSocketHandler extends AbstractWebSocketHandler {
    private AnalysisRepository analysisRepository;
    private SegmentationRepository segmentationRepository;
    private PictureRepository pictureRepository;
    private AlbumRepository albumRepository;
    private MapRepository mapRepository;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // client로부터 메세지 받음
        String payload = message.getPayload();
        JSONObject jsonObject = SiriusUtils.validateMessageForAlbumId(payload,session);
        if (jsonObject == null) return;

        // 넘겨받은 albumId에 데이터가 들어있는지 확인
        PageRequest pageRequest = PageRequest.of(0, 1);
        PictureEntity pictureEntity = pictureRepository.findByAlbumIdWhereLimitOne(jsonObject.getInt("albumId"), pageRequest).get(0);

        if (pictureEntity == null) {
            session.sendMessage(new TextMessage("[Error] : Data not found!"));
            return;
        }

        // DB Analyses
        AlbumEntity albumEntity = albumRepository.findById(jsonObject.getInt("albumId")).orElse(null);
        AnalysisEntity already_analysisEntity = analysisRepository.findByAlbumId(jsonObject.getInt("albumId")).orElse(null);
        if (already_analysisEntity == null) { // 분석한적 없음
            already_analysisEntity = AnalysisEntity.from("segmentation",albumEntity);
            analysisRepository.save(already_analysisEntity);
        } else { // 있으면 patch
            already_analysisEntity.setStatus(0);
            LocalDateTime now = LocalDateTime.now();
            already_analysisEntity.setModifyDate(now);
            analysisRepository.save(already_analysisEntity);
        }

        long startTimeSec = System.nanoTime();
        // mmsegmentaiton
        // multi gpu inference
        String pythonPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/venv_seg/bin/torchrun";
        String gpuNum = "--nproc_per_node=6";

        String scriptPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/inferences/inference_and_quantification_mmseg.py";
        List<String> args = Arrays.asList("--config", "/home/sb/Desktop/vsc/0926koceti/20230901_crack/convnext_tiny_fpn_crack.py", "--checkpoint", "/home/sb/Desktop/vsc/0926koceti/20230901_crack/iter_32000.pth",
                "--srx_dir", Paths.get(pictureEntity.getFilePath()).getParent().toString(), "--srx_suffix", "."+FilenameUtils.getExtension(pictureEntity.getFilePath()));
        SiriusUtils.executePythonScript(pythonPath, scriptPath, args, "mmseg", gpuNum);
        // multi gpu inference

        // single gpu inference
//        String pythonPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/venv_seg/bin/python3";
//        String scriptPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/inferences/inference_and_quantification_mmseg_single.py";
//        List<String> args = Arrays.asList("--config", "/home/sb/Desktop/vsc/0926koceti/20230901_crack/convnext_tiny_fpn_crack.py", "--checkpoint", "/home/sb/Desktop/vsc/0926koceti/20230901_crack/iter_32000.pth",
//                "--srx_dir", Paths.get(pictureEntity.getFilePath()).getParent().toString(), "--srx_suffix", "."+FilenameUtils.getExtension(pictureEntity.getFilePath()));
//        SiriusUtils.executePythonScript(pythonPath, scriptPath, args, "mmseg", null);
        // single gpu inference

        long endTimeSec = System.nanoTime();
        long timeElapsedSec = endTimeSec - startTimeSec;
        double timeInSecondSec = (double)timeElapsedSec / 1_000_000_000;
        System.out.println("python inference 실행 시간 : "+timeInSecondSec + "초");

        // Run Calculate Distance
        long startTimeFifth = System.nanoTime();

        String secondScriptPath = "/home/sb/workspace/calc_dis/build/calcDistance";
        MapEntity mapEntity = mapRepository.findByAlbumIdAndFileName(albumEntity.getId(),"GlobalMap.pcd").orElse(null);
        List<String> secondArgs = Arrays.asList("0",Paths.get(pictureEntity.getFilePath()).getParent().toString().replace("/origin",""), mapEntity.getMapPath());
        SiriusUtils.executePythonScript(null, secondScriptPath, secondArgs, secondScriptPath.split("/")[secondScriptPath.split("/").length - 1], null);
        long endTimeFifth = System.nanoTime();
        long timeElapsedFifth = endTimeFifth - startTimeFifth;
        double timeInSecondFifth = (double) timeElapsedFifth / 1_000_000_000;
        System.out.println("CalDistance 실행 시간 : "+timeInSecondFifth + "초");


        // Run visualizer.py
//        pythonPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/venv_seg/bin/python3";
//
//        long startTimeThird = System.nanoTime();
//        String anotherScriptPath = "/home/sb/Desktop/vsc/0926koceti/analyzer_cracks/visualizer.py";
//        List<String> anotherArgs = Arrays.asList("--folder_path", Paths.get(FilenameUtils.removeExtension(pictureEntity.getFilePath())).getParent().toString());
//        System.out.println(anotherArgs);
//        SiriusUtils.executePythonScript(pythonPath, anotherScriptPath, anotherArgs, anotherScriptPath.split("/")[anotherScriptPath.split("/").length - 1],null);
//        long endTimeThird = System.nanoTime();
//
//        long timeElapsedThird = endTimeThird - startTimeThird;
//        double timeInSecondThird = (double)timeElapsedThird / 1_000_000_000;
//        System.out.println("Visualize 실행 시간 : " + timeInSecondThird + "초");

        // analyses db update
        long startTimeForth = System.nanoTime();
        already_analysisEntity.setStatus(1);
        analysisRepository.save(already_analysisEntity);

        // segmentations db update
        AnalysisEntity final_analysisEntity = already_analysisEntity;
        List<String> fileList = SiriusUtils.listFilesInDirectoryNIO(Paths.get(FilenameUtils.removeExtension(pictureEntity.getFilePath())).getParent().toString());
        fileList.stream().map(x -> {
            String originPath = Paths.get(FilenameUtils.removeExtension(pictureEntity.getFilePath())).getParent().toString();
            String originName = FilenameUtils.removeExtension(x);
            String jsonPattern = String.format("%s%s_.*\\.json", originPath.replace("origin", "result/json/"), originName);
            String jsonName = "";

            try {
                List<String> matchedJsonFiles = SiriusUtils.listFilesMatchingPattern(originPath.replace("origin", "result/json/"), jsonPattern);
                if (matchedJsonFiles.size() > 0) {
                    if (matchedJsonFiles.size() > 1){
                        jsonName = originPath.replace("origin","result/json/")+matchedJsonFiles.get(1);
                    } else {
                        jsonName = originPath.replace("origin","result/json/")+matchedJsonFiles.get(0);
                    }
//                    jsonName = originPath.replace("origin","result/json/")+matchedJsonFiles.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

//            String drawName = originPath.replace("origin","result/drawImage/")+originName+".png";
//            String maskName = originPath.replace("origin","result/maskImage/")+originName+".png";

            SegmentationEntity segmentationEntity = SegmentationEntity.from(jsonName,final_analysisEntity);
            segmentationRepository.save(segmentationEntity);

            return null;
        }).collect(Collectors.toList());

        long endTimeForth = System.nanoTime();

        long timeElapsedForth = endTimeForth - startTimeForth;
        double timeInSecondForth = (double)timeElapsedForth / 1_000_000_000;
        System.out.println("DB Upload 실행 시간 : " + timeInSecondForth + "초");

        super.handleTextMessage(session, new TextMessage("[Message] Success"));
    }
}
