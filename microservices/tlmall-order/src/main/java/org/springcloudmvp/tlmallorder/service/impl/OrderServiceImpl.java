/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springcloudmvp.tlmallorder.service.impl;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springcloudmvp.tlmallcommon.BusinessException;
import org.springcloudmvp.tlmallcommon.Result;
import org.springcloudmvp.tlmallorder.entity.Order;
import org.springcloudmvp.tlmallorder.feign.AccountServiceFeignClient;
import org.springcloudmvp.tlmallorder.feign.StorageServiceFeignClient;
import org.springcloudmvp.tlmallorder.feign.dto.AccountDTO;
import org.springcloudmvp.tlmallorder.feign.dto.StorageDTO;
import org.springcloudmvp.tlmallorder.mapper.OrderMapper;
import org.springcloudmvp.tlmallorder.service.OrderService;

import java.sql.Timestamp;
import java.util.List;

import static org.springcloudmvp.tlmallcommon.ResultEnum.COMMON_FAILED;


@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AccountServiceFeignClient accountService;

    @Autowired
    private StorageServiceFeignClient storageService;

    @Autowired
    RestTemplate restTemplate;

    @Override
    @GlobalTransactional(name = "createOrder", rollbackFor = Exception.class)
    public Result<?> createOrder(String userId, String commodityCode, Integer count) {

        logger.info("[createOrder] current XID: {}", RootContext.getXID());

        // deduct storage
        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setCommodityCode(commodityCode);
        storageDTO.setCount(count);

        // 方法1：直接用RestTemplate远程调用
        // String storage_url = "http://localhost:8010/storage/reduce-stock";

        // 方法2：使用Nacos + Load Balaner
        // 可以使用微服务名tlmall-account代替localhost:8020
        // String storage_url = "http://tlmall-storage/storage/reduce-stock";
        // Integer storageCode = restTemplate.postForObject(storage_url,storageDTO, Result.class).getCode();

        // 方法3：使用OpenFeign远程调用
        // 进一步减少硬编码，向调用本地API一样调用Rest API
        Integer storageCode = storageService.reduceStock(storageDTO).getCode();
        if (storageCode.equals(COMMON_FAILED.getCode())) {
            throw new BusinessException("stock not enough");
        }

        // deduct balance
        int price = count * 2;
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUserId(userId);
        accountDTO.setPrice(price);

        // 方法1：RestTemplate远程调用
        // String account_url = "http://localhost:8020/account/reduce-balance";

        // 方法2：使用Nacos + Load Balaner
        // 可以使用微服务名tlmall-account代替localhost:8020
        // String account_url = "http://tlmall-account/account/reduce-balance";
        // Integer accountCode = restTemplate.postForObject(account_url, accountDTO, Result.class).getCode();

        // 方法3：使用OpenFeign远程调用
        // 进一步减少硬编码，向调用本地API一样调用Rest API
        Integer accountCode = accountService.reduceBalance(accountDTO).getCode();
        if (accountCode.equals(COMMON_FAILED.getCode())) {
            throw new BusinessException("balance not enough");
        }

        // save order
        Order order = new Order();
        order.setUserId(userId);
        order.setCommodityCode(commodityCode);
        order.setCount(count);
        order.setMoney(price);
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        orderMapper.saveOrder(order);
        logger.info("[createOrder] orderId: {}", order.getId());

        return Result.success(order);
    }

    @Override
    public Result<?> getOrderByUserId(String userId) {
        List<Order> list = orderMapper.getOrderByUserId(userId);

        return Result.success(list);
    }

}
