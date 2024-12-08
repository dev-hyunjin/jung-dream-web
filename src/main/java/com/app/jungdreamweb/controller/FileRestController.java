package com.app.jungdreamweb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/files/*")
@RequiredArgsConstructor
public class FileRestController {

    @Value("${upload.file.path}")
    private String uploadFilePath;

    @PostMapping("upload")
    public Map<String, Object> upload(@RequestParam("file") List<MultipartFile> multipartFiles) throws IOException {
        Map<String, Object> map = new HashMap<>();

        List<String> uuids = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        String path = uploadFilePath + getPath();
        String filePath = "";
        File file = new File(path);

        if(!file.exists()) {
            file.mkdirs();
        }


        for(int i = 0; i < multipartFiles.size(); i++){
            uuids.add(UUID.randomUUID().toString());
            filePath = uuids.get(i) + "_" + multipartFiles.get(i).getOriginalFilename();
            multipartFiles.get(i).transferTo(new File(path, filePath));

            new FileInputStream(path + "/" + uuids.get(i)+ "_" + multipartFiles.get(i).getOriginalFilename());
//            InputStream inputStream = new FileInputStream("/C:/upload/" + getPath() + "/" + uuids.get(i)+ "_" + multipartFiles.get(i).getOriginalFilename());

//            if(multipartFiles.get(i).getContentType().startsWith("image")){
//                FileOutputStream out = new FileOutputStream(new File(path, "t_" + uuids.get(i) + "_" + multipartFiles.get(i).getOriginalFilename()));
//                Thumbnailator.createThumbnail(inputStream, out, 400, 400);
//                out.close();
//            }

            filePaths.add(getPath() + "/" + filePath);
        }

        map.put("uuids", uuids);
        map.put("paths", filePaths);

        return map;
    }

    //    파일 불러오기
    @GetMapping("display")
    public byte[] businessDisplay(String filePath) throws Exception {
        try {
            return filePath.contentEquals("null") || filePath.isBlank() ? null : FileCopyUtils.copyToByteArray(new File(uploadFilePath, filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    현재 날짜 경로 구하기
    private String getPath() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

}

