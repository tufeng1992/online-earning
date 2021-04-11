package com.powerboot.dao;

import com.powerboot.domain.WindowContentDO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface WindowContentDao {

    List<WindowContentDO> getContent();
}
