package com.powerboot.controller;

import com.google.common.collect.Maps;
import com.powerboot.base.BaseResponse;
import com.powerboot.common.JsonUtils;
import com.powerboot.consts.DictAccount;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.OrderDO;
import com.powerboot.domain.ProductDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.request.order.OrderGrabbingRequest;
import com.powerboot.request.order.OrderRequest;
import com.powerboot.response.OrderGrabbingDto;
import com.powerboot.response.OrderInfoDto;
import com.powerboot.response.OrderListDto;
import com.powerboot.service.*;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;


@RestController
@RequestMapping("/order")
@Api(tags = "刷单信息")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private EhcacheService ehcacheService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserTaskOrderMissionService userTaskOrderMissionService;


    private final BigDecimal hundred = new BigDecimal(100);
    private final BigDecimal four_hundred = new BigDecimal(400);
    private final BigDecimal five_hundred = new BigDecimal("500");
    private final long registerDay = 5L;

    @ApiOperation(value = "抢单接口orderGrabbing")
    @PostMapping("/orderGrabbing")
    @ResponseBody
    public BaseResponse<OrderGrabbingDto> orderGrabbing(@RequestBody @Valid OrderRequest param) {
        BaseResponse<OrderGrabbingDto> response = BaseResponse.success();
        OrderGrabbingDto orderGrabbingDto = new OrderGrabbingDto();

        ProductDO productDO = productService.get(param.getProductId());

        orderGrabbingDto.setOrderNumber(DateUtils.generateNumber());
        orderGrabbingDto.setCapTureTime(new Date());

        String[] balanceArr = productDO.getBalanceInfo().split(",");

        orderGrabbingDto.setRatio(new BigDecimal(balanceArr[3]).divide(new BigDecimal(100)));

        response.setResultData(orderGrabbingDto);
        return response;
    }


    @ApiOperation(value = "获取刷单列表")
    @PostMapping("/getOrderList")
    @ResponseBody
    public BaseResponse<OrderListDto> getOrderList(@RequestBody @Valid OrderRequest param) {
        Long userId = getUserId(param);
        BaseResponse<OrderListDto> response = BaseResponse.success();
        OrderListDto orderListDto = new OrderListDto();

        UserDO userDO = userService.get(userId);

        //获取分页参数
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        Integer totalCount = orderService.count(map);


        List<OrderDO> todayList = orderService.getTodayList(userId);
        HashMap<Integer, List<Integer>> vipInfo = ehcacheService.getVipInfo();
        //获取今日已刷单数量
        orderListDto.setTodayOrder(todayList.size() + "/" + vipInfo.get(userDO.getMemberLevel()).get(3));
        //余额
        orderListDto.setRemainingAvailableAssets(userDO.getBalance());


        Integer totalPages = totalCount / param.getPageSize();
        if (totalCount % param.getPageSize() != 0) {
            totalPages++;
        }
        orderListDto.setPage(param.getPage());
        orderListDto.setPageSize(param.getPageSize());
        orderListDto.setTotalPage(totalPages);

        //获取列表
        map.put("offset", (param.getPage() - 1) * param.getPageSize());
        map.put("limit", param.getPageSize());
        List<OrderDO> orders = orderService.list(map);

        List<OrderInfoDto> orderList = new ArrayList<>();
        orders.forEach(o -> {
            OrderInfoDto infoDto = new OrderInfoDto();
            infoDto.setCreateTime(o.getCreateTime());
            infoDto.setOrderNumber(o.getOrderNumber());
            infoDto.setRatio(new BigDecimal(o.getProductRation()).divide(hundred));
            infoDto.setTotalOrderAmount(o.getAmount());
            infoDto.setExpectedReturn(o.getProductCommission());
            orderList.add(infoDto);
        });

        orderListDto.setOrderList(orderList);
        response.setResultData(orderListDto);
        return response;
    }

    @ApiOperation(value = "purchase下单接口")
    @PostMapping("/orderPurchase")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse orderPurchase(@RequestBody @Valid OrderGrabbingRequest param) {
        BaseResponse response = BaseResponse.success();
        Long userId = getUserId(param);
        String OrderNumber = RedisUtils.getValue(param.getOrderNumber(), String.class);
        if (!StringUtils.isEmpty(OrderNumber)) {
            logger.error("重复刷单userid:" + userId + " OrderNumber:" + OrderNumber);
            response = BaseResponse.fail(I18nEnum.PARAMS_FAIL.getMsg());
            return response;
        }
        UserDO userDO = userService.get(userId);

        //刷单开关 0为关闭，不可刷单
        if (userDO.getSdSwitch() == 0) {
            response = BaseResponse.fail(I18nEnum.ACCOUNT_RESTRICTED_FAIL.getMsg());
            return response;
        }
        List<OrderDO> todayList = orderService.getTodayList(userId);
        HashMap<Integer, List<Integer>> vipInfo = ehcacheService.getVipInfo();
        int todayLimit = todayList.size();
        int memberLimit = vipInfo.get(userDO.getMemberLevel()).get(3);
        if (todayLimit >= memberLimit) {
            response = BaseResponse.fail(I18nEnum.ORDER_UPPER_LIMIT_FAIL.getMsg());
            return response;
        }
        String createOrderChildLimitSwitch = RedisUtils.getString(DictConsts.CREATE_ORDER_PURCHASE_CHILD_LIMIT_SWITCH);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(createOrderChildLimitSwitch)
                && "true".equalsIgnoreCase(createOrderChildLimitSwitch)) {
            //判断用户是否可以刷单
            if (!canOrderPurchase(userDO, param.getProductId())) {
                return BaseResponse.fail(RedisUtils.getString(DictConsts.ORDER_PURCHASE_LIMIT_FAIL));
            }
        }

        OrderDO orderDO = new OrderDO();
        orderDO.setOrderNumber(param.getOrderNumber());
        orderDO.setUserId(userId);


        orderDO.setProductId(param.getProductId());
        orderDO.setProductRation(param.getRatio().multiply(new BigDecimal(100)).longValue());

