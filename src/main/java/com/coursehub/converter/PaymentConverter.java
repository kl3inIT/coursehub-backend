package com.coursehub.converter;

import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.payment.PaymentHistoryResponseDTO;
import com.coursehub.dto.response.payment.PaymentResponseDTO;
import com.coursehub.entity.DiscountEntity;
import com.coursehub.entity.PaymentEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.PaymentStatus;
import com.coursehub.enums.UserStatus;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.repository.DiscountRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentConverter {


    @Value("${bank.bank-number}")
    private String bankNumber;

    @Value("${bank.bank-code}")
    private String bankCode;

    @Value("${bank.account-holder}")
    private String accountHolder;

    private final CourseService courseService;
    private final UserRepository userRepository;
    private final DiscountRepository discountRepository;


    public PaymentEntity toPaymentEntity(PaymentRequestDTO paymentRequestDTO) {
        PaymentEntity paymentEntity = new PaymentEntity();

        String transactionCode = generateOrderCode();

        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);

        if(paymentRequestDTO.getDiscountId() != null){
            DiscountEntity discountEntity = discountRepository.findById(paymentRequestDTO.getDiscountId()).orElseThrow(() -> new DataNotFoundException("Discount not found"));
            paymentEntity.setDiscountEntity(discountEntity);
        }

        paymentEntity.setTransactionCode(transactionCode);
        paymentEntity.setCourseEntity(courseService.findCourseEntityById(paymentRequestDTO.getCourseId()));
        paymentEntity.setUserEntity(userEntity);
        paymentEntity.setStatus(PaymentStatus.PENDING.getStatus());
        paymentEntity.setAmount(paymentRequestDTO.getAmount());
        return paymentEntity;
    }


    public PaymentResponseDTO toPaymentResponseDTO(PaymentEntity paymentEntity) {
        return PaymentResponseDTO.builder()
                .transactionCode(paymentEntity.getTransactionCode())
                .amount(paymentEntity.getAmount() * 25000)
                .bankNumber(bankNumber)
                .bankCode(bankCode)
                .accountHolder(accountHolder)
                .build();
    }

    public static String generateOrderCode() {
        String uuid = UUID.randomUUID().toString().replace("-", ""); // 32 kí tự hex
        return uuid.substring(0, 12).toUpperCase(); // Lấy 12 ký tự đầu
    }

    public Page<PaymentHistoryResponseDTO> toPaymentHistoryResponseDTO(Page<PaymentEntity> paymentEntities) {
        return paymentEntities.map(paymentEntity -> PaymentHistoryResponseDTO.builder()
                .id(paymentEntity.getId())
                .transactionCode(paymentEntity.getTransactionCode())
                .courseName(paymentEntity.getCourseEntity().getTitle())
                .userName(paymentEntity.getUserEntity().getEmail())
                .amount(paymentEntity.getAmount())
                .status(paymentEntity.getStatus())
                .date(paymentEntity.getModifiedDate())
                .build());
    }

    public List<PaymentHistoryResponseDTO> toPaymentHistoryResponseDTO(List<PaymentEntity> paymentEntities) {
        return paymentEntities.stream().map(paymentEntity -> PaymentHistoryResponseDTO.builder()
                .id(paymentEntity.getId())
                .transactionCode(paymentEntity.getTransactionCode())
                .courseName(paymentEntity.getCourseEntity().getTitle())
                .userName(paymentEntity.getUserEntity().getEmail())
                .amount(paymentEntity.getAmount())
                .status(paymentEntity.getStatus())
                .date(paymentEntity.getModifiedDate())
                .build()).toList();
    }
}
