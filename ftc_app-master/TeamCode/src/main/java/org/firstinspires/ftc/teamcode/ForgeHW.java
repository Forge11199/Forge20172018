package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by pedro_000 on 10/8/2017.
 */

public class ForgeHW {
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
    public DcMotor frontRightDrive   = null;
    public DcMotor frontLeftDrive    = null;
    public DcMotor backRightDrive    = null;
    public DcMotor backLeftDrive     = null;

    /* Jewel splitter mapping(jewelsplit)*/
    public Servo jewelSplit = null;

    /* Glyph intake mapping */
    public Servo giLeft     = null;
    public Servo giRight    = null;
    public Servo liftLeft  = null;
    public Servo liftRight = null;
    public Servo phoneSpin = null;

    /* Sensor for jewel split */
    public NormalizedColorSensor jewelColor = null;


    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public ForgeHW(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;


        // *************** MOTORS ********************
        // Define and Initialize Motors
        frontRightDrive  = hwMap.get(DcMotor.class, "front_right_drive");
        frontLeftDrive = hwMap.get(DcMotor.class, "front_left_drive");
        backRightDrive= hwMap.get(DcMotor.class, "back_right_drive");
        backLeftDrive= hwMap.get(DcMotor.class, "back_left_drive");

        // Set Braking Mode
        frontRightDrive.setZeroPowerBehavior((DcMotor.ZeroPowerBehavior.BRAKE));
        frontLeftDrive.setZeroPowerBehavior((DcMotor.ZeroPowerBehavior.BRAKE));
        backRightDrive.setZeroPowerBehavior((DcMotor.ZeroPowerBehavior.BRAKE));
        backLeftDrive.setZeroPowerBehavior((DcMotor.ZeroPowerBehavior.BRAKE));

        frontRightDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        backRightDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors

        // Set all motors to zero power
        frontRightDrive.setPower(0);
        frontLeftDrive.setPower(0);
        backRightDrive.setPower(0);
        backLeftDrive.setPower(0);

                //***** NEED TO CHANGE TO ENCODER *******
        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // *************** MOTORS ********************


        jewelSplit = hwMap.get (Servo.class, "js_Servo");
        giLeft = hwMap.get (Servo.class, "gi_Left");
        giRight = hwMap.get (Servo.class, "gi_Right");
        liftLeft = hwMap.get (Servo.class, "lift_Left");
        liftRight = hwMap.get  (Servo.class, "lift_Right");
        phoneSpin = hwMap.get   (Servo.class,"phone_Spin");
        //jewelColor = hwMap.get(NormalizedColorSensor.class, "js_Color");


        //Set all servo to zero and another unknown number for the moment
        //jewelSplit.setPosition(1);
        giRight.setPosition(.51);   // Stop Right
        giLeft.setPosition(.51);    // Stop Left

        liftLeft.setPosition(.31);   // Set to servo 1bottom,
        liftRight.setPosition(.34);    //Set to servo 2 bottom
        jewelSplit.setPosition(1);

        phoneSpin.setPosition(.80);     //spins phone to left position


        //turn on light for color sensor
        if (jewelColor instanceof SwitchableLight) {
            ((SwitchableLight)jewelColor).enableLight(true);
        }




    }


}

