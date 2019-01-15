package com.majy.zlj.qualityprize.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 单位得分
 * @Author majingyuan
 * @Date Create in 2017/10/31 15:08
 */
@Data
public class DepartmentScoreInfo {

    /**
     * 得分ID
     */
    private String scoreId;

    /**
     * 比赛ID
     */
    private String gameId;

    /**
     * 单位名
     */
    private String departmentName;

    /**
     * 样品得分
     */
    private BigDecimal score1;

    /**
     * 市场大比 武得分
     */
    private BigDecimal score2;

    /**
     * 厂内产品 考评得分
     */
    private BigDecimal score3;
    /**
     * 市场产品 考评得分
     */
    private BigDecimal score4;
    /**
     * 销售市场满意度测评得分
     */
    private BigDecimal score5;
    /**
     * 市场反馈产品质量问题考评得分
     */
    private BigDecimal score6;
    /**
     * 产品质量综合评比得分，score3*0.4+score4*0.4+score6*0.2
     */
    private BigDecimal score7;
    /**
     * 总裁质量 奖得分，score2*0.3+score7*0.6+score5*0.1
     */
    private BigDecimal scoreAverage;

    /**
     * 综合排名
     */
    private Integer departmentRanking;

    /**
     * 分组数量
     */
    private Integer groupCount;

    /**
     * 样品得分明细
     */
    private List<DepartmentScoreDetailInfo> scoreDetailInfoList;
}

