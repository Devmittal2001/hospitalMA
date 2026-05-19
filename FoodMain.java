package fooddelivery;

import java.util.*;
import java.util.stream.Collectors;

enum OrderStatus {
    PLACED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELED
}

class Order {
    int orderId;
    int restaurantId;
    int customerId;
    double orderValue;
    double distanceKm;
    OrderStatus status;

    Order(int orderId, int restaurantId, int customerId, double orderValue, double distanceKm, OrderStatus status) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.orderValue = orderValue;
        this.distanceKm = distanceKm;
        this.status = status;
    }
}

class OrderStats {
    int totalOrders;
    int activeOrders;
    int closedOrders;

    OrderStats(int totalOrders, int activeOrders, int closedOrders) {
        this.totalOrders = totalOrders;
        this.activeOrders = activeOrders;
        this.closedOrders = closedOrders;
    }
}

class OrderManager {
    List<Order> orders;

    OrderManager() {
        this.orders = new ArrayList<>();
    }

    void addOrder(Order order) {
        if (order != null) {
            orders.add(order);
        }
    }

    void updateOrderStatus(int orderId, OrderStatus newStatus) {
        for (Order o : orders) {
            if (o.orderId == orderId) {
                o.status = newStatus;
                return;
            }
        }
    }

    OrderStats getOrderStatistics() {
        int total = orders.size();
        int active = 0;
        int closed = 0;

        for (Order o : orders) {
            if (o.status == OrderStatus.PLACED
                    || o.status == OrderStatus.PREPARING
                    || o.status == OrderStatus.OUT_FOR_DELIVERY) {
                active++;
            } else if (o.status == OrderStatus.DELIVERED
                    || o.status == OrderStatus.CANCELED) {
                closed++;
            }
        }

        return new OrderStats(total, active, closed);
    }

    Map<Integer, Double> getRevenuePerRestaurant() {
        Map<Integer, Double> revenue = new HashMap<>();

        for (Order o : orders) {
            if (o.status == OrderStatus.DELIVERED) {
                revenue.put(o.restaurantId,
                        revenue.getOrDefault(o.restaurantId, 0.0) + o.orderValue);
            }
        }

        return revenue;
    }

    Map<Integer, Double> getAverageDeliveryDistancePerCustomer() {
        Map<Integer, Double> totalDistance = new HashMap<>();
        Map<Integer, Integer> deliveredCount = new HashMap<>();

        for (Order o : orders) {
            if (o.status == OrderStatus.DELIVERED) {
                totalDistance.put(o.customerId,
                        totalDistance.getOrDefault(o.customerId, 0.0) + o.distanceKm);
                deliveredCount.put(o.customerId,
                        deliveredCount.getOrDefault(o.customerId, 0) + 1);
            }
        }

        Map<Integer, Double> result = new HashMap<>();
        for (Integer customerId : totalDistance.keySet()) {
            result.put(customerId, totalDistance.get(customerId) / deliveredCount.get(customerId));
        }

        return result;
    }

    List<Integer> getHighlyActiveCustomers() {
        Map<Integer, Integer> orderCountByCustomer = new HashMap<>();

        for (Order o : orders) {
            orderCountByCustomer.put(o.customerId,
                    orderCountByCustomer.getOrDefault(o.customerId, 0) + 1);
        }

//        List<Integer> list2 = new ArrayList<>(orderCountByCustomer.entrySet().stream().filter(a -> a.getValue() > 3).map(Map.Entry::getKey).toList());
//         Collections.sort(list2);
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : orderCountByCustomer.entrySet()) {
            if (entry.getValue() >= 3) {
                result.add(entry.getKey());
            }
        }

        Collections.sort(result);
        return result;
    }
}

public class Main {

    public static void main(String[] args) {
        testOrderManager();
        testRevenuePerRestaurant();
        testAverageDeliveryDistancePerCustomer();
        testHighlyActiveCustomers();
        System.out.println("All tests passed.");
    }

    private static void assertEquals(int expected, int actual) {
        assert expected == actual : "Expected " + expected + " but got " + actual;
    }

    private static void assertEquals(List<Integer> expected, List<Integer> actual) {
        assert expected.equals(actual) : "Expected " + expected + " but got " + actual;
    }

    private static void assertAlmost(double expected, double actual, double eps) {
        assert Math.abs(expected - actual) <= eps : "Expected " + expected + " but got " + actual;
    }

