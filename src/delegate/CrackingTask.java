package delegate;

import javax.swing.SwingWorker;

public abstract class CrackingTask extends SwingWorker<String, Void> {
	
	public abstract void stop();
	
}
