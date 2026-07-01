# API 명세

Base URL: `http://localhost:18180`

## 상태 확인

### GET `/health`

백엔드 서버 실행 상태를 확인합니다.

## 독거노인

### GET `/api/seniors`

독거노인 목록을 조회합니다.

### POST `/api/seniors`

```json
{
  "name": "김영희",
  "age": 78,
  "address": "서울시 중구",
  "guardianName": "김민수",
  "guardianPhone": "010-1234-5678"
}
```

## 센서 이벤트

### POST `/api/sensor-events`

센서 데이터를 저장하고 AI 분석 서버에 위험도 분석을 요청합니다.

```json
{
  "seniorId": 1,
  "motionDetected": true,
  "doorOpened": false,
  "temperature": 24.5,
  "humidity": 45.0,
  "illuminance": 320.0,
  "eventTime": "2026-07-01T10:30:00"
}
```

### GET `/api/sensor-events/senior/{seniorId}`

최근 센서 이벤트 50개를 조회합니다.

## 대시보드

### GET `/api/dashboard/{seniorId}`

위험도, 최근 센서 상태, 알림 이력, 생활패턴 차트 데이터를 한 번에 조회합니다.

## 알림

### GET `/api/alerts/senior/{seniorId}`

주의/위험 상태 발생 시 생성된 알림 이력을 조회합니다.

## 데모

### POST `/api/demo/{seniorId}/{scenario}`

`scenario` 값:

- `normal`: 정상 생활패턴
- `caution`: 오전 활동 부족과 약한 이상징후
- `danger`: 장시간 움직임 없음, 위험 온도, 새벽 문 열림

## AI Server

Base URL: `http://localhost:18080`

### POST `/analyze`

```json
{
  "seniorId": 1,
  "motionDetected": false,
  "doorOpened": true,
  "temperature": 34.0,
  "humidity": 70.0,
  "illuminance": 20.0,
  "eventTime": "2026-07-01T03:20:00",
  "lastMotionAt": "2026-06-30T22:00:00",
  "baselineActiveStartHour": 7,
  "baselineActiveEndHour": 22
}
```

응답:

```json
{
  "score": 85,
  "status": "DANGER",
  "reasons": [
    "3시간 이상 움직임이 감지되지 않았습니다.",
    "실내 온도가 안전 범위를 벗어났습니다.",
    "새벽 시간대 문 열림이 감지되었습니다."
  ]
}
```
