package com.coursehub.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.announcement.AnnouncementCreateRequestDTO;
import com.coursehub.dto.request.announcement.AnnouncementSearchRequest;
import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.dto.response.announcement.TargetGroupDTO;
import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.service.AnnouncementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> createAnnouncement(
            @RequestBody AnnouncementCreateRequestDTO dto
    ) {
        AnnouncementResponseDTO created = announcementService.createAnnouncement(dto);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Announcement created successfully");
        response.setData(created);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<AnnouncementResponseDTO>>> getAnnouncements(
        @ModelAttribute AnnouncementSearchRequest request
    ) {
        Pageable pageable = PageRequest.of(
            request.getPage(),
            request.getSize(),
            Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy())
        );
        List<AnnouncementStatus> statuses = null;
        // Nếu filter status HIDDEN thì chỉ lấy HIDDEN
        if (request.getStatus() == AnnouncementStatus.HIDDEN) {
            statuses = List.of(AnnouncementStatus.HIDDEN);
        } else {
            statuses = resolveStatuses(request);
        }
        Page<AnnouncementResponseDTO> result = announcementService.getAllAnnouncements(
            request.getType(),
            statuses,
            request.getTargetGroup(),
            request.getSearch(),
            pageable
        );
        ResponseGeneral<Page<AnnouncementResponseDTO>> response = new ResponseGeneral<>();
        response.setMessage("Get announcement list successfully");
        response.setData(result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody AnnouncementCreateRequestDTO dto
    ) {
        AnnouncementResponseDTO updated = announcementService.updateAnnouncement(id, dto);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Announcement updated successfully");
        response.setData(updated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> getAnnouncementById(@PathVariable Long id) {
        AnnouncementResponseDTO announcement = announcementService.getAnnouncement(id);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Get announcement successfully");
        response.setData(announcement);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> sendAnnouncement(@PathVariable Long id) {
        AnnouncementResponseDTO sent = announcementService.sendAnnouncement(id);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Announcement sent successfully");
        response.setData(sent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/schedule")
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> scheduleAnnouncement(
            @PathVariable Long id,
            @RequestParam String scheduledTime
    ) {
        AnnouncementResponseDTO scheduled = announcementService.scheduleAnnouncement(id, scheduledTime);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Announcement scheduled successfully");
        response.setData(scheduled);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/draft")
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> saveAsDraft(@PathVariable Long id) {
        AnnouncementResponseDTO draft = announcementService.saveAsDraft(id);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Announcement saved as draft successfully");
        response.setData(draft);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> cancelAnnouncement(@PathVariable Long id) {
        AnnouncementResponseDTO cancelled = announcementService.cancelAnnouncement(id);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Announcement cancelled successfully");
        response.setData(cancelled);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ResponseGeneral<Void>> markAsRead(
            @PathVariable Long id
    ) {
        announcementService.markAsRead(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Marked as read");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Announcement deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ResponseGeneral<Void>> permanentlyDeleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Announcement permanently deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ResponseGeneral<Void>> markAnnouncementAsDeleted(
            @PathVariable Long id
    ) {
        announcementService.markAsDeleted(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Announcement marked as deleted");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<ResponseGeneral<Void>> restoreAnnouncement(
            @PathVariable Long id
    ) {
        announcementService.restoreAnnouncement(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Announcement restored successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ResponseGeneral<Void>> archiveAnnouncement(
            @PathVariable Long id
    ) {
        announcementService.archiveAnnouncement(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Announcement archived successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("{id}/clone")
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> cloneAnnouncement(@PathVariable Long id) {
        AnnouncementResponseDTO cloned = announcementService.cloneAnnouncement(id);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Announcement cloned successfully");
        response.setData(cloned);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/target-groups")
    public ResponseEntity<ResponseGeneral<List<TargetGroupDTO>>> getAllTargetGroups() {
        List<TargetGroupDTO> groups = announcementService.getAllTargetGroups();
        ResponseGeneral<List<TargetGroupDTO>> response = new ResponseGeneral<>();
        response.setMessage("Get all target groups successfully");
        response.setData(groups);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<ResponseGeneral<List<Map<String, String>>>> getAllTypes() {
        List<Map<String, String>> types = Arrays.stream(AnnouncementType.values())
            .map(type -> {
                Map<String, String> typeMap = new HashMap<>();
                typeMap.put("value", type.name());
                typeMap.put("label", type.name().replace("_", " "));
                return typeMap;
            }).toList();
        
        ResponseGeneral<List<Map<String, String>>> response = new ResponseGeneral<>();
        response.setMessage("Get all announcement types successfully");
        response.setData(types);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseGeneral<List<AnnouncementResponseDTO>>> getUserAnnouncements() {
        List<AnnouncementResponseDTO> list = announcementService.getAnnouncementsForCurrentUser();
        ResponseGeneral<List<AnnouncementResponseDTO>> response = new ResponseGeneral<>();
        response.setMessage("Get user announcements successfully");
        response.setData(list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseGeneral<Map<String, Integer>>> getAnnouncementStats() {
        Map<String, Integer> stats = announcementService.getAnnouncementStats();
        ResponseGeneral<Map<String, Integer>> response = new ResponseGeneral<>();
        response.setMessage("Get announcement stats successfully");
        response.setData(stats);
        return ResponseEntity.ok(response);
    }

    private List<AnnouncementStatus> resolveStatuses(AnnouncementSearchRequest request) {
        if (request.getMode().equalsIgnoreCase("history")) {
            if (request.getStatus() != null) {
                return List.of(request.getStatus());
            } else {
                return Arrays.asList(
                        AnnouncementStatus.SENT,
                        AnnouncementStatus.CANCELLED
                );
            }
        } else {
            if (request.getStatus() != null) {
                return List.of(request.getStatus());
            } else {
                return Arrays.asList(
                        AnnouncementStatus.DRAFT,
                        AnnouncementStatus.SCHEDULED
                );
            }
        }
    }

}
