package com.powerboot.service;

import com.powerboot.common.JsonUtils;
import com.powerboot.config.BaseException;
import com.powerboot.consts.AmountConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.consts.TipConsts;
import com.powerboot.consts.cache.FinancialCache;
import com.powerboot.dao.FinancialProductDao;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.FinancialOrderDO;
import com.powerboot.domain.FinancialProductDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.FinancialOrderStatusEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.param.FinancialOrderParam;
import com.powerboot.param.FinancialProductParam;
import com.powerboot.request.financial.PurchaseFinancialRequest;
import com.powerboot.response.FinancialAssetsResponse;
import com.powerboot.response.FinancialOrderResponse;
import com.powerboot.response.FinancialProductResponse;
import com.powerboot.response.TakeOutWarningResponse;
import com.powerboot.utils.BigDecimalUtils;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 理财产品表
 */
@Service
public class FinancialProductService {

    private Logger logger = LoggerFactory.getLogger(FinancialProductService.class);

    @Autowired
    private FinancialProductDao financialProductDao;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private FinancialOrderService financialOrderService;
    @Autowired
    private UserService userService;

    /**
     * 根据id获取理财产品信息（前端用）
     */
    public FinancialProductResponse getFinancialProductResponseById(Integer productId) {
        FinancialProductResponse financialRes = new FinancialProductResponse();
        FinancialProductDO financialProductDO = getByIdWithCache(productId);
        BeanUtils.copyProperties(financialProductDO, financialRes);
        return financialRes;
    }

    /**
     * 根据id获取理财产品信息（底层用）
     */
    public FinancialProductDO getByIdWithCache(Integer productId) {
        return this.getById(productId);
        /*String key = String.format(FinancialCache.CACHE_KEY_PRODUCT, productId);
        return RedisUtils.setIfNotExists(key, () -> this.getById(productId),
            FinancialCache.CACHE_TIME_LIST, FinancialProductDO.class);*/
    }

    /**
     * 购买理财产品
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean purchaseFinancial(PurchaseFinancialRequest request, UserDO userDO,
        FinancialProductDO financialProductDO) {
        Long userId = userDO.getId();
        BigDecimal amount = request.getAmount();
        Integer productId = request.getProductId();
        //查理财产品
        Integer lockDay = financialProductDO.getLockDays();
        BigDecimal dayRate = financialProductDO.getDayRate();
        BigDecimal calledRate = financialProductDO.getCalledRate();
        //扣余额 + 增加余额流水
        BalanceDO balance = new BalanceDO();
        balance.setUserId(userId);
        balance.setAmount(BigDecimalUtils.convertNegative(amount));
        balance.setType(BalanceTypeEnum.I.getCode());
        balance.setWithdrawAmount(BigDecimal.ZERO);
        balance.setServiceFee(BigDecimal.ZERO);
        balance.setStatus(StatusTypeEnum.SUCCESS.getCode());
        int balanceCount = balanceService.addBalanceDetail(balance);
        if (balanceCount == 0) {
            logger.error("【购买理财】余额操作异常，request={}，userId={}", JsonUtils.toJSONString(request), userId);
            throw new BaseException(I18nEnum.CREATE_FINANCIAL_PRODUCT_FAIL.getMsg());
        }
        //生成理财订单
        FinancialOrderDO financialOrder = new FinancialOrderDO();
        Date now = new Date();
        financialOrder.setUserId(userId.intValue());
        financialOrder.setProductId(productId);
        financialOrder.setOrderStatus(FinancialOrderStatusEnum.UNEXPIRED.getCode());
        financialOrder.setAmount(amount);
        financialOrder.setBuyDate(now);
        Date lastDay = DateUtils.setDateHMS(DateUtils.addDays(now, lockDay - 1), 23, 59, 59);
        financialOrder.setLastDate(lastDay);
        BigDecimal dayInterest = amount.multiply(dayRate).divide(AmountConsts.AMOUNT_100, 2, BigDecimal.ROUND_DOWN);
        financialOrder.setDayInterest(dayInterest);
        financialOrder.setTotalInterest(dayInterest.multiply(BigDecimal.valueOf(lockDay)));
        BigDecimal calledAmount = amount.multiply(calledRate).divide(AmountConsts.AMOUNT_100, 2, BigDecimal.ROUND_DOWN);
        financialOrder.setCalledAmount(calledAmount);
        financialOrder.setCalledTime(DateUtils.DEFAULT_DATE);
        financialOrder.setProductName(financialProductDO.getName());
        financialOrder.setDayRate(financialProductDO.getDayRate());
        financialOrder.setYearRate(financialProductDO.getYearRate());
        financialOrder.setLockDays(financialProductDO.getLockDays());
        financialOrder.setCalledRate(financialProductDO.getCalledRate());
        financialOrder.setSaleId(userDO.getSaleId());
        int count = financialOrderService.save(financialOrder);
        if (count == 0) {
            logger.error("【购买理财】添加理财订单失败，request={}，userId={}", JsonUtils.toJSONString(request), userId);
            throw new BaseException(I18nEnum.CREATE_FINANCIAL_PRODUCT_FAIL.getMsg());
        }
        return true;
    }

    /**
     * 个人理财订单列表
     */
    public List<FinancialOrderResponse> financialOrderList(Long userId) {
        FinancialOrderParam param = new FinancialOrderParam();
        param.setUserId(userId.intValue());
        param.setSort(" id desc ");
        List<FinancialOrderDO> list = financialOrderService.getByParams(param);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<FinancialOrderResponse> resultList = new ArrayList<>(list.size());
        list.forEach(o -> {
            FinancialOrderResponse response = new FinancialOrderResponse();
            response.setOrderId(o.getId());
            response.setAmount(o.getAmount());
            response.setOrderStatus(o.getOrderStatus());
            response.setBuyDate(DateUtils.formatDateYMDHMS(o.getBuyDate()));
            response.setLastDate(DateUtils.formatDateYMDHMS(o.getLastDate()));
            response.setProductName(o.getProductName());
            response.setDayRate(o.getDayRate());
            response.setLockDays(o.getLockDays());
            if (FinancialOrderStatusEnum.CALLED_AWAY.getCode().equals(o.getOrderStatus())) {
                response.setCalledAmount(o.getCalledAmount());
                response.setCalledBalance(o.getAmount().subtract(o.getCalledAmount()));
                response.setCalledTime(DateUtils.formatDateYMDHMS(o.getCalledTime()));
            } else {
                response.setTotalInterest(o.getTotalInterest());
                response.setTotalAmount(o.getAmount().add(o.getTotalInterest()));
            }
            resultList.add(response);
        });
        return resultList;
    }

