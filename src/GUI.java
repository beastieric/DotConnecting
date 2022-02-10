import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class GUI implements ActionListener{
	protected JTextField fname, pT, mTD, mPT;
	protected JLabel errors;
	public void run() throws IOException {
		JFrame frame = new JFrame();
		frame.setBounds(10, 10, 800, 300);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		
		JPanel options = new JPanel();
		options.setLayout(new GridLayout(5, 2));
		options.add(new JLabel("Options:"));
		options.add(new JLabel(""));
		options.add(new JLabel("Input File Name: (w/o .csv)"));
		fname = new JTextField(20);
		options.add(fname);
		options.add(new JLabel("Point tolerance: (in.)"));
		pT = new JTextField(20);
		options.add(pT);
		options.add(new JLabel("Max total deflection: (% grade)"));
		mTD = new JTextField(20);
		options.add(mTD);
		options.add(new JLabel("Max point deflection: (% grade)"));
		mPT = new JTextField(20);
		options.add(mPT);
		
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
			Main m = new Main(fname.getText(), Double.parseDouble(pT.getText()), Double.parseDouble(mTD.getText())/100, Double.parseDouble(mPT.getText()));
			m.run();
			errors.setText("Computed successfully");
		}catch (Exception exc) {
			errors.setText(exc.getMessage());
		}
		
	}
	
}
