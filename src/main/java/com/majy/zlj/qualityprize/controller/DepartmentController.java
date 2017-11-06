package com.majy.zlj.qualityprize.controller;

import com.majy.zlj.qualityprize.domain.*;
import com.majy.zlj.qualityprize.mapper.DepartmentMapper;
import com.majy.zlj.qualityprize.mapper.DepartmentScoreDetailInfoMapper;
import com.majy.zlj.qualityprize.mapper.DepartmentScoreInfoMapper;
import com.majy.zlj.qualityprize.mapper.GroupInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author majingyuan
 * @Date Create in 2017/10/31 16:32
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    DepartmentScoreInfoMapper departmentScoreInfoMapper;

    @Autowired
    DepartmentScoreDetailInfoMapper detailInfoMapper;

    @Autowired
    GroupInfoMapper groupInfoMapper;

    @Autowired
    DepartmentMapper departmentMapper;

    /**
     * 查询比赛的部门列表
     * @param gameId 比赛ID
     * @return 返回比分组、单位列表
     */
    @RequestMapping("/getDepartmentList")
    public Map<String, Object> getDepartmentList(String gameId){
        Map<String, Object> result = new HashMap<>();

        //查询单位列表
        List<DepartmentInfo> departmentInfoList = departmentMapper.getDepartmentList(gameId);

        //查询分组数量
        Integer groupCount = 0;
        //查询分组信息
        List<GroupInfo> groupInfoList = groupInfoMapper.getScoreGroupListByGameId(gameId);
        if (groupInfoList != null){
            groupCount = groupInfoList.size();
        }

        //查询单位得分表头列表
        List<DepartmentScoreInfo> departmentScoreInfoList = departmentScoreInfoMapper.getDepartmentScoreList(gameId);
        //查询明细加入表头
        if (departmentScoreInfoList != null && departmentScoreInfoList.size() > 0){

            //完善列表
            for (int i = 0; i < departmentScoreInfoList.size(); i++) {
                String scoreId = departmentScoreInfoList.get(i).getScoreId();
                departmentScoreInfoList.get(i).setScoreDetailInfoList(detailInfoMapper.getDepartmentScoreDetailList(scoreId));
                departmentScoreInfoList.get(i).setGroupCount(groupCount);
            }
        }

        result.put("departmentInfoList", departmentInfoList);
        result.put("groupInfoList", groupInfoList);
        result.put("departmentScoreInfoList", departmentScoreInfoList);
        return result;
    }

    /**
     * 保存之前的得分记录
     * @param requestList 得分列表
     * @return  新增结果
     */
    @RequestMapping("/saveDepartmentList")
    public Map<String, Object> saveDepartmentList(@RequestBody DepartmentList requestList){
        Map<String, Object> result = new HashMap<>();
        //得分列表不是空
        if (requestList != null && requestList.getScoreList() != null && requestList.getScoreList().size() > 0){
            for (DepartmentScoreInfo scoreInfo : requestList.getScoreList()){

                if (scoreInfo != null && scoreInfo.getScore2() != null) {
                    departmentScoreInfoMapper.deleteByGameId(scoreInfo.getGameId());
                    scoreInfo.setScoreId(UUID.randomUUID().toString());
                    if (departmentScoreInfoMapper.insert(scoreInfo) > 0){
                        if (scoreInfo.getScoreDetailInfoList() != null && scoreInfo.getScoreDetailInfoList().size() > 0){
                            for (DepartmentScoreDetailInfo detailInfo : scoreInfo.getScoreDetailInfoList()) {
                                if (detailInfo.getScore2() != null ){
                                    detailInfoMapper.deleteByParentId(scoreInfo.getScoreId());
                                    detailInfo.setScoreItemId(UUID.randomUUID().toString());
                                    detailInfo.setScoreId(scoreInfo.getScoreId());
                                    detailInfoMapper.insert(detailInfo);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
