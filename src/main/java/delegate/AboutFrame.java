package delegate;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class AboutFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String text = "MozillaRecovery" + "\n\nVersion: 0.4"
			+ "\n\nAuthor: Katja Hahn"
			+ "\n\nLast upate: 26. December 2012";

	public AboutFrame() {
		initGUI();
	}

	private void initGUI() {
		this.setSize(200, 170);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setText(text);
		area.setEditable(false);
		this.add(area);
	}
}
