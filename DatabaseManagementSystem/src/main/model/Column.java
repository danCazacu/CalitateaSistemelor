package main.model;
import main.exception.InvalidValue;

import static main.service.FilteringService.isValid;
import static main.service.FilteringService.validate;
public class Column {
    public enum Type {
        INT("int"),
        STRING("string");
        private String type;

        Type(String type) {

            assert type != null: "Precondition failed: input parameter can not be null";

            this.type = type;
        }

        public String getValue() {
            return type;
        }

        public static Column.Type getEnum(String value) {
            for (Column.Type type : Column.Type.values())
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }

            throw new IllegalArgumentException();
        }

    }

    private String name;
    private Type type;

    public Column(String name, Type type) throws InvalidValue {

        assert name != null: "Precondition failed: input name parameter can not be null";
        assert type != null: "Precondition failed: input type parameter can not be null";


        isValid(name);
        this.name = name;
        this.type = type;
    }

    public void setName(String name) throws InvalidValue {

        assert name != null: "Precondition failed: input parameter can not be null";

        isValid(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int superHash = super.hashCode();
        superHash += type.hashCode();
        return superHash;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if (!(obj instanceof Column))
            return false;
        Column column = (Column) obj;

        return this.name.equalsIgnoreCase(column.name) && this.type.equals(column.type);
    }

    @Override
    public String toString() {
        return getName();
    }
}
