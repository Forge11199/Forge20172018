/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;


/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Forge Blue: Auto Full", group="Auto")

public class ForgeAutoBlueFull extends LinearOpMode {

    /* Declare OpMode members. */
    ForgeHW         robot   = new ForgeHW();   // Use a Pushbot's hardware
    private ElapsedTime     runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.8;
    static final double     TURN_SPEED              = 0.5;
    ColorSensor sensorColor;
    //DistanceSensor sensorDistance;

    // Vulforia Variables
    public static final String TAG = "Vuforia VuMark Sample";
    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia;
    RelicRecoveryVuMark vuMark;

    @Override
    public void runOpMode() {

        // KNO3 Transition
        AutoTransitioner.transitionOnStop(this, "Forge TeleOp");
        //

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // ******* VuMarks Setup ******************************
        // Vulforia Code
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        //FORGE 11199 KEY
        parameters.vuforiaLicenseKey = "Abalq3b/////AAAAGU+HLfwN7Ex0h9eBtGQ1tksoHCf8D3UiB+BOWeM8fTLhsr+a+ysYvifJvnfDyPrJuB8RcCXq2MtudAaviVZdV2nGw8Ny1QuA8k4RPCzqta5F/4jBapj/p0OURZ3Lue0DGQmUXGZDihyH9SetQEeHYRKV3U1U/PITxJnn6yOW3M6Q3AAZXFae893SR23xFq0fFD0FsiMp6DX7b54pMF2AZofVZ47wBzzF+HMy9FKaf3XRiLiW8RFzg/fIMCUq+79fyk9W3ekXDJGwzmFp8mdbCyjwto8mYhA4ZfKfdkK3wJcFurFiu4i8a902DCtW+V/G1z+G0I1X61jW4UX50D6JQYGLVVXYRd4Fxgm062NR6P2q";


        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        // ******* VuMarks Setup ************************

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();



        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                          robot.frontLeftDrive.getCurrentPosition(),
                          robot.frontRightDrive.getCurrentPosition(),
                          robot.backLeftDrive.getCurrentPosition(),
                          robot.backLeftDrive.getCurrentPosition());

        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        /*
        relicTrackables.activate();   // Track VuMarks

        while (opModeIsActive()) {


            // ********************* VUMARK SCAN ****************************
            vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                telemetry.addData("VuMark", "%s visible", vuMark);
            } else {
                telemetry.addData("VuMark", "not visible");
            }

            telemetry.update();

            // Use ViewMark color to determine where to place Glyph
            // ********************* VUMARK SCAN ****************************

        }
        */
        // Face Camera Forward before Jewel
        robot.phoneSpin.setPosition(.35);
        sleep(500);

        // Move Lift To Middle
        robot.liftLeft.setPosition(.44);
        robot.liftRight.setPosition(.21);
        sleep(1000);



        robot.jewelSplit.setPosition(.15);
        sleep(2000);    //put jewel splitter down

        sensorColor = hardwareMap.get(ColorSensor.class, "js_Color");


        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attentuate the measured values.
        final double SCALE_FACTOR = 255;
        Color.RGBToHSV((int) (sensorColor.red() * SCALE_FACTOR),
                (int) (sensorColor.green() * SCALE_FACTOR),
                (int) (sensorColor.blue() * SCALE_FACTOR),
                hsvValues);

        // send the info back to driver station using telemetry function.
        //  telemetry.addData("Distance (cm)",
        //          String.format(Locale.US, "%.02f", sensorDistance.getDistance(DistanceUnit.CM)));
        telemetry.addData("Alpha", sensorColor.alpha());
        telemetry.addData("Red  ", sensorColor.red());
        telemetry.addData("Green", sensorColor.green());
        telemetry.addData("Blue ", sensorColor.blue());
        telemetry.addData("Hue", hsvValues[0]);
        telemetry.update();
        sleep(1000);



