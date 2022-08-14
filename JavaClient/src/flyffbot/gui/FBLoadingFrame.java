package flyffbot.gui;

import lombok.val;

import javax.swing.*;
import java.awt.*;

public class FBLoadingFrame extends JFrame {
    public FBLoadingFrame(){
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        val wrapper = new JPanel();
        wrapper.add(new JLabel("Loading configurations..."));
        add(wrapper, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize((GuiConstants.padding * 5) + (GuiConstants.rowWidth + GuiConstants.padding), GuiConstants.frameHeight);
        setResizable(false);
        setVisible(true);
    }
}
