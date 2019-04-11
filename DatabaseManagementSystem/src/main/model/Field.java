package main.model;

public class Field {
    private String stringValue;
    private Integer intValue;
    private Column.Type type;
    public Field(){
        stringValue = null;
        intValue = null;
    }
    public Field(String val){
        setValue(val);
    }
    public Field(int val){
        setValue(val);
    }
    /**
     * Call isStringValueSet before this in order to avoid NPE
     *
     * @return value as String
     */
    public String getStringValue() {
        return stringValue;
    }

    public void setValue(String stringValue) {
        this.stringValue = stringValue;
        this.intValue = null;
        this.type = Column.Type.STRING;
    }
    /**
     * Call isIntValueSet before this in order to avoid NPE
     *
     * @return value as Integer
     */
    public Integer getIntValue() {
        return intValue;
    }

    public void setValue(Integer intValue) {
        this.intValue = intValue;
        this.stringValue = null;
        this.type = Column.Type.INT;
    }

    public boolean isStringValueSet(){
        return stringValue != null;
    }

    public boolean isIntValueSet(){
        return intValue != null;
    }

    public Column.Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Field)) {
            return false;
        }

        Field field = (Field) obj;
        if(!field.getType().equals(this.getType()))
            return false;
        if(field.isIntValueSet()!=this.isIntValueSet())
            return false;
        if(field.isStringValueSet()!=this.isStringValueSet())
            return false;
        if(field.isStringValueSet()){
            if(!field.getStringValue().equals(this.getStringValue()))
                return false;
        }
        if(field.isIntValueSet()){
            if(!field.getIntValue().equals(this.getIntValue()))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int superHash = super.hashCode();
        if(isIntValueSet())
            superHash+=getIntValue().hashCode();
        if(isStringValueSet())
            superHash-=getStringValue().hashCode();
        return superHash;
    }
}
