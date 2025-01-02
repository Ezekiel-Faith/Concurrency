package threadChallenge;

public class Main {
	public static void main(String[] args) {
		CustomThread customThread = new CustomThread();

		Thread runnableThread = new Thread(() -> {
			try {
				for (int i = 0; i < 5; i++) {
					System.out.println("Odd Thread: " + (i * 2 + 1));
					Thread.sleep(300);
				}
			} catch (InterruptedException e) {
				System.out.println(customThread.getName() + " was interrupted " + e.getMessage());
			}
		});

		customThread.start();
		runnableThread.setName("Odd Thread");
		runnableThread.start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		customThread.interrupt();
	}
}