    public static void testOrderManager() {
        System.out.println("Running testOrderManager");
        OrderManager om = new OrderManager();

        om.addOrder(new Order(1, 10, 100, 25.0, 3.2, OrderStatus.PLACED));
        om.addOrder(new Order(2, 10, 101, 55.0, 1.4, OrderStatus.PREPARING));
        om.addOrder(new Order(3, 11, 102, 15.0, 6.0, OrderStatus.OUT_FOR_DELIVERY));
        om.addOrder(new Order(4, 11, 103, 40.0, 2.0, OrderStatus.DELIVERED));
        om.addOrder(new Order(5, 12, 104, 18.0, 4.5, OrderStatus.CANCELED));

        OrderStats stats = om.getOrderStatistics();
        assertEquals(5, stats.totalOrders);
        assertEquals(3, stats.activeOrders);
        assertEquals(2, stats.closedOrders);
    }

    public static void testRevenuePerRestaurant() {
        System.out.println("Running testRevenuePerRestaurant");
        OrderManager om = new OrderManager();

        om.addOrder(new Order(1, 10, 100, 25.0, 3.2, OrderStatus.DELIVERED));
        om.addOrder(new Order(2, 10, 101, 55.0, 1.4, OrderStatus.CANCELED));
        om.addOrder(new Order(3, 11, 102, 15.0, 6.0, OrderStatus.DELIVERED));
        om.addOrder(new Order(4, 10, 103, 40.0, 2.0, OrderStatus.DELIVERED));
        om.addOrder(new Order(5, 12, 104, 18.0, 4.5, OrderStatus.PREPARING));

        Map<Integer, Double> revenue = om.getRevenuePerRestaurant();

        assertAlmost(65.0, revenue.get(10), 0.0001);
        assertAlmost(15.0, revenue.get(11), 0.0001);
        assert !revenue.containsKey(12) : "Restaurant 12 should not appear.";
    }

    public static void testAverageDeliveryDistancePerCustomer() {
        System.out.println("Running testAverageDeliveryDistancePerCustomer");
        OrderManager om = new OrderManager();

        om.addOrder(new Order(1, 10, 100, 25.0, 4.0, OrderStatus.DELIVERED));
        om.addOrder(new Order(2, 10, 100, 30.0, 6.0, OrderStatus.DELIVERED));
        om.addOrder(new Order(3, 11, 101, 15.0, 2.5, OrderStatus.DELIVERED));
        om.addOrder(new Order(4, 11, 101, 18.0, 8.0, OrderStatus.CANCELED));
        om.addOrder(new Order(5, 12, 102, 22.0, 3.0, OrderStatus.PREPARING));

        Map<Integer, Double> avgDistance = om.getAverageDeliveryDistancePerCustomer();

        assertAlmost(5.0, avgDistance.get(100), 0.0001);
        assertAlmost(2.5, avgDistance.get(101), 0.0001);
        assert !avgDistance.containsKey(102) : "Customer 102 should not appear.";
    }

    public static void testHighlyActiveCustomers() {
        System.out.println("Running testHighlyActiveCustomers");
        OrderManager om = new OrderManager();

        om.addOrder(new Order(1, 10, 100, 25.0, 3.2, OrderStatus.PLACED));
        om.addOrder(new Order(2, 10, 100, 55.0, 1.4, OrderStatus.CANCELED));
        om.addOrder(new Order(3, 11, 100, 15.0, 6.0, OrderStatus.DELIVERED));

        om.addOrder(new Order(4, 11, 101, 40.0, 2.0, OrderStatus.DELIVERED));
        om.addOrder(new Order(5, 12, 101, 18.0, 4.5, OrderStatus.CANCELED));

        om.addOrder(new Order(6, 12, 102, 12.0, 2.1, OrderStatus.PLACED));
        om.addOrder(new Order(7, 12, 102, 19.0, 3.6, OrderStatus.PREPARING));
        om.addOrder(new Order(8, 12, 102, 10.0, 1.1, OrderStatus.OUT_FOR_DELIVERY));
        om.addOrder(new Order(9, 12, 102, 8.0, 0.9, OrderStatus.DELIVERED));

        List<Integer> activeCustomers = om.getHighlyActiveCustomers();
        assertEquals(Arrays.asList(100, 102), activeCustomers);
    }
}
