package com.abc.p2p.service;

import com.abc.p2p.model.FinanceAccount;

public interface FinanceAccountService {

    FinanceAccount queryMoneyByUid(Integer uid);
}
