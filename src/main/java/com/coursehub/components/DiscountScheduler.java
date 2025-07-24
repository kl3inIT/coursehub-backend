package com.coursehub.components;

import com.coursehub.entity.DiscountEntity;
import com.coursehub.enums.DiscountStatus;
import com.coursehub.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
public class DiscountScheduler {

    private final DiscountRepository discountRepository;


//  @Scheduled(cron = "*/10 * * * * ?") // Uncomment for testing every 10 seconds
    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateAllDiscountStatus(){
        List<DiscountEntity> discountEntityList = discountRepository.findAllWithUserDiscounts();
        for (DiscountEntity discountEntity : discountEntityList) {
            updateDiscountStatus(discountEntity);
        }
    }

    public void updateDiscountStatus(DiscountEntity discountEntity) {
        if(discountEntity.getStartDate().after(new Date())) {
            discountEntity.setStatus(DiscountStatus.NOT_STARTED.status());
        } else if(discountEntity.getEndDate().before(new Date())) {
            discountEntity.setStatus(DiscountStatus.EXPIRED.status());
        }  else if(Objects.equals(getUsedDiscount(discountEntity), discountEntity.getQuantity())){
            discountEntity.setStatus(DiscountStatus.USED_UP.status());
        } else if(Long.valueOf(discountEntity.getUserDiscountEntities().size()).equals(discountEntity.getQuantity())){
            discountEntity.setStatus(DiscountStatus.OUT_OF_STOCK.status());
        }
        else {
            discountEntity.setStatus(DiscountStatus.AVAILABLE.status());
        }
        discountRepository.save(discountEntity);
    }

    public Long getUsedDiscount(DiscountEntity discountEntity) {
        if(!discountEntity.getUserDiscountEntities().isEmpty()){
            return discountEntity.getUserDiscountEntities().stream()
                    .filter(e -> e.getIsActive() == 0)
                    .count();
        }
        return 0L;
    }



}
