package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (begin.isBefore(end)){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }

        List<Double> turnoverList = new ArrayList<>();
        dateList.forEach(date -> {
            Map map =new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("beginTime", LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime", LocalDateTime.of(date, LocalTime.MAX));
            Double turnover = ordersMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (begin.isBefore(end)){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        dateList.forEach(date -> {
            Map map = new HashMap();
            map.put("beginTime", LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime", LocalDateTime.of(date, LocalTime.MAX));
            Integer newUsers = userMapper.countByMap(map);
            newUserList.add(newUsers);

            map.put("beginTime", null);
            Integer totalUsers = userMapper.countByMap(map);
            totalUserList.add(totalUsers);
        });



        return new UserReportVO(
                StringUtils.join(dateList,","),
                StringUtils.join(totalUserList,","),
                StringUtils.join(newUserList,",")
        );
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (begin.isBefore(end)){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        for(LocalDate date : dateList){
            Map map = new HashMap();
            map.put("beginTime", LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime", LocalDateTime.of(date, LocalTime.MAX));
            Integer orderCount = ordersMapper.countByMap(map);
            orderCountList.add(orderCount);
            map.put("status", Orders.COMPLETED);
            Integer validOrder = ordersMapper.countByMap(map);
            validOrderCountList.add(validOrder);
            totalOrderCount += orderCount;
            validOrderCount += validOrderCount;
        }
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return new OrderReportVO(
                StringUtils.join(dateList,","),
                StringUtils.join(orderCountList,","),
                StringUtils.join(validOrderCountList,","),
                totalOrderCount,
                validOrderCount,
                orderCompletionRate
        );
    }

    /**
     * 销量排名
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        Map map = new HashMap();
        map.put("status", Orders.COMPLETED);
        map.put("beginTime", LocalDateTime.of(begin, LocalTime.MIN));   // 2024-05-14 00:00
        map.put("endTime", LocalDateTime.of(end, LocalTime.MAX));      // 2024-05-20 23:59:59.999999999
        List<GoodsSalesDTO> list = ordersMapper.sumTop10(map);

        for (GoodsSalesDTO dto : list) {
            nameList.add(dto.getName());
            numberList.add(dto.getNumber());
        }

        // 3.封装 SalesTop10ReportVO 对象并返回
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }
}
