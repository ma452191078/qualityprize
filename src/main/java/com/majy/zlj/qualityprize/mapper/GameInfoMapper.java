package com.majy.zlj.qualityprize.mapper;


import com.majy.zlj.qualityprize.domain.GameInfo;

import java.util.List;

/**
 * Created by majingyuan on 2017/5/28.
 * 用户
 */

public interface GameInfoMapper {

    List<GameInfo> getAll();

    List<GameInfo> getGameList(GameInfo gameInfo);

    GameInfo getGameInfoById(String gameId);

    int insert(GameInfo gameInfo);

    int update(GameInfo gameInfo);

    int delete(String gameId);
}
