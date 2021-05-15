package net.corda.samples.obligation.states;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.samples.obligation.contracts.IOUContract;
import org.jetbrains.annotations.NotNull;

import java.util.Currency;
import java.util.List;

/**
 * The IOU State object, with the following properties:
 * - [amount] The amount owed by the [borrower] to the [lender]
 * - [lender] The lending party.
 * - [borrower] The borrowing party.
 * - [contracts] Holds a reference to the [IOUContract]
 * - [paid] Records how much of the [amount] has been paid.
 * - [linearId] A unique id shared by all LinearState states representing the same agreement throughout history within
 *   the vaults of all parties. Verify methods should check that one input and one output share the id in a transaction,
 *   except at issuance/termination.
 */

@BelongsToContract(IOUContract.class)
public class AccountState implements ContractState, LinearState {

    public final UniqueIdentifier account;
    public final Party accountNode;
    public final Boolean isIssuer;

    // Private constructor used only for copying a State object
    @ConstructorForDeserialization
    private AccountState(UniqueIdentifier account, Party accountNode, Boolean isIssuer){
        this.account = account;
        this.accountNode = accountNode;
        this.isIssuer = isIssuer;
    }

    public AccountState(UniqueIdentifier account, Party accountNode) {
        this(account, accountNode, false);
    }

    public Party getAccountNode() {
        return accountNode;
    }

    public UniqueIdentifier getAccount() {
        return account;
    }

    public Boolean getIsIssuer() {
        return isIssuer;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return account;
    }

    /**
     *  This method will return a list of the nodes which can "use" this states in a valid transaction. In this case, the
     *  lender or the borrower.
     */
    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(accountNode);
    }

    public AccountState acceptIssuer() {
        return new AccountState(account, accountNode, true);
    }

    public AccountState banIssuer() {
        return new AccountState(account, accountNode, false);
    }

}
