# Мониторинг с Grafana + Prometheus

Этот документ описывает настройку и использование системы мониторинга для Media Pipeline Lite.

## Архитектура

```
┌─────────────────┐
│  Spring Boot    │
│  Application    │ :8080/actuator/prometheus
│  (on host)      │
└────────┬────────┘
         │ scrape every 15s
         │
┌────────▼────────┐
│   Prometheus    │ :9090
│   (container)   │ Сбор и хранение метрик
└────────┬────────┘
         │ query
         │
┌────────▼────────┐
│    Grafana      │ :3000
│   (container)   │ Визуализация дашбордов
└─────────────────┘
```

## Быстрый старт

### 1. Запуск докер-контейнеров

```bash
# Запускаем Kafka, Prometheus, Grafana
docker-compose up -d

# Проверяем что все контейнеры запущены
docker-compose ps
```

Должны быть запущены:
- ✅ zookeeper (порт 2181)
- ✅ kafka (порт 9092)
- ✅ kafka-ui (порт 8090)
- ✅ prometheus (порт 9090)
- ✅ grafana (порт 3000)

### 2. Запустите Spring Boot приложение

```bash
./mvnw spring-boot:run

# Или через IDE: запустите MediaPipelineApplication.main()
```

### 3. Проверьте доступность метрик

```bash
# Spring Boot Actuator endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus
```

### 4. Откройте Prometheus UI

**URL:** http://localhost:9090

**Полезные страницы:**
- **Status → Targets:** проверка подключения к Spring Boot
  - Должен быть `spring-boot-app (1/1 up)`
- **Graph:** выполнение PromQL запросов
- **Status → Configuration:** текущая конфигурация

**Пример PromQL запросов:**
```promql
# JVM Memory использование (heap)
jvm_memory_used_bytes{area="heap"}

# HTTP запросов в секунду
rate(http_server_requests_seconds_count[1m])

# Kafka Producer отправленных сообщений
kafka_producer_record_send_total

# Kafka Consumer обработанных сообщений
kafka_consumer_records_consumed_total
```

### 5. Откройте Grafana

**URL:** http://localhost:3000

**Credentials:**
- Username: `admin`
- Password: `admin`

При первом входе Grafana предложит сменить пароль (можно пропустить для dev).

## 📈 Создание дашбордов

### Вариант 1: Импорт готового дашборда (рекомендуется)

1. Откройте Grafana → http://localhost:3000
2. Нажмите **"+"** → **"Import dashboard"**
3. Введите ID готового дашборда:
   - **12900** - Spring Boot 2.1 Statistics
   - **4701** - JVM (Micrometer)
   - **11962** - Spring Boot Kafka
4. Нажмите **"Load"**
5. Выберите datasource: **Prometheus**
6. Нажмите **"Import"**

Дашборд автоматически отобразит метрики вашего приложения.

### Вариант 2: Создание кастомного дашборда

1. **"+"** → **"Dashboard"** → **"Add new panel"**
2. В поле **"Metrics browser"** введите PromQL запрос:
   ```promql
   rate(http_server_requests_seconds_count{status="200"}[1m])
   ```
3. Настройте визуализацию (Graph, Gauge, Stat, Table)
4. Нажмите **"Apply"**
5. **"Save dashboard"** → введите название
