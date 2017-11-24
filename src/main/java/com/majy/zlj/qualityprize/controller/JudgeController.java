package com.majy.zlj.qualityprize.controller;

import com.majy.zlj.qualityprize.constant.AppConstant;
import com.majy.zlj.qualityprize.domain.JudgeInfo;
import com.majy.zlj.qualityprize.mapper.JudgeInfoMapper;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpOAuth2Service;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 评委
 * @author majingyuan
 * @date 2017/5/2
 */
@RestController
@RequestMapping("/judge")
public class JudgeController {

    @Autowired
    private JudgeInfoMapper judgeInfoMapper;

    @Autowired
    private WxCpService wxCpService;

    /**
     * 查询裁判信息
     * @param judgeId
     * @return
     */
    @RequestMapping("/getJudgeInfo")
    public Map<String,Object> getJudgeInfo(String judgeId){
        Map<String,Object> map = new HashMap<String, Object>();
        JudgeInfo judgeInfo = judgeInfoMapper.getJudgeInfoById(judgeId);

        map.put("judgeInfo", judgeInfo);
        return map;
    }

    /**
     * 根据比赛查询裁判列表
     * @param gameId
     * @return
     */
    @RequestMapping("/getJudgeListByGame")
    public Map<String, Object> getJudgeListByGame(String gameId){
        Map<String,Object> map = new HashMap<String, Object>();
        List<JudgeInfo> judgeInfoList = judgeInfoMapper.getJudgeInfoByGameId(gameId);

        map.put("judgeInfoList", judgeInfoList);
        return map;
    }

    /**
     * 创建评委
     * @param judgeInfo
     * @return
     */
    @RequestMapping("/createJudge")
    public Map<String, Object> createJudge(@RequestParam("gameId") String gameId, @RequestParam("code") String code) throws WxErrorException {
        Map<String,Object> map = new HashMap<String, Object>();
        int errFlag = AppConstant.DB_WRITE_FAILED;
        String errMsg = "信息创建失败，请重试";
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setGameId(gameId);
        if (code != null){
            WxCpOAuth2Service wxCpOAuth2Service = wxCpService.getOauth2Service();
            String[] res = wxCpOAuth2Service.getUserInfo(code);
            WxCpUser wxCpUser = wxCpService.getUserService().getById(res[0]);

            if (wxCpUser != null){
                judgeInfo.setJudgeId(wxCpUser.getUserId());
                judgeInfo.setJudgeName(wxCpUser.getName());
            }
        } else {
            judgeInfo.setJudgeId(UUID.randomUUID().toString());
            judgeInfo.setJudgeName(AppConstant.JUDGE_NAME);
        }
        if (judgeInfoMapper.insert(judgeInfo) > 0){
            errFlag = AppConstant.DB_WRITE_SUCCESS;
            errMsg = "创建成功";
        }

        map.put("judgeInfo", judgeInfo);
        map.put("errFlag", errFlag);
        map.put("errMsg", errMsg);
        return map;

    }
    
}
