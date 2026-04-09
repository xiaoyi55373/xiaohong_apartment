package com.xiaohong.lease.web.admin.controller.export;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.export.ExcelExportService;
import com.xiaohong.lease.common.export.ExportType;
import com.xiaohong.lease.common.export.PdfExportService;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.model.entity.SystemUser;
import com.xiaohong.lease.model.enums.AppointmentStatus;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.enums.LeaseSourceType;
import com.xiaohong.lease.model.enums.LeaseStatus;
import com.xiaohong.lease.model.enums.OperatorType;
import com.xiaohong.lease.model.enums.ReleaseStatus;
import com.xiaohong.lease.model.enums.SystemUserType;
import com.xiaohong.lease.web.admin.service.ApartmentInfoService;
import com.xiaohong.lease.web.admin.service.LeaseAgreementService;
import com.xiaohong.lease.web.admin.service.SystemUserService;
import com.xiaohong.lease.web.admin.service.ViewAppointmentService;
import com.xiaohong.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.xiaohong.lease.web.admin.vo.agreement.AgreementVo;
import com.xiaohong.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.xiaohong.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.xiaohong.lease.web.admin.vo.appointment.AppointmentQueryVo;
import com.xiaohong.lease.web.admin.vo.appointment.AppointmentVo;
import com.xiaohong.lease.web.admin.vo.export.AgreementExportVo;
import com.xiaohong.lease.web.admin.vo.export.ApartmentExportVo;
import com.xiaohong.lease.web.admin.vo.export.AppointmentExportVo;
import com.xiaohong.lease.web.admin.vo.export.SystemUserExportVo;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据导出控制器
 *
 * @author 小红
 * @date 2026-04-03
 */
