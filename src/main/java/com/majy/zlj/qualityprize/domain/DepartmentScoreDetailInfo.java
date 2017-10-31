package com.majy.zlj.qualityprize.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 样品得分明细
 * @Author majingyuan
 * @Date Create in 2017/10/31 15:19
 */
@Data
public class DepartmentScoreDetailInfo {

    /**
     * 明细ID
     */
    private String scoreItemId;

    /**
     * 父ID
     */
    private String scoreId;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 分组名
     */
    private String gourpName;

    /**
     * 外观质 量得分，由评分产生
     */
    private BigDecimal score1;

    /**
     * 养分含 量扣分
     */
    private BigDecimal score2;

    /**
     * 合计
     */
    private BigDecimal scoreSum;
}
