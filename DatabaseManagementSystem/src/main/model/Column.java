package main.model;

public class Column {
    public enum Type{
        INT,
        STRING
    }
    private String name;
    private Type type;

    public Column(String name, Type type){
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
        superHash+=name.hashCode()+type.hashCode();
        return superHash;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Column))
            return false;
        Column column = (Column)obj;
        return this.name.equalsIgnoreCase(column.name) && this.type.equals(column.type);
    }
}
