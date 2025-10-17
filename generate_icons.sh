#!/bin/bash

# Script to generate Android app icons from SVG
# Requires: inkscape or imagemagick

echo "🎨 Generating Android App Icons from icon.svg"

# Check if icon.svg exists
if [ ! -f "icon.svg" ]; then
    echo "❌ Error: icon.svg not found in current directory"
    exit 1
fi

# Check if inkscape is available (preferred for SVG)
if command -v inkscape &> /dev/null; then
    CONVERTER="inkscape"
    echo "✓ Using Inkscape for conversion"
elif command -v convert &> /dev/null; then
    CONVERTER="imagemagick"
    echo "✓ Using ImageMagick for conversion"
else
    echo "❌ Error: Neither Inkscape nor ImageMagick found"
    echo "Install one of them:"
    echo "  Ubuntu/Debian: sudo apt-get install inkscape"
    echo "  macOS: brew install inkscape"
    exit 1
fi

# Define icon sizes for each density
declare -A SIZES=(
    ["mdpi"]=48
    ["hdpi"]=72
    ["xhdpi"]=96
    ["xxhdpi"]=144
    ["xxxhdpi"]=192
)

# Define output directory
RES_DIR="app/src/main/res"

# Function to generate icon using Inkscape
generate_with_inkscape() {
    local size=$1
    local output=$2
    inkscape icon.svg --export-type=png --export-filename="$output" -w $size -h $size
}

# Function to generate icon using ImageMagick
generate_with_imagemagick() {
    local size=$1
    local output=$2
    convert -background none icon.svg -resize ${size}x${size} "$output"
}

# Generate icons for each density
for density in "${!SIZES[@]}"; do
    size=${SIZES[$density]}
    output_dir="${RES_DIR}/mipmap-${density}"
    
    echo "📱 Generating ${density} (${size}x${size})..."
    
    # Create directory if it doesn't exist
    mkdir -p "$output_dir"
    
    # Generate ic_launcher.png
    if [ "$CONVERTER" = "inkscape" ]; then
        generate_with_inkscape $size "${output_dir}/ic_launcher.png"
    else
        generate_with_imagemagick $size "${output_dir}/ic_launcher.png"
    fi
    
    # Copy as ic_launcher_round.png (same icon for round)
    cp "${output_dir}/ic_launcher.png" "${output_dir}/ic_launcher_round.png"
    
    echo "  ✓ Created ic_launcher.png and ic_launcher_round.png"
done

# Generate Play Store icon (512x512)
echo "🏪 Generating Play Store icon (512x512)..."
if [ "$CONVERTER" = "inkscape" ]; then
    generate_with_inkscape 512 "play_store_icon.png"
else
    generate_with_imagemagick 512 "play_store_icon.png"
fi
echo "  ✓ Created play_store_icon.png"

# Generate high-res icon for adaptive icon foreground (432x432 for xxxhdpi)
echo "🎯 Generating adaptive icon foreground..."
if [ "$CONVERTER" = "inkscape" ]; then
    generate_with_inkscape 432 "${RES_DIR}/mipmap-xxxhdpi/ic_launcher_foreground.png"
    generate_with_inkscape 216 "${RES_DIR}/mipmap-xxhdpi/ic_launcher_foreground.png"
    generate_with_inkscape 144 "${RES_DIR}/mipmap-xhdpi/ic_launcher_foreground.png"
    generate_with_inkscape 108 "${RES_DIR}/mipmap-hdpi/ic_launcher_foreground.png"
    generate_with_inkscape 72 "${RES_DIR}/mipmap-mdpi/ic_launcher_foreground.png"
else
    generate_with_imagemagick 432 "${RES_DIR}/mipmap-xxxhdpi/ic_launcher_foreground.png"
    generate_with_imagemagick 216 "${RES_DIR}/mipmap-xxhdpi/ic_launcher_foreground.png"
    generate_with_imagemagick 144 "${RES_DIR}/mipmap-xhdpi/ic_launcher_foreground.png"
    generate_with_imagemagick 108 "${RES_DIR}/mipmap-hdpi/ic_launcher_foreground.png"
    generate_with_imagemagick 72 "${RES_DIR}/mipmap-mdpi/ic_launcher_foreground.png"
fi
echo "  ✓ Created adaptive icon foregrounds"

echo ""
echo "✅ Icon generation complete!"
echo ""
echo "📋 Generated files:"
echo "  • app/src/main/res/mipmap-mdpi/ic_launcher.png (48x48)"
echo "  • app/src/main/res/mipmap-hdpi/ic_launcher.png (72x72)"
echo "  • app/src/main/res/mipmap-xhdpi/ic_launcher.png (96x96)"
echo "  • app/src/main/res/mipmap-xxhdpi/ic_launcher.png (144x144)"
echo "  • app/src/main/res/mipmap-xxxhdpi/ic_launcher.png (192x192)"
echo "  • play_store_icon.png (512x512)"
echo ""
echo "🚀 Next steps:"
echo "  1. Rebuild your app: ./gradlew clean assembleDebug"
echo "  2. Install on device: ./gradlew installDebug"
echo "  3. Check the new icon!"
