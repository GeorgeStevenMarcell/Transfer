#!/usr/bin/env bash
set -euo pipefail

URL="${URL:-http://localhost:8080/v1/transaction/transfer}"
DURATION="${DURATION:-10}"        # seconds
CONCURRENCY="${CONCURRENCY:-10}"
BODY='{"merchantId":1,"amount":"1000","destinationAcct":"123"}'

TMP=$(mktemp -d); trap 'rm -rf "$TMP"' EXIT
END=$(( $(date +%s) + DURATION ))

worker() {
  while [ "$(date +%s)" -lt "$END" ]; do
    curl -s -o /dev/null -w '%{http_code}\n' -X POST "$URL" \
         -H 'Content-Type: application/json' -d "$BODY" >> "$TMP/codes"
  done
}

echo "firing ${CONCURRENCY} workers for ${DURATION}s at ${URL}"
for _ in $(seq 1 "$CONCURRENCY"); do worker & done
wait

total=$(wc -l < "$TMP/codes")
echo "total requests : $total"
echo "throughput     : $(( total / DURATION )) rps"
echo "status codes   :"
sort "$TMP/codes" | uniq -c | sed 's/^/  /'