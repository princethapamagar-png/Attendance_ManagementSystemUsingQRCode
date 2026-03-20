package com.attendance.main;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javax.swing.JFrame;
import com.github.sarxos.webcam.WebcamPanel;

public class WebcamTest {
    public static void main(String[] args) {
        // 1. Get the default webcam
        Webcam webcam = Webcam.getDefault();

        if (webcam != null) {
            System.out.println("Webcam found: " + webcam.getName());
            
            // 2. Set the resolution
            webcam.setViewSize(WebcamResolution.VGA.getSize());

            // 3. Create a test window (using Swing for a quick test)
            WebcamPanel panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(true);
            panel.setDisplayDebugInfo(true);
            panel.setImageSizeDisplayed(true);
            panel.setMirrored(true);

            JFrame window = new JFrame("Webcam Test - Smart Attendance");
            window.add(panel);
            window.setResizable(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.pack();
            window.setVisible(true);
        } else {
            System.out.println("No webcam detected. Check your drivers!");
        }
    }
}