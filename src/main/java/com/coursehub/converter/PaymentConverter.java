package com.coursehub.converter;

import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.payment.PaymentResponseDTO;
import com.coursehub.entity.DiscountEntity;
import com.coursehub.entity.PaymentEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.PaymentStatus;
import com.coursehub.repository.DiscountRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
        UserEntity userEntity = userRepository.findByEmailAndIsActive(email, 1L);
        DiscountEntity discountEntity = null;
        if(paymentRequestDTO.getDiscountCode() != null){
            discountEntity = discountRepository.findByCodeAndIsActive(paymentRequestDTO.getDiscountCode(), 1L);
        }

        paymentEntity.setTransactionCode(transactionCode);
        paymentEntity.setCourseEntity(courseService.findCourseEntityById(paymentRequestDTO.getCourseId()));
        paymentEntity.setUserEntity(userEntity);
        paymentEntity.setStatus(PaymentStatus.PENDING.getStatus());
        paymentEntity.setAmount(paymentRequestDTO.getAmount() * 25000);
        paymentEntity.setDiscountEntity(discountEntity);
        return paymentEntity;
    }


    public PaymentResponseDTO toPaymentResponseDTO(PaymentEntity paymentEntity) {
        return PaymentResponseDTO.builder()
                .transactionCode(paymentEntity.getTransactionCode())
                .amount(paymentEntity.getAmount())
                .bankNumber(bankNumber)
                .bankCode(bankCode)
                .accountHolder(accountHolder)
                .build();
    }

    public static String generateOrderCode() {
        String uuid = UUID.randomUUID().toString().replace("-", ""); // 32 kí tự hex
        return uuid.substring(0, 12).toUpperCase(); // Lấy 12 ký tự đầu
    }
}
