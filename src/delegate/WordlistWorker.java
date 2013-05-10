package delegate;

import java.util.concurrent.ExecutionException;

import model.PasswordCracker;

import org.apache.log4j.Logger;

public class WordlistWorker extends CrackingTask {

	private final static Logger logger = Logger
			.getLogger(WordlistWorker.class);
	private final ProgressDisplay display;
	private final String key3Path;
	private final String wordlistPath;
	private PasswordCracker cracker;

	public WordlistWorker(ProgressDisplay display, String key3Path,
			String wordlistPath) {
		this.display = display;
		this.key3Path = key3Path;
		this.wordlistPath = wordlistPath;
	}

	@Override
	protected String doInBackground() throws Exception {
		cracker = new PasswordCracker(key3Path, display);
		return cracker.recoverByWordList(wordlistPath);
	}

	@Override
	protected void done() {
		try {
			display.setResult(get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			logger.warn(e.getMessage());
		}
	}

	@Override
	public void stop() {
		cracker.setRunning(false);
	}

}
