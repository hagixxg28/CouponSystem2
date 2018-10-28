package i.threads;

public class ThreadTest {
	public static void main(String[] args) {
		DailyCouponExpirationTask thread = new DailyCouponExpirationTask();
		Thread task = new Thread(thread);
		task.start();
		System.out.println("MAIN THREAD SLEEP");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("MAIN THREAD AWAKE, SHUTTING DOWN OTHER THREAD");
		thread.stop();
		task.interrupt();
	}
}
