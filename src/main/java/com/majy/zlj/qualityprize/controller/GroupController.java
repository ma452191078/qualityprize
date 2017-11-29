package com.majy.zlj.qualityprize.controller;

import com.majy.zlj.qualityprize.constant.AppConstant;
import com.majy.zlj.qualityprize.domain.GroupInfo;
import com.majy.zlj.qualityprize.mapper.GroupInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author majingyuan
 * @date Create in 2017/10/26 20:59
 */

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupInfoMapper groupInfoMapper;

    private String errFlag = AppConstant.REQUEST_ERROR;
    private String errMsg = AppConstant.REQUEST_ERROR_VALUE;
    private final String ERRFLAG = "errFlag";
    private final String ERRMSG = "errMsg";
    private final String RESULT = "result";

    /**
     * 查询比赛的分组列表
     * @param
     * @return
     */
    @RequestMapping("/getGroupInfoList")
    public Map<String, Object> getGroupInfoList(String gameId){
        Map<String, Object> result = new HashMap<>();
        List<GroupInfo> groupInfoList = null;
        if (gameId != null && !"".equals(gameId)){
            GroupInfo searchInfo = new GroupInfo();
            searchInfo.setGameId(gameId);
            groupInfoList = groupInfoMapper.getGroupList(searchInfo);

            if (groupInfoList != null && groupInfoList.size() > 0){
                errFlag = AppConstant.REQUEST_SUCCESS;
                errMsg = AppConstant.REQUEST_SUCCESS_VALUE;
            }
        }

        result.put(ERRFLAG, errFlag);
        result.put(ERRMSG, errMsg);
        result.put(RESULT, groupInfoList);

        return result;
    }

    /**
     * 查询比赛的分组列表
     * @param
     * @return
     */
    @RequestMapping("/getGroupInfoListReact")
    public Map<String, Object> getGroupInfoListReact(@RequestBody GroupInfo groupInfo){
        Map<String, Object> result = new HashMap<>();
        List<GroupInfo> groupInfoList = null;
        if (groupInfo != null && !"".equals(groupInfo.getGameId())){
            GroupInfo searchInfo = new GroupInfo();
            searchInfo.setGameId(groupInfo.getGameId());
            groupInfoList = groupInfoMapper.getGroupList(searchInfo);

            if (groupInfoList != null && groupInfoList.size() > 0){
                errFlag = AppConstant.REQUEST_SUCCESS;
                errMsg = AppConstant.REQUEST_SUCCESS_VALUE;
            }
        }

        result.put(ERRFLAG, errFlag);
        result.put(ERRMSG, errMsg);
        result.put(RESULT, groupInfoList);

        return result;
    }

    /**
     * 查询比赛的分组列表
     * @param gameId 比赛ID
     * @return
     */
    @RequestMapping(value = "/getScoreGroupInfoList")
    public Map<String, Object> getScoreGroupInfoList(@RequestParam("gameId") String gameId){
        Map<String, Object> result = new HashMap<>();

        List<GroupInfo> groupInfoList = groupInfoMapper.getScoreGroupListByGameId(gameId);

        if (groupInfoList != null && groupInfoList.size() > 0){
            errFlag = AppConstant.REQUEST_SUCCESS;
            errMsg = AppConstant.REQUEST_SUCCESS_VALUE;
        }

        result.put(ERRFLAG, errFlag);
        result.put(ERRMSG, errMsg);
        result.put(RESULT, groupInfoList);

        return result;
    }

    /**
     * 创建一个分组
     * @param groupInfo 分组信息
     * @return
     */
    @RequestMapping("/createGroup")
    public Map<String, Object> createGroup(GroupInfo groupInfo){
        Map<String, Object> result = new HashMap<>();

        if (groupInfo == null && !"".equals(groupInfo.getGroupName())) {
            groupInfo.setGroupId(UUID.randomUUID().toString());
            if (groupInfoMapper.insert(groupInfo) > 0){
                errFlag = AppConstant.REQUEST_SUCCESS;
                errMsg = AppConstant.REQUEST_SUCCESS_VALUE;
            }
        }
        result.put(ERRFLAG, errFlag);
        result.put(ERRMSG, errMsg);

        return result;
    }

    /**
     * 删除一个分组
     * @param groupId 分组ID
     * @return
     */
    @RequestMapping("/deleteGroup")
    public Map<String, Object> deleteGroup(String groupId){
        Map<String, Object> result = new HashMap<>();

        if (groupId == null && !"".equals(groupId)) {
            if (groupInfoMapper.delete(groupId) > 0){
                errFlag = AppConstant.REQUEST_SUCCESS;
                errMsg = AppConstant.REQUEST_SUCCESS_VALUE;
            }
        }
        result.put(ERRFLAG, errFlag);
        result.put(ERRMSG, errMsg);

        return result;
    }
}
