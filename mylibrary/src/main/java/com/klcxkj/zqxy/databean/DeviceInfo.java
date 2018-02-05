package com.klcxkj.zqxy.databean;

import java.io.Serializable;

public class DeviceInfo implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {
	 "data": [
	 {
	 "LCID": 86,
	 "LDName": "B栋",
	 "DevName": "B栋_2层_201房",
	 "DevID": 12,
	 "QUID": 1,
	 "FJName": "201房",
	 "LCName": "2层",
	 "LDID": 3,
	 "devMac": "00:15:83:33:D9:17",
	 "IsUse": 1,
	 "DevStatusID": 0,
	 "PrjID": 1,
	 "DevDescript": "B栋_2层_201房",
	 "PrjName": "凯路创新",
	 "DevTypeName": "热水表",
	 "FJID": 87,
	 "QUName": "办公区",
	 "DevTypeID": 1
	 }
	 ],
	 "error_code": "0"
	 }
	 */
	public int LCID;  //楼层ID
	public String LDName;
	public String DevName;
	public int DevID;
	public int QUID;  //区域ID
	public String FJName;
	public String LCName;
	public int LDID;  //楼栋	id
	public String devMac;
	public int IsUse;
	public int DevStatusID;
	public int PrjID;
	public String DevDescript;
	public String PrjName;
	public String DevTypeName;
	public int FJID; //房间ID
	public String QUName;
	public int DevTypeID; // 1.表示是水表 2 .表示开水器
	public int Dsbtypeid; //大类





	@Override
	public String toString() {
		return "DeviceInfo{" +
				"LCID=" + LCID +
				", LDName='" + LDName + '\'' +
				", DevName='" + DevName + '\'' +
				", DevID=" + DevID +
				", QUID=" + QUID +
				", FJName='" + FJName + '\'' +
				", LCName='" + LCName + '\'' +
				", LDID=" + LDID +
				", devMac='" + devMac + '\'' +
				", IsUse=" + IsUse +
				", DevStatusID=" + DevStatusID +
				", PrjID=" + PrjID +
				", DevDescript='" + DevDescript + '\'' +
				", PrjName='" + PrjName + '\'' +
				", DevTypeName='" + DevTypeName + '\'' +
				", FJID=" + FJID +
				", QUName='" + QUName + '\'' +
				", DevTypeID=" + DevTypeID +
				'}';
	}
}
