#!/usr/bin/env bash
set -euo pipefail

# === Defaults ===
MODE=""
CUSTOM_FILENAME=""
VERBOSE=false
CURRENT_DATE="$(date +%Y-%m-%d)"
ARCHIVE_NAME=""
ARCHIVE_BASENAME=""
MARKER_FILE=".encoded.txt"
EXCLUDES=(".git" "target" "log")

# === Paths ===
SCRIPT_PATH="$(realpath "$0")"
SCRIPT_DIR="$(dirname "$SCRIPT_PATH")"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
TMP_DIR="$(mktemp -d)"

# === Usage ===
print_usage() {
  echo "Usage: $0 [encode|decode] [-f|--file archive.zip] [-v|--verbose]" >&2
}

# === Argument Parsing ===
while [[ $# -gt 0 ]]; do
  case "$1" in
    encode|decode)
      MODE="$1"
      shift
      ;;
    -f|--file)
      CUSTOM_FILENAME="$2"
      shift 2
      ;;
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    -h|--help)
      print_usage
      exit 0
      ;;
    -*)
      echo "❌ Unknown option: $1" >&2
      print_usage
      exit 1
      ;;
    *)
      echo "❌ Unexpected argument: $1" >&2
      print_usage
      exit 1
      ;;
  esac
done

# === Validate Mode ===
if [[ -z "$MODE" ]]; then
  print_usage
  exit 1
fi

# === Compute Archive Name and Base Name ===
DEFAULT_ARCHIVE_NAME="encoded-${CURRENT_DATE}.zip"
ARCHIVE_NAME="${CUSTOM_FILENAME:-$DEFAULT_ARCHIVE_NAME}"

if [[ "$ARCHIVE_NAME" != *.zip ]]; then
  echo "❌ Archive name must end in .zip: $ARCHIVE_NAME" >&2
  exit 1
fi

ARCHIVE_BASENAME="${ARCHIVE_NAME%.zip}"

# === Logging Helper ===
log() {
  if $VERBOSE; then
    echo "$1"
  fi
}

# === Encode Function ===
encode() {
  echo "📦 Encoding project into: $ARCHIVE_NAME"
  cd "$ROOT_DIR"

  # Build rsync exclude arguments
  EXCLUDE_ARGS=()
  for pattern in "${EXCLUDES[@]}"; do
    EXCLUDE_ARGS+=(--exclude="$pattern")
  done

  # Copy all files, excluding patterns in EXCLUDES, into archive-named folder
  rsync -a "${EXCLUDE_ARGS[@]}" ./ "$TMP_DIR/$ARCHIVE_BASENAME"

  # Rename all non-.txt files by appending .txt
  mapfile -t files_to_rename < <(find "$TMP_DIR/$ARCHIVE_BASENAME" -type f ! -name "*.txt")
  for file in "${files_to_rename[@]}"; do
    mv "$file" "$file.txt"
    log "Renamed: $file → $file.txt"
  done

  # Add a marker file inside the archive root
  echo "encoded" > "$TMP_DIR/$ARCHIVE_BASENAME/$MARKER_FILE"

  # Create the archive
  cd "$TMP_DIR"
  zip -qr "$ROOT_DIR/$ARCHIVE_NAME" "$ARCHIVE_BASENAME"
  echo "✅ Archive created: $ARCHIVE_NAME"

  # Clean up temp directory
  rm -rf "$TMP_DIR"
}

# === Decode Function ===
decode() {
  echo "♻️  Decoding files in: $ROOT_DIR"

  # Check for and remove the marker file early
  if [[ ! -f "$ROOT_DIR/$MARKER_FILE" ]]; then
    echo "❌ Not an encoded project (missing $MARKER_FILE marker)." >&2
    exit 1
  fi

  rm "$ROOT_DIR/$MARKER_FILE"

  # Find all .txt files outside the scripts directory
  mapfile -t files_to_restore < <(
    find "$ROOT_DIR" -type f -name "*.txt" ! -path "$ROOT_DIR/scripts/*"
  )

  for file in "${files_to_restore[@]}"; do
    dest="${file%.txt}"
    if [[ "$file" != "$dest" ]]; then
      mv "$file" "$dest"
      log "Restored: $file → $dest"
    fi
  done

  echo "✅ Decode complete."
}

# === Execute Selected Mode ===
case "$MODE" in
  encode) encode ;;
  decode) decode ;;
esac
