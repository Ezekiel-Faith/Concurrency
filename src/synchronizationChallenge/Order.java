package synchronizationChallenge;

import java.time.LocalDateTime;
import java.util.UUID;

public record Order(String orderID, String shoeType, int quantityOrdered, LocalDateTime orderTime) {
	public Order(String shoeType, int quantityOrdered) {
		this(UUID.randomUUID().toString(), shoeType, quantityOrdered, LocalDateTime.now());
	}

	public Order {
		if(quantityOrdered < 0) {
			throw new IllegalArgumentException("Quantity must be greater than zero");
		}
	}

	public long estimatedProcessingTimeInMins() {
		return quantityOrdered * 2L;
	}

	@Override
	public String toString() {
		return String.format("OrderID: %s | ShoeType: %s | Quantity: %d | OrderTime: %s",
				orderID, shoeType, quantityOrdered, orderTime);
	}
}
