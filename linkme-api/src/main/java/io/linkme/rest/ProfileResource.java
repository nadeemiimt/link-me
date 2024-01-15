package io.linkme.rest;

import io.linkme.model.ProfileDTO;
import io.linkme.service.contracts.FileIoService;
import io.linkme.service.user.ProfileServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping(value = "/api/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileResource.class);
    private final ProfileServiceImpl profileServiceImpl;
    private final FileIoService fileIoService;

    public ProfileResource(final ProfileServiceImpl profileServiceImpl, final FileIoService fileIoService) {
        this.profileServiceImpl = profileServiceImpl;
        this.fileIoService = fileIoService;
    }

    @GetMapping("/{email}")
    public ProfileDTO getProfileByEmail(@PathVariable(name = "email") final String email) {
        return profileServiceImpl.getByEmail(email);
    }

    @GetMapping("/resume/{fileKey}")
    public byte[] downloadResume(@PathVariable(name = "fileKey") final String fileKey) {
        return fileIoService.downloadFile(fileKey);
    }

    @PostMapping(consumes = {"multipart/form-data", "application/octet-stream"})
    public ResponseEntity<String> upsertUserProfile(@RequestParam("file") MultipartFile file, @RequestPart(value = "body") @Valid ProfileDTO profileDTO) throws IOException {

        try {

            if(ObjectUtils.isEmpty(profileDTO.getEmail())) {
                LOGGER.error("Email is must");
                return ResponseEntity.badRequest().body("Email is must");
            }

            // Process the form data here
            String fileIoUrl = saveFileAndGetFileUrl(file, profileDTO);

            profileDTO.setFileUrl(fileIoUrl);

            profileServiceImpl.upsert(profileDTO);

            // Return a response
            return ResponseEntity.ok("Form data submitted successfully!");
        } catch (Exception ex) {
            LOGGER.error("Error while profile save. Details {}", ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String saveFileAndGetFileUrl(MultipartFile file, ProfileDTO profileDTO) throws IOException {
        Path tempFile = Files.createTempFile(profileDTO.getEmail(), ".pdf");
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        // Upload the resume to File.io and get the file URL
        String fileIoUrl = profileServiceImpl.uploadFileAndGetUrl(tempFile);
        return fileIoUrl;
    }
}
