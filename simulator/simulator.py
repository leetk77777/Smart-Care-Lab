import argparse
import random
import time
from datetime import datetime, timedelta

import requests


API_URL = "http://localhost:18180/api/sensor-events"


def event(senior_id, when, motion, door, temp, humidity, illuminance):
    return {
        "seniorId": senior_id,
        "motionDetected": motion,
        "doorOpened": door,
        "temperature": temp,
        "humidity": humidity,
        "illuminance": illuminance,
        "eventTime": when.isoformat(timespec="seconds"),
    }


def build_events(scenario, senior_id):
    now = datetime.now().replace(microsecond=0)
    if scenario == "normal":
        return [
            event(senior_id, now - timedelta(minutes=40), True, False, 24.0, 45.0, 320.0),
            event(senior_id, now - timedelta(minutes=15), True, False, 24.3, 46.0, 360.0),
            event(senior_id, now, True, False, 24.6, 44.0, 390.0),
        ]
    if scenario == "caution":
        morning = now.replace(hour=10, minute=10, second=0)
        return [
            event(senior_id, morning - timedelta(hours=2), False, False, 24.0, 45.0, 250.0),
            event(senior_id, morning, False, False, 30.5, 50.0, 270.0),
        ]
    danger_time = now.replace(hour=3, minute=20, second=0)
    return [
        event(senior_id, danger_time - timedelta(hours=5), True, False, 25.0, 47.0, 120.0),
        event(senior_id, danger_time, False, True, 34.0, 82.0, 20.0),
    ]


def main():
    parser = argparse.ArgumentParser(description="Virtual sensor simulator")
    parser.add_argument("--senior-id", type=int, default=1)
    parser.add_argument("--scenario", choices=["normal", "caution", "danger", "random"], default="normal")
    parser.add_argument("--interval", type=float, default=1.0)
    args = parser.parse_args()

    if args.scenario == "random":
        while True:
            now = datetime.now().replace(microsecond=0)
            payload = event(
                args.senior_id,
                now,
                random.random() > 0.35,
                random.random() > 0.85,
                round(random.uniform(18, 31), 1),
                round(random.uniform(35, 70), 1),
                round(random.uniform(30, 500), 1),
            )
            response = requests.post(API_URL, json=payload, timeout=5)
            print(response.status_code, response.text)
            time.sleep(args.interval)

    for payload in build_events(args.scenario, args.senior_id):
        response = requests.post(API_URL, json=payload, timeout=5)
        print(response.status_code, response.text)
        time.sleep(args.interval)


if __name__ == "__main__":
    main()