    /**
     * 获取提前赎回警告信息
     */
    public TakeOutWarningResponse getTakeOutWarning(Integer orderId) {
        FinancialOrderDO financialOrderDO = financialOrderService.getById(orderId);
        if (financialOrderDO == null || financialOrderDO.getOrderStatus()
            .equals(FinancialOrderStatusEnum.CALLED_AWAY.getCode()) || financialOrderDO.getOrderStatus()
            .equals(FinancialOrderStatusEnum.EXPIRED.getCode())) {
            throw new BaseException(I18nEnum.TRY_AGAIN.getMsg());
        }
        TakeOutWarningResponse response = new TakeOutWarningResponse();
        response.setCalledAmount(financialOrderDO.getCalledAmount());
        response.setCalledRate(financialOrderDO.getCalledRate());
        return response;
    }

    /**
     * 提前赎回
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean takeOutFinancial(Integer orderId, Integer userId) {
        FinancialOrderParam param = new FinancialOrderParam();
        param.setId(orderId);
        param.setUserId(userId);
        List<FinancialOrderDO> list = financialOrderService.getByParams(param);
        if (CollectionUtils.isEmpty(list)) {
            throw new BaseException(I18nEnum.TRY_AGAIN.getMsg());
        }
        FinancialOrderDO financialOrderDO = list.get(0);
        if (!financialOrderDO.getOrderStatus().equals(FinancialOrderStatusEnum.UNEXPIRED.getCode())) {
            throw new BaseException(I18nEnum.TRY_AGAIN.getMsg());
        }
        //更新订单为 提前赎回
        FinancialOrderDO entity = new FinancialOrderDO();
        entity.setOrderStatus(FinancialOrderStatusEnum.CALLED_AWAY.getCode());
        entity.setCalledTime(new Date());
        FinancialOrderParam orderParam = new FinancialOrderParam();
        orderParam.setId(orderId);
        orderParam.setUserId(userId);
        orderParam.setOrderStatus(FinancialOrderStatusEnum.UNEXPIRED.getCode());
        int updateCount = financialOrderService.update(entity, orderParam);
        if (updateCount == 0) {
            logger.error("【提前赎回理财】更新理财订单操作异常，entity={},orderParam={}", JsonUtils.toJSONString(entity),
                JsonUtils.toJSONString(orderParam));
            throw new BaseException(I18nEnum.TRY_AGAIN.getMsg());
        }
        //增加余额 + 增加余额流水
        BalanceDO balance = new BalanceDO();
        balance.setUserId(userId.longValue());
        BigDecimal balanceAmount = financialOrderDO.getAmount().subtract(financialOrderDO.getCalledAmount());
        balance.setAmount(balanceAmount);
        balance.setType(BalanceTypeEnum.D.getCode());
        balance.setWithdrawAmount(BigDecimal.ZERO);
        balance.setServiceFee(BigDecimal.ZERO);
        balance.setStatus(StatusTypeEnum.SUCCESS.getCode());
        int balanceCount = balanceService.addBalanceDetail(balance);
        if (balanceCount == 0) {
            logger.error("【提前赎回理财】余额操作异常，balance={}", JsonUtils.toJSONString(balance));
            throw new BaseException(I18nEnum.TRY_AGAIN.getMsg());
        }
        return true;
    }

    /**
     * 正常到期赎回
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean redeemOrder(Integer orderId) {
        FinancialOrderDO financialOrderDO = financialOrderService.getById(orderId);
        if (financialOrderDO == null || !financialOrderDO.getOrderStatus()
            .equals(FinancialOrderStatusEnum.UNEXPIRED.getCode())) {
            throw new BaseException("【到期赎回理财】订单状态异常 orderId：" + orderId);
        }
        //更新订单为 到期
        FinancialOrderDO entity = new FinancialOrderDO();
        entity.setOrderStatus(FinancialOrderStatusEnum.EXPIRED.getCode());
        FinancialOrderParam orderParam = new FinancialOrderParam();
        orderParam.setId(orderId);
        orderParam.setOrderStatus(FinancialOrderStatusEnum.UNEXPIRED.getCode());
        int updateCount = financialOrderService.update(entity, orderParam);
        if (updateCount == 0) {
            logger.error("【到期赎回理财】更新理财订单操作异常，entity={},orderParam={}", JsonUtils.toJSONString(entity),
                JsonUtils.toJSONString(orderParam));
            throw new BaseException("【到期赎回理财】更新理财订单操作异常 orderId：" + orderId);
        }
        //增加余额 + 增加余额流水
        BalanceDO balance = new BalanceDO();
        balance.setUserId(financialOrderDO.getUserId().longValue());
        BigDecimal balanceAmount = financialOrderDO.getAmount().add(financialOrderDO.getTotalInterest());
        balance.setAmount(balanceAmount);
        balance.setType(BalanceTypeEnum.D.getCode());
        balance.setWithdrawAmount(BigDecimal.ZERO);
        balance.setServiceFee(BigDecimal.ZERO);
        balance.setStatus(StatusTypeEnum.SUCCESS.getCode());
        int balanceCount = balanceService.addBalanceDetail(balance);
        if (balanceCount == 0) {
            logger.error("【到期赎回理财】余额操作异常，balance={}", JsonUtils.toJSONString(balance));
            throw new BaseException("【到期赎回理财】余额操作异常 orderId：" + orderId);
        }
        return true;
    }

    public FinancialAssetsResponse getAssets(Integer userId) {
        FinancialAssetsResponse response = new FinancialAssetsResponse();
        UserDO userDO = userService.get(userId.longValue());
        if (userDO != null) {
            response.setBalance(userDO.getBalance());
        }
        FinancialOrderParam param = new FinancialOrderParam();
        param.setUserId(userId);
        List<FinancialOrderDO> orderDOList = financialOrderService.getByParams(param);
        if (CollectionUtils.isEmpty(orderDOList)) {
            response.setInterest(BigDecimal.ZERO);
            response.setYesterdayEarning(BigDecimal.ZERO);
            response.setTotalRevenue(BigDecimal.ZERO);
            return response;
        }
        //今日
        BigDecimal todayInterest = orderDOList.stream()
            .filter(o -> FinancialOrderStatusEnum.UNEXPIRED.getCode().equals(o.getOrderStatus()))
            .map(FinancialOrderDO::getDayInterest)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setInterest(todayInterest);
        //昨日
        Date now = new Date();
        Date startDate = DateUtils.setDateHMS(now, 0, 0, 0);
        Date endDate = DateUtils.setDateHMS(DateUtils.addDays(now, -2), 23, 59, 59);
        BigDecimal yesterdayEarning = orderDOList.stream()
            .filter(o -> o.getBuyDate().before(startDate) && o.getLastDate().after(endDate)
                && !FinancialOrderStatusEnum.CALLED_AWAY.getCode().equals(o.getOrderStatus()))
            .map(FinancialOrderDO::getDayInterest)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setYesterdayEarning(yesterdayEarning);
        //总计
        BigDecimal totalRevenu = orderDOList.stream()
            .filter(o -> FinancialOrderStatusEnum.EXPIRED.getCode().equals(o.getOrderStatus()))
            .map(FinancialOrderDO::getTotalInterest)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<FinancialOrderDO> unOrderList = orderDOList.stream()
            .filter(o -> FinancialOrderStatusEnum.UNEXPIRED.getCode().equals(o.getOrderStatus()))
            .collect(Collectors.toList());
        for (FinancialOrderDO o : unOrderList) {
            int days = DateUtils.getDaysBetweenDate(now, o.getBuyDate());
            totalRevenu = totalRevenu.add(o.getDayInterest().multiply(BigDecimal.valueOf(days)));
        }
        response.setTotalRevenue(totalRevenu);
        return response;
    }

    public FinancialProductDO getById(Integer id) {
        return financialProductDao.getById(id);
    }

    public List<FinancialProductDO> getByParams(FinancialProductParam param) {
        return financialProductDao.getByParams(param);
    }

    public int count(FinancialProductParam param) {
        return financialProductDao.count(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public int save(FinancialProductDO financialProduct) {
        return financialProductDao.save(financialProduct);
    }

    @Transactional(rollbackFor = Exception.class)
    public int update(FinancialProductDO entity, FinancialProductParam param) {
        return financialProductDao.update(entity, param);
    }

}
