package com.majy.zlj.qualityprize.mapper;


import com.majy.zlj.qualityprize.domain.ScoreInfo;

import java.util.List;

/**
 * Created by majingyuan on 2017/5/28.
 * 评分
 */

public interface ScoreInfoMapper {

    List<ScoreInfo> getAll();

    List<ScoreInfo> getScoreList(ScoreInfo scoreInfo);

    int getScoreListCount(ScoreInfo scoreInfo);

    List<ScoreInfo> getScoreListByPlayer(String playerId);

    List<ScoreInfo> getScoreListByJudge(String JudgeId);

    ScoreInfo getScoreInfoById(String scoreId);

    int insert(ScoreInfo scoreInfo);

    int update(ScoreInfo scoreInfo);

    int delete(ScoreInfo scoreInfo);
}
