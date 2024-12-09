import java.util.concurrent.Semaphore; 

public class InternetShopSimulation {

    // Семафор для контролю наявності товарів
    private static final Semaphore productStock = new Semaphore(5); // Початкова кількість товарів - 5
    private static volatile boolean shopOpen = true; // Флаг для позначення робочих годин магазину

    public static void main(String[] args) {
        Thread adminThread = new Thread(new Admin());
        Thread customer1 = new Thread(new Customer("Покупець 1"));
        Thread customer2 = new Thread(new Customer("Покупець 2"));
        Thread customer3 = new Thread(new Customer("Покупець 3"));

        adminThread.start();
        customer1.start();
        customer2.start();
        customer3.start();

        try {
            // Магазин працює 10 секунд
            Thread.sleep(10000);
            shopOpen = false; // Магазин закривається
            System.out.println("Магазин закрито.");
        } catch (InterruptedException e) {
            System.out.println("Виникла помилка пiд час роботи магазину: " + e.getMessage());
        }
    }

    // Клас для адміністрування товарів
    static class Admin implements Runnable {
        @Override
        public void run() {
            while (shopOpen) {
                try {
                    Thread.sleep(2000); // Додавання товарів кожні 2 секунди
                    productStock.release(1);
                    System.out.println("Адмiнiстратор додав 1 товар. Доступно товарiв: " + productStock.availablePermits());
                } catch (InterruptedException e) {
                    System.out.println("Помилка у роботi адмiнiстратора: " + e.getMessage());
                }
            }
        }
    }

    // Клас для покупців
    static class Customer implements Runnable {
        private final String name;

        public Customer(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (shopOpen) {
                try {
                    if (productStock.tryAcquire()) { // Спроба купити товар
                        System.out.println(name + " купив товар. Залишилось товарiв: " + productStock.availablePermits());
                        Thread.sleep(1000); // Час на обробку покупки
                    } else {
                        System.out.println(name + " хотiв купити товар, але його немає в наявностi.");
                        Thread.sleep(1500); // Чекає перед наступною спробою
                    }
                } catch (InterruptedException e) {
                    System.out.println("Помилка у роботi покупця " + name + ": " + e.getMessage());
                }
            }
        }
    }
}
