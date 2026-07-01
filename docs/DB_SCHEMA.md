# DB 테이블 설계

## seniors

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGSERIAL PK | 독거노인 ID |
| name | VARCHAR | 이름 |
| age | INT | 나이 |
| address | VARCHAR | 주소 |
| guardian_name | VARCHAR | 보호자 이름 |
| guardian_phone | VARCHAR | 보호자 연락처 |
| baseline_active_start_hour | INT | 평소 활동 시작 시간 |
| baseline_active_end_hour | INT | 평소 활동 종료 시간 |
| created_at | TIMESTAMP | 등록일 |

## sensor_events

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGSERIAL PK | 이벤트 ID |
| senior_id | BIGINT FK | 독거노인 ID |
| motion_detected | BOOLEAN | 움직임 감지 |
| door_opened | BOOLEAN | 문 열림 |
| temperature | DOUBLE | 온도 |
| humidity | DOUBLE | 습도 |
| illuminance | DOUBLE | 조도 |
| event_time | TIMESTAMP | 센서 발생 시간 |
| created_at | TIMESTAMP | 저장 시간 |

## risk_assessments

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGSERIAL PK | 분석 ID |
| senior_id | BIGINT FK | 독거노인 ID |
| sensor_event_id | BIGINT FK | 센서 이벤트 ID |
| score | INT | 위험 점수 |
| status | VARCHAR | NORMAL, CAUTION, DANGER |
| reasons | TEXT | 판단 사유 |
| assessed_at | TIMESTAMP | 분석 시간 |

## alert_histories

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGSERIAL PK | 알림 ID |
| senior_id | BIGINT FK | 독거노인 ID |
| risk_assessment_id | BIGINT FK | 분석 ID |
| level | VARCHAR | CAUTION, DANGER |
| message | TEXT | 알림 메시지 |
| receiver | VARCHAR | 수신자 |
| sent | BOOLEAN | 알림 시뮬레이션 성공 여부 |
| created_at | TIMESTAMP | 생성 시간 |
