package com.mgcoders.entities;


/**
 * Created by rsperoni on 02/05/17.
 */
public class Tool extends AbstractMongoEntity<Tool> {


    private String name;
    private String cwl;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCwl() {
        return cwl;
    }

    public void setCwl(String cwl) {
        this.cwl = cwl;
    }


    @Override
    public String toString() {
        return "Tool{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", cwl='" + cwl + '\'' +
                '}';
    }
}
