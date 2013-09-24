package delegate;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Application;
import model.DefaultKey3Location;
import model.PasswordCracker;

import org.apache.log4j.Logger;

public class MainFrame extends JFrame implements ProgressDisplay {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger
			.getLogger(MainFrame.class);
	
	private final static File DEFAULT_WORDLIST = new File("wordlist.txt");
	private final static String TITLE_IMAGE = "/logohc.png";

	private final JTextField key3Path = new JTextField(25);
	private final JTextField wordlistPath = new JTextField(25);

	private final JButton key3PathButton = new JButton("...");
	private final JButton wordlistPathButton = new JButton("...");
	private final JButton recoverButton = new JButton("recover password");
	private final JButton cancel = new JButton("cancel");

	private final JLabel output = new JLabel(
			"If checkbox is set or no wordlist given, a bruteforce attack up to"
					+ " wordlength " + PasswordCracker.MAX_WORDLENGTH
					+ " will be tried.");

	private final JCheckBox bruteforce = new JCheckBox("generate words");

	private BufferedImage titleImg;
	private JLabel titleLabel;

	private CrackingTask task;

	public MainFrame() {
		super("MozillaRecovery 0.4.1");
		initGUI();
		initListener();
	}

	protected void enableButtons(boolean b) {
		recoverButton.setEnabled(b);
		key3PathButton.setEnabled(b);
		wordlistPathButton.setEnabled(b);
		cancel.setEnabled(!b);
	}

	private void recoverPasswords() {
		if (wordlistValueSet() && keyPathValueSet() && !bruteforce.isSelected()) {
			task = new WordlistWorker(this, key3Path.getText(),
					wordlistPath.getText());
			output.setText("Initializing wordlist attack. Parsing key3.db ...");
			task.execute();
			enableButtons(false);
		} else if (keyPathValueSet()) {
			task = new BruteforceWorker(this, key3Path.getText());
			output.setText("Initializing bruteforce attack. Parsing key3.db ...");
			task.execute();
			enableButtons(false);
		} else {
			output.setText("Please enter a valid path for key3.db");
		}
	}

	private boolean keyPathValueSet() {
		return key3Path.getText() != null && !key3Path.getText().equals("");
	}

	private boolean wordlistValueSet() {
		return wordlistPath.getText() != null
				&& !wordlistPath.getText().equals("");
	}

	private String getFileNameFromUser() {
		File userdir = new File(System.getProperty("user.dir"));
		JFileChooser fc = new JFileChooser(userdir);

		int state = fc.showOpenDialog(null);

		if (state == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file.getAbsolutePath();
		}
		return null;
	}

	private void initGUI() {
		setSize(650, 300);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		enableButtons(true);
		wordlistPath.setEditable(false);
		key3Path.setEditable(false);
		DefaultKey3Location loc = new DefaultKey3Location();
		String path = loc.findLocation(Application.FIREFOX);
		if (path == null) {
			path = loc.findLocation(Application.THUNDERBIRD);
		}
		key3Path.setText(path == null ? "" : path);

		if (DEFAULT_WORDLIST.exists()) {
			wordlistPath.setText(DEFAULT_WORDLIST.getAbsolutePath());
		}

		try {
			titleImg = ImageIO.read(getClass().getResource(TITLE_IMAGE));
			titleLabel = new JLabel(new ImageIcon(titleImg));
			this.add(titleLabel, BorderLayout.PAGE_START);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		JPanel panel = new JPanel();

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 10, 5);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		panel.add(new Label("key3.db:"), c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		panel.add(key3Path, c);

		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		panel.add(key3PathButton, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(new Label("wordlist:"), c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(wordlistPath, c);

		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(wordlistPathButton, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		panel.add(recoverButton, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		panel.add(bruteforce, c);

		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		panel.add(cancel, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		panel.add(output, c);

		this.add(panel, BorderLayout.CENTER);
	}

	private void initListener() {
		wordlistPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String path = getFileNameFromUser();
				if (path != null) {
					wordlistPath.setText(path);
				}
			}

		});

		key3PathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String path = getFileNameFromUser();
				if (path != null) {
					key3Path.setText(path);
				}
			}

		});

		recoverButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recoverPasswords();
			}
		});

		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				task.stop();
				enableButtons(true);
			}
		});

		titleLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				AboutFrame frame = new AboutFrame();
				frame.setLocationRelativeTo(MainFrame.this);
			}
		});
	}

	@Override
	public void setNewProgress(long progress) {
		output.setText("Passwords tried: " + progress);
	}

	@Override
	public void setResult(String password) {
		if (password == null) {
			output.setText("Could not find password. Passwords tried: "
					+ PasswordCracker.getWordsTried());
		} else {
			output.setText("Found password: " + password + " Passwords tried: "
					+ PasswordCracker.getWordsTried());
		}
		enableButtons(true);
	}
}
