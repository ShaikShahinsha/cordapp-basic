package com.template.contracts;

import com.template.states.CarState;
import com.template.states.TemplateState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class CarContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.CarContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        if(tx.getCommands().size() != 1 ) throw new IllegalArgumentException("There can only be one command");

        Command command = tx.getCommand(0);
        CommandData commandData = command.getValue();

        List<PublicKey> requiredSigners = command.getSigners();
        if(commandData instanceof  Shipment){
    //shipment rule, shape rules, content rules.,signer rules.
            if(tx.getInputStates().size() != 0){
                    throw new IllegalArgumentException("there cannot be input states");
            }
            if(tx.getOutputStates().size() != 1){
                throw new IllegalArgumentException("Only one vehicle can be shipped at a time");
            }

            //content rules

            ContractState outputState = tx.getOutput(0);

            if(!(outputState instanceof CarState)){
                throw new IllegalArgumentException("Output has to be of type car state");
            }

            CarState carState = (CarState) outputState;

            if(!carState.getModel().equals("Cybertruck")){
                throw new IllegalArgumentException("This is not a cybertruck");
            }

            //signer rules

            PublicKey manufacturerKey = carState.getManufacturer().getOwningKey();

            if(!requiredSigners.contains(manufacturerKey)){
                throw new IllegalArgumentException("manufacturer must sign the transaction");
            }
        }
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
//    @Override
//    public void verify(LedgerTransaction tx) {
//
//        /* We can use the requireSingleCommand function to extract command data from transaction.
//         * However, it is possible to have multiple commands in a signle transaction.*/
//        //final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
//
//    }

    // Used to indicate the transaction's intent.
//    public interface Commands extends CommandData {
//        //In our hello-world app, We will only have one command.
//        class Send implements Commands {}
//    }



    public static class Shipment implements CommandData{

    }
}