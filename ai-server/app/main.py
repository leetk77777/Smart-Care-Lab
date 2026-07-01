from datetime import datetime
from typing import List, Optional

from fastapi import FastAPI
from pydantic import BaseModel, Field


app = FastAPI(
    title="Senior Safety AI Analysis API",
    description="Rule-based risk analysis service for privacy-preserving senior safety monitoring.",
    version="1.0.0",
)


class AnalyzeRequest(BaseModel):
    seniorId: int
    motionDetected: bool
    doorOpened: bool
    temperature: float
    humidity: float
    illuminance: float
    eventTime: datetime
    lastMotionAt: Optional[datetime] = None
    baselineActiveStartHour: int = Field(default=7, ge=0, le=23)
    baselineActiveEndHour: int = Field(default=22, ge=0, le=23)


class AnalyzeResponse(BaseModel):
    score: int
    status: str
    reasons: List[str]


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


@app.post("/analyze", response_model=AnalyzeResponse)
def analyze(request: AnalyzeRequest) -> AnalyzeResponse:
    score = 0
    reasons: List[str] = []

    if request.lastMotionAt:
        inactive_hours = (request.eventTime - request.lastMotionAt).total_seconds() / 3600
        if inactive_hours >= 3:
            score += 35
            reasons.append(f"{inactive_hours:.1f}시간 동안 움직임이 감지되지 않았습니다.")

    if request.eventTime.hour >= 9 and not request.motionDetected and request.lastMotionAt is None:
        score += 25
        reasons.append("오전 9시 이후 활동 기록이 없어 안부 확인이 필요합니다.")

    if request.temperature <= 16 or request.temperature >= 32:
        score += 30
        reasons.append("실내 온도가 안전 범위를 벗어났습니다.")
    elif request.temperature <= 18 or request.temperature >= 30:
        score += 15
        reasons.append("실내 온도가 주의 범위에 있습니다.")

    if request.doorOpened and 0 <= request.eventTime.hour < 5:
        score += 25
        reasons.append("새벽 시간대 문 열림이 감지되었습니다.")

    active_start = request.baselineActiveStartHour
    active_end = request.baselineActiveEndHour
    if request.motionDetected and not (active_start <= request.eventTime.hour <= active_end):
        score += 15
        reasons.append("평소 생활패턴과 다른 시간대 활동이 감지되었습니다.")

    if request.humidity < 25 or request.humidity > 80:
        score += 10
        reasons.append("습도가 쾌적 범위를 벗어났습니다.")

    score = min(score, 100)
    if score >= 70:
        status = "DANGER"
    elif score >= 40:
        status = "CAUTION"
    else:
        status = "NORMAL"

    if not reasons:
        reasons.append("생활패턴과 실내 환경이 정상 범위입니다.")

    return AnalyzeResponse(score=score, status=status, reasons=reasons)
