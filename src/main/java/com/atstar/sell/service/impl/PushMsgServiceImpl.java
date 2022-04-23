package com.atstar.sell.service.impl;

import com.atstar.sell.config.WechatAccountConfig;
import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.service.PushMsgService;
import com.atstar.sell.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Dawn
 * @Date: 2022/4/22 15:29
 */
@Service
@Slf4j
public class PushMsgServiceImpl implements PushMsgService {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private WechatAccountConfig wechatAccountConfig;

    @Override
    public void pushOrderStatus(OrderDTO orderDTO) {

        WxMpTemplateMessage mpTemplateMessage = new WxMpTemplateMessage();
        mpTemplateMessage.setToUser(orderDTO.getBuyerOpenid());
        mpTemplateMessage.setTemplateId(wechatAccountConfig.getTemplateId().get("orderStatus"));

        List<WxMpTemplateData> data = Arrays.asList(
                new WxMpTemplateData("first", "亲，请记得收获！"),
                new WxMpTemplateData("keyword1", "微信点餐", "#173177"),
                new WxMpTemplateData("keyword2", "15918649241", "#173177"),
                new WxMpTemplateData("keyword3", String.valueOf(orderDTO.getOrderId()), "#173177"),
                new WxMpTemplateData("keyword4", orderDTO.getOrderStatusEnum().getMsg(), "#173177"),
                new WxMpTemplateData("keyword5", "￥" + orderDTO.getOrderAmount(), "#173177"),
                new WxMpTemplateData("remark", "亲，记得五星好评哦！")
        );

        mpTemplateMessage.setData(data);

        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(mpTemplateMessage);

        } catch (WxErrorException e) {
            log.error("【微信模板消息】发生失败{}", JsonUtil.print(e));
        }

    }
}
