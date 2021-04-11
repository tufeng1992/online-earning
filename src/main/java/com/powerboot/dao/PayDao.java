package com.powerboot.dao;

import com.powerboot.domain.PayDO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.powerboot.response.PayVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 支付结果
 */
@Mapper
public interface PayDao {

	PayDO get(Long id);
	
	List<PayDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(PayDO pay);
	
	int update(PayDO pay);

	int updatePay(PayDO pay);

	int remove(Long id);
	
	int batchRemove(Long[] ids);

	PayDO getByOrderNo(String orderNo);

	PayDO getByOutNo(String outNo);

	/**
	 * 获取充值 记录
	 * @param userId
	 * @return
	 */
	List<PayDO> getRechargeByUserId(@Param("userId") Long userId);

	/**
	 * 获取提现 记录
	 * @param userId
	 * @return
	 */
	List<PayDO> getWithdrawByUserId(@Param("userId") Long userId);

	List<PayDO> getPayinOrder();

	PayVO getCountByTypeStatus(@Param("typeList") List<Integer> typeList, @Param("status") Integer status,@Param("userId")Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	int timeout(@Param("yesterday") Date yesterday);
}
