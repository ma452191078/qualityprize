package com.majy.zlj.qualityprize.mapper;

import com.majy.zlj.qualityprize.domain.DepartmentInfo;

import java.util.List;

/**
 * @Author majingyuan
 * @Date Create in 2017/10/31 17:17
 */
public interface DepartmentMapper {

    List<DepartmentInfo> getDepartmentList(String gameId);
}
