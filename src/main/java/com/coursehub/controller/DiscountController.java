package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.request.discount.DiscountSearchRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import com.coursehub.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // all for admin
    @GetMapping()
    public ResponseEntity<ResponseGeneral<Page<DiscountSearchResponseDTO>>> searchDiscount(@ModelAttribute DiscountSearchRequestDTO discountSearchRequestDTO) {
        ResponseGeneral<Page<DiscountSearchResponseDTO>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(discountService.searchDiscount(discountSearchRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }



    @GetMapping("/{courseId}/my")
    public ResponseEntity<ResponseGeneral<List<DiscountResponseDTO>>> getMyDiscount(@PathVariable ("courseId") Long courseId) {
        ResponseGeneral<List<DiscountResponseDTO>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(discountService.getMyDiscountByCourseId(courseId));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseGeneral<Page<DiscountSearchResponseDTO>>> searchMyDiscount(@ModelAttribute DiscountSearchRequestDTO discountSearchRequestDTO) {
        ResponseGeneral<Page<DiscountSearchResponseDTO>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(discountService.searchMyAvailableDiscount(discountSearchRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }


    @PostMapping()
    public ResponseEntity<ResponseGeneral<DiscountResponseDTO>> createDiscount(@Valid @RequestBody DiscountRequestDTO discountRequestDTO) {
        ResponseGeneral<DiscountResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(discountService.createDiscount(discountRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseGeneral<DiscountResponseDTO>> updateDiscount(@Valid @RequestBody DiscountRequestDTO discountRequestDTO,
                                                                              @PathVariable("id") Long id) {
        ResponseGeneral<DiscountResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        discountRequestDTO.setId(id);
        responseDTO.setData(discountService.createDiscount(discountRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    // for web
    @GetMapping("/available")
    public ResponseEntity<ResponseGeneral<Page<DiscountSearchResponseDTO>>> searchAvailableDiscount(@ModelAttribute DiscountSearchRequestDTO discountSearchRequestDTO) {
        ResponseGeneral<Page<DiscountSearchResponseDTO>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(discountService.searchAvailableDiscount(discountSearchRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<String>> deleteDiscount(@PathVariable("id") Long id) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(discountService.deleteDiscount(id));
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/statuses")
    public ResponseEntity<ResponseGeneral<Map<String, String>>> getStatus() {
        ResponseGeneral<Map<String, String>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(discountService.getDiscountStatus());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseGeneral<Map<String, String>>> getOverall() {
        ResponseGeneral<Map<String, String>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(discountService.getOverall());
        return ResponseEntity.ok(responseDTO);
    }
    
}
