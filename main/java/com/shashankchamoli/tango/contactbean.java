package com.shashankchamoli.tango;

/**
 * Created by sam on 11-07-2017.
 */

public class contactbean implements Comparable<contactbean> {
    String name;
    String number;
    String id;

    contactbean(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id=id;
    }
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(obj instanceof contactbean)
        {
            contactbean temp = (contactbean) obj;
            if(this.name.equals(temp.name) && this.number.equals(temp.number))
                return true;
        }
        return false;

    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub

        return (this.name.hashCode() + this.number.hashCode());
    }
    @Override
    public int compareTo(contactbean candidate) {
        return (this.getName().compareToIgnoreCase(candidate.getName()) );
    }
}
