package cartransfer;

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
        name = "CarTransfer",
        info = @Info(
                title = "Car Transfer Contract",
                description = "A simple Car Transfer Contract example",
                version = "0.0.1-SNAPSHOT"
        )
)

@Default
public class CarTransfer implements ContractInterface {

    private final Genson genson = new Genson();
    private enum CarTransferErrors {
        CAR_NOT_FOUND,
        CAR_ALREADY_EXISTS
    }

    @Transaction()
    public void initLedger(final Context ctx){
        ChaincodeStub stub = ctx.getStub();

        Car car = new Car("1", "FirstType","Mark","30000");
        String carState = genson.serialize(car);
        stub.putStringState("1", carState);

    }

    @Transaction()
    public Car addNewCar(final Context ctx, final String id,
                         final String type, final String owner,
                         final String value){

        ChaincodeStub stub = ctx.getStub();
        String carState = stub.getStringState(id);

        if(!carState.isEmpty()){

            String errorMessage = String.format("Car %s already exists.", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CarTransferErrors.CAR_ALREADY_EXISTS.toString());
        }

        Car newCar = new Car(id, type, owner, value);
        String newCarState = genson.serialize(newCar);
        stub.putStringState(id, newCarState);

        return newCar;
    }

    @Transaction()
    public Car queryCarById(final Context ctx, final String id){

        ChaincodeStub stub = ctx.getStub();
        String carState = stub.getStringState(id);

        if(carState.isEmpty()){
            String errorMessage = String.format("Car %s does not exist.", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CarTransferErrors.CAR_NOT_FOUND.toString());
        }
        Car car = genson.deserialize(carState, Car.class);
        return car;
    }

    @Transaction
    public Car changeCarOwnership(final Context ctx, final String id,
                                  final String newOwner){

        ChaincodeStub stub = ctx.getStub();

        String carState = stub.getStringState(id);

        if(carState.isEmpty()){
            String errorMessage = String.format("Car %s does not exist", id);
            System.out.println(errorMessage);
            throw  new ChaincodeException(errorMessage, CarTransferErrors.CAR_NOT_FOUND.toString());
        }

        Car car = genson.deserialize(carState, Car.class);
        Car newCar = new Car(id, car.getType(), newOwner, car.getValue());

        String newCarState = genson.serialize(newCar);

        stub.putStringState(id, newCarState);

        return newCar;
    }

}
