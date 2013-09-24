package delegate;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Starter {

	private final static Logger logger = Logger.getLogger(Starter.class);

	public static void main(String[] args) {
		PropertyConfigurator.configure(Starter.class.getResourceAsStream("/log4j.properties"));
		logger.debug("starting program");
		setLookAndFeel2();
		initMainFrame();
	}

	private static void initMainFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainFrame();
			}
		});
	}

	private static void setLookAndFeel() {
		UIManager.put("nimbusBase", new Color(0, 2, 5));
		UIManager.put("nimbusBlueGrey", Color.black);
		UIManager.put("control", Color.darkGray);
		UIManager.put("text", Color.white);
		UIManager.put("List.background", Color.black);
		UIManager.put("nimbusLightBackground", Color.black);

		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					System.err.println(e.getMessage());
				}
				break;
			}
		}
	}

	private static void setLookAndFeel2() {
		UIManager.put("nimbusBase", new Color(15, 0, 0));
		UIManager.put("nimbusBlueGrey", new Color(170, 0, 0));
		UIManager.put("control", Color.black);
		UIManager.put("text", Color.white);

		UIManager.put("nimbusSelectionBackground", Color.gray);
		UIManager.put("nimbusSelectedText", Color.white);
		UIManager.put("textHighlight", Color.lightGray);
		UIManager.put("nimbusFocus", new Color(170, 0, 0));
		UIManager.put("nimbusSelection", new Color(170, 0, 0));

		UIManager.put("textBackground", Color.black);
		UIManager.put("nimbusLightBackground", Color.black);

		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					System.err.println(e.getMessage());
				}
				break;
			}
		}
	}

}
