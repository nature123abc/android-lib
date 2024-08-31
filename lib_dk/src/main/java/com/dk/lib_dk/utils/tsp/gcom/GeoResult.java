package com.dk.lib_dk.utils.tsp.gcom;

import com.dk.common.DateUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/12
 */
public class GeoResult {
    String result;
    Integer geoCode;//GeoCom调用是否成功编码   0表示成功
    Integer tranId = 0;//请求Id编码，如果没有  默认为0

    Integer rpcCode;//功能是否实现编码，       0表示成功
    public List<String> parms;//参数信息
    static final String strHeader = "%R1P";

    public GeoResult(String result) {
        this.result = result;
    }


    public void comput() {
        if (!result.startsWith(strHeader)) {
            throw new CommonException("测量数据有误" + result);
        }
        result = result.replace("\r", "");
        result = result.replace("\n", "");

        String[] list = result.split(":");
        if (list.length != 2) {
            throw new CommonException("测量数据有误" + result);
        }

        String[] first = list[0].split(",");
        String[] second = list[1].split(",");
        geoCode = Integer.valueOf(first[1]);
        tranId = Integer.valueOf(first[2]);
        rpcCode = Integer.valueOf(second[0]);

        if (GeoError.GRC_OK.code != geoCode) {
            throw new CommonException(GeoError.parse(geoCode).info);
        }
        if (GeoError.GRC_OK.code != rpcCode) {
            throw new CommonException(GeoError.parse(rpcCode).info);
        }
        parms = new ArrayList<>();
        for (int i = 1; i < second.length; i++) {
            parms.add(second[i]);
        }
    }


    public static int obtFace(List<String> parms) {
        if (parms.size() < 1) {
            throw new CommonException("数据有误");
        }
        return Integer.valueOf(parms.get(0));
    }

    public static BaseObser obtObsInfo(List<String> parms) {
        if (parms.size() < 3) {
            throw new CommonException("距离测量有误");
        }
        BaseObser t = new BaseObser(null, Double.valueOf(parms.get(0)), Double.valueOf(parms.get(1)), Double.valueOf(parms.get(2)));
        t.dateTime = DateUtils.getDate();
        return t;
    }

    public static Double[] obtEquipParm(List<String> parms) {
        if (parms.size() < 4) {
            throw new CommonException("参数有误");
        }
        Double[] inf = new Double[4];
        for (int i = 0; i < parms.size(); i++) {
            inf[i] = Double.valueOf(parms.get(i));
        }
        return inf;
    }

    // %R1P,0,0:RC,E0[double],N0[double],H0[double],Hi[double]
    public static Double[] obtStationXYH(List<String> parms) {
        if (parms.size() < 4) {
            throw new CommonException("参数有误");
        }
        Double[] inf = new Double[4];
        for (int i = 1; i < parms.size(); i++) {
            inf[i] = Double.valueOf(parms.get(i));
        }
        return inf;
    }
}
