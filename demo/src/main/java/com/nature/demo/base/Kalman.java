package com.nature.demo.base;

import android.location.Location;


import com.dk.base.Point2D;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * @author hq
 * @date 2021-10-30 16:29
 * @desc
 */
public class Kalman {

    private boolean isInitialised;

    private RealMatrix a;
    private RealVector x;
    private RealMatrix r;
    private KalmanFilter filter;


    public Kalman() {
        this.isInitialised = false;
        double dt = 1.0d;

        // State transition matrix  状态转移矩阵
        a = new Array2DRowRealMatrix(new double[][]{
                {1d, 0d, dt, 0d},
                {0d, 1d, 0d, dt},
                {0d, 0d, 1d, 0d},
                {0d, 0d, 0d, 1d}
        });

        // control input matrix  控制输入矩阵
        RealMatrix b = new Array2DRowRealMatrix(new double[][]{
                {Math.pow(dt, 2d) / 2},
                {Math.pow(dt, 2d) / 2},
                {dt},
                {dt}
        });

        // Measurement matrix  测量矩阵
        RealMatrix h = new Array2DRowRealMatrix(new double[][]{
                {1d, 0d, 0d, 0d},
                {0d, 1d, 0d, 0d}
        });

        // Noise covariance matrix  噪声协方差矩阵
        RealMatrix q = new Array2DRowRealMatrix(new double[][]{
                {Math.pow(dt, 4d) / 4d, 0d, Math.pow(dt, 3d) / 2d, 0d},
                {0d, Math.pow(dt, 4d) / 4d, 0d, Math.pow(dt, 3d) / 2d},
                {Math.pow(dt, 3d) / 2d, 0d, Math.pow(dt, 2d), 0d},
                {0d, Math.pow(dt, 3d) / 2d, 0d, Math.pow(dt, 2d)}
        });

        double measurementNoise = 10d;
        r = new Array2DRowRealMatrix(new double[][]{
                {Math.pow(measurementNoise, 2d), 0d},
                {0d, Math.pow(measurementNoise, 2d)}
        });

        //initialize to reading position and speeds?  初始化到读取位置和速度？
        x = new ArrayRealVector(new double[]{51.0127, 3.708612, 0.0, 0.0});

        ProcessModel pm = new DefaultProcessModel(a, b, q, x, null);
        MeasurementModel mm = new DefaultMeasurementModel(h, r);
        filter = new KalmanFilter(pm, mm);

    }

    /**
     * Estimate the position by:  通过以下方式估计位置：
     * 1. predicting, based on previous events  根据以前的事件进行预测
     * 2. improving the state of the filter by looking at the error we made in step 1. 通过查看我们在步骤1中产生的错误来改进过滤器的状态。
     *
     * @param position {@link Location} which contains location information.
     * @return {@link  } containing the estimation we made.
     */
    public Point2D estimatePosition(Location position) {
        if (!this.isInitialised) {
            x.setEntry(0, position.getLatitude());
            x.setEntry(1, position.getLongitude());
            this.isInitialised = true;
        }

        filter.predict();

        // set noise  GPS精度
        r.setEntry(0, 0, Math.pow(position.getAccuracy(), 2d));
        r.setEntry(1, 1, Math.pow(position.getAccuracy(), 2d));

        // x = a*x (state prediction)  状态预测
        x = new ArrayRealVector(new double[]{position.getLatitude(), position.getLatitude(), position.getSpeed(), position.getSpeed()});
        x = a.operate(x);

        // The measurement we got as an argument. (position X and Y)  我们得到的度量作为一个参数。（位置X和Y）
        RealVector z = new ArrayRealVector(new double[]{position.getLatitude(), position.getLongitude()});

        //correct the state estimate  修正状态估计
        filter.correct(z);

        // get the correct state - the position  获得正确的状态-位置
        double pX = filter.getStateEstimation()[0];
        double pY = filter.getStateEstimation()[1];

        return new Point2D(pX, pY);
    }
}
