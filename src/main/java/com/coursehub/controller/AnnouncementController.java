package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.announcement.AnnouncementCreateRequestDTO;
import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.dto.response.announcement.TargetGroupDTO;
import com.coursehub.enums.TargetGroup;
import com.coursehub.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ResponseGeneral<List<AnnouncementResponseDTO>>> getAnnouncements(
            @RequestParam(required = false) TargetGroup targetGroup
    ) {
        List<AnnouncementResponseDTO> list = announcementService.getAnnouncements(targetGroup);
        ResponseGeneral<List<AnnouncementResponseDTO>> response = new ResponseGeneral<>();
        response.setMessage("Get announcement list successfully");
        response.setData(list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseGeneral<AnnouncementResponseDTO>> getAnnouncement(@PathVariable Long id) {
        AnnouncementResponseDTO dto = announcementService.getAnnouncement(id);
        ResponseGeneral<AnnouncementResponseDTO> response = new ResponseGeneral<>();
        response.setMessage("Get announcement successfully");
        response.setData(dto);
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

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ResponseGeneral<Void>> markAnnouncementAsDeleted(
            @PathVariable Long id
    ) {
        announcementService.markAsDeleted(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Announcement marked as deleted");
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

    @GetMapping("/user")
    public ResponseEntity<ResponseGeneral<List<AnnouncementResponseDTO>>> getUserAnnouncements() {
        List<AnnouncementResponseDTO> list = announcementService.getAnnouncementsForCurrentUser();
        ResponseGeneral<List<AnnouncementResponseDTO>> response = new ResponseGeneral<>();
        response.setMessage("Get user announcements successfully");
        response.setData(list);
        return ResponseEntity.ok(response);
    }
}
