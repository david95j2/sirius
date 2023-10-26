package com.example.sirius.websocket.domain;


import com.example.sirius.album.analysis.AnalysisRepository;
import com.example.sirius.album.analysis.SegmentationRepository;
import com.example.sirius.album.analysis.domain.SegmentationEntity;
import com.example.sirius.album.picture.PictureRepository;
import com.example.sirius.album.picture.domain.PictureEntity;
import com.example.sirius.utils.SiriusUtils;
import com.example.sirius.websocket.AbstractWebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("analysesWebSocketHandler")
@AllArgsConstructor
@Slf4j
public class AnalysesWebSocketHandler extends AbstractWebSocketHandler {
    private SegmentationRepository segmentationRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Analyses 관련 로직
        String payload = message.getPayload();
        JSONObject jsonObject = SiriusUtils.validateMessageForRemoveIdAndMaskId(payload,session);
        if (jsonObject == null) return;

        JSONArray removeIdArray = jsonObject.getJSONArray("remove_id");
        String removeIdString = SiriusUtils.joinArrayWithComma(removeIdArray);

        SegmentationEntity segmentationEntity = segmentationRepository.findSegBySegId(jsonObject.getInt("maskId")).orElse(null);
        if (segmentationEntity == null) {
            session.sendMessage(new TextMessage("[Error] : Data not found!"));
            return;
        }

        String pythonPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/venv_seg/bin/python3";

        // Run modifier.py
        String scriptPath = "/home/sb/Desktop/vsc/0926koceti/analyzer_cracks/modifier.py";
        List<String> args = Arrays.asList("--remove_id", removeIdString, "--mask_path", segmentationEntity.getMaskFilePath());
        String modifyResult = SiriusUtils.executePythonScript(pythonPath, scriptPath, args,scriptPath.split("/")[scriptPath.split("/").length - 1], null);

        segmentationEntity.setJsonFilePath(modifyResult.split(" ")[1]);
        segmentationRepository.save(segmentationEntity);

        // Run visualizer.py
        String anotherScriptPath = "/home/sb/Desktop/vsc/0926koceti/analyzer_cracks/visualizer.py";
        List<String> anotherArgs = Arrays.asList("--folder_path", FilenameUtils.removeExtension(segmentationEntity.getMaskFilePath()).replace("result/maskImage","origin")+".JPG");
        SiriusUtils.executePythonScript(pythonPath, anotherScriptPath, anotherArgs, anotherScriptPath.split("/")[anotherScriptPath.split("/").length - 1],null);

        super.handleTextMessage(session, new TextMessage("[Message] Success"));
    }


}
