package com.nature.demo.utils.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/12/28
 */
@Entity(nameInDb = "BaseReferInfo",createInDb = false)
public class BaseReferInfo {

    @Property(nameInDb = "ID")
    @Id(autoincrement = true)
    public Long id;

    @Property(nameInDb = "MeaInstrumentID")
    public String meaInstrumentID;

    @Property(nameInDb = "MeaHandBookID")
    public String meaHandBookID;

    @Property(nameInDb = "UserDepartment")
    public String userDepartment;

    @Property(nameInDb = "MaterialRef")
    public Double materialRef;

    @Property(nameInDb = "LenCD")
    public Double lenCD;

    @Property(nameInDb = "LenAE")
    public Double lenAE;

    @Property(nameInDb = "LenBF")
    public Double lenBF;

    @Property(nameInDb = "LenH")
    public Double lenH;

    @Property(nameInDb = "WheelDiameter")
    public Double wheelDiameter;

    @Property(nameInDb = "LenAC")
    public Double lenAC;

    @Property(nameInDb = "LenBD")
    public Double lenBD;

    @Property(nameInDb = "LenAB")
    public Double lenAB;

    @Property(nameInDb = "LenEO")
    public Double lenEO;

    @Property(nameInDb = "LenFO")
    public Double lenFO;

    @Property(nameInDb = "DistSensorAT")
    public Double distSensorAT;

    @Property(nameInDb = "DistSensorBT")
    public Double distSensorBT;

    @Property(nameInDb = "DistSensorCT")
    public Double distSensorCT;

    @Property(nameInDb = "DistSensorDT")
    public Double distSensorDT;

    @Property(nameInDb = "LeftDistSensorKL")
    public Double leftDistSensorKL;

    @Property(nameInDb = "RightDistSensorKR")
    public Double rightDistSensorKR;

    @Property(nameInDb = "AngleSensorXT")
    public Double angleSensorXT;

    @Property(nameInDb = "AngleSensorYT")
    public Double angleSensorYT;

    @Property(nameInDb = "AngleSensorKX")
    public Double angleSensorKX;

    @Property(nameInDb = "AngleSensorKY")
    public Double angleSensorKY;

    @Property(nameInDb = "Temperature")
    public Double temperature;

    @Property(nameInDb = "WheelResolution")
    public Integer wheelResolution;

    @Generated(hash = 947562451)
    public BaseReferInfo(Long id, String meaInstrumentID, String meaHandBookID,
            String userDepartment, Double materialRef, Double lenCD, Double lenAE,
            Double lenBF, Double lenH, Double wheelDiameter, Double lenAC,
            Double lenBD, Double lenAB, Double lenEO, Double lenFO,
            Double distSensorAT, Double distSensorBT, Double distSensorCT,
            Double distSensorDT, Double leftDistSensorKL, Double rightDistSensorKR,
            Double angleSensorXT, Double angleSensorYT, Double angleSensorKX,
            Double angleSensorKY, Double temperature, Integer wheelResolution) {
        this.id = id;
        this.meaInstrumentID = meaInstrumentID;
        this.meaHandBookID = meaHandBookID;
        this.userDepartment = userDepartment;
        this.materialRef = materialRef;
        this.lenCD = lenCD;
        this.lenAE = lenAE;
        this.lenBF = lenBF;
        this.lenH = lenH;
        this.wheelDiameter = wheelDiameter;
        this.lenAC = lenAC;
        this.lenBD = lenBD;
        this.lenAB = lenAB;
        this.lenEO = lenEO;
        this.lenFO = lenFO;
        this.distSensorAT = distSensorAT;
        this.distSensorBT = distSensorBT;
        this.distSensorCT = distSensorCT;
        this.distSensorDT = distSensorDT;
        this.leftDistSensorKL = leftDistSensorKL;
        this.rightDistSensorKR = rightDistSensorKR;
        this.angleSensorXT = angleSensorXT;
        this.angleSensorYT = angleSensorYT;
        this.angleSensorKX = angleSensorKX;
        this.angleSensorKY = angleSensorKY;
        this.temperature = temperature;
        this.wheelResolution = wheelResolution;
    }

    @Generated(hash = 165923021)
    public BaseReferInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeaInstrumentID() {
        return this.meaInstrumentID;
    }

