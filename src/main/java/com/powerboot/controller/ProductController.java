package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictAccount;
import com.powerboot.consts.DictConsts;
import com.powerboot.domain.OrderDO;
import com.powerboot.domain.ProductDO;
import com.powerboot.domain.UserDO;
import com.powerboot.request.product.ProductDetailsRequest;
import com.powerboot.request.product.ProductRequest;
import com.powerboot.response.ProductDetailsDto;
import com.powerboot.response.ProductListDto;
import com.powerboot.response.TodayResultDto;
import com.powerboot.service.EhcacheService;
import com.powerboot.service.OrderService;
import com.powerboot.service.ProductService;
import com.powerboot.service.UserService;
import com.powerboot.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 刷单商品
 */

@RestController
@RequestMapping("/product")
@Api(tags = "刷单商品信息")
public class ProductController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private EhcacheService ehcacheService;

    @ApiOperation(value = "获取刷单商品列表")
    @PostMapping("/getProductList")
    @ResponseBody
    public BaseResponse<List<ProductListDto>> getProductList(@RequestBody @Valid ProductRequest param) {
        BaseResponse<List<ProductListDto>> response = BaseResponse.success();
        HashMap<String, Object> map = new HashMap<>();
        Long userId = getUserId(param);

        map.put("status", 1);
        UserDO userDO = userService.get(userId);
        List<ProductDO> list = productService.list(map);

        HashMap<Integer, List<Integer>> hashMapBalance = ehcacheService.getBalanceInfo();
        HashMap<Integer, List<Integer>> priceSectionMap = ehcacheService.getPriceSection();

        List<ProductListDto> productListDtoList = new ArrayList<>();

        //获取用户余额，乘以随机系数获取商品价格
        BigDecimal virtualBalance = new BigDecimal(Objects.requireNonNull(RedisUtils.getValue(DictAccount.VIRTUAL_BALANCE, String.class)));
        BigDecimal totalBalance = virtualBalance.add(userDO.getBalance());

        List<ProductDO> sortList = list.stream().filter(o -> o.getType().equals(param.getType())).sorted(Comparator.comparing(ProductDO::getSort).reversed()).collect(Collectors.toList());

        sortList.forEach(
                o -> {
                    ProductListDto po = new ProductListDto();
                    po.setPicture(o.getPicture());
                    BigDecimal levelLowPrice = new BigDecimal(hashMapBalance.get(o.getLevel()).get(0));
                    //刷单商品计算随机金额
                    if (o.getType().equals(2)) {
                        //判断余额在哪个区间等级
                        Integer low = priceSectionMap.get(o.getLevel()).get(0);
                        Integer high = priceSectionMap.get(o.getLevel()).get(1);
                        BigDecimal resultAmount = BigDecimal.valueOf(Math.random() * (high - low) + low).setScale(2, BigDecimal.ROUND_DOWN);
                        o.setOriginPrice(levelLowPrice);
                        po.setPrice(resultAmount);
                        po.setReturnFund(resultAmount.add(resultAmount.multiply(
                                new BigDecimal(hashMapBalance.get(o.getLevel())
                                        .get(3)).divide(new BigDecimal(10000)))).setScale(2, BigDecimal.ROUND_DOWN));
                    }

                    //新增改造字段，赋值
                    addNewInfo(po, o);

                    if (o.getLevel() > 1) {
                        po.setLevel(String.valueOf((o.getLevel() - 1)));
                    } else {
                        po.setLevel(String.valueOf(o.getLevel()));
                    }
                    po.setProductId(o.getId());
                    po.setRatio(new BigDecimal(hashMapBalance.get(o.getLevel()).get(3)).divide(new BigDecimal(100)));
                    po.setComment(o.getComment());
                    po.setDescribtion(o.getDescribtion());

                    if (o.getLevel().equals(1)) {
                        po.setClickButton(true);
                    } else if (o.getLevel().equals(2) && totalBalance.compareTo(levelLowPrice) >= 0) {
                        po.setClickButton(true);
                    } else if (o.getLevel().equals(3) && totalBalance.compareTo(levelLowPrice) >= 0) {
                        if (userDO.getMemberLevel() + 1 >= o.getLevel()) {
                            po.setClickButton(true);
                        }else {
                            po.setClickButton(false);
                        }
                    } else if (o.getLevel().equals(4) && totalBalance.compareTo(levelLowPrice) >= 0) {
                        if (userDO.getMemberLevel() + 2 >= o.getLevel()) {
                            po.setClickButton(true);
                        }else {
                            po.setClickButton(false);
                        }
                    } else if (o.getLevel().equals(5) && totalBalance.compareTo(levelLowPrice) >= 0) {
                        if (userDO.getMemberLevel() + 1 >= o.getLevel()) {
                            po.setClickButton(true);
                        }else {
                            po.setClickButton(false);
                        }
                    } else {
                        po.setClickButton(false);
                    }

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
                                po.setReturnFund(po.getPrice().add(
                                        po.getReturnFund().subtract(
                                                po.getPrice()).divide(new BigDecimal("2"), 2, BigDecimal.ROUND_DOWN)));
                            }
                        }
                    }

                    //缓存校验
                    RedisUtils.setValue(userId.toString() + po.getProductId() + "price", po.getPrice().toString(), 1200);
                    RedisUtils.setValue(userId.toString() + po.getProductId() + "returnFund", po.getReturnFund().toString(), 1200);
                    productListDtoList.add(po);
                }
        );
        response.setResultData(productListDtoList);
        return response;
    }

    @ApiOperation(value = "刷详详情页")
    @PostMapping("/getProductDetails")
    @ResponseBody
    public BaseResponse<ProductDetailsDto> getProductDetails(@RequestBody @Valid ProductDetailsRequest param) {
        long start = System.currentTimeMillis();
        BaseResponse<ProductDetailsDto> response = BaseResponse.success();

        Long userId = getUserId(param);
        UserDO userDO = userService.get(userId);
        List<OrderDO> todayList = orderService.getTodayList(userId);
        HashMap<Integer, List<Integer>> vipInfo = ehcacheService.getVipInfo();

        if (todayList.size() >= vipInfo.get(userDO.getMemberLevel()).get(3)) {
            response = BaseResponse.fail("All tasks have been completed today");
            return response;
        }

        //刷单开关 0为关闭，不可刷单
        if (userDO.getSdSwitch() == 0) {
            response = BaseResponse.fail("The account has been restricted,please contact Customer Service");
            return response;
        }

        ProductDO productDO = productService.get(param.getProductId());

        HashMap<Integer, List<Integer>> hashMapBalance = ehcacheService.getBalanceInfo();

        ProductDetailsDto productDetailsDto = new ProductDetailsDto();
        ProductListDto productListDto = new ProductListDto();

        BigDecimal levelLowPrice = new BigDecimal(hashMapBalance.get(productDO.getLevel()).get(0));
        productListDto.setPicture(productDO.getPicture());
        productListDto.setPrice(levelLowPrice);
        if (productDO.getLevel() > 1) {
            productListDto.setLevel(String.valueOf(productDO.getLevel() - 1));
        } else {
            productListDto.setLevel(String.valueOf(productDO.getLevel()));
        }
        productListDto.setProductId(productDO.getId());
        productListDto.setRatio(new BigDecimal(hashMapBalance.get(productDO.getLevel()).get(3)).divide(new BigDecimal(100)));
        productListDto.setComment(productDO.getComment());
        productListDto.setDescribtion(productDO.getDescribtion());

        productDetailsDto.setProductListDto(productListDto);

        TodayResultDto todayResultDto = orderService.getTodayResult(userDO);
        productDetailsDto.setTodayResultDto(todayResultDto);
        response.setResultData(productDetailsDto);
        long end = System.currentTimeMillis();
        logger.info("刷详详情页总耗时" + (end - start));

        return response;
    }

    private void addNewInfo(ProductListDto po, ProductDO productDO) {
        String a = RedisUtils.getString(DictConsts.PRODUCT_DESC_AMOUNT);
        String[] amounts = a.split("\\|");
        String i = RedisUtils.getString(DictConsts.PRODUCT_INTRODUCTION);
        String[] introductions = i.split("\\|");
        if (productDO.getLevel().equals(1)) {
            po.setIntroduction(introductions[0]);
            po.setDescAmount(new BigDecimal(amounts[0]));
        }
        if (productDO.getLevel().equals(2)) {
            po.setIntroduction(introductions[1]);
            po.setDescAmount(new BigDecimal(amounts[1]));
        }
        if (productDO.getLevel().equals(3)) {
            po.setIntroduction(introductions[2]);
            po.setDescAmount(new BigDecimal(amounts[2]));
        }
        if (productDO.getLevel().equals(4)) {
            po.setIntroduction(introductions[3]);
            po.setDescAmount(new BigDecimal(amounts[3]));
        }
        if (productDO.getLevel().equals(5)) {
            po.setIntroduction(introductions[4]);
            po.setDescAmount(new BigDecimal(amounts[4]));
        }
    }
}
