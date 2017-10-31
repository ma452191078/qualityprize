package com.majy.zlj.qualityprize.mapper;

import com.majy.zlj.qualityprize.domain.DepartmentScoreDetailInfo;

import java.util.List;

/**
 * @Author majingyuan
 * @Date Create in 2017/10/31 15:37
 */
public interface DepartmentScoreDetailInfoMapper {

    List<DepartmentScoreDetailInfo> getAll();

    List<DepartmentScoreDetailInfo> getDepartmentScoreDetailList(String scoreId);

    int insert(DepartmentScoreDetailInfo scoreInfo);

    int update(DepartmentScoreDetailInfo scoreInfo);

    int delete(String scoreItemId);

    int deleteByParentId(String scoreId);
}
