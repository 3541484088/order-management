package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 统计营业额数据
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额数据统计：{}到{}", begin, end);
        TurnoverReportVO turnoverStatistics = reportService.turnoverStatistics(begin, end);
        return Result.success(turnoverStatistics);
    }
    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    public Result userStatistics( @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户数据统计：{}到{}", begin, end);
        UserReportVO userStatistics = reportService.userStatistics(begin, end);
        return Result.success(userStatistics);
    }
    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    public Result ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end ){
        log.info("订单数据统计：{}到{}", begin, end);
        OrderReportVO ordersStatistics = reportService.ordersStatistics(begin, end);
        return Result.success(ordersStatistics);
    }
    /**
     * 销量排名
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    public Result top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)
        {
        log.info("销量排名：{}到{}", begin, end);
            SalesTop10ReportVO top10 = reportService.top10(begin, end);
        return Result.success(top10);
    }
    /**
     * 报表导出
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        log.info("报表导出");
        reportService.export(response);
    }
}
