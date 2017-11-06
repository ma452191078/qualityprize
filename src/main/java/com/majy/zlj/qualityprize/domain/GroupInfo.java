package com.majy.zlj.qualityprize.domain;

import lombok.Data;

/**
 * 比赛分组信息
 * @Author majingyuan
 * @Date Create in 2017/10/25 22:58
 */
@Data
public class GroupInfo {

    /**
     * 分组ID
     */
    private String groupId;
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 分组排序
     */
    private String groupIndex;
    /**
     * 比赛ID
     */
    private String gameId;

    /**
     * 计分项,0否，1是
     */
    private String scoreFlag;
}
