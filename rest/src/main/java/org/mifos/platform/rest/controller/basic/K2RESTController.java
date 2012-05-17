/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.platform.rest.controller.basic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.config.service.AccountingConfigurationService;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class K2RESTController {

    public static final String STATUS_CODE_01 = "01";
    public static final String STATUS_CODE_02 = "02";
    public static final String STATUS_CODE_03 = "03";

    public static final String DESCRIPTION_01 = "Accepted";
    public static final String DESCRIPTION_02 = "Account not Found";
    public static final String DESCRIPTION_03 = "Invalid Payment";

    public static final String STATUS = "status";
    public static final String DESCRIPTION = "description";

    public static final String DEFAULT_PAYMENT_TYPE_NAME = "M-PESA";

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountingConfigurationService accountingConfigurationService;

    @Autowired
    private LoanDao loanDao;

    @RequestMapping(value = "/basic/k2/processTransaction", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> processTransaction(@RequestParam(required = false, value = "k2_account_id") String k2AccountId,
            @RequestParam(required = false, value = "k2_transaction_id") String k2TransactionId,
            @RequestParam(required = false, value = "mm_system_id") String mmSystemId,
            @RequestParam(required = false, value = "mp_transaction_id") String mpTransactionId,
            @RequestParam(required = false, value = "biller_number") String billerNumber,
            @RequestParam(required = false, value = "transaction_date") LocalDate transactionDate,
            @RequestParam(required = false, value = "transaction_time") String transactionTime,
            @RequestParam(required = false, value = "transaction_type") String transactionType,
            @RequestParam(required = false, value = "ac_no") String acNo,
            @RequestParam(required = false) String sender,
            @RequestParam(required = false, value = "first_name") String firstName,
            @RequestParam(required = false, value = "last_name") String lastName,
            @RequestParam(required = false) BigDecimal amount, @RequestParam(required = false) String currency)
            throws Exception {

        LoanBO loanBO = loanDao.findByGlobalAccountNum(acNo);

        if (loanBO == null) {
            return accountNotFound();
        }

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReferenceDto userDto = new UserReferenceDto((short) user.getUserId());

        AccountReferenceDto accountReferenceDto = new AccountReferenceDto(loanBO.getAccountId());

        String paymentTypeName = mmSystemId;
        if (paymentTypeName.isEmpty()) {
            paymentTypeName = DEFAULT_PAYMENT_TYPE_NAME;
        }

        PaymentTypeDto paymentTypeDto = null;
        List<PaymentTypeDto> loanPaymentTypes = accountService.getLoanPaymentTypes();
        for (PaymentTypeDto paymentTypeDtoIterator : loanPaymentTypes) {
            if (paymentTypeDtoIterator.getName().equals(paymentTypeName)) {
                paymentTypeDto = paymentTypeDtoIterator;
                break;
            }
        }

        if (paymentTypeDto == null || !accountingConfigurationService.isDefaultMifosCurrency(currency)) {
            return invalidPayment();
        }

        CustomerDto customerDto = loanBO.getCustomer().toCustomerDto();

        LocalDate receiptLocalDate = transactionDate;

        AccountPaymentParametersDto payment = new AccountPaymentParametersDto(userDto, accountReferenceDto, amount,
                transactionDate, paymentTypeDto, acNo, receiptLocalDate, k2TransactionId, customerDto);

        accountService.makePayment(payment);

        return accepted();
    }

    private Map<String, String> accepted() {
        Map<String, String> response = new HashMap<String, String>();
        response.put(STATUS, STATUS_CODE_01);
        response.put(DESCRIPTION, DESCRIPTION_01);
        return response;
    }

    private Map<String, String> accountNotFound() {
        Map<String, String> response = new HashMap<String, String>();
        response.put(STATUS, STATUS_CODE_02);
        response.put(DESCRIPTION, DESCRIPTION_02);
        return response;
    }

    private Map<String, String> invalidPayment() {
        Map<String, String> response = new HashMap<String, String>();
        response.put(STATUS, STATUS_CODE_03);
        response.put(DESCRIPTION, DESCRIPTION_03);
        return response;
    }

}