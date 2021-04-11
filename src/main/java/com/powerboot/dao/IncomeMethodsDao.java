package com.powerboot.dao;

import com.powerboot.domain.IncomeMethodsDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface IncomeMethodsDao {

    List<IncomeMethodsDO> getContent();
}
