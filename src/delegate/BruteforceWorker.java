package delegate;

import java.util.concurrent.ExecutionException;

import model.PasswordCracker;

import org.apache.log4j.Logger;

public class BruteforceWorker extends CrackingTask {

	private final ProgressDisplay display;
	private final String key3Path;
	private final PasswordCracker[] cracker = new PasswordCracker[PasswordCracker.MAX_WORDLENGTH];
	private final Thread[] threads = new Thread[PasswordCracker.MAX_WORDLENGTH];
	private String result;
	private final static Logger logger = Logger
			.getLogger(BruteforceWorker.class);

	public BruteforceWorker(ProgressDisplay display, String key3Path) {
		this.display = display;
		this.key3Path = key3Path;
	}

	@Override
	protected String doInBackground() throws Exception {
		for (int i = 1; i <= PasswordCracker.MAX_WORDLENGTH; i++) {
			final int index = i - 1;
			threads[index] = new Thread() {
				@Override
				public void run() {
					cracker[index] = new PasswordCracker(key3Path, display);
					String pass = cracker[index]
							.recoverByBruteforcing(index + 1);
					if (pass != null) {
						BruteforceWorker.this.result = pass;
						BruteforceWorker.this.stop();
					}
				}
			};
			threads[index].start();
		}
		for (int i = 0; i < PasswordCracker.MAX_WORDLENGTH; i++) {
			threads[i].join();
			logger.info("thread " + i + " joined");
		}
		return result;
	}

	@Override
	protected void done() {
		try {
			display.setResult(get());
		} catch (InterruptedException | ExecutionException e) {
			logger.warn(e.getMessage());
		}
	}

	@Override
	public void stop() {
		for (int i = 0; i < PasswordCracker.MAX_WORDLENGTH; i++) {
			cracker[i].setRunning(false);
		}
	}

}
