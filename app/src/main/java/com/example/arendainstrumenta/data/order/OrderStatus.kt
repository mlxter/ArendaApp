package com.example.arendainstrumenta.data.order

sealed class OrderStatus(val status: String) {

    object Ordered: OrderStatus("Оформлен")
    object Canceled: OrderStatus("Отменен")
    object Confirmed: OrderStatus("Получен")
    object Returned: OrderStatus("Возвращен")
}

fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Оформлен" -> {
            OrderStatus.Ordered
        }
        "Отменен" -> {
            OrderStatus.Canceled
        }
        "Получен" -> {
            OrderStatus.Confirmed
        }
        else -> OrderStatus.Returned
    }
}