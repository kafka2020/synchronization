import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        // Создаем и запускаем 1000 потоков
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                int rCount = countChar(route, 'R');

                // Синхронизированное обновление мапы
                synchronized (sizeToFreq) {
                    sizeToFreq.put(rCount, sizeToFreq.getOrDefault(rCount, 0) + 1);
                }
            });
            threads.add(thread);
            thread.start();
        }

        // Ожидаем завершения всех потоков
        for (Thread thread : threads) {
            thread.join();
        }

        // Находим наиболее часто встречающуюся частоту
        Map.Entry<Integer, Integer> maxEntry = Collections.max(sizeToFreq.entrySet(),
                Map.Entry.comparingByValue());

        // Выводим результаты
        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n",
                maxEntry.getKey(), maxEntry.getValue());
        System.out.println("Другие размеры:");

        sizeToFreq.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (!entry.getKey().equals(maxEntry.getKey())) {
                        System.out.printf("- %d (%d раз)\n", entry.getKey(), entry.getValue());
                    }
                });
    }

    // Генерация маршрута
    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    // Подсчет количества указанных символов в строке
    private static int countChar(String str, char ch) {
        return (int) str.chars().filter(c -> c == ch).count();
    }
}