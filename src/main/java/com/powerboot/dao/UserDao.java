package com.powerboot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.powerboot.domain.MemberInfoDO;
import com.powerboot.domain.UserDO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表
 */
@Mapper
public interface UserDao extends BaseMapper<UserDO> {

    UserDO get(Long id);

    Integer getInviteUserCount(Long parentId);

    /**
     * 根据邀请人code
     *
     * @param referralCode
     * @return
     */
    UserDO getByReferralCode(String referralCode);


    UserDO getByMobileAndAppId(@Param("mobile") String mobile, @Param("appId") String appId);

    int save(UserDO user);

    int insertSale(UserDO user);

    int update(UserDO user);

    int updateClose(@Param("userId") Long userId);

    int addMoney(@Param("id") Long userId, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    int reduceMoney(@Param("id") Long userId, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    int updateMoney(@Param("id") Long userId, @Param("amount") BigDecimal amount);

    int updateUserVIP(@Param("id") Long userId, @Param("memberLevel") Integer memberLevel);

    int updateFirstRechargeById(@Param("id") Long id);

    List<UserDO> getUserByParentId(@Param("parentId") Long parentId);

    List<UserDO> getUserByParentIdByPage(Map<String, Object> map);

    List<UserDO> getUserList(@Param("list") List<Long> list);

    List<UserDO> getUserListByPage(@Param("list") List<Long> list, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取下一级数量
     *
     * @param id
     * @return
     */
    List<UserDO> getTotalPeople(@Param("id") Long id);

    Integer getCountPeople(@Param("id") Long id);

    /**
     * 获取所有vip
     *
     * @return
     */
    List<UserDO> getAllVIP();

    /**
     * 根据saleId获取所有VIP
     * @param map
     * @return
     */
    List<UserDO> getAllVIPBySale(Map<String, Object> map);

    /**
     * 分组获取saleId
     * @return
     */
    List<UserDO> getAllSaleId();

    Integer getUserCount(@Param("role") Integer role,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         @Param("userReferral") Integer userReferral,
                         @Param("saleReferral") Integer saleReferral,
                         @Param("saleId") Long saleId);

    Integer getRegisterIpCount(@Param("registerIp") String registerIp);

    List<UserDO> getUserByAccountNumber(@Param("accountNumber") String accountNumber);

    BigDecimal getUserRechargeAmount(Long id);

     Integer count(Map<String, Object> params);

    List<UserDO> list(Map<String, Object> params);
}
