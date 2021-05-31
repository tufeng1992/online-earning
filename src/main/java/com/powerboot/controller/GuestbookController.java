package com.powerboot.controller;

import com.google.common.collect.Lists;
import com.powerboot.base.BaseResponse;
import com.powerboot.common.JsonUtils;
import com.powerboot.domain.*;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.request.BaseRequest;
import com.powerboot.request.order.OrderGrabbingRequest;
import com.powerboot.request.order.OrderRequest;
import com.powerboot.response.GuestbookDTO;
import com.powerboot.response.OrderGrabbingDto;
import com.powerboot.response.OrderInfoDto;
import com.powerboot.response.OrderListDto;
import com.powerboot.service.*;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/guestbook")
@Api(tags = "留言板信息")
public class GuestbookController extends BaseController {

    @Autowired
    private GuestbookService guestbookService;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取留言板列表")
    @PostMapping("/getGuestbookList")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<List<GuestbookDTO>> getUserGuestbookList(@RequestBody @Valid BaseRequest param) {
        Long userId = getUserId(param);
        BaseResponse<List<GuestbookDTO>> response = BaseResponse.success();
        List<GuestbookDTO> list = Lists.newArrayList();
        response.setResultData(list);
        UserDO userDO = userService.get(userId);
        if (null == userDO) {
            return response;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("guestbookTargetId", userId);
        List<GuestbookDO> guestbookDOList = guestbookService.list(map);
        guestbookDOList.forEach(o -> {
            GuestbookDTO guestbookDTO = new GuestbookDTO();
            BeanUtils.copyProperties(o, guestbookDTO);
            response.getResultData().add(guestbookDTO);
            if (!o.getReaded()) {
                o.setReaded(true);
                guestbookService.update(o);
            }
        });
        return response;
    }

    @ApiOperation(value = "获取未读数量")
    @PostMapping("/getUnreadCount")
    @ResponseBody
    public BaseResponse<Integer> getUnreadCount(@RequestBody @Valid BaseRequest param) {
        Long userId = getUserId(param);
        BaseResponse<Integer> response = BaseResponse.success();
        response.setResultData(0);
        UserDO userDO = userService.get(userId);
        if (null == userDO) {
            return response;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("guestbookTargetId", userId);
        map.put("readed", false);
        Integer count = guestbookService.selectUnreadCount(map);
        response.setResultData(count);
        return response;
    }

}
