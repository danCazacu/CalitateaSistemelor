package main.model;
import main.exception.InvalidValue;

import static main.service.FilteringService.validate;
public class Column {
    public enum Type {
        INT("int"),
        STRING("string");
        private String type;

        Type(String type) {
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
        validate(name);
        this.name = name;
        this.type = type;
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
        superHash += name.hashCode() + type.hashCode();
        return superHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Column))
            return false;
        Column column = (Column) obj;
        return this.name.equalsIgnoreCase(column.name) && this.type.equals(column.type);
    }
}
