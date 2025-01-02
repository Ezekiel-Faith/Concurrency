package threadChallenge;

public class CustomThread extends Thread {
	@Override
	public void run() {
		try {
			for (int i = 1; i <= 5; i++) {
				this.setName("Even Thread");
				System.out.println(this.getName() + ": " +(i * 2));
				Thread.sleep(300);
			}
		} catch (InterruptedException e) {
			System.out.println(this.getName() + " was interrupted.");
		}
	}
}
