package synchronizationChallenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoeWarehouse {
	public static final Map<String, Integer> productList = new HashMap<>();

	private final List<Order> orderList = new ArrayList<>();

	private static final int MAX_CAPACITY = 10;

	public synchronized void receiverOrder(Order order) {
		while (orderList.size() >= MAX_CAPACITY) {
			try {
				System.out.println("Warehouse is full, waiting for space...");
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		orderList.add(order);
		System.out.println("Order received: " + order);
		notifyAll();
	}

	public synchronized void fulfillOrder() {
		while (orderList.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		Order order = orderList.remove(0);
		String shoeType = order.shoeType();
		int qunatity = order.quantityOrdered();

		synchronized (productList) {
			int stock = productList.getOrDefault(shoeType, 0);
			if(stock >= qunatity) {
				productList.put(shoeType, stock - qunatity);
				System.out.printf("Order fulfilled: %s (Remaining Stock: %d)%n", order, productList.get(shoeType));
			} else {
				System.out.printf("Insufficient stock to fulfill order: %s%n", order);
			}
		}

		notifyAll();
	}

}
