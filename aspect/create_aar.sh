#!/bin/bash
AAPT2="third_party/java/android/android_sdk_linux/platform-tools/aapt2"
TMP="tmp"
ROOT=$PWD
OUTPUT_AAR=$1
MANIFEST_FILE=$2
RESOURCE_FILES=($3)
RESOURCE_ROOT=$4
SCRATCH=$(basename "${OUTPUT_AAR}")
SCRATCH=${SCRATCH//".aar"}

rm -rf "${SCRATCH}/"
mkdir -p "${SCRATCH}/"

# copy AndroidManifest.xml
cp "${MANIFEST_FILE}" "${SCRATCH}/"

# copy resource files
for RESOURCE_FILE in "${RESOURCE_FILES[@]}"
do
  DEST="${SCRATCH}/res${RESOURCE_FILE//$RESOURCE_ROOT/}"
  mkdir -p  $(dirname "${DEST}") && cp "${RESOURCE_FILE}" "${DEST}"
done
#cp -r "${RESOURCE_ROOT}" "${SCRATCH}"

# generate R.txt and remove compiled resource files
#mkdir -p "${SCRATCH}/tmp/"
#${AAPT2} compile --output-text-symbols "${SCRATCH}/R.txt" -o "${SCRATCH}/tmp/" ${RESOURCE_FILES}
#rm -rf "${SCRATCH}/tmp/"

cd "${SCRATCH}" && zip -r --symlinks "${ROOT}/${OUTPUT_AAR}" ./*