@Slf4j
@Tag(name = "数据导出管理")
@RestController
@RequestMapping("/admin/export")
public class ExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Autowired
    private ViewAppointmentService viewAppointmentService;

    @Autowired
    private SystemUserService systemUserService;

    /**
     * 导出公寓信息
     */
    @CheckPermission
    @OperationLog(title = "数据导出", businessType = BusinessType.EXPORT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'导出公寓信息: ' + #exportType")
    @Operation(summary = "导出公寓信息")
    @GetMapping("/apartment")
    public void exportApartment(
            @Parameter(description = "导出类型: excel/pdf") @RequestParam String exportType,
            @Parameter(description = "省份ID") @RequestParam(required = false) Long provinceId,
            @Parameter(description = "城市ID") @RequestParam(required = false) Long cityId,
            @Parameter(description = "区县ID") @RequestParam(required = false) Long districtId,
            HttpServletResponse response) throws IOException {

        // 查询数据（不分页，导出全部）
        ApartmentQueryVo queryVo = new ApartmentQueryVo();
        queryVo.setProvinceId(provinceId);
        queryVo.setCityId(cityId);
        queryVo.setDistrictId(districtId);
        
        // 查询所有数据（使用大分页）
        Page<ApartmentItemVo> page = new Page<>(1, 10000);
        IPage<ApartmentItemVo> result = apartmentInfoService.pageItem(page, queryVo);
        List<ApartmentItemVo> list = result.getRecords();

        // 转换为导出VO
        List<ApartmentExportVo> exportList = convertToApartmentExportVo(list);

        // 生成文件名
        String fileName = "公寓信息_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        ExportType type = ExportType.fromString(exportType);
        if (type == ExportType.PDF) {
            exportApartmentToPdf(response, fileName, exportList);
        } else {
            excelExportService.export(response, fileName, "公寓信息", exportList, ApartmentExportVo.class);
        }
    }

    /**
     * 导出租约信息
     */
    @CheckPermission
    @OperationLog(title = "数据导出", businessType = BusinessType.EXPORT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'导出租约信息: ' + #exportType")
    @Operation(summary = "导出租约信息")
    @GetMapping("/agreement")
    public void exportAgreement(
            @Parameter(description = "导出类型: excel/pdf") @RequestParam String exportType,
            @Parameter(description = "公寓ID") @RequestParam(required = false) Long apartmentId,
            @Parameter(description = "承租人姓名") @RequestParam(required = false) String name,
            @Parameter(description = "手机号码") @RequestParam(required = false) String phone,
            HttpServletResponse response) throws IOException {

        // 查询数据
        AgreementQueryVo queryVo = new AgreementQueryVo();
        queryVo.setApartmentId(apartmentId);
        queryVo.setName(name);
        queryVo.setPhone(phone);

        Page<AgreementVo> page = new Page<>(1, 10000);
        IPage<AgreementVo> result = leaseAgreementService.pageItem(page, queryVo);
        List<AgreementVo> list = result.getRecords();

        // 转换为导出VO
        List<AgreementExportVo> exportList = convertToAgreementExportVo(list);

        // 生成文件名
        String fileName = "租约信息_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        ExportType type = ExportType.fromString(exportType);
        if (type == ExportType.PDF) {
            exportAgreementToPdf(response, fileName, exportList);
        } else {
            excelExportService.export(response, fileName, "租约信息", exportList, AgreementExportVo.class);
        }
    }

    /**
     * 导出预约信息
     */
    @CheckPermission
    @OperationLog(title = "数据导出", businessType = BusinessType.EXPORT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'导出预约信息: ' + #exportType")
    @Operation(summary = "导出预约信息")
    @GetMapping("/appointment")
    public void exportAppointment(
            @Parameter(description = "导出类型: excel/pdf") @RequestParam String exportType,
            @Parameter(description = "公寓ID") @RequestParam(required = false) Long apartmentId,
            @Parameter(description = "预约人姓名") @RequestParam(required = false) String name,
            @Parameter(description = "手机号码") @RequestParam(required = false) String phone,
            HttpServletResponse response) throws IOException {

        // 查询数据
        AppointmentQueryVo queryVo = new AppointmentQueryVo();
        queryVo.setApartmentId(apartmentId);
        queryVo.setName(name);
        queryVo.setPhone(phone);

        Page<AppointmentVo> page = new Page<>(1, 10000);
        IPage<AppointmentVo> result = viewAppointmentService.pageItem(page, queryVo);
        List<AppointmentVo> list = result.getRecords();

        // 转换为导出VO
        List<AppointmentExportVo> exportList = convertToAppointmentExportVo(list);

        // 生成文件名
        String fileName = "预约信息_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        ExportType type = ExportType.fromString(exportType);
        if (type == ExportType.PDF) {
            exportAppointmentToPdf(response, fileName, exportList);
        } else {
            excelExportService.export(response, fileName, "预约信息", exportList, AppointmentExportVo.class);
        }
    }

    /**
     * 导出系统用户信息
     */
    @CheckPermission
    @OperationLog(title = "数据导出", businessType = BusinessType.EXPORT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'导出系统用户信息: ' + #exportType")
    @Operation(summary = "导出系统用户信息")
    @GetMapping("/systemUser")
    public void exportSystemUser(
            @Parameter(description = "导出类型: excel/pdf") @RequestParam String exportType,
            @Parameter(description = "姓名") @RequestParam(required = false) String name,
            @Parameter(description = "手机号码") @RequestParam(required = false) String phone,
            HttpServletResponse response) throws IOException {

        // 查询数据
        SystemUserQueryVo queryVo = new SystemUserQueryVo();
        queryVo.setName(name);
        queryVo.setPhone(phone);

        Page<SystemUser> page = new Page<>(1, 10000);
        IPage<SystemUserItemVo> result = systemUserService.pageItem(page, queryVo);
        List<SystemUserItemVo> list = result.getRecords();

        // 转换为导出VO
        List<SystemUserExportVo> exportList = convertToSystemUserExportVo(list);

        // 生成文件名
        String fileName = "系统用户信息_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        ExportType type = ExportType.fromString(exportType);
        if (type == ExportType.PDF) {
            exportSystemUserToPdf(response, fileName, exportList);
        } else {
            excelExportService.export(response, fileName, "系统用户信息", exportList, SystemUserExportVo.class);
        }
    }

    // ==================== 数据转换方法 ====================

    private List<ApartmentExportVo> convertToApartmentExportVo(List<ApartmentItemVo> list) {
        List<ApartmentExportVo> result = new ArrayList<>();
        for (ApartmentItemVo item : list) {
            ApartmentExportVo vo = new ApartmentExportVo();
            vo.setId(item.getId());
            vo.setName(item.getName());
            vo.setProvinceName(item.getProvinceName());
            vo.setCityName(item.getCityName());
            vo.setDistrictName(item.getDistrictName());
            vo.setAddressDetail(item.getAddressDetail());
            vo.setLatitude(item.getLatitude());
            vo.setLongitude(item.getLongitude());
            vo.setPhone(item.getPhone());
            vo.setReleaseStatus(item.getIsRelease() != null ? item.getIsRelease().getName() : "未知");
            vo.setAvgScore(BigDecimal.ZERO); // 从评分表获取
            vo.setScoreCount(0); // 从评分表获取
            vo.setCreateTime(new Date()); // 需要从实体获取
            vo.setIntroduction(item.getIntroduction());
            result.add(vo);
        }
        return result;
    }

    private List<AgreementExportVo> convertToAgreementExportVo(List<AgreementVo> list) {
        List<AgreementExportVo> result = new ArrayList<>();
        for (AgreementVo item : list) {
            AgreementExportVo vo = new AgreementExportVo();
            BeanUtils.copyProperties(item, vo);
            // 转换状态
            if (item.getStatus() != null) {
                vo.setStatus(item.getStatus().getName());
            }
            // 转换来源
            if (item.getSourceType() != null) {
                vo.setSourceType(item.getSourceType().getName());
            }
            result.add(vo);
        }
        return result;
    }

    private List<AppointmentExportVo> convertToAppointmentExportVo(List<AppointmentVo> list) {
        List<AppointmentExportVo> result = new ArrayList<>();
        for (AppointmentVo item : list) {
            AppointmentExportVo vo = new AppointmentExportVo();
            BeanUtils.copyProperties(item, vo);
            // 转换状态
            if (item.getAppointmentStatus() != null) {
                vo.setAppointmentStatus(item.getAppointmentStatus().getName());
            }
            result.add(vo);
        }
        return result;
    }

    private List<SystemUserExportVo> convertToSystemUserExportVo(List<SystemUserItemVo> list) {
        List<SystemUserExportVo> result = new ArrayList<>();
        for (SystemUserItemVo item : list) {
            SystemUserExportVo vo = new SystemUserExportVo();
            BeanUtils.copyProperties(item, vo);
            // 转换类型
            if (item.getType() != null) {
                vo.setType(item.getType().getName());
            }
            // 转换状态
            if (item.getStatus() != null) {
                vo.setStatus(item.getStatus().getName());
            }
            result.add(vo);
        }
        return result;
    }

    // ==================== PDF导出方法 ====================

    private void exportApartmentToPdf(HttpServletResponse response, String fileName, 
                                      List<ApartmentExportVo> list) throws IOException {
        String[] headers = {"公寓ID", "公寓名称", "省份", "城市", "区县", "详细地址", "联系电话", "发布状态", "创建时间"};
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (ApartmentExportVo vo : list) {
            String[] row = {
                String.valueOf(vo.getId()),
                vo.getName(),
                vo.getProvinceName(),
                vo.getCityName(),
                vo.getDistrictName(),
                vo.getAddressDetail(),
                vo.getPhone(),
                vo.getReleaseStatus(),
                vo.getCreateTime() != null ? sdf.format(vo.getCreateTime()) : ""
            };
            data.add(row);
        }
        
        pdfExportService.export(response, fileName, "小红公寓claw - 公寓信息报表", headers, data);
    }

    private void exportAgreementToPdf(HttpServletResponse response, String fileName,
                                      List<AgreementExportVo> list) throws IOException {
        String[] headers = {"租约ID", "承租人姓名", "手机号码", "公寓名称", "房间号", "租约开始日期", "租约结束日期", 
                           "租期", "租金(元/月)", "押金(元)", "支付方式", "租约状态", "创建时间"};
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (AgreementExportVo vo : list) {
            String[] row = {
                String.valueOf(vo.getId()),
                vo.getName(),
                vo.getPhone(),
                vo.getApartmentName(),
                vo.getRoomNumber(),
                vo.getLeaseStartDate() != null ? sdf.format(vo.getLeaseStartDate()) : "",
                vo.getLeaseEndDate() != null ? sdf.format(vo.getLeaseEndDate()) : "",
                vo.getLeaseTermName(),
                vo.getRent() != null ? vo.getRent().toString() : "",
                vo.getDeposit() != null ? vo.getDeposit().toString() : "",
                vo.getPaymentTypeName(),
                vo.getStatus(),
                vo.getCreateTime() != null ? sdf.format(vo.getCreateTime()) : ""
            };
            data.add(row);
        }
        
        pdfExportService.export(response, fileName, "小红公寓claw - 租约信息报表", headers, data);
    }

    private void exportAppointmentToPdf(HttpServletResponse response, String fileName,
                                        List<AppointmentExportVo> list) throws IOException {
        String[] headers = {"预约ID", "用户ID", "预约人姓名", "手机号码", "公寓名称", "预约时间", "预约状态", "创建时间"};
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (AppointmentExportVo vo : list) {
            String[] row = {
                String.valueOf(vo.getId()),
                String.valueOf(vo.getUserId()),
                vo.getName(),
                vo.getPhone(),
                vo.getApartmentName(),
                vo.getAppointmentTime() != null ? sdf.format(vo.getAppointmentTime()) : "",
                vo.getAppointmentStatus(),
                vo.getCreateTime() != null ? sdf.format(vo.getCreateTime()) : ""
            };
            data.add(row);
        }
        
        pdfExportService.export(response, fileName, "小红公寓claw - 预约信息报表", headers, data);
    }

    private void exportSystemUserToPdf(HttpServletResponse response, String fileName,
                                       List<SystemUserExportVo> list) throws IOException {
        String[] headers = {"用户ID", "用户名", "姓名", "用户类型", "手机号码", "岗位", "账号状态", "创建时间"};
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (SystemUserExportVo vo : list) {
            String[] row = {
                String.valueOf(vo.getId()),
                vo.getUsername(),
                vo.getName(),
                vo.getType(),
                vo.getPhone(),
                vo.getPostName(),
                vo.getStatus(),
                vo.getCreateTime() != null ? sdf.format(vo.getCreateTime()) : ""
            };
            data.add(row);
        }
        
        pdfExportService.export(response, fileName, "小红公寓claw - 系统用户信息报表", headers, data);
    }
}
