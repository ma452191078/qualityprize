package com.majy.zlj.qualityprize.mapper;

import com.majy.zlj.qualityprize.domain.PlayerInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by majingyuan on 2017/5/28.
 * 用户
 */

public interface PlayerInfoMapper {

    List<PlayerInfo> getAll();

    List<PlayerInfo> getPlayerListByGameId(String gameId);

    List<PlayerInfo> getPlayerList(PlayerInfo playerInfo);

    List<PlayerInfo> getAvgListByPlayer(String gameId);

    List<PlayerInfo> getAvgListByPlayerAndJudge(Map<String, String> param);

    List<PlayerInfo> getAvgListByDepartment(String gameId);

    List<PlayerInfo> getAvgListByGroup(String gameId);

    List<PlayerInfo> getPlayerListByJudge(Map<String, String> param);

    List<PlayerInfo> getPlayerScoreListByJudge(Map<String, Object> param);

    PlayerInfo getPlayerInfoById(String playerId);

    int insert(PlayerInfo playerInfo);

    int update(PlayerInfo playerInfo);

    int delete(String playerId);
}
