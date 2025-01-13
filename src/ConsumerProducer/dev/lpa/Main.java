package ConsumerProducer.dev.lpa;

class MessageRepository {
	private String message;
	private boolean hasMessage = false;

	public synchronized String read() {
		while (!hasMessage) {

		}
		hasMessage = false;
		return message;
	}

	public synchronized void wirte(String message) {
		while (hasMessage) {

		}
		hasMessage = true;
		this.message = message;
	}
}

class MessageWriter implements Runnable {
	private MessageRepository outgoingMessage;

	private final String text = """
			Humpty Dumpty sat on a wall,
			Humpty Dumpty had a great fall,
			All the king's horses and all the king's men,
			Couldn't put humpty together again.
			""";

	@Override
	public void run() {

	}
}

public class Main {
	public static void main(String[] args) {

	}
}
