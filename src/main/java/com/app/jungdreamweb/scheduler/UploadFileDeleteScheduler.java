package com.app.jungdreamweb.scheduler;

import com.app.jungdreamweb.dto.FileDTO;
import com.app.jungdreamweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadFileDeleteScheduler {

    private final AdminService adminService;

    @Value("${upload.file.path}")
    private String uploadFilePath;

    // 매달 1일마다 현재 올라가있는 파일 제외한 모든 파일, 폴더 삭제
    @Scheduled(cron = "0 0 0 1 * *")
    public void uploadFileDelete() {

        FileDTO fileDTO = adminService.getFile();
        String filePath = fileDTO.getFilePath();

        Path localFilePath = Paths.get(uploadFilePath);
        Path useFilePath = Paths.get(uploadFilePath + filePath);

        try {
            Files.walk(localFilePath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(file -> {
                        if(file.equals(localFilePath) || file.equals(useFilePath)) {
                            return;
                        }

                        if(Files.isRegularFile(file)) {
                            try {
                                log.info("삭제 파일 : " + file);
                                Files.delete(file);
                            } catch (Exception e) {
                                log.error("파일 삭제 중 오류 발생 : " + file + ", Error Msg : " + e.getMessage());
                            }
                        } else if (Files.isDirectory(file)) {
                            try {
                                if(Files.list(file).count() == 0) {
                                    log.info("삭제 폴더 : " + file);
                                    Files.delete(file);
                                }
                            } catch (Exception e) {
                                log.error("폴더 삭제 중 오류 발생 : " + file.getFileName() + ", Error Msg : " + e.getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            log.error("스케줄러 오류 발생 : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
