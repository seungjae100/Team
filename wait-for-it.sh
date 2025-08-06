#!/bin/sh
# shellcheck disable=SC1128

# Split host and port
HOST=$(echo "$1" | cut -d: -f1)
PORT=$(echo "$1" | cut -d: -f2)
shift

# Remove '--' if exists
if [ "$1" = "--" ]; then
  shift
fi

# Wait until the host:port is available
until nc -z "$HOST" "$PORT"; do
  >&2 echo "$HOST:$PORT is not available yet. Waiting..."
  sleep 1
done

>&2 echo "$HOST:$PORT is up - executing command"
exec "$@"
