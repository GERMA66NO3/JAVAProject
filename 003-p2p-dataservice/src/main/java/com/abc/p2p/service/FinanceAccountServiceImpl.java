package com.abc.p2p.service;

import com.abc.p2p.mapper.FinanceAccountMapper;
import com.abc.p2p.model.FinanceAccount;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Service(interfaceClass  = FinanceAccountService.class, version = "1.0.0", timeout = 20000)
@Component
public class FinanceAccountServiceImpl implements FinanceAccountService {
    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryMoneyByUid(Integer uid) {
        return financeAccountMapper.selectAccountByUid(uid);
    }
}