package com.majy.zlj.qualityprize.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by majingyuan on 2017/5/28.
 * 参赛选手类
 */
@Data
public class ReactPlayerInfo {
    /**
     * 选手ID
     */
    private String playerId;
    /**
     * 比赛id
     */
    private String gameId;
    /**
     * 姓名
     */
    private String playerName;
    /**
     * 顺序
     */
    private Integer playerNum;
    /**
     * 部门
     */
    private String playerDepartment;
    /**
     * 总分
     */
    private BigDecimal playerSum;
    /**
     * 平均分
     */
    private BigDecimal playerAverage;
    /**
     * 名次
     */
    private Integer playerRanking;
    /**
     * 选手照片
     */
    private String playerImg;
    /**
     * 创建人
     */
    private String playerAddBy;
    /**
     * 删除标识（0未删除，1删除）
     */
    private String playerDeleted;
    /**
     * 可用标识（0可评分，1不可评分）
     */
    private String playerActive;

    /**
     * 已经打分 （0未打分，1已打分）
     */
    private String playerIsScore;
    /**
     * 分组ID
     */
    private String groupId;
    private String groupName;

    /**
     * 配比
     */
    private String playerFormula;

    /**
     * 批号
     */
    private String playerBatchNum;

    private String judgeId;

}
