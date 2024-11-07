package cartransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Car {

    @Property()
    private final String id;

    @Property()
    private final String type;

    @Property()
    private final String owner;

    @Property()
    private final String value;

    public Car(@JsonProperty("id") String id, @JsonProperty("type") String type,
               @JsonProperty("owner") String owner, @JsonProperty("value") String value) {
        this.id = id;
        this.type = type;
        this.owner = owner;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getOwner() {
        return owner;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Car other = (Car) obj;

        return Objects.deepEquals(new String[]{getId(), getType(), getOwner(), getValue()},
                new String[]{other.getId(), other.getType(), other.getOwner(), other.getValue()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),getType(), getOwner(),getValue());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"@"+ Integer.toHexString(hashCode())+
                "[id="+getId()+", type="+getType()+", owner="+getOwner()+", value="+getValue()+"]";
    }
}
