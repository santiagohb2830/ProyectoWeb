#!/bin/bash
set -e

# Default thresholds if not provided
P95_THRESHOLD=${P95_THRESHOLD_MS:-2000}
ERROR_RATE_THRESHOLD=${ERROR_RATE_PCT:-1}
INFLUX_HOST="http://localhost:8086"
DB_NAME="jmeter"

echo "Verificando umbrales de rendimiento en InfluxDB..."
echo "Umbral P95: ${P95_THRESHOLD} ms"
echo "Umbral Tasa de Error: ${ERROR_RATE_THRESHOLD} %"

# Query P95 (Maximo valor del percentil 95 global durante la prueba)
P95_QUERY="SELECT max(\"pct95.0\") FROM \"jmeter\" WHERE \"statut\"='all' AND \"transaction\"='all'"
P95_RESPONSE=$(curl -s -G "${INFLUX_HOST}/query" --data-urlencode "db=${DB_NAME}" --data-urlencode "q=${P95_QUERY}")

# Parse P95 using jq. InfluxDB returns json: {"results":[{"series":[{"columns":["time","max"],"values":[["2024...", 1234.5]]}]}]}
P95_ACTUAL=$(echo "$P95_RESPONSE" | jq -r '.results[0].series[0].values[0][1] // "0"')
# Round to integer for comparison
P95_ACTUAL_INT=$(printf "%.0f" "$P95_ACTUAL")

# Query Error Rate
ERROR_QUERY="SELECT sum(\"countError\"), sum(\"count\") FROM \"jmeter\" WHERE \"statut\"='all' AND \"transaction\"='all'"
ERROR_RESPONSE=$(curl -s -G "${INFLUX_HOST}/query" --data-urlencode "db=${DB_NAME}" --data-urlencode "q=${ERROR_QUERY}")

SUM_ERRORS=$(echo "$ERROR_RESPONSE" | jq -r '.results[0].series[0].values[0][1] // "0"')
SUM_COUNT=$(echo "$ERROR_RESPONSE" | jq -r '.results[0].series[0].values[0][2] // "0"')

if [ "$SUM_COUNT" -eq 0 ]; then
  ERROR_RATE_ACTUAL=0
else
  ERROR_RATE_ACTUAL=$(awk "BEGIN {print ($SUM_ERRORS / $SUM_COUNT) * 100}")
fi
# Round error rate to 2 decimals for display
ERROR_RATE_DISPLAY=$(printf "%.2f" "$ERROR_RATE_ACTUAL")

# Write to GitHub Step Summary
cat <<EOF >> $GITHUB_STEP_SUMMARY
## 📊 Resultados de Pruebas de Carga (JMeter)

| Métrica | Umbral (Max) | Valor Obtenido | Estado |
|---------|--------------|----------------|--------|
| **P95 Response Time** | ${P95_THRESHOLD} ms | **${P95_ACTUAL_INT} ms** | $(if [ "$P95_ACTUAL_INT" -gt "$P95_THRESHOLD" ]; then echo "❌ Falló"; else echo "✅ Pasó"; fi) |
| **Error Rate** | ${ERROR_RATE_THRESHOLD} % | **${ERROR_RATE_DISPLAY} %** | $(if [ $(echo "$ERROR_RATE_ACTUAL > $ERROR_RATE_THRESHOLD" | bc -l) -eq 1 ]; then echo "❌ Falló"; else echo "✅ Pasó"; fi) |

EOF

FAILED=0

if [ "$P95_ACTUAL_INT" -gt "$P95_THRESHOLD" ]; then
  echo "❌ ERROR: El percentil 95 (${P95_ACTUAL_INT} ms) supera el umbral de ${P95_THRESHOLD} ms."
  FAILED=1
else
  echo "✅ OK: P95 es ${P95_ACTUAL_INT} ms (Umbral: ${P95_THRESHOLD} ms)."
fi

if [ $(echo "$ERROR_RATE_ACTUAL > $ERROR_RATE_THRESHOLD" | bc -l) -eq 1 ]; then
  echo "❌ ERROR: La tasa de error (${ERROR_RATE_DISPLAY}%) supera el umbral de ${ERROR_RATE_THRESHOLD}%."
  FAILED=1
else
  echo "✅ OK: Tasa de error es ${ERROR_RATE_DISPLAY}% (Umbral: ${ERROR_RATE_THRESHOLD}%)."
fi

if [ $FAILED -ne 0 ]; then
  echo "El pipeline fallará debido a que no se cumplieron los umbrales de rendimiento."
  exit 1
fi

echo "Todas las validaciones de rendimiento fueron exitosas."
exit 0
