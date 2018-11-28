package com.depart.cea.test1.net;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.math.BigDecimal;

/**
 * Created by zhongpeng on 18/2/21.
 */
public class MapUtil {
    private static final double EARTH_RADIUS = 6378245;
    private static final double E_FACTOR = 0.00669342;
    private static final double PI = 3.1415926;

    private static double yj_sin2(double B) {
        double N = 0;
        if (B < 0) {
            B = -B;
            N = 1;
        }
        int D = (int) (B / (2 * Math.PI));
        double A = B - D * (2 * Math.PI);
        if (A > Math.PI) {
            A = A - Math.PI;
            if (N == 1)
                N = 0;
            else if (N == 0)
                N = 1;
        }
        B = A;
        double _r = B, C = B;
        A = A * A;
        C = C * A;

        _r = _r - C * 0.166666666666667;
        C = C * A;

        _r = _r + C * 0.00833333333333333;
        C = C * A;

        _r = _r - C * 0.000198412698412698;
        C = C * A;

        _r = _r + C * 0.00000275573192239859;
        C = C * A;

        _r = _r - C * 2.50521083854417 * Math.pow(10, -8);
        if (N == 1)
            _r = -_r;
        return _r;
    }

    private static double Transform_yj5(double a, double b) {
        double A = 300 + 1 * a + 2 * b + 0.1 * a * a + 0.1 * a * b + 0.1
                * Math.sqrt(Math.sqrt(a * a));
        A += (20 * yj_sin2(6 * Math.PI * a) + 20 * yj_sin2(2 * Math.PI * a)) * 0.6667;
        A += (20 * yj_sin2(Math.PI * a) + 40 * yj_sin2(Math.PI * a / 3)) * 0.6667;
        A += (150 * yj_sin2(Math.PI * a / 12) + 300 * yj_sin2(Math.PI * a / 30)) * 0.6667;
        return A;
    }

    private static double Transform_yjy5(double a, double b) {
        double A = -100 + 2 * a + 3 * b + 0.2 * b * b + 0.1 * a * b + 0.2
                * Math.sqrt(Math.sqrt(a * a));
        A += (20 * yj_sin2(6 * Math.PI * a) + 20 * yj_sin2(2 * Math.PI * a)) * 0.6667;
        A += (20 * yj_sin2(Math.PI * b) + 40 * yj_sin2(Math.PI * b / 3)) * 0.6667;
        A += (160 * yj_sin2(Math.PI * b / 12) + 320 * yj_sin2(Math.PI * b / 30)) * 0.6667;
        return A;
    }

    private static double Transform_jy5(double a, double b) {
        double A = yj_sin2(a * Math.PI / 180);
        double _r = Math.sqrt((1 - (E_FACTOR * A * A)));
        _r = (b * 180)
                / (EARTH_RADIUS / _r * Math.cos(a * Math.PI / 180) * Math.PI);
        return _r;
    }

    private static double Transform_jyj5(double a, double b) {
        double _r = yj_sin2(a * Math.PI / 180);
        double c = (1 - (E_FACTOR * _r * _r));
        double d = (EARTH_RADIUS * (1 - E_FACTOR)) / (c * Math.sqrt(c));
        return (b * 180) / (d * Math.PI);
    }

    /*
     * 真实经纬度 转 较偏经纬度
     */
    public static QCPoint encode(QCPoint pt) {
        QCPoint qc = new QCPoint();
        double _r = Transform_yj5(pt.x - 105, pt.y - 35);
        // Log.d("FOLLOWME", "R="+_r);
        double C = Transform_yjy5(pt.x - 105, pt.y - 35);
        // Log.d("FOLLOWME", "C="+C);

        qc.x = (Math.round((pt.x + Transform_jy5(pt.y, _r)) * 1000000)) / 1000000.0;
        qc.y = (Math.round((pt.y + Transform_jyj5(pt.y, C)) * 1000000)) / 1000000.0;
        return qc;
    }

    /*
     * 较偏经纬度 转 真实经纬度
     */
    public static QCPoint decode(QCPoint pt) {
        QCPoint E = encode(pt);
        double D = 0.00001;
        double ff = 1;
        int A = 0;
        QCPoint F = new QCPoint(
                Math.round((pt.x - (E.x - pt.x)) * 1000000) / 1000000,
                Math.round((pt.y - (E.y - pt.y)) * 1000000) / 1000000);
        while (ff > D) {
            A++;
            if (A > 1000)
                break;
            QCPoint P = encode(F);
            double I = F.y - (P.y - pt.y);
            double C = F.x - (P.x - pt.x);
            QCPoint M = new QCPoint(I, C);
            QCPoint H = encode(M);
            ff = Math.abs(P.y - pt.y) + Math.abs(P.x - pt.x);
            F.y = (Math.round(I * 1000000)) / 1000000.0;
            F.x = (Math.round(C * 1000000)) / 1000000.0;
        }
        return F;

    }

    /*
     * 计算2个经纬度之间的距离,单位:公里
     */
    public static double Distance(double x1, double y1, double x2, double y2) {
        double radLat1 = Radian(y1);
        double radLat2 = Radian(y2);
        double a = radLat1 - radLat2;
        double b = Radian(x1) - Radian(x2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }

    // 将角度转换为弧度
    private static double Radian(double val) {
        return val * PI / 180.0;
    }

    public static void bd_encrypt(double gg_lat, double gg_lon) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
    }

    /**
     * gps坐标转换为百度坐标
     */
    public static LatLng gps2Bd(LatLng latLng) {
        CoordinateConverter convert = new CoordinateConverter();
        convert.from(CoordinateConverter.CoordType.GPS);
        convert.coord(latLng);
        return convert.convert();
    }

    /**
     * gps坐标转换为百度坐标
     */
    public static LatLng gps2Bd(double longitude, double latitude) {
        return gps2Bd(new LatLng(latitude, longitude));
    }

    /**
     * 百度坐标转换为 gps坐标
     */
    public static LatLng bd2Gps(LatLng latLng) {
        double lat1 = latLng.latitude;
        double lng1 = latLng.longitude;
        latLng = MapUtil.gps2Bd(latLng);
        double lat2 = latLng.latitude;
        double lng2 = latLng.longitude;
        double lat = 2 * lat1 - lat2;
        double lng = 2 * lng1 - lng2;
        return new LatLng(lat, lng);
    }

    /**
     * 百度坐标转换为 gps坐标
     */
    public static int[] bd2Gps(int lngInt, int latInt) {
        LatLng latLng = new LatLng(latInt / 1000000, lngInt / 1000000);
        double lat1 = latLng.latitude;
        double lng1 = latLng.longitude;
        latLng = MapUtil.gps2Bd(latLng);
        double lat2 = latLng.latitude;
        double lng2 = latLng.longitude;
        double lat = 2 * lat1 - lat2;
        double lng = 2 * lng1 - lng2;
        return new int[]{(int) (lng * 1000000), (int) (lat * 1000000)};
    }

    /**
     * 取小数点后6位
     */
    public static double decimalPoint(double point) {
        String s = point + "";
        int position = s.length() - s.indexOf(".") + 1;
        if (position > 6) {
            BigDecimal b = new BigDecimal(point);
            return b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return point;
    }


}
