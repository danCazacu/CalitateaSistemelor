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

}
