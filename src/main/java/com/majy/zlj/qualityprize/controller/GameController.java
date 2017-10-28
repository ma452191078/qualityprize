package com.majy.zlj.qualityprize.controller;

import com.majy.zlj.qualityprize.constant.AppConstant;
import com.majy.zlj.qualityprize.domain.GameInfo;
import com.majy.zlj.qualityprize.domain.GameRoleInfo;
import com.majy.zlj.qualityprize.domain.GroupInfo;
import com.majy.zlj.qualityprize.domain.UserInfo;
import com.majy.zlj.qualityprize.mapper.GameInfoMapper;
import com.majy.zlj.qualityprize.mapper.GameRoleInfoMapper;
import com.majy.zlj.qualityprize.mapper.GroupInfoMapper;
import com.majy.zlj.qualityprize.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by majingyuan on 2017/5/28.
 * 比赛
 */
@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameInfoMapper gameInfoMapper;
    @Autowired
    private GameRoleInfoMapper gameRoleInfoMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @RequestMapping("/getGameList")
    public Map<String,Object> getGameList(GameInfo gameInfo){
        Map<String,Object> map = new HashMap<String, Object>();
        if (gameInfo == null) {
            gameInfo = new GameInfo();
        }
        UserInfo loginUser = userInfoMapper.getUserInfoById(gameInfo.getAddBy());
        if (AppConstant.USER_ADMIN.equals(loginUser.getUserRole())){
            gameInfo.setAddBy("");
        }

        List<GameInfo> gameInfoList = gameInfoMapper.getGameList(gameInfo);
        if (gameInfoList != null && gameInfoList.size() > 0){
            for (int i = 0; i < gameInfoList.size(); i ++){

                switch (gameInfoList.get(i).getGameActive()){
                    case "0":
                        gameInfoList.get(i).setGameStatus("进行中");
                        break;
                    case "1":
                        gameInfoList.get(i).setGameStatus("已结束");
                        break;
                    default:
                        break;
                }
                List<GameRoleInfo> result = gameRoleInfoMapper.getGameRoleListByGame(gameInfoList.get(i).getGameId());
                if (result != null && result.size() > 0){
                    gameInfoList.get(i).setGameRoleInfoList(result);
                }

                List<GroupInfo> groupInfoList = groupInfoMapper.getGroupListByGameId(gameInfoList.get(i).getGameId());
                if (groupInfoList != null && groupInfoList.size() > 0){
                    gameInfoList.get(i).setGroupInfoList(groupInfoList);
                }
            }
        }
        map.put("gameList", gameInfoList);
        return map;
    }

    /**
     * 比赛ID查询比赛详情
     * @param gameId 比赛id
     * @return
     */
    @RequestMapping("/getGameInfoById")
    public GameInfo getGameInfoById(@RequestParam("gameId") String gameId){
        GameInfo gameInfo = gameInfoMapper.getGameInfoById(gameId);
        gameInfo.setGameRoleInfoList(gameRoleInfoMapper.getGameRoleListByGame(gameInfo.getGameId()));
        return gameInfo;
    }

    /**
     * 更新比赛信息
     * @param gameInfo 比赛信息
     * @return
     */
    @RequestMapping("/updateGameInfo")
    public Map<String, String> updateGameInfo(@RequestBody GameInfo gameInfo){
        Map<String, String> param = new HashMap<>();
        String addFlag = "failed";
        String addMessage = "修改失败请稍后重试。";

        if (gameInfo != null){

            if (gameInfo.getGameRoleInfoList() != null && gameInfo.getGameRoleInfoList().size() > 0){
                if (gameInfo.getGameId() == null || "".equals(gameInfo.getGameId())){
                    //gameId不存在创建比赛
                    gameInfo.setGameId(UUID.randomUUID().toString());
                    int result = gameInfoMapper.insert(gameInfo);
                    if (result > 0) {
                        //创建评分规则
                        createGameRole(gameInfo);
                        addFlag = "success";
                        addMessage = "比赛" + gameInfo.getGameName() + "创建成功，请为此次比赛添加选手。";
                    }
                } else {
                    //gameId存在修改比赛
                    int result = gameInfoMapper.update(gameInfo);
                    if (result > 0){
                        //更新评分规则
                        createGameRole(gameInfo);
                        addFlag = "success";
                        addMessage = "比赛" + gameInfo.getGameName() + "修改成功。";
                    }
                }
            } else {
                addMessage = "评分项目不能为空，请输入至少一项";
            }
        }
        param.put("gameId",gameInfo.getGameId());
        param.put("flag", addFlag);
        param.put("message", addMessage);
        return param;
    }

    /**
     * 终止比赛，改变比赛状态，写入截止时间
     * @param gameId 比赛id
     * @return
     */
    @RequestMapping("/killGame")
    public Map<String, String> killGame(@RequestParam("gameId") String gameId){
        Map<String, String> param = new HashMap<>();
        String addFlag = "failed";
        String addMessage = "比赛停止失败，请重试。";
        if (gameId != null && !"".equals(gameId)){
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            GameInfo gameInfo = new GameInfo();
            gameInfo.setGameId(gameId);
            gameInfo.setEndDate(df.format(new Date()));
            gameInfo.setGameActive("1");

            int result = gameInfoMapper.update(gameInfo);
            if (result > 0){
                addFlag = "success";
                addMessage = "比赛已停止。";
            }
        }
        param.put("flag", addFlag);
        param.put("message", addMessage);
        return param;
    }

    /**
     * 获取评委id
     * @param gameId 比赛id
     * @return 查询结果
     */
    @RequestMapping("/getGameJudgeId")
    public Map<String, String> getGameJudgeId(@RequestParam("gameId") String gameId){
        Map<String, String> param = new HashMap<>();
        String getFlag = "failed";
        GameInfo gameInfo;
        String judgeId = "";
        gameInfo = gameInfoMapper.getGameInfoById(gameId);
        if (gameInfo != null){
            judgeId = UUID.randomUUID().toString();
            getFlag = "success";
        }
        param.put("flag", getFlag);
        param.put("judgeId", judgeId);
        return param;
    }

    /**
     * 创建比赛的评分规则
     * @param gameInfo 比赛信息
     * @return 创建结果
     */
    private void createGameRole(GameInfo gameInfo){
        //创建比赛评分规则功能
        gameRoleInfoMapper.deleteAllByGame(gameInfo.getGameId());
        List<GameRoleInfo> roleInfoList = gameInfo.getGameRoleInfoList();
        if (roleInfoList != null && roleInfoList.size() > 0){
            //新增规则
            int i = 0;
            for (GameRoleInfo gameRoleInfo : roleInfoList){
                gameRoleInfo.setRoleIndex(i);
                gameRoleInfo.setRoleId(UUID.randomUUID().toString());
                gameRoleInfo.setGameId(gameInfo.getGameId());
                gameRoleInfoMapper.insert(gameRoleInfo);
                i ++ ;
            }
        }
    }

    /**
     * 创建比赛分组
     * @param gameInfo
     */
    private void createGroup(GameInfo gameInfo){
        groupInfoMapper.deleteByGameId(gameInfo.getGameId());
        List<GroupInfo> groupInfoList = gameInfo.getGroupInfoList();
        if (groupInfoList != null && groupInfoList.size() > 0){
            int i = 0;
            for(GroupInfo groupInfo : groupInfoList){
                groupInfo.setGourpId(UUID.randomUUID().toString());
                groupInfo.setGameId(gameInfo.getGameId());
                groupInfoMapper.insert(groupInfo);
                i ++;
            }
        }
    }
}
