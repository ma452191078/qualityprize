package com.majy.zlj.qualityprize.controller;

import com.majy.zlj.qualityprize.constant.AppConstant;
import com.majy.zlj.qualityprize.domain.*;
import com.majy.zlj.qualityprize.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

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

    @Autowired
    PlayerInfoMapper playerInfoMapper;

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
     * 获取最终得分
     * @param gameId
     * @return
     */
    @RequestMapping("/getFinalResult")
    public Map<String, Object> getFinalResult(String gameId){
        Map<String, Object> result = new HashMap<>();
        //样品平均分
        BigDecimal simpleAvg;
        //样品总得分
        BigDecimal simpleSum;
        Integer simpleNum;
        //总得分
        BigDecimal scoreSum ;
        BigDecimal pointThree = new BigDecimal(0.3);
        BigDecimal pointOne = new BigDecimal(0.1);
        Integer pointNum = 2;

        //查询单位得分表头列表
        List<DepartmentScoreInfo> departmentScoreInfoList = departmentScoreInfoMapper.getDepartmentScoreList(gameId);
        List<PlayerInfo> playerScoreList = playerInfoMapper.getAvgListByPlayer(gameId);

        if (departmentScoreInfoList != null && departmentScoreInfoList.size() > 0){
            for (int i = 0; i < departmentScoreInfoList.size(); i++) {
                simpleSum = new BigDecimal(0.00);
                String scoreId = departmentScoreInfoList.get(i).getScoreId();
                departmentScoreInfoList.get(i).setScoreDetailInfoList(detailInfoMapper.getDepartmentScoreDetailList(scoreId));
                departmentScoreInfoList.get(i).setGroupCount(departmentScoreInfoList.get(i).getScoreDetailInfoList().size());

                List<DepartmentScoreDetailInfo> detailList = departmentScoreInfoList.get(i).getScoreDetailInfoList();
                if (detailList != null && detailList.size() > 0){
                    for (int j = 0; j < detailList.size(); j++) {
                        //计算每个工艺的得分
                        simpleNum = 0;
                        simpleAvg = new BigDecimal(0.00);
                        for (int k = 0; k < playerScoreList.size(); k++) {
                            //查找部门名称一致且分组一致的数据，计算分组得分
                            if (departmentScoreInfoList.get(i).getDepartmentName().equals(playerScoreList.get(k).getPlayerDepartment())
                                    && detailList.get(j).getGroupId().equals(playerScoreList.get(k).getGroupId())){
                                //计算分组样品平均分的总分
                                simpleAvg = simpleAvg.add(playerScoreList.get(k).getPlayerAverage());
                                //分组样品数量
                                simpleNum = simpleNum + 1;
                            }
                        }
                        if (simpleNum > 0){
                            //根据分组平均分的总分/分组数量得到平均分，生产中心要求以此计算
                            simpleAvg = simpleAvg.divide(new BigDecimal(simpleNum), pointNum, BigDecimal.ROUND_HALF_UP);
                            detailList.get(j).setScore1(simpleAvg);
                            //平均分-养分扣分=分组最终得分
                            detailList.get(j).setScoreSum(simpleAvg.subtract(detailList.get(j).getScore2()));
                            //计算样品总得分
                            simpleSum = simpleSum.add(detailList.get(j).getScoreSum()).setScale(pointNum, BigDecimal.ROUND_HALF_UP);;
                            departmentScoreInfoList.get(i).setScore1(simpleSum.setScale(pointNum, BigDecimal.ROUND_HALF_UP));
                        }
                    }

                    departmentScoreInfoList.get(i).setScoreDetailInfoList(detailList);
                    //计算单位的最终得分
                    //总得分=厂内考评得分*30%+市场产品考评得分*30%+(A区得分+B区得分)/2*30%+销售满意度测评得分*10%
                    scoreSum = new BigDecimal(0.00);
                    //样品得分 = 市场大比 武得分
                    ///scoreSum = scoreSum.add((departmentScoreInfoList.get(i).getScore1().divide(new BigDecimal(2))).multiply(pointThree));
                    departmentScoreInfoList.get(i).setScore2(departmentScoreInfoList.get(i).getScore1().divide(new BigDecimal(2), pointNum, BigDecimal.ROUND_HALF_UP));
                    scoreSum = scoreSum.add(departmentScoreInfoList.get(i).getScore2().multiply(pointThree));
                    //厂内产品 考评得分
                    scoreSum = scoreSum.add(departmentScoreInfoList.get(i).getScore3().multiply(pointThree));
                    //市场产品 考评得分
                    scoreSum = scoreSum.add(departmentScoreInfoList.get(i).getScore4().multiply(pointThree));
                    //销售市场满意度测评得分
                    scoreSum = scoreSum.add(departmentScoreInfoList.get(i).getScore5().multiply(pointOne));
                    //总裁质量 奖得分
                    departmentScoreInfoList.get(i).setScoreAverage(scoreSum.setScale(pointNum, BigDecimal.ROUND_HALF_UP));
                }
            }

            //排序
            if (departmentScoreInfoList.size()>0){
                Collections.sort(departmentScoreInfoList, new Comparator<DepartmentScoreInfo>() {
                    @Override
                    public int compare(DepartmentScoreInfo o1, DepartmentScoreInfo o2) {
                        return o2.getScoreAverage().compareTo(o1.getScoreAverage());
                    }
                });
                departmentScoreInfoList = sortDepartmentScore(departmentScoreInfoList);
            }
        }

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
        int errFlag = AppConstant.DB_WRITE_FAILED;
        String errMsg = "保存失败，请重试";
        //得分列表不是空
        if (requestList != null && requestList.getScoreList() != null && requestList.getScoreList().size() > 0){
            departmentScoreInfoMapper.deleteByGameId(requestList.getGameId());
            for (DepartmentScoreInfo scoreInfo : requestList.getScoreList()){
                if (scoreInfo != null && scoreInfo.getScore2() != null) {
                    scoreInfo.setScoreId(UUID.randomUUID().toString());
                    if (departmentScoreInfoMapper.insert(scoreInfo) > 0){
                        if (scoreInfo.getScoreDetailInfoList() != null && scoreInfo.getScoreDetailInfoList().size() > 0){
                            for (DepartmentScoreDetailInfo detailInfo : scoreInfo.getScoreDetailInfoList()) {
                                if (detailInfo.getScore2() != null ){
                                    detailInfo.setScoreItemId(UUID.randomUUID().toString());
                                    detailInfo.setScoreId(scoreInfo.getScoreId());
                                    if (detailInfoMapper.insert(detailInfo) > 0){
                                        errFlag = AppConstant.DB_WRITE_SUCCESS;
                                        errMsg = "保存成功";
                                    }else{
                                        errFlag = AppConstant.DB_WRITE_FAILED;
                                        errMsg = "保存失败，请重试";
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }else {
                    errFlag = AppConstant.DB_WRITE_FAILED;
                    errMsg = "保存失败，请重试";
                    break;
                }
            }
        }
        result.put("errMsg", errMsg);
        result.put("errFlag", errFlag);
        return result;
    }

    /**
     * 排序
     * @param departmentScoreInfoList
     * @return
     */
    private List<DepartmentScoreInfo> sortDepartmentScore(List<DepartmentScoreInfo> departmentScoreInfoList){
        DepartmentScoreInfo tempPlayer = new DepartmentScoreInfo();
        if (departmentScoreInfoList != null && departmentScoreInfoList.size() > 0){

            for (int i = 0; i < departmentScoreInfoList.size(); i++) {
                if (i > 0 && departmentScoreInfoList.get(i).getScoreAverage().equals(tempPlayer.getScoreAverage())){
                    departmentScoreInfoList.get(i).setDepartmentRanking(tempPlayer.getDepartmentRanking());
                }else{
                    departmentScoreInfoList.get(i).setDepartmentRanking(i + 1);
                }
                tempPlayer = departmentScoreInfoList.get(i);
            }
        }

        return departmentScoreInfoList;
    }
}

