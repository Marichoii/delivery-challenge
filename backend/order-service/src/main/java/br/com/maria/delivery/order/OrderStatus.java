package br.com.maria.delivery.order;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED;

    public boolean canMoveTo(OrderStatus nextStatus) {
        return switch (this) {
            case CREATED -> nextStatus == CONFIRMED;
            case CONFIRMED -> nextStatus == PREPARING;
            case PREPARING -> nextStatus == READY;
            case READY -> nextStatus == DELIVERED;
            case DELIVERED -> false;
        };
    }
}
