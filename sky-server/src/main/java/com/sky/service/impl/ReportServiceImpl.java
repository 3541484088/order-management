package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceServiceImpl workspaceService;
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

    @Override
    public void export(HttpServletResponse response) {
        // 1.查询近30天的运营数据
        LocalDate now = LocalDate.now();
        LocalDate end = now.minusDays(1);
        LocalDate begin = now.minusDays(30);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);

        // 2.往Excel表中写入数据
        // 根据类加载器读取项目中的模板文件, 基于模板文件创建Excel对象
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + "至" + end);
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());    //营业额
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());    //订单完成率
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());    //新增用户数

            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount()); //有效订单
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());       //平均客单价

            // 30天明细数据填充
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                beginTime = LocalDateTime.of(date, LocalTime.MIN); //4-23 00:00
                endTime = LocalDateTime.of(date, LocalTime.MAX);   //4-23 23:59:59.999999
                businessData = workspaceService.getBusinessData(beginTime, endTime);

                sheet.getRow(7+i).getCell(1).setCellValue(date.toString());
                sheet.getRow(7+i).getCell(2).setCellValue(businessData.getTurnover());
                sheet.getRow(7+i).getCell(3).setCellValue(businessData.getValidOrderCount());
                sheet.getRow(7+i).getCell(4).setCellValue(businessData.getOrderCompletionRate());
                sheet.getRow(7+i).getCell(5).setCellValue(businessData.getUnitPrice());
                sheet.getRow(7+i).getCell(6).setCellValue(businessData.getNewUsers());
            }

            // 3.通过输出流将Excel文件对象下载到客户端浏览器
            ServletOutputStream os = response.getOutputStream();
            workbook.write(os);

            // 4.释放资源
            os.close();
            workbook.close();
            is.close();
        } catch (IOException e) {
            log.error("文件写入失败！！！{}",e.getMessage());
        }
    }

}
