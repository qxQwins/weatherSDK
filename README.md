# Weather SDK

## Описание

Этот SDK позволяет интегрировать OpenWeather API для получения актуальной информации о погоде в различных городах. Он включает следующие компоненты:

- `WeatherClient`: основной клиент для работы с данными о погоде.
- `WeatherService`: сервис для получения и обработки данных о погоде.
- `ApiClient`: клиент для выполнения HTTP-запросов к OpenWeather API.
- `CachedWeatherData`: класс для кеширования данных о погоде.
- Конфигурация Spring для инициализации необходимых зависимостей.

## Установка

### 1. Добавление зависимостей

Для использования SDK, добавьте зависимости в ваш `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20210307</version>
    </dependency>
</dependencies>
```
### 2. Конфигурация API-ключа

Создайте файл `application.properties` в ресурсах и добавьте ваш API ключ для OpenWeather API:

```properties
openweathermap.api.key=your_api_key_here
```

### 3. Конфигурация Spring

Используйте класс конфигурации Spring для инициализации всех необходимых зависимостей:

```java
@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("classpath:application.properties")
public class WeatherConfig {

    @Bean
    public ApiClient apiClient(@Value("${openweathermap.api.key}") String apiKey) {
        return new ApiClient(apiKey);
    }

    @Bean
    public WeatherService weatherService(ApiClient apiClient) {
        return new WeatherService(apiClient);
    }
}
```
Убедитесь, что в application.properties указан правильный API ключ.
### Использование
### 1. Создание и использование клиента погоды (WeatherClient)
Для работы с данными о погоде используйте класс WeatherClient, который инкапсулирует логику получения и кеширования данных.

Пример создания и использования
```java
@Component
public class WeatherClientApp {

    private final WeatherClient weatherClient;

    @Autowired
    public WeatherClientApp(WeatherService weatherService) {
        this.weatherClient = new WeatherClient(weatherService, WeatherClient.Mode.POLLING);
    }

    public void getWeatherData() {
        // Пример получения данных
        JSONObject weather = weatherClient.getWeather("London");
        System.out.println(weather.toString());
    }
}
```
### 2. Запуск Spring Boot приложения
Создайте главный класс для запуска Spring Boot приложения:
```java
@SpringBootApplication
public class WeatherApp {
    public static void main(String[] args) {
        SpringApplication.run(WeatherApp.class, args);
    }
}
```
### Основные методы

- **`checkCity(String city)`**: Проверяет, есть ли данные о погоде для указанного города в кеше. Если данных нет или они устарели (более 10 минут), они обновляются.
- **`getWeather(String city)`**: Возвращает данные о погоде для указанного города.
- **`updateAllCities()`**: Обновляет данные о погоде для всех сохраненных городов.
- **`removeOldestCity()`**: Удаляет из кеша самый старый город.
- **`addCity(String city)`**: Добавляет новый город в кеш.

### Пример использования

Пример использования `WeatherClient` в Spring Boot приложении:

```java
@Component
public class WeatherClientApp {

    private final WeatherClient weatherClient;

    @Autowired
    public WeatherClientApp(WeatherService weatherService) {
        this.weatherClient = new WeatherClient(weatherService, WeatherClient.Mode.POLLING);
    }

    public void getWeatherData() {
        // Пример получения данных
        JSONObject weather = weatherClient.getWeather("London");
        System.out.println(weather.toString());
    }
}
```
Результат

![image](https://github.com/user-attachments/assets/921eecf3-9959-4030-93b0-21bda551b25d)
