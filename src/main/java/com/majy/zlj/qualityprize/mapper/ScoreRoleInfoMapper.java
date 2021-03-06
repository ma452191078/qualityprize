package com.majy.zlj.qualityprize.mapper;


import com.majy.zlj.qualityprize.domain.ScoreRoleInfo;

import java.util.List;

/**
 * Created by majingyuan on 2017/5/28.
 * 评分
 */

public interface ScoreRoleInfoMapper {

    List<ScoreRoleInfo> getAll();

    List<ScoreRoleInfo> getScoreRoleList(ScoreRoleInfo scoreInfo);

    List<ScoreRoleInfo> getScoreRoleListByPlayer(String playerId);

    ScoreRoleInfo getScoreRoleInfoById(String scoreId);

    int insert(ScoreRoleInfo scoreInfo);

    int update(ScoreRoleInfo scoreInfo);

    int delete(ScoreRoleInfo scoreInfo);
}
