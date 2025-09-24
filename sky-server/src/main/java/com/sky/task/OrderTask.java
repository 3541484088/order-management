package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    @Autowired
    private OrdersMapper ordersMapper;

    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrder(){
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        log.info("处理支付超时订单，当前时间：{}", time);
        List<Orders> ordersList = ordersMapper.selectByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);

       if (ordersList != null && ordersList.size() > 0){

           ordersList.forEach(orders -> {
               orders.setStatus(Orders.CANCELLED);
               orders.setCancelReason("支付超时");
               orders.setCancelTime(LocalDateTime.now());
               ordersMapper.update(orders);
           });
       }
    }
    @Scheduled(cron = "0 * * * * ? ")
    public void processDeliveryOrder(){
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> ordersList = ordersMapper.selectByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);

        if (ordersList != null && ordersList.size() > 0){
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime( time);
                ordersMapper.update(orders);
            });
        }
    }

}
