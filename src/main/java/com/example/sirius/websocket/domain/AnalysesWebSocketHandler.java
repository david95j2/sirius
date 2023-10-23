package com.example.sirius.websocket.domain;


import com.example.sirius.album.analysis.AnalysisRepository;
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
    private AnalysisRepository analysisRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Analyses 관련 로직
        String payload = message.getPayload();
        JSONObject jsonObject = new JSONObject(payload);
        JSONArray removeIdArray = jsonObject.getJSONArray("remove_id");
        String removeIdString = SiriusUtils.joinArrayWithComma(removeIdArray);

        SegmentationEntity segmentationEntity = analysisRepository.findSegBySegId(jsonObject.getInt("maskId")).orElse(null);
        if (segmentationEntity == null) {
            session.sendMessage(new TextMessage("[Error Message] : Data not found!"));
            return;
        }

        String pythonPath = "/home/sb/Desktop/vsc/0926koceti/20230901_mmsegmentation/venv_seg/bin/python3";

        // Run modifier.py
        String scriptPath = "/home/sb/Desktop/vsc/0926koceti/analyzer_cracks/modifier.py";
        List<String> args = Arrays.asList("--remove_id", removeIdString, "--mask_path", segmentationEntity.getMaskFilePath());
        executePythonScript(pythonPath, scriptPath, args,scriptPath.split("/")[scriptPath.split("/").length - 1]);

        // Run visualizer.py
        String anotherScriptPath = "/home/sb/Desktop/vsc/0926koceti/analyzer_cracks/visualizer.py";
        List<String> anotherArgs = Arrays.asList("--folder_path", FilenameUtils.removeExtension(segmentationEntity.getMaskFilePath()).replace("result/maskImage","origin`")+".JPG");
        executePythonScript(pythonPath, anotherScriptPath, anotherArgs, anotherScriptPath.split("/")[anotherScriptPath.split("/").length - 1]);

        super.handleTextMessage(session, message);
    }

    private void executePythonScript(String pythonPath, String scriptPath, List<String> args, String type) {
        List<String> commandList = new ArrayList<>();
        commandList.add(pythonPath);
        commandList.add(scriptPath);
        commandList.addAll(args);

        ProcessBuilder processBuilder = new ProcessBuilder(commandList);

        try {
            Process process = processBuilder.start();
            readStream(process.getInputStream(), "Output", false, type);
            readStream(process.getErrorStream(), "Error", true, type);

            int exitCode = process.waitFor();
            log.info("[Python "+type+" Program] Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            log.error("[Python "+type+" Program] Error occurred while executing external process", e);
        }
    }

    private void readStream(InputStream inputStream, String type, boolean isError, String scriptype) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isError) {
                    log.error("[Python "+scriptype+" Program] " + type + " message: " + line);
                } else {
                    log.info("[Python "+scriptype+" Program] " + type + " message: " + line);
                }
            }
        } catch (IOException e) {
            log.error("[Python "+scriptype+" Program] Error occurred while reading " + type + " stream", e);
        }
    }
}
