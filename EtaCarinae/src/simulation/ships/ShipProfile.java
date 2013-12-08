package simulation.ships;

public enum ShipProfile  {
	DEFAULT(50, 1000);
	
	private final int capacity;
	private final int price;
	
	ShipProfile(int capacity, int price) {
		this.capacity = capacity;
		this.price = price;
	}

	public int price() {
		return price;
	}

	public int capacity() {
		return capacity;
	}
}
