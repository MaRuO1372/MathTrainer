package com.mathma.mathtrainew;

public class Model {
    private String name, ded, geo, insurance, timeZone;

    public Model(){

    }

    public Model(String name, String ded, String insurance) {
        this.name = name;
        this.ded = ded;
        this.insurance = insurance;
    }

    public String getName() {
        return name;
    }


    public String getDed() {
        return ded;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

}
