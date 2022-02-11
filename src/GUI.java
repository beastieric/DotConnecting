import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class GUI implements ActionListener{
	protected JTextField fname, pT, mTD, mPT, mDW;
	protected JLabel errors;
	public void run() throws IOException {
		JFrame frame = new JFrame();
		frame.setBounds(10, 10, 1000, 300);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		
		JPanel options = new JPanel();
		options.setLayout(new GridLayout(6, 2));
		options.add(new JLabel("Options:"));
		options.add(new JLabel(""));
		options.add(new JLabel("Input File Name: (w/o .csv)"));
		fname = new JTextField(20);
		options.add(fname);
		options.add(new JLabel("Point tolerance: (in.)"));
		pT = new JTextField(20);
		options.add(pT);
		options.add(new JLabel("Max overall slope: (% grade)"));
		mTD = new JTextField(20);
		options.add(mTD);
		options.add(new JLabel("Max point deflection: (% grade)"));
		mPT = new JTextField(20);
		options.add(mPT);
		options.add(new JLabel("Max deflection over a wing: (% grade)"));
		mDW = new JTextField(20);
		options.add(mDW);
		
		JPanel buttonText = new JPanel();
		buttonText.setLayout(new GridLayout(2, 1));
		JButton submit = new JButton("submit");
		submit.addActionListener(this);
		errors = new JLabel();
		buttonText.add(submit);
		buttonText.add(errors);
		
		panel.add(options);
		panel.add(buttonText);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Line Calculations");
		frame.setVisible(true);
	}

	public static void main(String[] args) throws IOException {
		GUI g = new GUI();
		g.run();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			//PB7_TOPO, 1, 3, 1
			Main m = new Main(fname.getText(), Double.parseDouble(pT.getText()),
					Double.parseDouble(mTD.getText())/100, Double.parseDouble(mPT.getText()), Double.parseDouble(mDW.getText()));
			m.run();
			if(m.failed.size() != 0) {
				String text = "<html>Could not find a line under the constraints<br>including row with motor pile: " + m.failed.get(0);
				
				errors.setText(text.substring(0,text.length()));
			}else {
				errors.setText("Computed successfully");
			}
			
		}catch (Exception exc) {
			errors.setText(exc.getMessage());
		}
		
	}
	
}
