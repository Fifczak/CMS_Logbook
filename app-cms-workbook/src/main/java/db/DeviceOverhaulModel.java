package db;


public class DeviceOverhaulModel {
    public Integer device_maintanance_rhs;
    public String device_maintanance_comment;
    public Boolean if_overhaul;
    public Integer device_fkey;
    public String device_maintanance_date;

    public String getRhs() {
        return device_maintanance_comment;
    }

    public DeviceOverhaulModel(Integer device_maintanance_rhs, String device_maintanance_comment, Boolean if_overhaul, Integer device_fkey, String device_maintanance_date) {
        this.device_maintanance_rhs = device_maintanance_rhs;
        this.device_maintanance_comment = device_maintanance_comment;
        this.if_overhaul = if_overhaul;
        this.device_fkey = device_fkey;
        this.device_maintanance_date = device_maintanance_date;
    }

    public String toString() {
        return "Date:" + device_maintanance_date + ", RHs:" + device_maintanance_rhs + ", Comment:" + device_maintanance_comment + ", Overhaul:" + if_overhaul;
    }


}