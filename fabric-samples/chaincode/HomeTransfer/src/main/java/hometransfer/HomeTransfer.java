package hometransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;


@Contract(
        name = "HomeTransfer",
        info = @Info(
                title = "Home Transfer contract",
                description = "A Sample home transfer chaincode example",
                version = "0.0.1-SNAPSHOT"
        )
)
@Default
public final class HomeTransfer implements ContractInterface {

    private final Genson genson = new Genson();
    private enum HomeTransferErrors {
        HOME_NOT_FOUND,
        HOME_ALREADY_EXISTS
    }

    @Transaction()
    public void initLedger(final Context ctx){
        ChaincodeStub stub = ctx.getStub();
        Home home = new Home("1", "FirstHome", "2000", "Mark", "6756");
        String homeState = genson.serialize(home);
        stub.putStringState("1", homeState);
    }

    @Transaction()
    public Home addNewHome(final Context ctx, final String id,
                           final String name, final String area,
                           final String ownername, final String value){

        ChaincodeStub stub = ctx.getStub();

        String homeState = stub.getStringState(id);

        if(!homeState.isEmpty()){
            String errorMessage = String.format("Home %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, HomeTransferErrors.HOME_ALREADY_EXISTS.toString());
        }

        Home home = new Home(id, name, area, ownername, value);
        homeState = genson.serialize(home);
        stub.putStringState(id, homeState);

        return home;
    }

    @Transaction()
    public Home queryHomeById(final Context ctx, final String id){

        ChaincodeStub stub = ctx.getStub();
        String homeState = stub.getStringState(id);

        if(homeState.isEmpty()){
            String errorMessage = String.format("Home %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, HomeTransferErrors.HOME_NOT_FOUND.toString());
        }

        Home home = genson.deserialize(homeState, Home.class);
        return home;
    }

    @Transaction()
    public Home changeHomeOwnership(final Context ctx, final String id, final String newHomeOwner){

        ChaincodeStub stub = ctx.getStub();

        String homeState = stub.getStringState(id);

        if(homeState.isEmpty()){

            String errorMessage = String.format("Home %s does not exist",id);
            throw new ChaincodeException(errorMessage, HomeTransferErrors.HOME_NOT_FOUND.toString());
        }

        Home home = genson.deserialize(homeState, Home.class);
        Home newHome = new Home(id, home.getName(), home.getArea(), newHomeOwner, home.getValue());

        String newHomeState = genson.serialize(newHome);
        stub.putStringState(id, newHomeState);

        return newHome;
    }
}