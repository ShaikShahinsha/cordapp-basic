package com.template.flows;

import com.template.contracts.CarContract;
import com.template.states.CarState;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.checkerframework.checker.units.qual.C;

@InitiatingFlow
@StartableByRPC
public class ShipmentFlow extends FlowLogic<Void> {

    private String model;
    private Party owner;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public ShipmentFlow(String model, Party owner) {
        this.model = model;
        this.owner = owner;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Override
    public Void call() throws FlowException {
        //Initiator flow logic

        //Retrieve the notary from the network map

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //create transaction components (Inputs/Outputs)

        CarState outputState = new CarState(model,owner,getOurIdentity());

        //create txn builder and add components

        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState, CarContract.ID)
                .addCommand(new CarContract.Shipment(),getOurIdentity().getOwningKey());
        //txBuilder.setNotary(notary); txBuilder.addOutpustate(");

        //Signing the transaction

        SignedTransaction shipmentTx = getServiceHub().signInitialTransaction(txBuilder);

        //creation session with counterparty

        FlowSession otherPartySession = initiateFlow(owner);

        //Finalising the transaction

        subFlow(new FinalityFlow(shipmentTx,otherPartySession));


        return null;
    }
}
