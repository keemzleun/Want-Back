package com.example.want.api.photo.controller;

import com.example.want.api.photo.dto.CreatePhotoRqDto;
import com.example.want.api.photo.service.PhotoService;
import com.example.want.api.photo.service.S3Uploader;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/photo")
public class PhotoController {

    private final S3Uploader s3Uploader;
    private final PhotoService photoService;

    @PostMapping(value = "/{blockId}/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@PathVariable Long blockId, @RequestParam List<MultipartFile> multipartFile) throws IOException {

        String url = s3Uploader.uploadFile(multipartFile);
        return url;
    }

//    @GetMapping("/api/list")
//    public String listPage(Model model) {
//        List<FileEntity> fileList =fileService.getFiles();
//        model.addAttribute("fileList", fileList);
//        return "list";
//    }

}
