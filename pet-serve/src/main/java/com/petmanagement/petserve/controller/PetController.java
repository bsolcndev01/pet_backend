package com.petmanagement.petserve.controller;

import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.pet.HealthSummaryResponse;
import com.petmanagement.petserve.dto.pet.MetricRecordRequest;
import com.petmanagement.petserve.dto.pet.MetricRecordResponse;
import com.petmanagement.petserve.dto.pet.PetPhotoDto;
import com.petmanagement.petserve.dto.pet.PetRequest;
import com.petmanagement.petserve.dto.pet.PetResponse;
import com.petmanagement.petserve.entity.PetPhoto;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.service.MetricService;
import com.petmanagement.petserve.service.PetService;
import com.petmanagement.petserve.service.TokenService;
import com.petmanagement.petserve.dto.pet.PhotoPositionRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;
    private final TokenService tokenService;
    private final MetricService metricService;

    @Autowired
    public PetController(PetService petService, TokenService tokenService, MetricService metricService) {
        this.petService = petService;
        this.tokenService = tokenService;
        this.metricService = metricService;
    }

    @GetMapping
    public ApiResponse<PageResponse<PetResponse>> listPets(@RequestParam(value = "keyword", required = false) String keyword,
                                                           @RequestParam(value = "page", defaultValue = "1") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                                           @RequestParam(value = "scope", required = false) String scope,
                                                           @RequestParam(value = "userId", required = false) Integer userId,
                                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        boolean includeAll = StringUtils.hasText(scope) && ("all".equalsIgnoreCase(scope) || "vet".equalsIgnoreCase(scope));
        return ApiResponse.success(petService.listPets(keyword, resolvedUserId, page, size, includeAll));
    }

    @GetMapping("/{petId}")
    public ApiResponse<PetResponse> getPet(@PathVariable Integer petId,
                                           @RequestParam(value = "userId", required = false) Integer userId,
                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(petService.getPet(petId, resolvedUserId));
    }

    @PostMapping
    public ApiResponse<PetResponse> addPet(@Valid @RequestBody PetRequest request,
                                           @RequestParam(value = "userId", required = false) Integer userId,
                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(petService.createPet(request, resolvedUserId));
    }

    @PutMapping("/{petId}")
    public ApiResponse<PetResponse> updatePet(@PathVariable Integer petId,
                                              @Valid @RequestBody PetRequest request,
                                              @RequestParam(value = "userId", required = false) Integer userId,
                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(petService.updatePet(petId, request, resolvedUserId));
    }

    @DeleteMapping("/{petId}")
    public ApiResponse<Void> deletePet(@PathVariable Integer petId,
                                       @RequestParam(value = "userId", required = false) Integer userId,
                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        petService.deletePet(petId, resolvedUserId);
        return ApiResponse.success();
    }

    @GetMapping("/health-summary")
    public ApiResponse<List<HealthSummaryResponse>> healthSummary(@RequestParam(value = "userId", required = false) Integer userId,
                                                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(petService.healthSummary(resolvedUserId));
    }

    @PostMapping(path = "/{petId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PetResponse> uploadPhoto(@PathVariable Integer petId,
                                                @RequestPart("file") MultipartFile file,
                                                @RequestParam(value = "userId", required = false) Integer userId,
                                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(petService.uploadPhoto(petId, resolvedUserId, file));
    }

    @GetMapping("/{petId}/photos")
    public ApiResponse<List<PetPhotoDto>> listPhotos(@PathVariable Integer petId,
                                                     @RequestParam(value = "userId", required = false) Integer userId,
                                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(petService.listPhotos(petId, resolvedUserId));
    }

    @PostMapping(path = "/{petId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PetPhotoDto> uploadPhotoToWall(@PathVariable Integer petId,
                                                      @RequestPart("file") MultipartFile file,
                                                      @RequestParam(value = "userId", required = false) Integer userId,
                                                      @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(petService.uploadPhotoToWall(petId, resolvedUserId, file));
    }

    @DeleteMapping("/photos/{photoId}")
    public ApiResponse<Void> deletePhoto(@PathVariable Integer photoId,
                                         @RequestParam(value = "petId") Integer petId,
                                         @RequestParam(value = "userId", required = false) Integer userId,
                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        petService.deletePhoto(photoId, petId, resolvedUserId);
        return ApiResponse.success();
    }

    @PatchMapping("/photos/{photoId}/position")
    public ApiResponse<PetPhotoDto> updatePhotoPosition(@PathVariable Integer photoId,
                                                        @RequestParam("petId") Integer petId,
                                                        @Valid @RequestBody PhotoPositionRequest request,
                                                        @RequestParam(value = "userId", required = false) Integer userId,
                                                        @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(petService.updatePhotoPosition(photoId, petId, resolvedUserId, request.getPosX(), request.getPosY()));
    }

    @GetMapping("/photos/{photoId}")
    public ResponseEntity<byte[]> getPhotoById(@PathVariable Integer photoId,
                                               @RequestParam(value = "userId", required = false) Integer userId,
                                               @RequestParam(value = "token", required = false) String token,
                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserIdNullable(authorization, userId, token);
        PetPhoto photo = petService.getPhotoById(photoId, resolvedUserId)
                .orElseThrow(() -> new RuntimeException("照片不存在"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(photo.getContentType() != null ? photo.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (photo.getFileName() != null ? photo.getFileName() : "photo.jpg") + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .body(photo.getPhotoData());
    }

    @GetMapping("/{petId}/metrics")
    public ApiResponse<List<MetricRecordResponse>> metricSeries(@PathVariable Integer petId,
                                                                @RequestParam("type") String type,
                                                                @RequestParam(value = "limit", defaultValue = "30") Integer limit,
                                                                @RequestParam(value = "userId", required = false) Integer userId,
                                                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(metricService.metrics(petId, resolvedUserId, type, limit));
    }

    @PostMapping("/{petId}/metrics")
    public ApiResponse<MetricRecordResponse> addMetric(@PathVariable Integer petId,
                                                       @Valid @RequestBody MetricRecordRequest request,
                                                       @RequestParam(value = "userId", required = false) Integer userId,
                                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserId(authorization, userId);
        return ApiResponse.success(metricService.addMetric(petId, resolvedUserId, request));
    }

    @GetMapping(path = "/{petId}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Integer petId,
                                           @RequestParam(value = "userId", required = false) Integer userId,
                                           @RequestParam(value = "token", required = false) String token,
                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer resolvedUserId = resolveUserIdNullable(authorization, userId, token);
        PetPhoto photo = petService.getPhoto(petId, resolvedUserId)
                .orElseThrow(() -> new com.petmanagement.petserve.exception.BusinessException(404, "照片不存在"));
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (StringUtils.hasText(photo.getContentType())) {
            mediaType = MediaType.parseMediaType(photo.getContentType());
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header("Content-Disposition", "inline; filename=\"" + (photo.getFileName() != null ? photo.getFileName() : "photo") + "\"")
                .body(photo.getPhotoData());
    }

    private Integer resolveUserId(String authorization, Integer userId) {
        if (userId != null) {
            return userId;
        }
        String token = extractToken(authorization);
        if (StringUtils.hasText(token)) {
            return tokenService.resolve(token)
                    .orElseThrow(() -> new BusinessException(401, "未登录或登录已失效"));
        }
        throw new BusinessException(401, "未登录或登录已失效");
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.replace("Bearer", "").trim();
    }

    private Integer resolveUserIdNullable(String authorization, Integer userId, String tokenParam) {
        if (userId != null) {
            return userId;
        }
        if (StringUtils.hasText(tokenParam)) {
            return tokenService.resolve(tokenParam).orElse(null);
        }
        String token = extractToken(authorization);
        if (StringUtils.hasText(token)) {
            return tokenService.resolve(token).orElse(null);
        }
        return null;
    }
}
