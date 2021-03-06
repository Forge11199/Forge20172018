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

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

/**
 * This file provides basic Telop driving for a Pushbot robot.
 * The code is structured as an Iterative OpMode
 *
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 *
 * This particular OpMode executes a basic Tank Drive Teleop for a PushBot
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Forge: Teleop", group="Forge")
public class ForgeTeleOp extends OpMode{

    /* Declare OpMode members. */
    ForgeHW robot       = new ForgeHW(); // use the class created to define a Pushbot's hardware


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */


        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Robot Init Complete");    //
        telemetry.update();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start()
    {
        robot.relicArm.setPosition(.10);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        double left;
        double right;
        left = 0;
        right =0;

        // Face Camera Forward before Jewel
        robot.phoneSpin.setPosition(.35);

        // Run wheels in tank mode (note: The joystick goes negative when pushed forwards, so negate it)
        left = -gamepad1.left_stick_y;
        right = -gamepad1.right_stick_y;


        robot.frontRightDrive.setPower(left);
        robot.frontLeftDrive.setPower(right);
        robot.backRightDrive.setPower(left);
        robot.backLeftDrive.setPower(right);

        // Send telemetry message to signify robot running;
        telemetry.addData("left", "%.2f", left);
        telemetry.addData("right", "%.2f", right);




        // Strafe Without Encoders - TEST
        if (gamepad1.dpad_right)    {       strafeLeft();      }
        if (gamepad1.dpad_left)     {       strafeRight();       }
        if (gamepad1.dpad_up)       {       strafeForward();    }
        if (gamepad1.dpad_down)     {       strafeBack();       }

        //close relic hand
        if(gamepad2.left_trigger > 0)
        {
            robot.relicHand.setPosition(0);
        }

        //open relic hand
        if(gamepad2.right_trigger > 0)
        {
            robot.relicHand.setPosition(.65);
        }

        // Control Relic Arm with sticks
        double lowerBound =.10;
        double upperBound =.9;
        double increment =.10;
        telemetry.addData("Trigger Right",gamepad1.right_trigger);
        telemetry.addData("Trigger Left",gamepad1.right_trigger);



        if (gamepad2.right_bumper)
        {
            double position;
            position = robot.relicArm.getPosition();
            telemetry.addData("Trigger Position",position);
            telemetry.addData("Lower ",lowerBound);
            telemetry.addData("greater",upperBound);


            if (position > lowerBound && position < upperBound)
            {
                position = position + increment;
                if (position > upperBound) // If over max, set to maximum
                    {
                        position = upperBound;
                    }

                robot.relicArm.setPosition(position);
                sleep(300);
            }
            position = 0;
        }


        if (gamepad2.left_bumper)
        {
            double position;
            position = robot.relicArm.getPosition();
            if (position > lowerBound   && position < upperBound)
            {
                position = position - increment;
                if (position < lowerBound) // If over max, set to maximum
                {position = lowerBound;}

                robot.relicArm.setPosition(position);
                sleep(300);
            }
            position = 0;

        }

        //Intake in
        if (gamepad2.y)
        {
            robot.glyphIntakeLeft.setPower(30);
            robot.glyphIntakeRight.setPower(30);
        }

        //intake spit
        if (gamepad2.a)
        {
            robot.glyphIntakeRight.setPower(-30);
            robot.glyphIntakeLeft.setPower(-30);
        }

        //Turn intake off
        if (gamepad2.x)
        {
            robot.glyphIntakeRight.setPower(0);
            robot.glyphIntakeLeft.setPower(0);

        }


        if (gamepad2.b)
        {
            robot.jewelSplit.setPosition(1);
        }



        if (gamepad2.dpad_up)
        {
            liftServoMax();
        }

        if (gamepad2.dpad_down)
        {
            liftServoDown();
        }
        if (gamepad2.dpad_right)
        {
            liftServoMiddle();
        }
        if (gamepad2.dpad_left)
        {
            liftServoGlyph1And2();
        }

        if (robot.glyphBump.getState() == true) {
            telemetry.addData("Digital Touch", "Is Not Pressed");

        } else {
            telemetry.addData("Digital Touch", "Is Pressed");
            //robot.glyphIntakeRight.setPower(0);
            //robot.glyphIntakeLeft.setPower(0);
        }

        telemetry.update();



    }



    private void strafeRight ()
    {
        robot.frontRightDrive.setPower(1.0);
        robot.frontLeftDrive.setPower(-1.0);
        robot.backRightDrive.setPower(-1.0);
        robot.backLeftDrive.setPower(1.0);
    }


    private void strafeLeft ()
    {
        robot.frontRightDrive.setPower(-1.0);
        robot.frontLeftDrive.setPower(1.0);
        robot.backRightDrive.setPower(1.0);
        robot.backLeftDrive.setPower(-1.0);
    }

    private void strafeForward ()
    {
        robot.frontRightDrive.setPower(.25);
        robot.frontLeftDrive.setPower(.25);
        robot.backRightDrive.setPower(.25);
        robot.backLeftDrive.setPower(.25);
    }

    private void strafeBack ()
    {
        robot.frontRightDrive.setPower(-.25);
        robot.frontLeftDrive.setPower(-.25);
        robot.backRightDrive.setPower(-.25);
        robot.backLeftDrive.setPower(-.25);
    }

    private void liftServoDown  ()
    {
        robot.liftLeft.setPosition(.68);
        robot.liftRight.setPosition(.71);
    }

    private void liftServoMax  ()
    {
        robot.liftLeft.setPosition(.08);
        robot.liftRight.setPosition(.10);
    }

    private void liftServoMiddle  ()
    {
        robot.liftLeft.setPosition(.25);
        robot.liftRight.setPosition(.32);
    }

    private void liftServoGlyph1And2  ()
    {
        robot.liftLeft.setPosition(.40);
        robot.liftRight.setPosition(.46);

    }




    @Override
    public void stop()
    {
        //robot.ggRight.setPosition(0);
        //robot.ggLeft.setPosition(1);
        //robot.gpServo.setPosition(.95);
        robot.frontRightDrive.setPower(0);
        robot.frontLeftDrive.setPower(0);
        robot.backRightDrive.setPower(0);
        robot.backLeftDrive.setPower(0);
        robot.glyphIntakeRight.setPower(0);
        robot.glyphIntakeLeft.setPower(0);
        robot.jewelSplit.setPosition(1);
        robot.liftLeft.setPosition(.68);   // Set to servo 1bottom,
        robot.liftRight.setPosition(.94);    //Set to servo 2 bottom
        robot.phoneSpin.setPosition(.35); // Face Forward for Teleop

        //PUT POSITION FOR LIFT HERE

    }

    /**
     * Sleeps for the given amount of milliseconds, or until the thread is interrupted. This is
     * simple shorthand for the operating-system-provided {@link Thread#sleep(long) sleep()} method.
     *
     * @param milliseconds amount of time to sleep, in milliseconds
     * @see Thread#sleep(long)
     */
    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /*
    private void openGlyphGrabber()

    {
        robot.ggRight.setPosition(1);
        robot.ggLeft.setPosition(0);

    }
    private void closeGlyphGrabber()
    {
        robot.ggRight.setPosition(0);
        robot.ggLeft.setPosition(1);

    }

    private void halfGlyphGrabber()
    {
        robot.ggRight.setPosition(.5);
        robot.ggLeft.setPosition(.5);

    }

    //Glyph pusher off position
    private void offGlyphPusher()
    {
        robot.gpServo.setPosition(.95);
    }



    private void partialGlyphPusher()
    {
        int sleepTime = 500;
        double startPos = .95;

        robot.gpServo.setPosition(startPos);
        sleep(sleepTime);
        robot.gpServo.setPosition(.80);
        sleep(sleepTime);
        robot.gpServo.setPosition(.70);
        sleep(sleepTime);
        robot.gpServo.setPosition(.70);
        sleep(sleepTime);
        robot.gpServo.setPosition(.70);
        sleep(sleepTime);
        robot.gpServo.setPosition(startPos);
    }

    //Full wobble for glyph pusher
    private void fullGlyphPusher()
    {
        int sleepTime = 500;
        double startPos = .95;

        robot.gpServo.setPosition(startPos);
        sleep(sleepTime);
        robot.gpServo.setPosition(.80);
        sleep(sleepTime);
        robot.gpServo.setPosition(startPos);
        sleep(sleepTime);
        robot.gpServo.setPosition(.70);
        sleep(sleepTime);
        robot.gpServo.setPosition(startPos);
        sleep(sleepTime);
        robot.gpServo.setPosition(.60);
        robot.gpServo.setPosition(startPos);
    }


//puts the glyph grabber in the start position; inside the robot
       /* if (gamepad2.right_bumper)
        {
            openGlyphGrabber();
        }
           */
    //opens the glyph grabber half way
        /* if (gamepad2.left_bumper)
        {
            halfGlyphGrabber();
        }2

        //closes it around the glyph
        if (gamepad2.x)
        {
            closeGlyphGrabber();
        }

        //off position/down position for glyph pusher
        if (gamepad2.dpad_left)
        {
            offGlyphPusher();
        }

        //1st level glyph pusher
        if (gamepad2.dpad_down)
        {
            partialGlyphPusher();
        }

        //2nd level glyph pusher
        if (gamepad2.dpad_up)
        {
            fullGlyphPusher();
        }

        //highest position for pushing glyphs into cryptobox
        if (gamepad2.dpad_right)
        {
            upGylphPusher ();
        }

    //Up glyph pusher to knock off glyph pit glyphs
    private void upGylphPusher()
    {
        robot.gpServo.setPosition(.30);
    }

    */

    /*
     * Code to run ONCE after the driver hits STOP
     */


}
