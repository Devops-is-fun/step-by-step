// k6 load test of the backend, run inside the cluster by scripts/load-test.sh.
// Reads the task list in a loop: enough CPU load to make the HPA add pods.
import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 }, // ramp up to 20 virtual users
    { duration: __ENV.DURATION || '3m', target: 20 }, // hold the load
    { duration: '30s', target: 0 }, // ramp down
  ],
  // The test fails if more than 1 % of the requests fail: zero-downtime check during a rolling update
  thresholds: {
    http_req_failed: ['rate<0.01'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://app.step-by-step.svc.cluster.local';

export default function () {
  const res = http.get(`${BASE_URL}/api/tasks`);
  check(res, { 'status 200': (r) => r.status === 200 });
}
