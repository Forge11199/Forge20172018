package org.firstinspires.ftc.teamcode;

import android.hardware.Sensor;

import com.qualcomm.hardware.motors.RevRoboticsCoreHexMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by pedro_000 on 10/8/2017.
 */

public class TestHW {
   /*
        *
        * This class can be used to define all the specific hardware for a single robot.
        * In this case that robot is the FORGE TEST Bot
        *
        * This hardware class assumes the following device n

ames have been configured on the robot:
        * Note:  All names are lower case and some have single spaces between words.
        *
        * HARDWARE COMPONENT NAMES -- SHOULD match profile on phone
        * Positions are based on you facing the robot
        *
        * Motor :  Front Right Drive Motor:   Variable : frontRightDrive      "front_right_drive"
        * Motor :  Front Left Drive Motor:         "front_left_drive"
        * Motor :  Back Right Drive Motor:         "back_right_drive"
        * Motor :  Back Left Drive Motor:          "back_left_drive"
        *
        * Servo : Glyph Grabber Left:              "gg_Left"
        * Servo : Glphy Grabber Right:             "gg_Right"
        */

    /* Define variables for hardware components. */
    //public DcMotor relicLiftBack    = null;
    //public DcMotor relicLiftFront     = null;
    public Servo glyphLiftLeft              = null;
    public Servo glyphLiftRight             = null;
    public DcMotor glyphIntakeLeft          = null;
    public DcMotor glyphIntakeRight         = null;
    public DigitalChannel glyphBump         = null;
  //  public Servo relicHand                  = null;
    //public Servo relicElbow                 = null;
    //public Servo relicExtender              = null;


    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public TestHW(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;


        // Define and Initialize Motors
        //relicLiftFront  = hwMap.get(DcMotor.class, "relic_lift_front");
        //relicLiftBack= hwMap.get(DcMotor.class, "relic_lift_back");
        glyphLiftLeft= hwMap.get(Servo.class, "glyph_lift_left");
        glyphLiftRight= hwMap.get(Servo.class, "glyph_lift_right");
        glyphIntakeLeft= hwMap.get(DcMotor.class, "glyph_intake_left");
        glyphIntakeRight= hwMap.get(DcMotor.class, "glyph_intake_right");
        glyphBump = hwMap.get(DigitalChannel.class, "glyph_bump");
       /*
        relicElbow = hwMap.get(Servo.class, "relic_elbow");
        relicHand  = hwMap.get(Servo.class, "relic_hand");
        relicExtender = hwMap.get(Servo.class, "relic_extender");
*/
        // set the digital channel to input.
        glyphBump.setMode(DigitalChannel.Mode.INPUT);


        // Set all motors to zero power
        //relicLiftFront.setPower(0);
        //relicLiftBack.setPower(0);

        //***** NEED TO CHANGE TO ENCODER *******
        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        //frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //relicLiftBack.setMode(DcMotor.RunMode.RUN_WITH_ENCODER);
        //backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }


}

