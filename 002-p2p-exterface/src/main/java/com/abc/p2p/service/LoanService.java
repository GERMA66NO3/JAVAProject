package com.abc.p2p.service;

import com.abc.p2p.model.LoanInfo;
import com.abc.p2p.utils.PageModel;

import java.util.List;
import java.util.Map;

public interface LoanService {
    Double queryAvgLoanInfoRate();


    List<LoanInfo> queryLoanInfo(Map<String, Object> paramMap);

    List<LoanInfo> queryLoanInfoByType(Integer ptype, PageModel pageModel);

    Integer queryLoanInfoTotalCount(Integer ptype);


    LoanInfo queryLoanInfoByLoanId(Integer loanId);
}
