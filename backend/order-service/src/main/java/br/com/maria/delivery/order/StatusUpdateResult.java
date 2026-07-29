package br.com.maria.delivery.order;

public class StatusUpdateResult {

    public final Order order;
    public final boolean notFound;
    public final boolean invalidTransition;
    public final String message;

    private StatusUpdateResult(Order order, boolean notFound, boolean invalidTransition, String message) {
        this.order = order;
        this.notFound = notFound;
        this.invalidTransition = invalidTransition;
        this.message = message;
    }

    public static StatusUpdateResult updated(Order order) {
        return new StatusUpdateResult(order, false, false, null);
    }

    public static StatusUpdateResult notFound() {
        return new StatusUpdateResult(null, true, false, "Pedido não encontrado.");
    }

    public static StatusUpdateResult invalidTransition(OrderStatus currentStatus, OrderStatus nextStatus) {
        return new StatusUpdateResult(
                null,
                false,
                true,
                "Transição inválida de " + currentStatus + " para " + nextStatus + "."
        );
    }
}
