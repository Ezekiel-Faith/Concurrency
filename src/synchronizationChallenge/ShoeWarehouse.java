package synchronizationChallenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ShoeWarehouse {
	public static final Map<String, Integer> productList = new HashMap<>();

	private final List<Order> orderList = new ArrayList<>();

	private static final int MAX_CAPACITY = 10;

	// Executors
	private final ExecutorService singleExecutor = Executors.newSingleThreadExecutor(); // For order fulfillment
	private final ExecutorService fixedExecutor = Executors.newFixedThreadPool(3); // For receiving orders

	public void receiverOrder(Order order) {
		fixedExecutor.execute(() -> {
			if (orderList.size() < MAX_CAPACITY) {
				orderList.add(order);
				System.out.println("Order received: " + order);
			} else {
				System.out.println("Warehouse is full, discarding order: " + order);
			}
		});
	}


	public void fulfillOrder() {
		singleExecutor.execute(() -> {
			if (!orderList.isEmpty()) {
				Order order = orderList.removeFirst();
				String shoeType = order.shoeType();
				int quantity = order.quantityOrdered();

				synchronized (productList) {
					int stock = productList.getOrDefault(shoeType, 0);
					if (stock >= quantity) {
						productList.put(shoeType, stock - quantity);
						System.out.printf("Order fulfilled: %s (Remaining Stock: %d)%n", order, productList.get(shoeType));
					} else {
						System.out.printf("Insufficient stock to fulfill order: %s%n", order);
					}
				}
			}
		});
	}

	// Shutdown method to terminate the executors
	public void shutdown() {
		fixedExecutor.shutdown();
		singleExecutor.shutdown();
	}

//	public synchronized void receiverOrder(Order order) {
//		while (orderList.size() >= MAX_CAPACITY) {
//			try {
//				System.out.println("Warehouse is full, waiting for space...");
//				wait();
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}
//		}
//
//		orderList.add(order);
//		System.out.println("Order received: " + order);
//		notifyAll();
//	}

//	public synchronized void fulfillOrder() {
//		while (orderList.isEmpty()) {
//			try {
//				wait();
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}
//		}
//
//		Order order = orderList.remove(0);
//		String shoeType = order.shoeType();
//		int quantity = order.quantityOrdered();
//
//		synchronized (productList) {
//			int stock = productList.getOrDefault(shoeType, 0);
//			if(stock >= quantity) {
//				productList.put(shoeType, stock - quantity);
//				System.out.printf("Order fulfilled: %s (Remaining Stock: %d)%n", order, productList.get(shoeType));
//			} else {
//				System.out.printf("Insufficient stock to fulfill order: %s%n", order);
//			}
//		}
//
//		notifyAll();
}

