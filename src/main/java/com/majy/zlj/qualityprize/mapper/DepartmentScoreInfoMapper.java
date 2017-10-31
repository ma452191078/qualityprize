package com.majy.zlj.qualityprize.mapper;

import com.majy.zlj.qualityprize.domain.DepartmentScoreInfo;

import java.util.List;

/**
 * @Author majingyuan
 * @Date Create in 2017/10/31 15:37
 */
public interface DepartmentScoreInfoMapper {

    List<DepartmentScoreInfo> getAll();

    List<DepartmentScoreInfo> getDepartmentScoreList(String gameId);

    int insert(DepartmentScoreInfo scoreInfo);

    int update(DepartmentScoreInfo scoreInfo);

    int delete(String scoreId);
}
