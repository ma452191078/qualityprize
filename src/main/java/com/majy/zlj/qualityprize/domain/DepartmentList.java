package com.majy.zlj.qualityprize.domain;

import lombok.Data;

import java.util.List;

/**
 * @Author majingyuan
 * @Date Create in 2017/11/2 17:01
 */
@Data
public class DepartmentList {
    private String gameId;
    private List<DepartmentScoreInfo> scoreList;


}
