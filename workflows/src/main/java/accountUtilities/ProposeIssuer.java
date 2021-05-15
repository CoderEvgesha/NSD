package accountUtilities;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.ShareStateAndSyncAccounts;
import com.r3.corda.lib.accounts.workflows.flows.ShareStateWithAccount;
import com.r3.corda.lib.accounts.workflows.services.KeyManagementBackedAccountService;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.flows.StartableByService;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.samples.obligation.states.AccountState;
import net.corda.samples.obligation.states.IOUState;

import java.util.Arrays;
import java.util.List;

@StartableByRPC
@StartableByService
public class ProposeIssuer extends FlowLogic<String>{

    private final Party shareTo;
    private final String account;

    public ProposeIssuer(String account, Party shareTo) {
        this.account = account;
        this.shareTo = shareTo;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        List<StateAndRef<AccountInfo>> allmyAccounts = getServiceHub().cordaService(KeyManagementBackedAccountService.class).ourAccounts();
        StateAndRef<AccountInfo> issuer = allmyAccounts.stream()
                .filter(it -> it.getState().getData().getIdentifier().equals(account))
                .findAny().get();

        return "Shared " + account + " with " + shareTo.getName().getOrganisation();
    }
}