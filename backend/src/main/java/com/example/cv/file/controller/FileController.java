package com.example.cv.file.controller;

import com.example.cv.common.api.ResponseMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private static final Set<String> EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "bmp", "svg", "webp",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv", "zip", "rar", "mp3", "mp4",
            "avi", "mkv", "mov");

    private final Path uploadRoot;

    public FileController(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.OK)
    @ResponseMessage("Upload Single File")
    public Map<String, String> upload(@RequestPart("fileUpload") MultipartFile file,
                                      @RequestHeader(value = "folder_type", defaultValue = "default") String folderType) {
        if (file.isEmpty() || file.getSize() > 1024 * 1024) {
            throw new com.example.cv.common.api.ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "File không hợp lệ hoặc vượt quá 1MB");
        }
        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String extension = extension(original);
        if (!EXTENSIONS.contains(extension)) {
            throw new com.example.cv.common.api.ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Định dạng file không được hỗ trợ");
        }
        String safeFolder = folderType.replaceAll("[^a-zA-Z0-9_-]", "");
        String base = original.substring(0, original.length() - extension.length() - 1)
                .replaceAll("[^a-zA-Z0-9._-]", "_");
        String filename = base + "-" + System.currentTimeMillis() + "." + extension;
        try {
            Path directory = uploadRoot.resolve(safeFolder).normalize();
            if (!directory.startsWith(uploadRoot)) {
                throw new SecurityException("Invalid folder");
            }
            Files.createDirectories(directory);
            Files.copy(file.getInputStream(), directory.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return Map.of("fileName", filename);
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file", ex);
        }
    }

    private String extension(String filename) {
        int index = filename.lastIndexOf('.');
        return index < 0 ? "" : filename.substring(index + 1).toLowerCase(Locale.ROOT);
    }
}
