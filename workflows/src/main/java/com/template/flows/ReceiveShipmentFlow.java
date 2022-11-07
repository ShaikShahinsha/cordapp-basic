package com.template.flows;

import net.corda.core.flows.*;

@InitiatedBy(ShipmentFlow.class)
public class ReceiveShipmentFlow extends FlowLogic<Void> {

    private FlowSession counterPartySession;

    public  ReceiveShipmentFlow(FlowSession counterPartySession) {
        this.counterPartySession = counterPartySession;
    }

    @Override
    public Void call() throws FlowException {
        subFlow(new ReceiveFinalityFlow(counterPartySession));
        return null;
    }
}
