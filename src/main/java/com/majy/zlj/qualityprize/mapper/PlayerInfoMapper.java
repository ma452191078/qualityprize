package com.majy.zlj.qualityprize.mapper;

import com.majy.zlj.qualityprize.domain.PlayerInfo;

import java.util.List;

/**
 * Created by majingyuan on 2017/5/28.
 * 用户
 */

public interface PlayerInfoMapper {

    List<PlayerInfo> getAll();

    List<PlayerInfo> getPlayerListByGameId(String gameId);

    List<PlayerInfo> getPlayerList(PlayerInfo playerInfo);

    List<PlayerInfo> getAvgListByPlayer(String gameId);

    List<PlayerInfo> getAvgListByDepartment(String gameId);

    List<PlayerInfo> getAvgListByGroup(String gameId);

    PlayerInfo getPlayerInfoById(String playerId);

    int insert(PlayerInfo playerInfo);

    int update(PlayerInfo playerInfo);

    int delete(String playerId);
}
