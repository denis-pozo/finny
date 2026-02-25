#!/bin/bash

# Script to verify Java version matches project requirements

set -e

echo "🔍 Verifying Java environment..."
echo ""

# Expected version
EXPECTED_MAJOR_VERSION="21"
RECOMMENDED_VERSION="21.0.8"
EXPECTED_VENDOR="Amazon"

# Get actual version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
JAVA_VENDOR=$(java -version 2>&1 | grep -i "runtime" | head -n 1)

echo "Expected: Java $EXPECTED_MAJOR_VERSION.x ($EXPECTED_VENDOR Corretto)"
echo "Recommended: Java $RECOMMENDED_VERSION"
echo "Actual:   Java $JAVA_VERSION"
echo "Vendor:   $JAVA_VENDOR"
echo ""

# Check major version
MAJOR_VERSION=$(echo "$JAVA_VERSION" | cut -d. -f1)
if [ "$MAJOR_VERSION" != "$EXPECTED_MAJOR_VERSION" ]; then
    echo "❌ ERROR: Java major version mismatch!"
    echo "   Expected: $EXPECTED_MAJOR_VERSION.x.x"
    echo "   Got:      $JAVA_VERSION"
    echo ""
    echo "💡 Fix with SDKMAN:"
    echo "   sdk install java 21.0.8-amzn"
    echo "   sdk use java 21.0.8-amzn"
    exit 1
fi

echo "✅ Java major version correct (21.x)"

# Check if using Corretto
if echo "$JAVA_VENDOR" | grep -qi "corretto\|amazon"; then
    echo "✅ Using Amazon Corretto (same as CI)"
    echo ""
    # Check if on recommended version
    if echo "$JAVA_VERSION" | grep -q "^21.0.8"; then
        echo "✅ Using recommended version 21.0.8"
    else
        echo "ℹ️  Using Java $JAVA_VERSION (recommended: 21.0.8)"
        echo "   This is fine - any Java 21.x works"
    fi
else
    echo "⚠️  WARNING: Not using Amazon Corretto"
    echo "   CI uses Amazon Corretto, you're using a different vendor"
    echo ""
    echo "💡 Recommended: Install Amazon Corretto with SDKMAN:"
    echo "   sdk install java 21.0.8-amzn"
    echo "   sdk use java 21.0.8-amzn"
fi

echo ""
echo "📋 Gradle toolchain check:"
./gradlew --version | grep -A 3 "JVM:"

echo ""
echo "✅ Java environment verified!"
