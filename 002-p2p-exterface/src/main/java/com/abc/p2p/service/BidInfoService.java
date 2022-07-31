package com.abc.p2p.service;

import com.abc.p2p.model.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoService {
    Long queryBidInfo();

    List<BidInfo> queryBidInfoByLoanId(Integer loanId);

    //投资
    String invest(Map<String, Object> parasMap);
}
