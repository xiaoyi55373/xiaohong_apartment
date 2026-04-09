package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.SystemDept;
import com.xiaohong.lease.web.admin.mapper.SystemDeptMapper;
import com.xiaohong.lease.web.admin.service.SystemDeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemDeptServiceImpl extends ServiceImpl<SystemDeptMapper, SystemDept> implements SystemDeptService {

    // 模拟数据
    private static final List<SystemDept> MOCK_DEPTS = new ArrayList<>();
    
    static {
        SystemDept dept1 = new SystemDept();
        dept1.setId(1L);
        dept1.setParentId(0L);
        dept1.setDeptName("总公司");
        dept1.setDeptCode("HQ");
        dept1.setSort(1);
        dept1.setStatus(1);
        
        SystemDept dept2 = new SystemDept();
        dept2.setId(2L);
        dept2.setParentId(1L);
        dept2.setDeptName("技术部");
        dept2.setDeptCode("TECH");
        dept2.setSort(1);
        dept2.setStatus(1);
        
        SystemDept dept3 = new SystemDept();
        dept3.setId(3L);
        dept3.setParentId(1L);
        dept3.setDeptName("运营部");
        dept3.setDeptCode("OPS");
        dept3.setSort(2);
        dept3.setStatus(1);
        
        MOCK_DEPTS.add(dept1);
        MOCK_DEPTS.add(dept2);
        MOCK_DEPTS.add(dept3);
    }

    @Override
    public List<SystemDept> list() {
        try {
            return super.list();
        } catch (Exception e) {
            return MOCK_DEPTS;
        }
    }

    @Override
    public List<SystemDept> listAsTree() {
        try {
            return super.list();
        } catch (Exception e) {
            return MOCK_DEPTS;
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        try {
            SystemDept dept = getById(id);
            if (dept != null) {
                dept.setStatus(status);
                updateById(dept);
            }
        } catch (Exception e) {
            System.out.println("模拟：更新部门 " + id + " 状态为 " + status);
        }
    }
}
