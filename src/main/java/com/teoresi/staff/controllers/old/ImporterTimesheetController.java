package com.teoresi.staff.controllers.old;

import com.teoresi.staff.dtos.old.ImportTimesheetDTO;
import com.teoresi.staff.mappers.old.TimesheetMapper;
import com.teoresi.staff.services.old.ImporterTimesheetService;
import com.teoresi.staff.services.old.TimesheetService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("timesheets/imports")
@PreAuthorize("hasAnyAuthority('ADMIN', 'GDM')")
public class ImporterTimesheetController {

    private final ImporterTimesheetService importerTimesheetService;
    private final TimesheetMapper timesheetMapper;
    private final Logger logger = LoggerFactory.getLogger(TimesheetService.class);
    private Flux<ImportTimesheetDTO> importTimesheetFlux;
    private Sinks.Many<ImportTimesheetDTO> importTimesheetSink;
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ImportTimesheetDTO> getUpdate() {
        return this.importTimesheetFlux;
    }
    @PostMapping(value = "", consumes = MULTIPART_FORM_DATA)
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            Path tempFile = Files.createTempFile("upload_", "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            this.importTimesheetSink = Sinks.many().multicast().onBackpressureBuffer();
            this.importTimesheetFlux = this.importTimesheetSink.asFlux();

            importerTimesheetService.importTimesheetProject(file, importTimesheetSink, tempFile);
            return ResponseEntity.ok("File uploaded successfully");

        } catch (IOException e) {
            logger.error("Error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

}

