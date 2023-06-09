package by.easycar.controller;

import by.easycar.model.user.UserPrincipal;
import by.easycar.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@Tag(name = "Image")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Post new image", security = {@SecurityRequirement(name = "User JWT")})
    @PostMapping(path = "/{adId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<String> postNewImage(@RequestParam MultipartFile file,
                                                @PathVariable Long adId,
                                                @AuthenticationPrincipal @Parameter(hidden = true) UserPrincipal user) {
        try {
            imageService.postNewImages(file, adId, user.getId());
            return new ResponseEntity<>("Image was post.", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Can't save image.", HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Replace image", security = {@SecurityRequirement(name = "User JWT")})
    @PutMapping(path = "/{adId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<String> replaceImage(@RequestParam MultipartFile file,
                                                @RequestParam String oldImage,
                                                @PathVariable Long adId,
                                                @AuthenticationPrincipal @Parameter(hidden = true) UserPrincipal user) {
        try {
            imageService.replaceImage(file, oldImage, adId, user.getId());
            return new ResponseEntity<>("Image was replaced.", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Can't save image.", HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Delete image", security = {@SecurityRequirement(name = "User JWT")})
    @DeleteMapping("/{adId}")
    private ResponseEntity<String> deleteImage(@RequestParam String oldImage,
                                               @PathVariable Long adId,
                                               @AuthenticationPrincipal @Parameter(hidden = true) UserPrincipal user) {
        try {
            imageService.deleteImage(oldImage, adId, user.getId());
            return new ResponseEntity<>("Image was deleted.", HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            return new ResponseEntity<>("Can't delete image.", HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Get image")
    @GetMapping("/{adId}")
    private ResponseEntity<Object> getImage(@PathVariable Long adId, @RequestParam String uuid) {
        try {
            byte[] image = imageService.getImage(adId, uuid);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg ");
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Can't find image.", HttpStatus.BAD_REQUEST);
        }
    }
}