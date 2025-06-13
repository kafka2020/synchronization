import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    private static final Object monitor = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread leaderThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (monitor) {
                    try {
                        monitor.wait(); // Ждем сигнала от рабочих потоков

                        // Находим текущего лидера
                        Optional<Map.Entry<Integer, Integer>> maxEntry = sizeToFreq.entrySet()
                                .stream()
                                .max(Map.Entry.comparingByValue());

                        if (maxEntry.isPresent()) {
                            System.out.printf("Текущий лидер: %d (встретилось %d раз)\n",
                                    maxEntry.get().getKey(), maxEntry.get().getValue());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });
        leaderThread.start();

        Thread[] threads = new Thread[1000];
        for (int i = 0; i < 1000; i++) {
            threads[i] = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                int count = route.replaceAll("[^R]", "").length();

                synchronized (sizeToFreq) {
                    sizeToFreq.put(count, sizeToFreq.getOrDefault(count, 0) + 1);
                }

                synchronized (monitor) {
                    monitor.notify();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        leaderThread.interrupt();
        leaderThread.join();

        int maxFreq = 0;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxFreq = entry.getKey();
            }
        }

        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n", maxFreq, maxCount);
        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getKey() != maxFreq) {
                System.out.printf("- %d (%d раз)\n", entry.getKey(), entry.getValue());
            }
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