//        if(userDO.getMemberLevel() + 1 < param.getProductId()){
//            logger.error("非法接口刷单:" + userId);
//            response = BaseResponse.fail("error product");
//            return response;
//        }

        String priceStr = RedisUtils.getValue(userId.toString() + param.getProductId() + "price", String.class);
        String returnFundStr = RedisUtils.getValue(userId.toString() + param.getProductId() + "returnFund", String.class);

        if (priceStr == null || returnFundStr == null) {
            logger.error("需要刷新userid:" + userId);
            response = BaseResponse.fail(I18nEnum.GO_HOME_REFRESH.getMsg());
            return response;
        }

        BigDecimal price = new BigDecimal(priceStr);
        BigDecimal returnFund = new BigDecimal(returnFundStr);

        if (price.subtract(param.getTotalOrderAmount()).abs().divide(price, 2, BigDecimal.ROUND_DOWN).compareTo(new BigDecimal("0.15")) > 0 ||
                returnFund.subtract(param.getExpectedReturn()).abs().divide(returnFund, 2, BigDecimal.ROUND_DOWN).compareTo(new BigDecimal("0.15")) > 0) {
            logger.error("缓存金额:" + price + "缓存返利:" + returnFund);
            logger.error("实际金额:" + param.getTotalOrderAmount() + "实际返利:" + param.getExpectedReturn());
            response = BaseResponse.fail(I18nEnum.AMOUNT_FAIL.getMsg());
            return response;
        }

        //原始金额利率校验
        if (param.getExpectedReturn().subtract(param.getTotalOrderAmount()).abs().divide(price, 4, BigDecimal.ROUND_DOWN).compareTo(new BigDecimal("0.0095")) > 0) {
            logger.error("原始实际金额:" + param.getTotalOrderAmount() + "原始实际返利:" + param.getExpectedReturn());
            response = BaseResponse.fail(I18nEnum.RATE_FAIL.getMsg());
            return response;
        }

        //重新赋值金额
        param.setExpectedReturn(returnFund);
        param.setTotalOrderAmount(price);

        orderDO.setAmount(param.getTotalOrderAmount());
        orderDO.setProductCommission(param.getExpectedReturn().subtract(param.getTotalOrderAmount()));

        //缓存金额利率校验
        if (param.getExpectedReturn().subtract(param.getTotalOrderAmount()).abs().divide(price, 4, BigDecimal.ROUND_DOWN).compareTo(new BigDecimal("0.0095")) > 0) {
            logger.error("缓存实际金额:" + param.getTotalOrderAmount() + "缓存实际返利:" + param.getExpectedReturn());
            response = BaseResponse.fail(I18nEnum.RATE_FAIL.getMsg());
            return response;
        }
        //上级返利减半check
        boolean productCommissionCheck = false;

        long time = (new Date().getTime() - userDO.getCreateTime().getTime()) / 1000 / 3600 / 24;
        String flag = RedisUtils.getValue(DictAccount.INVITE_FLAG_HALF, String.class);
        if (!StringUtils.isEmpty(flag) && Integer.parseInt(flag) > 0) {
            //受益减半判断，注册7日后，仍未有有效邀请记录用户
            if (time >= Integer.parseInt(flag)) {
                String userKey = "user_count_" + userId.toString();
                String countStr = RedisUtils.getValue(userKey, String.class);
                Integer count = 0;
                if (StringUtils.isEmpty(countStr)) {
                    count = userService.getCountPeople(userDO.getId());
                    RedisUtils.setValue(userKey, count.toString(), 600);
                } else {
                    count = Integer.valueOf(countStr);
                }
                if (count == 0) {
                    productCommissionCheck = true;
                }
            }
        }

        long days = getDays(userDO);
        //获取vip对应分润比例
        //获取上级
        if (userDO.getParentId() != null) {
            UserDO userDO1 = userService.getUser(userDO.getParentId());

            if (userDO.getFirstRecharge() >= 0) {
                orderDO.setOneId(userDO1.getId());
                logger.info("checkNull vipInfo:"+ JsonUtils.toJSONString(vipInfo.get(userDO1.getMemberLevel())));
                orderDO.setOneRatio(vipInfo.get(userDO1.getMemberLevel()).get(0).longValue());
                //注册满5天并且不充值，上级受益变为1/4
                if(days>= registerDay && userDO.getFirstRecharge() != 1){
                    orderDO.setOneAmount(orderDO.getProductCommission().multiply(new BigDecimal(orderDO.getOneRatio()).divide(productCommissionCheck ? five_hundred : four_hundred, 2, BigDecimal.ROUND_DOWN)));
                }else {
                    orderDO.setOneAmount(orderDO.getProductCommission().multiply(new BigDecimal(orderDO.getOneRatio()).divide(hundred, 2, BigDecimal.ROUND_DOWN)));
                }

                BalanceDO balance1 = new BalanceDO();
                balance1.setAmount(orderDO.getOneAmount());
                balance1.setOrderNo(orderDO.getOrderNumber());
                balance1.setType(BalanceTypeEnum.B.getCode());
                balance1.setUserId(userDO1.getId());
                balance1.setRelationUserId(userDO.getId());
                balance1.setStatus(2);
                balanceService.addBalanceDetail(balance1);


                //获取上上级
                if (userDO1.getParentId() != null) {
                    UserDO userDO2 = userService.getUser(userDO1.getParentId());
                    orderDO.setTwoId(userDO2.getId());
                    orderDO.setTwoRatio(vipInfo.get(userDO2.getMemberLevel()).get(1).longValue());
                    //注册满5天并且不充值，上级受益变为1/4
                    if(days >= registerDay && userDO.getFirstRecharge() != 1){
                        orderDO.setTwoAmount(orderDO.getProductCommission().multiply(new BigDecimal(orderDO.getTwoRatio()).divide(productCommissionCheck ? five_hundred : four_hundred, 2, BigDecimal.ROUND_DOWN)));
                    }else {
                        orderDO.setTwoAmount(orderDO.getProductCommission().multiply(new BigDecimal(orderDO.getTwoRatio()).divide(hundred, 2, BigDecimal.ROUND_DOWN)));
                    }

                    if (userDO.getFirstRecharge() >= 0) {
                        //更新余额
                        BalanceDO balance2 = new BalanceDO();
                        balance2.setAmount(orderDO.getTwoAmount());
                        balance2.setOrderNo(orderDO.getOrderNumber());
                        balance2.setType(BalanceTypeEnum.B.getCode());
                        balance2.setUserId(userDO2.getId());
                        balance2.setRelationUserId(userDO.getId());
                        balance2.setStatus(2);
                        balanceService.addBalanceDetail(balance2);
                    }
                    //获取上上上级
                    if (userDO2.getParentId() != null) {
                        UserDO userDO3 = userService.getUser(userDO2.getParentId());

                        orderDO.setThreeId(userDO3.getId());
                        orderDO.setThreeRatio(vipInfo.get(userDO3.getMemberLevel()).get(2).longValue());

                        //注册满5天并且不充值，上级受益变为1/4
                        if(days >= registerDay && userDO.getFirstRecharge() != 1){
                            orderDO.setThreeAmount(orderDO.getProductCommission().multiply(new BigDecimal(orderDO.getThreeRatio()).divide(productCommissionCheck ? five_hundred : four_hundred, 2, BigDecimal.ROUND_DOWN)));
                        }else {
                            orderDO.setThreeAmount(orderDO.getProductCommission().multiply(new BigDecimal(orderDO.getThreeRatio()).divide(hundred, 2, BigDecimal.ROUND_DOWN)));
                        }

                        if (userDO.getFirstRecharge() >= 0) {
                            //更新余额
                            BalanceDO balance3 = new BalanceDO();
                            balance3.setAmount(orderDO.getThreeAmount());
                            balance3.setOrderNo(orderDO.getOrderNumber());
                            balance3.setType(BalanceTypeEnum.B.getCode());
                            balance3.setUserId(userDO3.getId());
                            balance3.setRelationUserId(userDO.getId());
                            balance3.setStatus(2);
                            balanceService.addBalanceDetail(balance3);
                        }

                    }
                }
            }
        }
        BalanceDO balance = new BalanceDO();
        balance.setAmount(orderDO.getAmount());
        balance.setOrderNo(orderDO.getOrderNumber());
        balance.setType(BalanceTypeEnum.A.getCode());
        balance.setUserId(userDO.getId());
        balance.setStatus(2);
        balance.setAmount(orderDO.getProductCommission());
        balanceService.addBalanceDetail(balance);

        orderDO.setCreateTime(new Date());
        orderDO.setUpdateTime(new Date());
        orderService.save(orderDO);

        if (userDO.getFirstTask() == 0) {
            UserDO tempUser = new UserDO();
            tempUser.setId(userDO.getId());
            tempUser.setFirstTask(1);
            tempUser.setVersion(userDO.getVersion());
            userService.updateByIdAndVersion(tempUser);
        }
        //如果今天刷单数已达到限制，记录用户完成今日的刷单任务
        if ((todayLimit + 1) >= memberLimit) {
            BigDecimal taskOrderAmount = orderDO.getProductCommission();
            for (OrderDO todayOrder : todayList) {
                taskOrderAmount = taskOrderAmount.add(todayOrder.getProductCommission());
            }
            userTaskOrderMissionService.addMissionLog(userId, memberLimit, taskOrderAmount);
        }
        RedisUtils.setValue(param.getOrderNumber(), param.getOrderNumber(), 600);
        return response;
    }

    /**
     * 判断用户是否可以刷单
     * @param userDO
     * @param productId
     * @return
     */
    private boolean canOrderPurchase(UserDO userDO, Long productId) {
        ProductDO productDO = productService.get(productId);
        if (null == productDO) {
            return false;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userDO.getId());
        params.put("productId", productId);
        int count = orderService.count(params);
        List<UserDO> userDOList = userService.getUserByParentId(userDO.getId());
        //完成首充的子集用户数量
        int childFirstRechargedCount = userDOList.size();
        int limitCount = getOrderLimitByProduct(productDO, childFirstRechargedCount);
        return count < limitCount;
    }

    /**
     * 获取商品可以刷单数量
     * @param productDO
     * @param childFirstRechargedCount
     * @return
     */
    private int getOrderLimitByProduct(ProductDO productDO, int childFirstRechargedCount) {
        //刷单商品限制基数
        int productLimit = 0;
        if (1 == productDO.getLevel()) {
            productLimit += 300;
            productLimit += childFirstRechargedCount * 300;
        } else if (2 == productDO.getLevel()) {
            productLimit += 200;
            productLimit += (childFirstRechargedCount / 2) * 200;
        } else if (3 == productDO.getLevel()) {
            productLimit += 120;
            productLimit += (childFirstRechargedCount / 3) * 120;
        } else if (4 == productDO.getLevel()) {
            productLimit += 100;
            productLimit += (childFirstRechargedCount / 4) * 100;
        } else if (5 == productDO.getLevel()) {
            productLimit += 80;
            productLimit += (childFirstRechargedCount / 5) * 80;
        }
        return productLimit;
    }



    //注册天数
    private long getDays(UserDO userDO){
        return  (new Date().getTime() - userDO.getCreateTime().getTime()) / 1000 / 3600 / 24;
    }

}