    public void setMeaInstrumentID(String meaInstrumentID) {
        this.meaInstrumentID = meaInstrumentID;
    }

    public String getMeaHandBookID() {
        return this.meaHandBookID;
    }

    public void setMeaHandBookID(String meaHandBookID) {
        this.meaHandBookID = meaHandBookID;
    }

    public String getUserDepartment() {
        return this.userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public Double getMaterialRef() {
        return this.materialRef;
    }

    public void setMaterialRef(Double materialRef) {
        this.materialRef = materialRef;
    }

    public Double getLenCD() {
        return this.lenCD;
    }

    public void setLenCD(Double lenCD) {
        this.lenCD = lenCD;
    }

    public Double getLenAE() {
        return this.lenAE;
    }

    public void setLenAE(Double lenAE) {
        this.lenAE = lenAE;
    }

    public Double getLenBF() {
        return this.lenBF;
    }

    public void setLenBF(Double lenBF) {
        this.lenBF = lenBF;
    }

    public Double getLenH() {
        return this.lenH;
    }

    public void setLenH(Double lenH) {
        this.lenH = lenH;
    }

    public Double getWheelDiameter() {
        return this.wheelDiameter;
    }

    public void setWheelDiameter(Double wheelDiameter) {
        this.wheelDiameter = wheelDiameter;
    }

    public Double getLenAC() {
        return this.lenAC;
    }

    public void setLenAC(Double lenAC) {
        this.lenAC = lenAC;
    }

    public Double getLenBD() {
        return this.lenBD;
    }

    public void setLenBD(Double lenBD) {
        this.lenBD = lenBD;
    }

    public Double getLenAB() {
        return this.lenAB;
    }

    public void setLenAB(Double lenAB) {
        this.lenAB = lenAB;
    }

    public Double getLenEO() {
        return this.lenEO;
    }

    public void setLenEO(Double lenEO) {
        this.lenEO = lenEO;
    }

    public Double getLenFO() {
        return this.lenFO;
    }

    public void setLenFO(Double lenFO) {
        this.lenFO = lenFO;
    }

    public Double getDistSensorAT() {
        return this.distSensorAT;
    }

    public void setDistSensorAT(Double distSensorAT) {
        this.distSensorAT = distSensorAT;
    }

    public Double getDistSensorBT() {
        return this.distSensorBT;
    }

    public void setDistSensorBT(Double distSensorBT) {
        this.distSensorBT = distSensorBT;
    }

    public Double getDistSensorCT() {
        return this.distSensorCT;
    }

    public void setDistSensorCT(Double distSensorCT) {
        this.distSensorCT = distSensorCT;
    }

    public Double getDistSensorDT() {
        return this.distSensorDT;
    }

    public void setDistSensorDT(Double distSensorDT) {
        this.distSensorDT = distSensorDT;
    }

    public Double getLeftDistSensorKL() {
        return this.leftDistSensorKL;
    }

    public void setLeftDistSensorKL(Double leftDistSensorKL) {
        this.leftDistSensorKL = leftDistSensorKL;
    }

    public Double getRightDistSensorKR() {
        return this.rightDistSensorKR;
    }

    public void setRightDistSensorKR(Double rightDistSensorKR) {
        this.rightDistSensorKR = rightDistSensorKR;
    }

    public Double getAngleSensorXT() {
        return this.angleSensorXT;
    }

    public void setAngleSensorXT(Double angleSensorXT) {
        this.angleSensorXT = angleSensorXT;
    }

    public Double getAngleSensorYT() {
        return this.angleSensorYT;
    }

    public void setAngleSensorYT(Double angleSensorYT) {
        this.angleSensorYT = angleSensorYT;
    }

    public Double getAngleSensorKX() {
        return this.angleSensorKX;
    }

    public void setAngleSensorKX(Double angleSensorKX) {
        this.angleSensorKX = angleSensorKX;
    }

    public Double getAngleSensorKY() {
        return this.angleSensorKY;
    }

    public void setAngleSensorKY(Double angleSensorKY) {
        this.angleSensorKY = angleSensorKY;
    }

    public Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getWheelResolution() {
        return this.wheelResolution;
    }

    public void setWheelResolution(Integer wheelResolution) {
        this.wheelResolution = wheelResolution;
    }


}
