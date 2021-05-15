package net.corda.samples.obligation.flows.account;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.workflows.services.AccountService;
import com.r3.corda.lib.accounts.workflows.services.KeyManagementBackedAccountService;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.flows.StartableByService;

import java.util.List;
import java.util.stream.Collectors;

@StartableByRPC
@StartableByService
public class MyAccountsFlow extends FlowLogic<List<String>> {


    public MyAccountsFlow() {
    }

    @Override
    @Suspendable
    public List<String> call() throws FlowException {
        AccountService accountService = getServiceHub().cordaService(KeyManagementBackedAccountService.class);
        List<String> aAccountsQuery = accountService.ourAccounts().stream().map(it -> it.getState().getData().getName()).collect(Collectors.toList());
        return aAccountsQuery;
    }
}