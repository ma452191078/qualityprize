package com.majy.zlj.qualityprize.mapper;

import com.majy.zlj.qualityprize.domain.GameInfo;
import com.majy.zlj.qualityprize.domain.GroupInfo;

import java.util.List;

/**
 * 比赛选手分组
 * @author majingyuan
 * @Date Create in 2017/10/26 11:50
 */
public interface GroupInfoMapper {
    List<GroupInfo> getAll();

    List<GroupInfo> getGroupList(GroupInfo groupInfo);

    List<GroupInfo> getGroupListByGameId(String gameId);

    List<GroupInfo> getScoreGroupListByGameId(String gameId);

    GroupInfo getGroupInfoById(String groupId);

    int getGroupCountByGameId(String gameId);

    int insert(GroupInfo groupInfo);

    int update(GroupInfo groupInfo);

    int delete(String groupId);

    int deleteByGameId(String gameId);
}
