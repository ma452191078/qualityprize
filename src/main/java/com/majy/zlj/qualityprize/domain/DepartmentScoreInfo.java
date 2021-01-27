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
     * 市场大比武得分
     */
    private BigDecimal score2;

    /**
     * 厂内产品考评得分
     * 养分稳定性考评得分
     */
    private BigDecimal score3;
    /**
     * 市场产品监督考评得分
     */
    private BigDecimal score4;
    /**
     * 销售市场满意度测评得分
     */
    private BigDecimal score5;
    /**
     * 市场反馈产品质量问题考评得分
     * 客户VOC综合考评得分
     */
    private BigDecimal score6;
    /**
     * 产品质量综合评比得分
     */
    private BigDecimal score7;
    /**
     * 总裁质量奖得分
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