        robot.frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // Use MAth Positive and Negative, Subtract into one number and determine
        // Blue Alliance
        boolean forward=true;
        if (sensorColor.red() > sensorColor.blue()) {
            encoderDrive(.4,  -1.5,  -1.5, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            forward=false;
            /*
            robot.frontRightDrive.setPower(-.25);
            robot.frontLeftDrive.setPower(-.25);
            robot.backRightDrive.setPower(-.25);
            robot.backLeftDrive.setPower(-.25);
            */

            sleep(500);
            robot.frontRightDrive.setPower(0);
            robot.frontLeftDrive.setPower(0);
            robot.backRightDrive.setPower(0);
            robot.backLeftDrive.setPower(0);
        } else {
            encoderDrive(.4,  1.5   ,  1.5, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            forward=true;
            /*
            robot.frontRightDrive.setPower(.25);
             */
            /*
            robot.frontLeftDrive.setPower(.25);
            robot.backRightDrive.setPower(.25);
            robot.backLeftDrive.setPower(.25);
            */
            sleep(500);
            robot.frontRightDrive.setPower(0);
            robot.frontLeftDrive.setPower(0);
            robot.backRightDrive.setPower(0);
            robot.backLeftDrive.setPower(0);
        }


        robot.jewelSplit.setPosition(1);
        sleep(2000);

        double extraDistance = 0; //22 ;// Extra distance to get starting point
        double distanceToTravelL = extraDistance + -4 ; // Total Distance to Sweet Spot
        double distanceToTravelR = extraDistance + -6.0   ; // Total Distance to Sweet Spot

        if (forward=false)
        {
            encoderDrive(.4, distanceToTravelL, distanceToTravelR, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        }

        else
        {
            encoderDrive(.4, distanceToTravelL +-5, distanceToTravelR + -5, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        }
        //strafeLeft();
        //sleep(100);

        //strafeForward();
        //sleep(100);

        liftServoDown();
        sleep(200);

        robot.giLeft.setPosition(.40); //robot.giLeft.setPosition(.75);
        robot.giRight.setPosition(.60);


        sleep(3000);

        // Drive with Encoder
        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        //encoderDrive(DRIVE_SPEED,  48,  48, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        //encoderDrive(TURN_SPEED,   12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        //encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout



        //telemetry.addData("Path", "Complete");
        //telemetry.update();

    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = robot.frontRightDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = robot.frontLeftDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newRightTarget = robot.backRightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newRightTarget = robot.backLeftDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);

            robot.frontLeftDrive.setTargetPosition(newLeftTarget);
            robot.frontRightDrive.setTargetPosition(newRightTarget);
            robot.backLeftDrive.setTargetPosition(newLeftTarget);
            robot.backRightDrive.setTargetPosition(newLeftTarget);

            // Turn On RUN_TO_POSITION
            robot.frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // *** ADD OTHER Motors

            // reset the timeout time and start motion.
            runtime.reset();
            robot.frontLeftDrive.setPower(Math.abs(speed));
            robot.frontRightDrive.setPower(Math.abs(speed));
            robot.backLeftDrive.setPower(Math.abs(speed));
            robot.backRightDrive.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                   (runtime.seconds() < timeoutS) &&
                   (robot.frontLeftDrive.isBusy() && robot.frontRightDrive.isBusy() & robot.backLeftDrive.isBusy() & robot.backRightDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d :%7d :%7d",
                                            robot.frontLeftDrive.getCurrentPosition(),
                                            robot.frontRightDrive.getCurrentPosition(),
                                            robot.backLeftDrive.getCurrentPosition(),
                                            robot.backRightDrive.getCurrentPosition()
                                                                                    );
                telemetry.update();
            }

            // Stop all motion;
            robot.frontLeftDrive.setPower(0);
            robot.frontRightDrive.setPower(0);
            robot.backLeftDrive.setPower(0);
            robot.backRightDrive.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


            //  sleep(250);   // optional pause after each move
        }
    }
    private void strafeRight ()
    {
        robot.frontRightDrive.setPower(.90);
        robot.frontLeftDrive.setPower(-.90);
        robot.backRightDrive.setPower(-.90);
        robot.backLeftDrive.setPower(.90);
    }


    private void strafeLeft ()
    {
        robot.frontRightDrive.setPower(-.90);
        robot.frontLeftDrive.setPower(.90);
        robot.backRightDrive.setPower(.90);
        robot.backLeftDrive.setPower(-.90);
    }

    private void strafeForward ()
    {
        robot.frontRightDrive.setPower(.25);
        robot.frontLeftDrive.setPower(.25);
        robot.backRightDrive.setPower(.25);
        robot.backLeftDrive.setPower(.25);
    }
    private void liftServoDown  ()
    {
        robot.liftLeft.setPosition(.31);
        robot.liftRight.setPosition(.34);
    }

    private void liftServoMax  ()
    {
        robot.liftLeft.setPosition(.50);
        robot.liftRight.setPosition(.16);
    }

    private void liftServoMiddle  ()
    {
        robot.liftLeft.setPosition(.44);
        robot.liftRight.setPosition(.21);
    }

    private void liftServoGlyph1And2  ()
    {
        robot.liftLeft.setPosition(.44);
        robot.liftRight.setPosition(.21);

    }


}