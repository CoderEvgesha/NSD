package net.corda.samples.obligation.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.corda.client.jackson.JacksonSupport;
import net.corda.core.contracts.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import net.corda.finance.contracts.asset.Cash;
import net.corda.samples.obligation.flows.account.MyAccountsFlow;
import net.corda.samples.obligation.flows.account.NewAccountFlow;
import net.corda.samples.obligation.flows.account.ShareAccountFlow;
import net.corda.samples.obligation.flows.iou.IOUIssueFlow;
import net.corda.samples.obligation.flows.iou.IOUSettleFlow;
import net.corda.samples.obligation.flows.iou.IOUTransferFlow;
import net.corda.samples.obligation.flows.iou.SelfIssueCashFlow;
import net.corda.samples.obligation.states.IOUState;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.corda.finance.workflows.GetBalances.getCashBalances;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/api/iou") // The paths for HTTP requests are relative to this base path.
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(RestController.class);
    private final CordaRPCOps proxy;
    private final CordaX500Name me;

    public MainController(NodeRPCConnection rpc) {
        this.proxy = rpc.getProxy();
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();

    }

    /**
     * Helpers for filtering the network map cache.
     */
    public String toDisplayString(X500Name name) {
        return BCStyle.INSTANCE.toString(name);
    }

    private boolean isNotary(NodeInfo nodeInfo) {
        return !proxy.notaryIdentities()
                .stream().filter(el -> nodeInfo.isLegalIdentity(el))
                .collect(Collectors.toList()).isEmpty();
    }

    private boolean isMe(NodeInfo nodeInfo) {
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    private boolean isNetworkMap(NodeInfo nodeInfo) {
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }

    @Configuration
    class Plugin {
        @Bean
        public ObjectMapper registerModule() {
            return JacksonSupport.createNonRpcMapper();
        }
    }

    @GetMapping(value = "/status", produces = TEXT_PLAIN_VALUE)
    private String status() {
        return "200";
    }

    @GetMapping(value = "/servertime", produces = TEXT_PLAIN_VALUE)
    private String serverTime() {
        return (LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC"))).toString();
    }

    @GetMapping(value = "/addresses", produces = TEXT_PLAIN_VALUE)
    private String addresses() {
        return proxy.nodeInfo().getAddresses().toString();
    }

    @GetMapping(value = "/identities", produces = TEXT_PLAIN_VALUE)
    private String identities() {
        return proxy.nodeInfo().getLegalIdentities().toString();
    }

    @GetMapping(value = "/platformversion", produces = TEXT_PLAIN_VALUE)
    private String platformVersion() {
        return Integer.toString(proxy.nodeInfo().getPlatformVersion());
    }

    @GetMapping(value = "/peers", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, List<String>> getPeers() {
        HashMap<String, List<String>> myMap = new HashMap<>();

        // Find all nodes that are not notaries, ourself, or the network map.
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el) && !isMe(el) && !isNetworkMap(el));
        // Get their names as strings
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());

        myMap.put("peers", nodeNames);
        return myMap;
    }

    @GetMapping(value = "/accounts", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAccounts() {
        try {

            List<String> accounts = proxy.startTrackedFlowDynamic(MyAccountsFlow.class).getReturnValue().get();
            return ResponseEntity.ok(accounts);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonList(e.getMessage()));
        }
    }

    @GetMapping(value = "/notaries", produces = TEXT_PLAIN_VALUE)
    private String notaries() {
        return proxy.notaryIdentities().toString();
    }

    @GetMapping(value = "/flows", produces = TEXT_PLAIN_VALUE)
    private String flows() {
        return proxy.registeredFlows().toString();
    }

    @GetMapping(value = "/states", produces = TEXT_PLAIN_VALUE)
    private String states() {
        return proxy.vaultQuery(ContractState.class).getStates().toString();
    }

    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    private HashMap<String, String> whoami() {
        HashMap<String, String> myMap = new HashMap<>();
        myMap.put("me", me.toString());
        return myMap;
    }

    @PostMapping(value = "/create-account/{account}", produces = TEXT_PLAIN_VALUE)
    private ResponseEntity<String> createAccount(@PathVariable String account) {
        try {

            String result = proxy.startTrackedFlowDynamic(NewAccountFlow.class, account).getReturnValue().get();

            Party issuer = Optional.ofNullable(proxy.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Issuer,L=Tver,C=RU"))).orElseThrow(() -> new IllegalArgumentException("Unknown party name."));
            proxy.startTrackedFlowDynamic(ShareAccountFlow.class, account, issuer);

            return ResponseEntity.status(HttpStatus.CREATED).body("Account " + account + " Created");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping(value = "/ious", produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<IOUState>> getIOUs() {
        // Filter by states type: IOU.
        return proxy.vaultQuery(IOUState.class).getStates();
    }

    @GetMapping(value = "/cash", produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<Cash.State>> getCash() {
        // Filter by states type: Cash.
        return proxy.vaultQuery(Cash.State.class).getStates();
    }

    @GetMapping(value = "/cash-balances", produces = APPLICATION_JSON_VALUE)
    public Map<Currency, Amount<Currency>> cashBalances() {
        return getCashBalances(proxy);
    }

    final Currency defaultCurrency = Currency.getInstance("RUR");

    @PutMapping(value = "/issue-iou", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> issueIOU(@RequestParam(value = "amount") int amount,
                                           @RequestParam(value = "party") String party) throws IllegalArgumentException {
        // Get party objects for myself and the counterparty.
        Party me = proxy.nodeInfo().getLegalIdentities().get(0);
        Party lender = Optional.ofNullable(proxy.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Operator,L=Moscow,C=RU"))).orElseThrow(() -> new IllegalArgumentException("Unknown party name."));
        // Create a new IOU states using the parameters given.
        try {
            IOUState state = new IOUState(new Amount<>((long) amount * 100, defaultCurrency), lender, me);
            // Start the IOUIssueFlow. We block and waits for the flows to return.
            SignedTransaction result = proxy.startTrackedFlowDynamic(IOUIssueFlow.InitiatorFlow.class, state).getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Transaction id "+ result.getId() +" committed to ledger.\n " + result.getTx().getOutput(0));
            // For the purposes of this demo app, we do not differentiate by exception type.
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping(value = "/transfer-iou", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> transferIOU(@RequestParam(value = "id") String id,
                                              @RequestParam(value = "party") String party) {
        UniqueIdentifier linearId = new UniqueIdentifier(null,UUID.fromString(id));
        Party newLender = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(party));
        try {
            proxy.startTrackedFlowDynamic(IOUTransferFlow.InitiatorFlow.class, linearId, newLender).getReturnValue().get();
            return ResponseEntity.status(HttpStatus.CREATED).body("IOU "+linearId.toString()+" transferred to "+party+".");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value = "/settle-iou", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> settleIOU(@RequestParam(value = "id") String id,
                                            @RequestParam(value = "amount") int amount) {

        UniqueIdentifier linearId = new UniqueIdentifier(null, UUID.fromString(id));
        try {
            proxy.startTrackedFlowDynamic(IOUSettleFlow.InitiatorFlow.class, linearId,
                    new Amount<>((long) amount * 100, defaultCurrency)).getReturnValue().get();
            return ResponseEntity.status(HttpStatus.CREATED).body("" + amount + defaultCurrency.getCurrencyCode() + " paid off on IOU id " + linearId.toString() + ".");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value = "/issue-cash", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> issueCash(@RequestParam(value = "account") String account,
                                            @RequestParam(value = "amount") int amount) {

        try {
            Cash.State cashState = proxy.startTrackedFlowDynamic(SelfIssueCashFlow.class, account,
                    new Amount<>((long) amount * 100, defaultCurrency)).getReturnValue().get();
            return ResponseEntity.status(HttpStatus.CREATED).body(cashState.toString());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
