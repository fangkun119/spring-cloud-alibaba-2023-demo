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

package org.spcloudmvp.tlmallaccount.service.impl;


import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.spcloudmvp.tlmallaccount.mapper.AccountMapper;
import org.spcloudmvp.tlmallaccount.service.AccountService;
import org.springcloudmvp.tlmallcommon.BusinessException;
import org.springcloudmvp.tlmallcommon.Result;

import java.sql.Timestamp;


@Service
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 扣减账户余额
     *
     * @param userId
     * @param price
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void reduceBalance(String userId, Integer price) throws BusinessException {
        logger.info("[reduceBalance] currenet XID: {}", RootContext.getXID());

        checkBalance(userId, price);

        Timestamp updateTime = new Timestamp(System.currentTimeMillis());
        int updateCount = accountMapper.reduceBalance(userId, price, updateTime);
        if (updateCount == 0) {
            throw new BusinessException("reduce balance failed");
        }
    }

    /**
     * 获取账户余额
     *
     * @param userId
     * @return
     */
    @Override
    public Result<?> getRemainAccount(String userId) {
        Integer balance = accountMapper.getBalance(userId);
        if (balance == null) {
            return Result.failed("wrong userId,please check the userId");
        }
        return Result.success(balance);
    }

    private void checkBalance(String userId, Integer price) throws BusinessException {
        Integer balance = accountMapper.getBalance(userId);
        if (balance < price) {
            throw new BusinessException("no enough balance");
        }
    }

}
