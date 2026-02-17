# Media Pipeline Lite

Бэкенд для видеостриминга с поддержкой HLS (HTTP Live Streaming) и аналитикой качества воспроизведения. Проект демонстрирует работу с асинхронной обработкой событий через Kafka, кэшированием в Redis и мониторингом через Prometheus/Grafana.

## Что внутри

- **HLS стриминг** с adaptive bitrate (480p, 720p, 1080p, 4K)
- **Асинхронная обработка** аналитических событий через Apache Kafka
- **Кэширование** HLS манифестов в Redis для снижения нагрузки
- **QoE метрики** (Quality of Experience) - оценка качества воспроизведения
- **Мониторинг** через Prometheus и дашборды в Grafana
- **Timeline events** - управление интро и главами

## Технологии

- Java 21 + Spring Boot 3.2
- Apache Kafka для асинхронной обработки аналитических событий
- Redis для кэширования
- H2 in-memory база данных
- Prometheus + Grafana для мониторинга
- Docker Compose для инфраструктуры

## Быстрый старт

### Требования

- Java 21
- Docker + Docker Compose
- Maven

### Запуск

1. Запустить докер-контейнеры (Kafka, Redis, Prometheus, Grafana):

```bash
docker-compose up -d
```

2. Собрать и запустить приложение:

```bash
mvn clean package
java -jar target/media-pipeline-lite-1.0.0.jar
```

Или через Maven:

```bash
mvn spring-boot:run
```
Или через интерфейс idea

### Доступные сервисы

После запуска будут доступны:

- **API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:mediapipeline`)
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Kafka UI**: http://localhost:8090

## API примеры

### Получить список контента

```bash
curl http://localhost:8080/api/content
```

### Начать воспроизведение

```bash
curl -X POST http://localhost:8080/api/playback/start \
  -H "Content-Type: application/json" \
  -d '{
    "contentId": 1,
    "userId": "user123",
    "deviceType": "WEB",
    "initialQuality": "720p"
  }'
```

Ответ:
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "masterPlaylistUrl": "/api/playback/session/550e8400-e29b-41d4-a716-446655440000/master.m3u8",
  "content": {
    "id": 1,
    "title": "Big Buck Bunny",
    "durationSeconds": 120
  }
}
```

### Получить HLS манифест

```bash
curl http://localhost:8080/api/playback/session/{sessionId}/master.m3u8
```

### Отправить аналитическое событие

```bash
curl -X POST http://localhost:8080/api/analytics/event \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "eventType": "PLAY",
    "timestamp": "2025-02-17T12:00:00Z",
    "currentPosition": 0,
    "currentQuality": "720p",
    "bufferHealth": 5.0
  }'
```

События обрабатываются асинхронно через Kafka и возвращают HTTP 202 Accepted.

### Получить QoE метрики

```bash
curl http://localhost:8080/api/analytics/session/{sessionId}/qoe
```

## Архитектура

### Асинхронная обработка событий

```
Client → POST /api/analytics/event → Kafka Producer → Kafka Topic
                                          ↓
                         Kafka Consumer → AnalyticsService → H2 Database
```

Преимущества:
- API отвечает мгновенно (~10-20ms вместо 200ms+)
- Kafka буферизует события при всплесках нагрузки
- События не теряются даже при падении consumer'а
- Горизонтальное масштабирование через consumer groups

### Кэширование в Redis

HLS манифесты кэшируются в Redis с TTL 1 час:
- **master-playlists** - список качеств для adaptive streaming
- Hit rate в production обычно ~98-99%

### Мониторинг

Prometheus собирает метрики:
- Throughput API endpoints
- Kafka consumer lag
- Redis cache hit rate
- QoE метрики
- JVM метрики

## Структура проекта

```
src/main/java/ru/sinvic/
├── controller/         # REST API endpoints
├── service/            # Бизнес-логика
├── kafka/              # Kafka producer/consumer
├── domain/             # JPA entities
├── dto/                # Data transfer objects
├── repository/         # Spring Data JPA repositories
├── exception/          # Исключения
└── config/             # Конфигурация (Kafka, Redis, properties)

```

## Мониторинг и отладка

### Prometheus метрики

```bash
curl http://localhost:8080/actuator/prometheus
```

### Health check

```bash
curl http://localhost:8080/actuator/health
```

### Kafka UI

Kafka UI (http://localhost:8090)

### Grafana дашборды

В Grafana предустановлены дашборды для мониторинга:
- JVM metrics (heap, threads, GC)
- Spring Boot metrics (request rate, errors)
- Kafka metrics (consumer lag, throughput)
- Custom QoE metrics

## Дополнительная документация

- [KAFKA_TESTING.md](KAFKA_TESTING.md) - подробнее про Kafka интеграцию
- [REDIS.md](REDIS.md) - настройка и использование Redis кэша
- [MONITORING.md](MONITORING.md) - настройка мониторинга
- [TESTING.md](TESTING.md) - запуск и написание тестов

## Лицензия

MIT
