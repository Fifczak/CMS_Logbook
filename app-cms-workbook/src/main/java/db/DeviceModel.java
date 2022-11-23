package db;

import java.util.ArrayList;
import java.util.Arrays;

public class DeviceModel {

    // variables for our coursename,
    // description, tracks and duration, id.
    private String deviceName;
    private String   lastDate;
    private String nextDate;
    private String remark;
    private String isoClass;
    private String reminderComment;
    private Integer imId;
    private String warningClass;
    private ArrayList<NoteModel> notes;
    private ArrayList<String> measurements;
    private ArrayList<DeviceOverhaulModel> overhauls;
    private ArrayList<RemarkModel> remarks;
    private int id;

    // creating getter and setter methods


    public String getDeviceName() {
        return deviceName;
    }
    public String getIsoClass() {
        return isoClass;
    }

    public String getLastDate() {
        return lastDate;
    }

    public String getNextDate() {
        return nextDate;
    }

    public String getRemark() {
        return remark;
    }

    public String getWarningClass() {
        return warningClass;
    }

    public int getImId() {
        return imId;
    }

    public String getReminderComment(){
        return reminderComment;
    }

    public int getId() {
        return id;
    }

    public ArrayList<NoteModel> getNotes() {
        return notes;
    }

    public ArrayList<RemarkModel> getRemarks() {
        return remarks;
    }

    public ArrayList<String> getMeasurements() {
        return measurements;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void addNote(NoteModel note) {
        this.notes.add(note);
    }

    public void deleteNote(int position){
        this.notes.remove(position);
    }

    public void deleteWorkParameters(int position){
        this.measurements.remove(position);
    }

    public void addMeasurement(String tempmeas) {
        this.measurements.add(tempmeas);
    }

    public void addOvh(DeviceOverhaulModel OvhModel) {
        this.overhauls.add(OvhModel);
    }

    public ArrayList<DeviceOverhaulModel> getDeviceOverhauls() {
        return overhauls;
    }


    @Override
    public String toString() {
        return "Device [imId=" + imId + ", deviceName=" + deviceName + "]";
    }

    // constructor
    public DeviceModel(Integer imId, String deviceName, String isoClass, String lastDate, String nextDate, String remark, String warningClass, ArrayList<NoteModel> notes, ArrayList<String> measurements, ArrayList<DeviceOverhaulModel> overhauls, ArrayList<RemarkModel> remarks) {
        this.imId = imId;
        this.deviceName = deviceName;
        this.isoClass = isoClass;
        this.lastDate = lastDate;
        this.nextDate = nextDate;
        this.remark = remark;
        this.warningClass = warningClass;
        this.notes = notes;
        this.measurements = measurements;
        this.overhauls = overhauls;
        this.remarks = remarks;

    }
}
