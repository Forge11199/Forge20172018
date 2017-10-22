package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by pedro_000 on 10/8/2017.
 */

public class ForgeHWTest {


   /*
        *
        * This class can be used to define all the specific hardware for a single robot.
        * In this case that robot is the FORGE TEST Bot
        *
        * This hardware class assumes the following device names have been configured on the robot:
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
    public DcMotor frontRightDrive   = null;
    public DcMotor frontLeftDrive = null;
    public DcMotor backRightDrive   = null;
    public DcMotor backLeftDrive = null;

   /* Glyph grabber mapping(gg) */
    public Servo ggRight    = null;
    public Servo ggLeft     = null;
    public Servo gpServo    = null;

    /* Jewel splitter mapping(jewelsplit)*/
    public Servo jewelSplit = null;

    /* Sensor for jewel split */
    public NormalizedColorSensor jewelColor = null;


    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public ForgeHWTest(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        frontRightDrive  = hwMap.get(DcMotor.class, "front_right_drive");
        frontLeftDrive = hwMap.get(DcMotor.class, "front_left_drive");
        backRightDrive= hwMap.get(DcMotor.class, "back_right_drive");
        backLeftDrive= hwMap.get(DcMotor.class, "back_left_drive");
        ggLeft= hwMap.get(Servo.class, "gg_Left");
        ggRight= hwMap.get(Servo.class, "gg_Right");
        gpServo= hwMap.get (Servo.class, "gp_Servo");
        jewelSplit = hwMap.get (Servo.class, "js_Servo");
        //jewelColor = hwMap.get(NormalizedColorSensor.class, "js_Color");

        frontRightDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        backRightDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors


        // Set all motors to zero power
        frontRightDrive.setPower(0);
        frontLeftDrive.setPower(0);
        backRightDrive.setPower(0);
        backLeftDrive.setPower(0);

        //Set all servo to zero and another unknown number for the moment
        ggLeft.setPosition(1);
        ggRight.setPosition(0);
        gpServo.setPosition(.95);
        jewelSplit.setPosition(0);

        //turn on light for color sensor
        if (jewelColor instanceof SwitchableLight) {
            ((SwitchableLight)jewelColor).enableLight(true);
        }



        //***** NEED TO CHANGE TO ENCODER *******
        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }


}

