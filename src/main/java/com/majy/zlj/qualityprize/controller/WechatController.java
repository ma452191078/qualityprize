package com.majy.zlj.qualityprize.controller;

import com.majy.zlj.qualityprize.configuration.WechatConfig;
import com.majy.zlj.qualityprize.constant.AppConstant;
import com.majy.zlj.qualityprize.domain.GameInfo;
import com.majy.zlj.qualityprize.mapper.GameInfoMapper;
import com.mysql.jdbc.StringUtils;
import groovy.util.logging.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpOAuth2Service;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.codehaus.groovy.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author majingyuan
 * @date Create in 2017/11/23 16:50
 */

@RestController
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    @Autowired
    WxCpService wxCpService;
    @Autowired
    WechatConfig wechatConfig;
    @Autowired
    GameInfoMapper gameInfoMapper;

    @RequestMapping("/authorize")
    public Map<String, String> authorize(@RequestParam("gameId") String gameId){
        GameInfo gameInfo = gameInfoMapper.getGameInfoById(gameId);
        String url = "";
        String qr = "";
        if (gameInfo != null) {
            if (gameInfo.getGameQr() != null && !"".equals(gameInfo.getGameQr())){
                qr = gameInfo.getGameQr();
            }
            if (gameInfo.getGameUrl() != null && !"".equals(gameInfo.getGameUrl())){
                url = gameInfo.getGameUrl();
            } else {
                url = getUrl(gameId);
                qr = url;
            }
        } else {
            url = getUrl(gameId);
            qr = url;
        }
        Map<String, String> param = new HashMap<>();
        param.put("url", url);
        param.put("qr", qr);
        return param;
    }

    /**
     * 获取url
     * @param gameId
     * @return
     */
    private String getUrl(String gameId){
        String redirectUrl = wechatConfig.getRedirectUrl();
        redirectUrl = redirectUrl.replace("GAMEID", gameId);
        WxCpOAuth2Service wxCpOAuth2Service = wxCpService.getOauth2Service();
        String url = wxCpOAuth2Service.buildAuthorizationUrl(redirectUrl, null);
        return url;
    }

    @RequestMapping("/getUserInfo")
    public String getUserInfo(@RequestParam("code") String code) throws WxErrorException {
        WxCpOAuth2Service wxCpOAuth2Service = wxCpService.getOauth2Service();
        String[] res = wxCpOAuth2Service.getUserInfo(code);
        WxCpUser wxCpUser = wxCpService.getUserService().getById(res[0]);
        return wxCpUser.toJson();
    }

    @RequestMapping("/getJsapiTicket")
    public Map<String, Object> getJsapiTicket(@RequestParam("pageUrl") String pageUrl){
        Map<String, Object> map = new HashMap<>(16);
        int errFlag = 99;
        String errMsg = "信息创建失败，请重试";

        try {
            String jsapiTicket = wxCpService.getJsapiTicket();
            WxJsapiSignature wxJsapiSignature = wxCpService.createJsapiSignature(pageUrl);
            map.put("jsapiTicket", jsapiTicket);
            map.put("wxJsapiSignature", wxJsapiSignature);

            errFlag = AppConstant.DB_WRITE_SUCCESS;
        } catch (WxErrorException e) {
            errFlag = AppConstant.DB_WRITE_FAILED;
            e.printStackTrace();
        }

        map.put("errFlag", errFlag);
        return map;
    }
}
