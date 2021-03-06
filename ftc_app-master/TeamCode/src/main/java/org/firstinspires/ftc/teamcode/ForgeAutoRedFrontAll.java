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

@Autonomous(name="Forge Red Front:All", group="Auto")

public class ForgeAutoRedFrontAll extends LinearOpMode {

    /* Declare OpMode members. */
    ForgeHW         robot   = new ForgeHW();   // Use a Pushbot's hardware
    private ElapsedTime     runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // eg: TETRIX Motor Encoder 1440
    static final double     DRIVE_GEAR_REDUCTION    = .66 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.8;
    static final double     TURN_SPEED              = 0.5;
    ColorSensor sensorColor;

    // Vulforia Variables
    public static final String TAG = "Vuforia VuMark Sample";
    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia;
    RelicRecoveryVuMark vuMark;


    @Override
    public void runOpMode() {

        robot.init(hardwareMap);
        robot.swivelArm.setPosition(.83);
        robot.jewelSplit.setPosition(.95);
        robot.phoneSpin.setPosition(.40);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        robot.phoneSpin.setPosition(.73);


        //ADD VUFORIA READING HERE!
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


        relicTrackables.activate();   // Track VuMarks

        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 5.0)) {
            // ********************* VUMARK SCAN ****************************

            vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                telemetry.addData("VuMark", "%s visible", vuMark);
                telemetry.update();
                sleep(2000);  // REMOVBE

                break;
            } else {
                telemetry.addData("VuMark", "not visible");
                telemetry.update();
                sleep(2000);  // REMOVBE
            }


        }
        relicTrackables.deactivate();

        // Use ViewMark color to determine where to place Glyph
        // ********************* VUMARK SCAN ****************************


        //--END VUFORIA CODE--
        robot.phoneSpin.setPosition(.40);

          //Put arm down
        robot.jewelSplit.setPosition(.10);

        //color sensor--read jewels
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

        //Move swivel right/left
        if (sensorColor.red() > sensorColor.blue()) {
            //encoderDrive(DRIVE_SPEED,  -1.5,  -1.5, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            robot.swivelArm.setPosition(.60);
            sleep(500);
            robot.swivelArm.setPosition(.77);
            sleep(1000);
            robot.swivelArm.setPosition(.80);
            robot.jewelSplit.setPosition(.83);
            sleep(500);
            robot.jewelSplit.setPosition(.93);

        } else {
            //encoderDrive(DRIVE_SPEED,  1.5   ,  1.5, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            robot.swivelArm.setPosition(1);
            sleep(500);
            robot.swivelArm.setPosition(.83);
            sleep(1000);
            robot.swivelArm.setPosition(.80);
            robot.jewelSplit.setPosition(.83);
            sleep(500);
            robot.jewelSplit.setPosition(.93);

        }
        sleep(1500); // Wait for platform to stop shaking

        // *** JEWEL PROCESS COMPLETE

        robot.frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("Current POS",  "Running at %7d :%7d :%7d :%7d",
                robot.frontLeftDrive.getCurrentPosition(),
                robot.frontRightDrive.getCurrentPosition(),
                robot.backLeftDrive.getCurrentPosition(),
                robot.backRightDrive.getCurrentPosition()
        );
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();


        sleep(1000);  // Pause to allow for viewing

        telemetry.addData("Current POS",  "Running at %7d :%7d :%7d :%7d",
                robot.frontLeftDrive.getCurrentPosition(),
                robot.frontRightDrive.getCurrentPosition(),
                robot.backLeftDrive.getCurrentPosition(),
                robot.backRightDrive.getCurrentPosition()
        );
        telemetry.update();


        robot.frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // Drive Forward to spot
        encoderDrive(.2, 18, 18, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        sleep(1000);

        // Based on View deliver Gliph
        if (vuMark==RelicRecoveryVuMark.UNKNOWN ||vuMark==RelicRecoveryVuMark.CENTER) {
            // Drive off platform
            encoderDrive(.2, 3, 3, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            encoderDrive(.4, -17, 17 , 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            encoderDrive(.2, 11, 11, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            sleep(1000);
            // Sorta Center = 18.5,17 @ .2
            // ??? 26/19
        }

       if (vuMark==RelicRecoveryVuMark.LEFT) {
           encoderDrive(.2, 2, 2, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout

           encoderDrive(.4, -14, 14 , 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
           encoderDrive(.2, 10, 10, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout

           sleep(1000);

       }


        if (vuMark==RelicRecoveryVuMark.RIGHT) {
            // Drive off platform
            encoderDrive(.4, -17.5, 17.5 , 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            encoderDrive(.2, 8, 8, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
            sleep(1000);

        }

        telemetry.addData("Current POS",  "Running at %7d :%7d :%7d :%7d",
                robot.frontLeftDrive.getCurrentPosition(),
                robot.frontRightDrive.getCurrentPosition(),
                robot.backLeftDrive.getCurrentPosition(),
                robot.backRightDrive.getCurrentPosition()
        );
        telemetry.update();



        //release glyph
        robot.glyphIntakeLeft.setPower(-30);
        robot.glyphIntakeRight.setPower(-30);

        sleep(1500);

        robot.glyphIntakeLeft.setPower(0); // Stop
        robot.glyphIntakeRight.setPower(0); //Stop


        encoderDrive(.2, -.75, .75, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        sleep(1000);

        encoderDrive(.2, 1.0, 1.0, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        sleep(1000);

        encoderDrive(.2, -1.2, -1.2, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        sleep(1000);


        // Drive with Encoder
        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        //encoderDrive(DRIVE_SPEED,  48,  48, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        //encoderDrive(TURN_SPEED,   12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        //encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout





    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftFrontTarget;
        int newLeftBackTarget;
        int newRightFrontTarget;
        int newRightBackTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newRightFrontTarget = robot.frontRightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newLeftFrontTarget = robot.frontLeftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightBackTarget = robot.backRightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newLeftBackTarget = robot.backLeftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);

            robot.frontLeftDrive.setTargetPosition(newLeftFrontTarget);
            robot.frontRightDrive.setTargetPosition(newRightFrontTarget);
            robot.backLeftDrive.setTargetPosition(newLeftBackTarget);
            robot.backRightDrive.setTargetPosition(newRightBackTarget);

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
                telemetry.addData("Path1",  "Running to %7d :%7d:%7d :%7d", newLeftFrontTarget,  newRightFrontTarget,newLeftBackTarget,newRightBackTarget);
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

    enum Direction
    {
        LEFT,RIGHT;
    }
    public void encoderDriveStrafe(double speed, Direction direction, double inches, double timeoutS)
    {
        int newLeftFrontTarget =0;
        int newRightFrontTarget=0;
        int newLeftBackTarget =0;
        int newRightBackTarget=0;


        // Ensure that the opmode is still active
        if (opModeIsActive()) {


            if (direction == Direction.LEFT)
            {
                // Determine new target position, and pass to motor controller
                newRightFrontTarget = -(robot.frontRightDrive.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH));
                newLeftFrontTarget =  robot.frontLeftDrive.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
                newRightBackTarget = robot.backRightDrive.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
                newLeftBackTarget = -(robot.backLeftDrive.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH));
            }
            if (direction == Direction.RIGHT)
            {
                // Determine new target position, and pass to motor controller
                newRightFrontTarget = robot.frontRightDrive.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
                newLeftFrontTarget =  -(robot.frontLeftDrive.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH));
                newRightBackTarget = -(robot.backRightDrive.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH));
                newLeftBackTarget = robot.backLeftDrive.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
            }

            robot.frontLeftDrive.setTargetPosition(newLeftFrontTarget);
            robot.frontRightDrive.setTargetPosition(newRightFrontTarget);
            robot.backLeftDrive.setTargetPosition(newLeftBackTarget);
            robot.backRightDrive.setTargetPosition(newRightBackTarget);

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
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftFrontTarget, newRightFrontTarget);
                telemetry.addData("Path2", "Running at %7d :%7d :%7d :%7d",
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
        robot.frontRightDrive.setPower(.30);
        robot.frontLeftDrive.setPower(-.60);
        robot.backRightDrive.setPower(-.60);
        robot.backLeftDrive.setPower(.30);
    }


    private void strafeLeft ()
    {
        robot.frontRightDrive.setPower(-.30);
        robot.frontLeftDrive.setPower(.60);
        robot.backRightDrive.setPower(.60);
        robot.backLeftDrive.setPower(-.30);
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
