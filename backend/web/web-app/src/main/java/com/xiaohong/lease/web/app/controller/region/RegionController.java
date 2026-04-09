package com.xiaohong.lease.web.app.controller.region;

import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.CityInfo;
import com.xiaohong.lease.model.entity.DistrictInfo;
import com.xiaohong.lease.model.entity.ProvinceInfo;
import com.xiaohong.lease.web.app.service.CityInfoService;
import com.xiaohong.lease.web.app.service.DistrictInfoService;
import com.xiaohong.lease.web.app.service.ProvinceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "地区信息")
@RequestMapping("/app/region")
public class RegionController {

    @Autowired
    private ProvinceInfoService provinceInfoService;

    @Autowired
    private CityInfoService cityInfoService;

    @Autowired
    private DistrictInfoService districtInfoService;

    @Operation(summary = "查询省份信息列表")
    @GetMapping("province/list")
    public Result<List<ProvinceInfo>> listProvince() {
        List<ProvinceInfo> list = provinceInfoService.list();
        return Result.ok(list);
    }

    @Operation(summary = "根据省份id查询城市信息列表")
    @GetMapping("city/listByProvinceId")
    public Result<List<CityInfo>> listCityInfoByProvinceId(@RequestParam Long id) {
        LambdaQueryWrapper<CityInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CityInfo::getProvinceId, id);
        List<CityInfo> list = cityInfoService.list(queryWrapper);
        return Result.ok(list);
    }

    @GetMapping("district/listByCityId")
    @Operation(summary = "根据城市id查询区县信息")
    public Result<List<DistrictInfo>> listDistrictInfoByCityId(@RequestParam Long id) {
        LambdaQueryWrapper<DistrictInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DistrictInfo::getCityId, id);
        List<DistrictInfo> list = districtInfoService.list(queryWrapper);
        return Result.ok(list);
    }

    @GetMapping("listAsTree")
    @Operation(summary = "查询省市区树形列表")
    public Result<List<Map<String, Object>>> listAsTree() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询所有省份
        List<ProvinceInfo> provinces = provinceInfoService.list();
        
        for (ProvinceInfo province : provinces) {
            Map<String, Object> provinceMap = new HashMap<>();
            provinceMap.put("id", province.getId());
            provinceMap.put("name", province.getName());
            provinceMap.put("type", "province");
            
            // 查询该省份下的城市
            LambdaQueryWrapper<CityInfo> cityWrapper = new LambdaQueryWrapper<>();
            cityWrapper.eq(CityInfo::getProvinceId, province.getId());
            List<CityInfo> cities = cityInfoService.list(cityWrapper);
            
            List<Map<String, Object>> cityList = new ArrayList<>();
            for (CityInfo city : cities) {
                Map<String, Object> cityMap = new HashMap<>();
                cityMap.put("id", city.getId());
                cityMap.put("name", city.getName());
                cityMap.put("type", "city");
                
                // 查询该城市下的区县
                LambdaQueryWrapper<DistrictInfo> districtWrapper = new LambdaQueryWrapper<>();
                districtWrapper.eq(DistrictInfo::getCityId, city.getId());
                List<DistrictInfo> districts = districtInfoService.list(districtWrapper);
                
                List<Map<String, Object>> districtList = new ArrayList<>();
                for (DistrictInfo district : districts) {
                    Map<String, Object> districtMap = new HashMap<>();
                    districtMap.put("id", district.getId());
                    districtMap.put("name", district.getName());
                    districtMap.put("type", "district");
                    districtList.add(districtMap);
                }
                
                cityMap.put("children", districtList);
                cityList.add(cityMap);
            }
            
            provinceMap.put("children", cityList);
            result.add(provinceMap);
        }
        
        return Result.ok(result);
    }
}
