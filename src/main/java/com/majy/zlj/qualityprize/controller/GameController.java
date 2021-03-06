package com.majy.zlj.qualityprize.controller;

import com.majy.zlj.qualityprize.configuration.WechatConfig;
import com.majy.zlj.qualityprize.constant.AppConstant;
import com.majy.zlj.qualityprize.domain.*;
import com.majy.zlj.qualityprize.mapper.*;
import com.majy.zlj.qualityprize.utils.BaiduDwz;
import me.chanjar.weixin.cp.api.WxCpOAuth2Service;
import me.chanjar.weixin.cp.api.WxCpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author majingyuan
 * @date 2017/5/28
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
    @Autowired
    private ScoreInfoMapper scoreInfoMapper;

    @Autowired
    WxCpService wxCpService;
    @Autowired
    WechatConfig wechatConfig;

    @RequestMapping("/getGameList")
    public Map<String,Object> getGameList(GameInfo gameInfo){
        Map<String,Object> map = new HashMap<String, Object>();
        if (gameInfo == null) {
            gameInfo = new GameInfo();
        }
        UserInfo loginUser = userInfoMapper.getUserInfoById(gameInfo.getAddBy());
        if (loginUser != null){
            // if (AppConstant.USER_ADMIN.equals(loginUser.getUserRole())){
            //     gameInfo.setAddBy("");
            // }
            gameInfo.setAddBy("");
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
        }

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
        gameInfo.setGroupInfoList(groupInfoMapper.getGroupListByGameId(gameInfo.getGameId()));
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
                    Map<String, String> url = getGameQrUrl(gameInfo.getGameId());
                    gameInfo.setGameQr(url.get("qr"));
                    gameInfo.setGameUrl(url.get("url"));
                    int result = gameInfoMapper.insert(gameInfo);
                    if (result > 0) {
                        //创建评分规则
                        createGameRole(gameInfo);
                        createGroup(gameInfo);
                        addFlag = "success";
                        addMessage = "比赛" + gameInfo.getGameName() + "创建成功，请为此次比赛添加选手。";
                    }
                } else {
                    //gameId存在修改比赛
                    ScoreInfo searchInfo = new ScoreInfo();
                    searchInfo.setGameId(gameInfo.getGameId());
                    if (scoreInfoMapper.getScoreListCount(searchInfo) > 0){
                        addMessage = "比赛已开始，不能进行修改";
                    } else {
                        int result = gameInfoMapper.update(gameInfo);
                        if (result > 0){
                            //更新评分规则
                            createGameRole(gameInfo);
                            createGroup(gameInfo);
                            addFlag = "success";
                            addMessage = "比赛" + gameInfo.getGameName() + "修改成功。";
                        }
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
        List<GameRoleInfo> roleInfoList = gameInfo.getGameRoleInfoList();
        List<GameRoleInfo> roleInfoOldList = gameRoleInfoMapper.getGameRoleListByGame(gameInfo.getGameId());
        Map<String,String> roleInfoListMap = new HashMap<>();

        if (roleInfoList != null && roleInfoList.size() > 0){
            //新增规则
            int i = 0;
            for (GameRoleInfo gameRoleInfo : roleInfoList){
                if (gameRoleInfo.getRoleId() != null && !"".equals(gameRoleInfo.getRoleId())){
                    //id存在执行更新操作
                    gameRoleInfoMapper.update(gameRoleInfo);
                    roleInfoListMap.put(gameRoleInfo.getRoleId(),gameRoleInfo.getRoleId());
                }else {
                    //ID不存在执行新增
                    gameRoleInfo.setRoleId(UUID.randomUUID().toString());
                    gameRoleInfo.setGameId(gameInfo.getGameId());
                    gameRoleInfoMapper.insert(gameRoleInfo);
                }
                i ++ ;
            }

            /**
             * 遍历旧的规则信息，如果Map中无此项则删除
             */
            if (!roleInfoListMap.isEmpty()){
                for (GameRoleInfo gameRoleInfo : roleInfoOldList){
                    String roleId = roleInfoListMap.get(gameRoleInfo.getRoleId());
                    if (roleId == null || "".equals(roleId)){
                        gameRoleInfoMapper.delete(gameRoleInfo.getRoleId());
                    }
                }
            }
        }
    }

    /**
     * 创建比赛分组
     * @param gameInfo
     */
    private void createGroup(GameInfo gameInfo){
        ///  groupInfoMapper.deleteByGameId(gameInfo.getGameId());
        List<GroupInfo> groupInfoList = gameInfo.getGroupInfoList();
        List<GroupInfo> groupInfoOldList = groupInfoMapper.getGroupListByGameId(gameInfo.getGameId());
        Map<String,String> groupInfoListMap = new HashMap<>();

        if (groupInfoList != null && groupInfoList.size() > 0){
            int i = 0;
            for(GroupInfo groupInfo : groupInfoList){
                if (groupInfo.getGroupId() != null && !"".equals(groupInfo.getGroupId())){
                    groupInfoMapper.update(groupInfo);
                    groupInfoListMap.put(groupInfo.getGroupId(),groupInfo.getGroupId());
                } else {
                    groupInfo.setGroupId(UUID.randomUUID().toString());
                    groupInfo.setGameId(gameInfo.getGameId());
                    groupInfoMapper.insert(groupInfo);
                }

                i ++;
            }

            /**
             * 遍历旧的分组信息，如果分组Map中无此项则删除
             */
            if (!groupInfoListMap.isEmpty()){
                for (GroupInfo groupInfo : groupInfoOldList){
                    String groupId = groupInfoListMap.get(groupInfo.getGroupId());
                    if (groupId == null || "".equals(groupId)){
                        groupInfoMapper.delete(groupInfo.getGroupId());
                    }
                }
            }
        }
    }

    /**
     * 获取比赛url
     * @param gameId
     * @return
     */
    private Map<String,String> getGameQrUrl(String gameId){
        Map<String,String> result = new HashMap<>();
        String redirectUrl = wechatConfig.getRedirectUrl();
        redirectUrl = redirectUrl.replace("GAMEID", gameId);
        WxCpOAuth2Service wxCpOAuth2Service = wxCpService.getOauth2Service();
        String url = wxCpOAuth2Service.buildAuthorizationUrl(redirectUrl, null);
        // String qr = BaiduDwz.createShortUrl(url,"1-year");
        result.put("qr",url);
        result.put("url",url);
        return result;
    }
}
