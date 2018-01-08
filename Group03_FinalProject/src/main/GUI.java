package main;

import java.awt.FlowLayout;

//import org.apache.commons.lang3.StringUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Jakub and Tyler Simple GUI for Habitat application 10 text fields for
 *         user to input cities, 5 textfields for user to input weight factors
 */
public class GUI {

	private JLabel lbl;
	private JTextField cityInput1 = new JTextField(30);
	private JTextField cityInput2 = new JTextField(30);
	private JTextField cityInput3 = new JTextField(30);
	private JTextField cityInput4 = new JTextField(30);
	private JTextField cityInput5 = new JTextField(30);
	private JTextField cityInput6 = new JTextField(30);
	private JTextField cityInput7 = new JTextField(30);
	private JTextField cityInput8 = new JTextField(30);
	private JTextField cityInput9 = new JTextField(30);
	private JTextField cityInput10 = new JTextField(30);
	private JTextField tempInput = new JTextField(5);
	private JTextField precipInput = new JTextField(5);
	private JTextField crimeInput = new JTextField(5);
	private JTextField incomeInput = new JTextField(5);
	private JTextField popInput = new JTextField(5);
	private JButton btn;
	JTextField[] inputCities = { cityInput1, cityInput2, cityInput3, cityInput4, cityInput5, cityInput6, cityInput7,
			cityInput8, cityInput9, cityInput10 };
	JTextField[] inputWeights = { tempInput, precipInput, crimeInput, incomeInput, popInput };

	public void runGui() {
		//creating the frame with its fields
		JFrame frame = new JFrame("Habitat");
		frame.setSize(480, 600);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel t1 = new JLabel("Enter cities below (Canadian cities only)");
		JLabel t2 = new JLabel("Must be city name followed by a comma followed by the province initials");
		JLabel t3 = new JLabel("E.g. Toronto,ON or Winnipeg,MB\n");
		panel.add(t1);
		panel.add(t2);
		panel.add(t3);
		panel.add(cityInput1);
		panel.add(cityInput2);
		panel.add(cityInput3);
		panel.add(cityInput4);
		panel.add(cityInput5);
		panel.add(cityInput6);
		panel.add(cityInput7);
		panel.add(cityInput8);
		panel.add(cityInput9);
		panel.add(cityInput10);
		frame.add(panel);
		JLabel lbl1 = new JLabel("Enter numbers between 1 and 10 below (including 1 and 10)");
		JLabel lbl2 = new JLabel("Closer to the ends of the spectrum == Higher importance.");
		JLabel lbl3 = new JLabel("1 and 10 indicate the highest importance, 5 and 6 indicate the least importance");
		JLabel lbl4 = new JLabel("Number less than or equal to 5 means lower value preferred");
		JLabel lbl5 = new JLabel("Number greater than or equal to 6 means higher value preferred");
		JLabel lbl6 = new JLabel("Note: For Crime Rate the number is still between 1 and 10 however it is");
		JLabel lbl7 = new JLabel("assumed that a lower crime rate is preferred, thus a number closer to 1");
		JLabel lbl8 = new JLabel("indicates lower importance and closer to 10 indicates higher importance\n");
		JLabel tempLabel = new JLabel("Temperature : ");
		JLabel precipLabel = new JLabel("Precipitation : ");
		JLabel crimeLabel = new JLabel("Crime : ");
		JLabel incomeLabel = new JLabel("Income : ");
		JLabel popLabel = new JLabel("Population : ");
		panel.add(lbl1);
		panel.add(lbl2);
		panel.add(lbl3);
		panel.add(lbl4);
		panel.add(lbl5);
		panel.add(lbl6);
		panel.add(lbl7);
		panel.add(lbl8);

		panel.add(tempLabel);
		panel.add(tempInput);
		panel.add(precipLabel);
		panel.add(precipInput);
		panel.add(crimeLabel);
		panel.add(crimeInput);
		panel.add(incomeLabel);
		panel.add(incomeInput);
		panel.add(popLabel);
		panel.add(popInput);

		//button onpress
		btn = new JButton("DONE");
		panel.add(btn);
		final JFrame frameD = new JFrame("the display");
		btn.addActionListener(new ActionListener() {
			public void thisAction(ActionEvent e) {
				JDialog d = new JDialog(frameD, "Override-method", true);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame displayFrame = new JFrame("Ranked Cities:");
				displayFrame.setSize(600, 100);
				int i = 0;

				ArrayList<String> citiesChosent = new ArrayList<String>();
				for (JTextField s : inputCities) {
					if (s.getText().length() > 2) {
						citiesChosent.add(s.getText());
					}
				}

				final String[] citiesChosen = new String[citiesChosent.size()];
				for (String f : citiesChosent) {
					citiesChosen[i] = f;
					i++;
				}
				int p = 0;
				final Integer[] weightsChosenS = new Integer[5];
				for (JTextField t : inputWeights) {
					weightsChosenS[p] = Integer.parseInt(t.getText());
					p++;
				}
				JPanel pnl1 = new JPanel();
				//computation and output
				try {
					Rank.init(weightsChosenS, citiesChosen);
					LinkedHashMap<String, Double> rankedCities = Rank.getRankedCities();
					String output = rankedCities.toString();
					JLabel c = new JLabel(output);
					pnl1.add(c);
					displayFrame.add(pnl1);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				displayFrame.setVisible(true);
			}
		});
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		GUI g = new GUI();
		g.runGui();
	}
}
