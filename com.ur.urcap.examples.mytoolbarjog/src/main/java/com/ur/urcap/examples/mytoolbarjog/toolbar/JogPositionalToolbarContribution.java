/**
 * BSD 3-Clause License
 * 
 * Copyright (c) 2022, Universal Robots A/S
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ur.urcap.examples.mytoolbarjog.toolbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.ur.urcap.api.contribution.toolbar.ToolbarAPIProvider;
import com.ur.urcap.api.contribution.toolbar.ToolbarContext;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarContribution;
import com.ur.urcap.api.domain.userinteraction.inputvalidation.InputValidationFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.examples.mytoolbarjog.URCommunicator.ScriptCommand;
import com.ur.urcap.examples.mytoolbarjog.URCommunicator.ScriptSender;

public class JogPositionalToolbarContribution implements SwingToolbarContribution {

    private final ToolbarAPIProvider apiProvider;
    private final KeyboardInputFactory keyboardInputFactory;
    private JTextField stepSizeInput;
    private final InputValidationFactory validatorFactory;

    // GUI
    private JSlider stepSizeSlider;
    private final JLabel stepSizeLabel = new JLabel("Step size");
    private final JLabel stepSizeSliderLabel = new JLabel(" mm");;
    private JButton[] controlButtons = new JButton[6];

    // ScriptCommunicator
    private static ScriptSender sender = new ScriptSender();

    // Ranges for slider
    private final int MIN_SLIDER = 1;
    private final int MAX_SLIDER = 10000;
    private final int SLIDER_INCREMENT = 1;
    private final double MIN_MM = 0.1;
    private final double MAX_MM = 10.0;
  
    
    
    JogPositionalToolbarContribution(ToolbarContext context) {
        apiProvider = context.getAPIProvider();
        keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
        validatorFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getInputValidationFactory();
    }
   
    
    
    @Override
    public void buildUI(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
      
        
        /**
         * Slider group
         */
        JPanel sliderJPanel = new JPanel();
        sliderJPanel.setLayout(new BoxLayout(sliderJPanel, BoxLayout.X_AXIS));

        stepSizeInput = createStepSizeInputField();

        sliderJPanel.add(stepSizeLabel);
        sliderJPanel.add(createStepSizeSlider());
        sliderJPanel.add(stepSizeInput);
        sliderJPanel.add(stepSizeSliderLabel);

        panel.add(sliderJPanel);
        
        
        /**
         * Jog button group
         */

        JPanel buttonJPanel = new JPanel();
        GroupLayout layout = new GroupLayout(buttonJPanel);
        buttonJPanel.setLayout(layout);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        JLabel label_x = createBoldColoredLabel("X", Color.RED, 28);
        JLabel label_y = createBoldColoredLabel("Y", Color.GREEN, 28);
        JLabel label_z = createBoldColoredLabel("Z", Color.BLUE ,28);

        Component c_x = label_x;
        Component c_y = label_y;
        Component c_z = label_z;
        Component c_xm = createJogButton(controlButtons[0], getIcon("minus_icon"), 0, false);
        Component c_xp = createJogButton(controlButtons[1], getIcon("plus_icon"), 0, true);
        Component c_ym = createJogButton(controlButtons[2], getIcon("minus_icon"), 1, false);
        Component c_yp = createJogButton(controlButtons[3], getIcon("plus_icon"), 1, true);
        Component c_zm = createJogButton(controlButtons[4], getIcon("minus_icon"), 2, false);
        Component c_zp = createJogButton(controlButtons[5], getIcon("plus_icon"), 2, true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(c_x)
                        .addComponent(c_y)
                        .addComponent(c_z))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(c_xm)
                        .addComponent(c_ym)
                        .addComponent(c_zm))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(c_xp)
                        .addComponent(c_yp)
                        .addComponent(c_zp)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(c_x)
                        .addComponent(c_xm)
                        .addComponent(c_xp))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(c_y)
                        .addComponent(c_ym)
                        .addComponent(c_yp))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(c_z)
                        .addComponent(c_zm)
                        .addComponent(c_zp)));

        panel.add(buttonJPanel);
    }
  
    

    
    
    
    
    /**
     * Create input field for step size
     * 
     * @return JTextField
     */
    private JTextField createStepSizeInputField() {
        stepSizeInput = new JTextField();
        stepSizeInput.setPreferredSize(new Dimension(60, 30));
        stepSizeInput.setMaximumSize(stepSizeInput.getPreferredSize());
        stepSizeInput.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                KeyboardNumberInput<Double> keyboardInput = getInputForStepSize();
                keyboardInput.show(stepSizeInput, getCallbackForStepSize());
            }
        });
        return stepSizeInput;
    }

    /**
     * Convenience function for changing the color and boldness of a JLabel
     * 
     * @param text
     * @param color
     * @param FontSize 
     * @return JLabel colored and bold
     */
    private JLabel createBoldColoredLabel(String text, Color color, int size ) {
        JLabel tmpJLabel = new JLabel(text);
        tmpJLabel.setForeground(color);
        Font f = tmpJLabel.getFont();
        tmpJLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD ,size ));
        return tmpJLabel;
    }

    /**
     * Convenience function to create buttons for jogging the robot
     * 
     * @param button
     * @param icon
     * @param index
     * @param plus
     * @return JButton
     */
    private JButton createJogButton(JButton button, Icon icon, final Integer index, final Boolean plus) {
        button = new JButton(icon);
        button.setPreferredSize(new Dimension(68, 68));
        button.setMaximumSize(button.getPreferredSize());
        button.setMinimumSize(button.getPreferredSize());

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                double delta = sliderPositionToStepSize() / 1000.0; // [mm] to [m]
                double[] pose_delta = new double[6];

                if (plus) {
                    pose_delta[index] = delta;
                } else {
                    pose_delta[index] = -1 * delta;
                }
                ScriptCommand cmd = new ScriptCommand("urcap_jog_positional");
                cmd.appendLine(
                        "movel(pose_trans(get_actual_tcp_pose(), p" + Arrays.toString(pose_delta) +
                                "))");
                sender.executeURScript(cmd);
            }

        });

        return button;
    }

    /**
     * Function to create the step size slider and its change listeners
     * 
     * @return JSlider object
     */
    private JSlider createStepSizeSlider() {
        stepSizeSlider = new JSlider(JSlider.HORIZONTAL, MIN_SLIDER, MAX_SLIDER, SLIDER_INCREMENT);
        setStepSize(sliderPositionToStepSize());

        stepSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setStepSize(sliderPositionToStepSize());
            }
        });

        return stepSizeSlider;
    }

    /**
     * Converts an integer value from a linear range of integers to an exponential
     * range of doubles
     * 
     * @param value
     * @param linear_min
     * @param linear_max
     * @param exp_min
     * @param exp_max
     * @return
     */
    private double scaleExponentialRange(int value, int linear_min, int linear_max, double exp_min,
            double exp_max) {
        // Output scaling where output_min and max are the values that the result are
        // scaled to lie within
        double minv = Math.log(exp_min);
        double maxv = Math.log(exp_max);

        // calculate adjustment factor
        double scale = (maxv - minv) / (linear_max - linear_min);
        return Math.exp(minv + scale * (value - linear_min));
    }

    /**
     * Computes the inverse of scaleExponentialRange
     * 
     * @param step_size
     * @param slider_min
     * @param slider_max
     * @param output_min
     * @param output_max
     * @return
     */
    private int inverseScaleExponentialRange(double step_size, int slider_min, int slider_max, double output_min,
            double output_max) {

        double minv = Math.log(output_min);
        double maxv = Math.log(output_max);

        double scale = (maxv - minv) / (slider_max - slider_min);
        return (int) ((Math.log(step_size) - minv) / scale + slider_min);
    }

    /**
     * Sets the step size directly in the JTextField and formats it to two decimals
     * 
     * @param step_size
     */
    public final void setStepSize(double step_size) {
        stepSizeInput.setText(String.format("%.2f", step_size));
    }

    /**
     * Convenience function for converting the linear discrete slider position into
     * a step size on an exponential scale s.t. lower slider positions has high
     * precision and higher positions has low precision.
     * 
     * @return step size [double]
     */
    private double sliderPositionToStepSize() {
        return scaleExponentialRange(stepSizeSlider.getValue(),
                stepSizeSlider.getMinimum(),
                stepSizeSlider.getMaximum(),
                MIN_MM,
                MAX_MM);
    }

    /**
     * Convenience function for converting step size [mm] to discrete slider
     * position [int]
     * 
     * @param step_size [double]
     * @return slider position [int]
     */
    private final int stepSizeToSliderPosition(double step_size) {
        return inverseScaleExponentialRange(step_size,
                stepSizeSlider.getMinimum(),
                stepSizeSlider.getMaximum(),
                MIN_MM,
                MAX_MM);
    }

    @Override
    public void openView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeView() {
        // TODO Auto-generated method stub

    }

    public KeyboardNumberInput<Double> getInputForStepSize() {
        KeyboardNumberInput<Double> keyboardInput = keyboardInputFactory.createPositiveDoubleKeypadInput();
        keyboardInput.setInitialValue(sliderPositionToStepSize());
        keyboardInput.setErrorValidator(validatorFactory.createDoubleRangeValidator(MIN_MM, MAX_MM));
        return keyboardInput;
    }

    public KeyboardInputCallback<Double> getCallbackForStepSize() {
        return new KeyboardInputCallback<Double>() {
            @Override
            public void onOk(Double step_size) {
                setStepSize(step_size);
                // Update slider position
                stepSizeSlider.setValue(stepSizeToSliderPosition(step_size));
            }
        };
    }

    /**
     * Reads in the specified icon file from the icons folder and rescales it to
     * 25x25px
     * 
     * @param icon - String name of icon file
     * @return icon in ImageIcon class format
     */
    public Icon getIcon(String icon) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource("/icons/" + icon + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageIcon(image.getScaledInstance(25, -1, Image.SCALE_SMOOTH));
    }

}
