import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        // Создаем и запускаем потоки
        Thread[] threads = new Thread[1000];
        for (int i = 0; i < 1000; i++) {
            threads[i] = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                int count = route.replaceAll("[^R]", "").length();

                synchronized (sizeToFreq) {
                    sizeToFreq.put(count, sizeToFreq.getOrDefault(count, 0) + 1);
                }
            });
            threads[i].start();
        }

        // Ждем завершения всех потоков
        for (Thread thread : threads) {
            thread.join();
        }

        // Находим самую частую частоту
        int maxFreq = 0;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxFreq = entry.getKey();
            }
        }

        // Выводим результаты
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
