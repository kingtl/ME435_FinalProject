package edu.rosehulman.kingtl.integratedimagerec;

import android.os.Handler;

import edu.rosehulman.me435.RobotActivity;
import edu.rosehulman.me435.NavUtils;

/**
 * Created by kingtl on 4/27/2017.
 */

public class Scripts {
    private GolfBallDeliveryActivity mGolfBallDeliveryActivity;
    private Handler mCommandHandler = new Handler();
    private int ARM_REMOVAL_TIME_MS = 4500;
    final String positionStand1 = "POSITION 35 100 51 0 160"; //25 115 31
    final String positionStand2 = "POSITION 10 115 25 0 0"; // 135 25
    final String positionStand3 = "POSITION -19 100 56 0 160"; //-19 115 31

    public Scripts(GolfBallDeliveryActivity golfBallDeliveryActivity) {
        this.mGolfBallDeliveryActivity = golfBallDeliveryActivity;
    }

    public void testStraightDriveScript() {
        mGolfBallDeliveryActivity.sendWheelSpeed(mGolfBallDeliveryActivity.mLeftStraightPwmValue,
                mGolfBallDeliveryActivity.mRightStraightPwmValue);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendWheelSpeed(0, 0);
            }
        }, 2000); //8000 is better for a real drive test

    }

    public void nearBallScript() {
        mGolfBallDeliveryActivity.sendWheelSpeed(mGolfBallDeliveryActivity.mLeftStraightPwmValue,
                mGolfBallDeliveryActivity.mRightStraightPwmValue);
        double distanceToNearBall = 100;

        GolfBallDeliveryActivity.BallColor color = mGolfBallDeliveryActivity.mLocationColors[mGolfBallDeliveryActivity.mNearBallLocation - 1];
            if (color.equals(GolfBallDeliveryActivity.BallColor.GREEN) || color.equals(GolfBallDeliveryActivity.BallColor.YELLOW)) {
                distanceToNearBall = NavUtils.getDistance(15, 0, 90, 50);
            }
            else if (color.equals(GolfBallDeliveryActivity.BallColor.RED) || color.equals(GolfBallDeliveryActivity.BallColor.BLUE)){
                distanceToNearBall = NavUtils.getDistance(15, 0, 90, -50);
            }

        long driveTimeToNearBallMs = (long) (distanceToNearBall / RobotActivity.DEFAULT_SPEED_FT_PER_SEC * 1000);

        //driveTimeToNearBallMs = 3000;  //TODO: Remove this hack when really driving.

        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendWheelSpeed(0, 0);
                removeBallAtLocation(mGolfBallDeliveryActivity.mNearBallLocation);
            }
        }, driveTimeToNearBallMs);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mGolfBallDeliveryActivity.mState == GolfBallDeliveryActivity.State.NEAR_BALL_SCRIPT) {
                    mGolfBallDeliveryActivity.setState(GolfBallDeliveryActivity.State.DRIVE_TOWARDS_FAR_BALL);
                }
            }
        }, driveTimeToNearBallMs + ARM_REMOVAL_TIME_MS);

    }

    public void farBallScript() {
        mGolfBallDeliveryActivity.sendWheelSpeed(0, 0);
        removeBallAtLocation(mGolfBallDeliveryActivity.mFarBallLocation);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mGolfBallDeliveryActivity.mWhiteBallLocation != 0) {
                    removeBallAtLocation(mGolfBallDeliveryActivity.mWhiteBallLocation);
                } else {
                    // Do nothing
                }
                if (mGolfBallDeliveryActivity.mState == GolfBallDeliveryActivity.State.FAR_BALL_SCRIPT) {
                    mGolfBallDeliveryActivity.setState(GolfBallDeliveryActivity.State.DRIVE_TOWARDS_HOME);
                }
            }
        }, ARM_REMOVAL_TIME_MS);
    }

    private void removeBallAtLocation(final int location) {
        // Remove ball from wild thumper at location passed in
        mGolfBallDeliveryActivity.sendCommand("ATTACH 111111");
        mGolfBallDeliveryActivity.sendCommand("POSITION 0 90 0 -90 90"); // Stand position
        mGolfBallDeliveryActivity.sendCommand("GRIPPER 50");

        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (location) {
                    case 1: // Grab ball at location 1
                        mGolfBallDeliveryActivity.sendCommand(positionStand1);
                        break;
                    case 2: // Grab ball at location 2
                        mGolfBallDeliveryActivity.sendCommand(positionStand2);
                        break;
                    case 3: // Grab ball at location 3
                        mGolfBallDeliveryActivity.sendCommand(positionStand3);
                        break;
                }
            }
        }, 1000);

        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand("GRIPPER 10");
            }
        }, 2000);

        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (location == 1) {
                    mGolfBallDeliveryActivity.sendCommand("POSITION 90 114 31 0 160"); // Drop off to side
                } else if (location == 2) {
                    mGolfBallDeliveryActivity.sendCommand("POSITION 10 55 75 0 0"); //  0 75 65 0 0 // Grab position 0 115 25 0 0
                } else if (location == 3) {
                    mGolfBallDeliveryActivity.sendCommand("POSITION -90 120 31 0 160"); // Drop off to side
                }
            }
        }, 3000);

        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand("GRIPPER 50");
            }
        }, 4000);
    }

}
