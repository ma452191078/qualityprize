package com.majy.zlj.qualityprize.controller;

import com.majy.zlj.qualityprize.constant.AppConstant;
import com.majy.zlj.qualityprize.domain.*;
import com.majy.zlj.qualityprize.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by majingyuan on 2017/5/28.
 * 参赛选手
 */
@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private PlayerInfoMapper playerInfoMapper;
    @Autowired
    private ScoreInfoMapper scoreInfoMapper;
    @Autowired
    private GameInfoMapper gameInfoMapper;
    @Autowired
    private GameRoleInfoMapper gameRoleInfoMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    /**
     * 多维度选手信息查询
     * @param playerInfo 参赛人信息作为查询条件
     * @return List<PlayerInfo> 选手列表
     */
    @RequestMapping("getPlayerList")
    public List<PlayerInfo> getPlayerList(PlayerInfo playerInfo){
        UserInfo loginUser = userInfoMapper.getUserInfoById(playerInfo.getPlayerAddBy());
        if (AppConstant.USER_ADMIN.equals(loginUser.getUserRole())){
            playerInfo.setPlayerAddBy("");
        }
        List<PlayerInfo> playerInfos = playerInfoMapper.getPlayerList(playerInfo);
        return playerInfos;
    }

    /**
     * 根据比赛ID查询对应选手列表，依据选手名次和选手序号排序
     * @param gameId 比赛id
     * @return List<PlayerInfo> 选手列表
     */
    @RequestMapping("/getPlayerListByGame")
    public Map<String, Object> getPlayerListByGame(@RequestParam("gameId") String gameId,
                                                   @RequestParam("judgeId") String judgeId){
        Map<String, Object> param = new HashMap<>();
        List<PlayerInfo> playerInfos = null;
        List<PlayerInfo> scoreList = null;
        GameInfo gameInfo = new GameInfo();
        List<PlayerInfo> playerScoreList = null;
        if (gameId != null && !"".equals(gameId)){
            gameInfo = gameInfoMapper.getGameInfoById(gameId);
            playerInfos = playerInfoMapper.getPlayerListByGameId(gameId);

            if (judgeId != null && playerInfos != null && playerInfos.size() > 0 ){
                Map<String, String> scoreMap = new HashMap<>();
                Map<String, String> searchMap = new HashMap<>();
                searchMap.put("gameId", gameId);
                searchMap.put("judgeId", judgeId);
                playerScoreList = playerInfoMapper.getAvgListByPlayerAndJudge(searchMap);
                scoreList = playerInfoMapper.getPlayerListByJudge(searchMap);
                if (scoreList != null && scoreList.size() > 0){

                    for (PlayerInfo item : scoreList) {
                        scoreMap.put(item.getPlayerId(), "1");
                    }
                }

                if (playerScoreList != null || playerScoreList.size() > 0){
                    for (PlayerInfo item : playerScoreList) {
                        scoreMap.put(item.getPlayerId()+"_score", item.getPlayerAverage().toString());
                    }
                }

                for (int i = 0; i < playerInfos.size(); i++) {
                    if ("1".equals(scoreMap.get(playerInfos.get(i).getPlayerId()))){
                        playerInfos.get(i).setPlayerIsScore("1");
                        playerInfos.get(i).setPlayerAverage(new BigDecimal(scoreMap.get(playerInfos.get(i).getPlayerId()+"_score")));
                    }else {
                        playerInfos.get(i).setPlayerIsScore("0");
                    }
                }
            }

            List<GameRoleInfo> result = gameRoleInfoMapper.getGameRoleListByGame(gameId);
            if (result != null && result.size() > 0){
                gameInfo.setGameRoleInfoList(result);
                gameInfo.setGroupInfoList(groupInfoMapper.getGroupListByGameId(gameId));
            }

        }
        param.put("gameInfo", gameInfo);
        param.put("playerList", playerInfos);
        return param;
    }

    /**
     * 根据比赛ID查询对应选手列表，依据选手名次和选手序号排序
     * @param playerInfo 比赛id
     * @return List<PlayerInfo> 选手列表
     */
    @RequestMapping("/getPlayerListByGameReact")
    public Map<String, Object> getPlayerListByGameReact(@RequestBody ReactPlayerInfo playerInfo){
        Map<String, Object> param = new HashMap<>();
        List<PlayerInfo> playerInfos = null;
        List<PlayerInfo> scoreList = null;
        GameInfo gameInfo = new GameInfo();
        PlayerInfo searchPlayer = new PlayerInfo();
        searchPlayer.setGameId(playerInfo.getGameId());
        searchPlayer.setGroupId(playerInfo.getGroupId());
        if (playerInfo != null && !"".equals(playerInfo.getGameId())){
            gameInfo = gameInfoMapper.getGameInfoById(playerInfo.getGameId());
            playerInfos = playerInfoMapper.getPlayerList(searchPlayer);

            if (playerInfo.getJudgeId() != null && playerInfos != null && playerInfos.size() > 0 ){
                Map<String, String> scoreMap = new HashMap<>();
                Map<String, String> searchMap = new HashMap<>();
                searchMap.put("gameId", playerInfo.getGameId());
                searchMap.put("judgeId", playerInfo.getJudgeId());

                scoreList = playerInfoMapper.getPlayerListByJudge(searchMap);
                if (scoreList != null && scoreList.size() > 0){

                    for (PlayerInfo item : scoreList) {
                        scoreMap.put(item.getPlayerId(), "1");
                    }
                }

                for (int i = 0; i < playerInfos.size(); i++) {

                    if ("1".equals(scoreMap.get(playerInfos.get(i).getPlayerId()))){
                        playerInfos.get(i).setPlayerIsScore("1");
                    }else {
                        playerInfos.get(i).setPlayerIsScore("0");
                    }
                }
            }

            List<GameRoleInfo> result = gameRoleInfoMapper.getGameRoleListByGame(playerInfo.getGameId());
            if (result != null && result.size() > 0){
                gameInfo.setGameRoleInfoList(result);
                gameInfo.setGroupInfoList(groupInfoMapper.getGroupListByGameId(playerInfo.getGameId()));
            }

        }
        param.put("gameInfo", gameInfo);
        param.put("playerList", playerInfos);
        return param;
    }


    /**
     * 根据选手id获取选手完整信息
     * @param playerId 参赛人id
     * @return PlayerInfo 参赛选手信息
     */
    @RequestMapping("/getPlayerInfoById")
    public PlayerInfo getPlayerInfoById(@RequestParam("playerId") String playerId){
        if (playerId != null && !"".equals(playerId)) {
            PlayerInfo playerInfo = playerInfoMapper.getPlayerInfoById(playerId);
            return playerInfo;
        }else {
            return null;
        }
    }

    /**
     * 创建选手信息
     * @param playerInfo 参赛人信息
     * @return 返回执行结果
     */
    @RequestMapping("/addPlayerInfo")
    public Map<String, Object> addPlayerInfo(PlayerInfo playerInfo){
        Map<String, Object> param = new HashMap<>();
        String addFlag = "failed";
        String addMessage = "添加失败请稍后重试。";

        if (playerInfo != null && playerInfo.getPlayerName() != null
                && !"".equals(playerInfo.getPlayerName())){
            //检查序号是否存在
            PlayerInfo searchInfo = new PlayerInfo();
            searchInfo.setGameId(playerInfo.getGameId());
            searchInfo.setPlayerNum(playerInfo.getPlayerNum());
            List<PlayerInfo> searchList = playerInfoMapper.getPlayerList(searchInfo);
            if (searchList != null && searchList.size() > 0){
                addMessage = "出场顺序重复，请重新填写出场顺序。";
            }else {
                //无重复出场顺序执行下一步操作
                playerInfo.setPlayerId(UUID.randomUUID().toString());
                int result = playerInfoMapper.insert(playerInfo);
                if (result > 0){
                    addFlag = "success";
                    addMessage = "选手" + playerInfo.getPlayerName() + "创建成功。";
                    param.put("playerInfo", playerInfo);
                }
            }
        }

        param.put("flag", addFlag);
        param.put("message", addMessage);
        return param;
    }

    /**
     * 更新选手信息
     * @param playerInfo 参赛人信息
     * @return 返回执行结果
     */
    @RequestMapping("/updatePlayerInfo")
    public Map<String,String> updatePlayerInfo(PlayerInfo playerInfo){
        Map<String, String> param = new HashMap<>();
        String addFlag = "failed";
        String addMessage = "修改失败，请稍后重试。";

        if (playerInfo != null && playerInfo.getPlayerId() != null
                && !"".equals(playerInfo.getPlayerId())){
            int result = playerInfoMapper.update(playerInfo);
            if (result > 0){
                addFlag = "success";
                addMessage = "选手" + playerInfo.getPlayerName() + "修改成功。";
            }
        }

        param.put("flag", addFlag);
        param.put("message", addMessage);
        return param;
    }

    /**
     * 选手评分终止，计算选手最终得分
     * @param playerId 参赛人id
     * @return 返回执行结果
     */
    @RequestMapping("/killPlayerInfo")
    public Map<String,String> killPlayerInfo(@RequestParam("playerId") String playerId){
        Map<String, String> param = new HashMap<>();
        String addFlag = "failed";
        String addMessage = "评分终止失败，请稍后重试。";
        //总得分
        BigDecimal sumScore = new BigDecimal(0.00);
        //平均分
        BigDecimal avgScore = new BigDecimal(0.00);

        if (playerId != null && !"".equals(playerId)){
            //选手得分计算方法
            List<ScoreInfo> scoreInfos = scoreInfoMapper.getScoreListByPlayer(playerId);
            if (scoreInfos != null && scoreInfos.size() > 0){
//                if (scoreInfos.size() > 2) {
//                    scoreInfos.remove(0);   //去除最低分
//                    scoreInfos.remove(scoreInfos.size() - 1); //去掉最高分
//                }

                //求得总分
                for (ScoreInfo item : scoreInfos){
                    sumScore = sumScore.add(item.getScoreValue());
                }
                //计算平均分
                avgScore = sumScore.divide(new BigDecimal(scoreInfos.size()),2,BigDecimal.ROUND_HALF_UP);

            }
            //选手状态变更
            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.setPlayerId(playerId);
            playerInfo.setPlayerActive("1");
            playerInfo.setPlayerSum(sumScore);
            playerInfo.setPlayerAverage(avgScore);
            int result = playerInfoMapper.update(playerInfo);
            if (result > 0){
                playerInfo = playerInfoMapper.getPlayerInfoById(playerId);
                PlayerInfo searchInfo = new PlayerInfo();
                searchInfo.setPlayerActive("1");
                searchInfo.setGameId(playerInfo.getGameId());
                List<PlayerInfo> playerInfoList = playerInfoMapper.getPlayerList(searchInfo);
                for (int i = 0; i < playerInfoList.size(); i++){
                    playerInfoList.get(i).setPlayerRanking(i + 1);
                    playerInfoMapper.update(playerInfoList.get(i));
                }
                addFlag = "success";
                addMessage = "选手" + playerInfo.getPlayerName() + "评分已终止。";
            }

        }

        param.put("flag", addFlag);
        param.put("message", addMessage);
        return param;
    }

    @RequestMapping("/delPlayer")
    public Map<String, Object> delPlayer(@RequestParam("playerId") String playerId){
        Map<String, Object> param = new HashMap<>();
        String addFlag = "failed";

        if (playerId != null && !"".equals(playerId)){
            int result = playerInfoMapper.delete(playerId);
            if (result > 0){
                addFlag = "success";
            }
        }
        param.put("flag", addFlag);
        return param;
    }

    /**
     * 查询比赛结果
     * @param gameId
     * @return
     */
    @RequestMapping("/getPlayerScoreListFroResult")
    public Map<String, Object> getPlayerScoreListFroResult(String gameId){
        Map<String, Object> param = new HashMap<>();
        List<PlayerInfo> playerResult = new ArrayList<>();
        List<PlayerInfo> tempPlayerList = new ArrayList<>();

        GameInfo gameInfo = gameInfoMapper.getGameInfoById(gameId);
        List<GroupInfo> groupList = groupInfoMapper.getGroupListByGameId(gameId);
        List<PlayerInfo> playerInfos = playerInfoMapper.getAvgListByPlayer(gameId);
        List<PlayerInfo> departmentResult = playerInfoMapper.getAvgListByDepartment(gameId);
        //对选手得分进行排序

        if (playerInfos.size() > 0){
            //遍历分组，对每个分组样品得分进行排序
            for (int i = 0; i < groupList.size(); i++) {
                for (PlayerInfo playerInfo : playerInfos) {
                    if (playerInfo.getGroupId().equals(groupList.get(i).getGroupId())){
                        tempPlayerList.add(playerInfo);
                    }
                }
                //排序完成加入传到前台的数组中
                if (tempPlayerList.size() > 0){
                    sortPlayerScore(tempPlayerList);
                    playerResult.addAll(tempPlayerList);
                }
                tempPlayerList.clear();
            }
        }
        //对公司得分进行排序
        sortPlayerScore(departmentResult);

        param.put("gameInfo", gameInfo);
        param.put("playerResult", playerResult);
        param.put("departmentResult", departmentResult);
        param.put("groupList", groupList);
        return param;
    }


    @RequestMapping("/getPlayerScoreListByJudge")
    public Map<String, Object> getPlayerScoreListByJudge(@RequestParam("judgeId") String judgeId,
                                                         @RequestParam("gameId") String gameId){
        Map<String, Object> param = new HashMap<>(16);
        List<PlayerInfo> playerResult;
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("judgeId", judgeId);
        searchMap.put("gameId", gameId);
        playerResult = playerInfoMapper.getPlayerScoreListByJudge(searchMap);

        param.put("playerResult", playerResult);
        return param;
    }


    /**
     * 排序
     * @param playerInfoList
     * @return
     */
    private List<PlayerInfo> sortPlayerScore(List<PlayerInfo> playerInfoList){
        PlayerInfo tempPlayer = new PlayerInfo();
        if (playerInfoList != null && playerInfoList.size() > 0){
            for (int i = 0; i < playerInfoList.size(); i++) {
                if (i > 0 && playerInfoList.get(i).getPlayerAverage().equals(tempPlayer.getPlayerAverage())){
                    playerInfoList.get(i).setPlayerRanking(tempPlayer.getPlayerRanking());
                }else{
                    playerInfoList.get(i).setPlayerRanking(i + 1);
                }
                tempPlayer = playerInfoList.get(i);
            }
        }

        return playerInfoList;
    }
}
