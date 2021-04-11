package com.powerboot.service;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictAccount;
import com.powerboot.dao.ProductDao;
import com.powerboot.domain.ProductDO;
import com.powerboot.domain.UserDO;
import com.powerboot.request.product.ProductRequest;
import com.powerboot.response.ProductListDto;
import com.powerboot.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private EhcacheService ehcacheService;

    public ProductDO get(Long id) {
        return productDao.get(id);
    }

    public List<ProductDO> list(Map<String, Object> map) {
        return productDao.list(map);
    }

    public int count(Map<String, Object> map) {
        return productDao.count(map);
    }

    public int save(ProductDO product) {
        return productDao.save(product);
    }

    public int update(ProductDO product) {
        return productDao.update(product);
    }

    public int remove(Long id) {
        return productDao.remove(id);
    }

    public int batchRemove(Long[] ids) {
        return productDao.batchRemove(ids);
    }

    public BaseResponse<List<ProductListDto>> getProductListOld(ProductRequest param, Long userId) {
        BaseResponse<List<ProductListDto>> response = BaseResponse.success();
        HashMap<String, Object> map = new HashMap<>();

        map.put("status", 1);
        UserDO userDO = userService.get(userId);
        List<ProductDO> list = productService.list(map);

        HashMap<Integer, List<Integer>> hashMapBalance = ehcacheService.getBalanceInfo();

        List<ProductListDto> productListDtoList = new ArrayList<>();

        //获取用户余额，乘以随机系数获取商品价格
        BigDecimal virtualBalance = new BigDecimal(Objects.requireNonNull(RedisUtils.getValue(DictAccount.VIRTUAL_BALANCE, String.class)));
        BigDecimal totalBalance = virtualBalance.add(userDO.getBalance());
        BigDecimal random = BigDecimal.valueOf(Math.random() * (68 - 58) + 58);
        BigDecimal resultAmount = totalBalance.multiply(random).divide(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_DOWN);

        List<ProductDO> sortList = list.stream().filter(o -> o.getType().equals(param.getType())).sorted(Comparator.comparing(ProductDO::getSort).reversed()).collect(Collectors.toList());

        sortList.forEach(
                o -> {
                    ProductListDto po = new ProductListDto();
                    po.setPicture(o.getPicture());
                    //刷单商品计算随机金额
                    if (o.getType().equals(2)) {
                        //判断余额在哪个区间等级
                        BigDecimal levelLowPrice = new BigDecimal(hashMapBalance.get(o.getLevel()).get(0));
                        BigDecimal levelTopPrice = new BigDecimal(hashMapBalance.get(o.getLevel()).get(1));

                        //设置金额上限
                        levelTopPrice = levelTopPrice.multiply(new BigDecimal("68")).divide(BigDecimal.valueOf(100));
                        if (totalBalance.compareTo(levelLowPrice) < 0) {
                            o.setOriginPrice(levelLowPrice);
                            po.setPrice(resultAmount);
                            po.setReturnFund(resultAmount.add(resultAmount.multiply(new BigDecimal(hashMapBalance.get(o.getLevel()).get(3)).divide(new BigDecimal(10000)))).setScale(2, BigDecimal.ROUND_DOWN));
                        } else {
                            if (resultAmount.compareTo(levelLowPrice) < 0) {
                                o.setOriginPrice(levelLowPrice);
                                po.setPrice(resultAmount);
                                po.setReturnFund(resultAmount.add(resultAmount.multiply(new BigDecimal(hashMapBalance.get(o.getLevel()).get(3))).divide(new BigDecimal(10000))).setScale(2, BigDecimal.ROUND_DOWN));
                            } else {
                                if (resultAmount.compareTo(levelTopPrice) >= 0) {
                                    o.setOriginPrice(levelTopPrice);
                                    po.setPrice(levelTopPrice);
                                    po.setReturnFund(levelTopPrice.add(levelTopPrice.multiply(new BigDecimal(hashMapBalance.get(o.getLevel()).get(3))).divide(new BigDecimal(10000))).setScale(2, BigDecimal.ROUND_DOWN));
                                } else {
                                    o.setOriginPrice(resultAmount.setScale(2, BigDecimal.ROUND_DOWN));
                                    po.setPrice(resultAmount.setScale(2, BigDecimal.ROUND_DOWN));
                                    po.setReturnFund(resultAmount.add(resultAmount.multiply(new BigDecimal(hashMapBalance.get(o.getLevel()).get(3))).divide(new BigDecimal(10000))).setScale(2, BigDecimal.ROUND_DOWN));
                                }
                            }
                        }

                    } else {
                        //首页商品直接获取金额
                        po.setPrice(o.getPrice());
                        po.setReturnFund(o.getReturnPrice());
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

                    //用户vip等级和余额等级的对应关系
//                    if (totalBalance.compareTo(o.getOriginPrice()) >= 0) {
//                        if (userDO.getMemberLevel() == 3) {
//                            po.setClickButton(true);
//                        } else if (userDO.getMemberLevel() == 2 && o.getOriginPrice().compareTo(new BigDecimal(30000)) < 0) {
//                            po.setClickButton(true);
//                        } else if (userDO.getMemberLevel() == 1 && o.getOriginPrice().compareTo(new BigDecimal(5000)) < 0) {
//                            po.setClickButton(true);
//                        } else {
//                            po.setClickButton(false);
//                        }
//                    } else {
//                        po.setClickButton(false);
//                    }


                    if (totalBalance.compareTo(o.getOriginPrice()) >= 0) {
                        if (userDO.getMemberLevel() == 4) {
                            po.setClickButton(true);
                        } else if (userDO.getMemberLevel() == 3 && o.getOriginPrice().compareTo(new BigDecimal(20000)) < 0) {
                            po.setClickButton(true);
                        } else if (userDO.getMemberLevel() == 2 && o.getOriginPrice().compareTo(new BigDecimal(8000)) < 0) {
                            po.setClickButton(true);
                        } else if (userDO.getMemberLevel() == 1 && o.getOriginPrice().compareTo(new BigDecimal(3000)) < 0) {
                            po.setClickButton(true);
                        } else {
                            po.setClickButton(false);
                        }
                    } else {
                        po.setClickButton(false);
                    }

                    long time = (new Date().getTime() - userDO.getCreateTime().getTime()) / 1000 / 3600 / 24;
//                    //受益减半判断 注册日期大于7天且没充值
//                    if (userDO.getFirstRecharge().equals(0)) {
//                        if (time >= 7) {
//                            po.setReturnFund(po.getPrice().add(po.getReturnFund().subtract(po.getPrice()).divide(new BigDecimal("2"), 2, BigDecimal.ROUND_DOWN)));
//                        }
//                    }

                    //受益减半判断，注册7日后，仍未有有效邀请记录用户
                    if (time >= 7) {
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
                            po.setReturnFund(po.getPrice().add(po.getReturnFund().subtract(po.getPrice()).divide(new BigDecimal("2"), 2, BigDecimal.ROUND_DOWN)));
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


    private void addNewInfo(ProductListDto po, ProductDO productDO) {

        if (productDO.getLevel().equals(1)) {
            po.setIntroduction("Accessories");
            po.setDescAmount(new BigDecimal(100));
        }
        if (productDO.getLevel().equals(2)) {
            po.setIntroduction("Kitchenware");
            po.setDescAmount(new BigDecimal(500));
        }
        if (productDO.getLevel().equals(3)) {
            po.setIntroduction("Consumer electronics");
            po.setDescAmount(new BigDecimal(3000));
        }
        if (productDO.getLevel().equals(4)) {
            po.setIntroduction("Fitness Equipment");
            po.setDescAmount(new BigDecimal(8000));
        }
        if (productDO.getLevel().equals(5)) {
            po.setIntroduction("Household appliances");
            po.setDescAmount(new BigDecimal(20000));
        }
    }
}
