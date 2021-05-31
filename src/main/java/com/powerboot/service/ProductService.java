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
}
