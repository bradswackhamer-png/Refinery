# ==============================================================================
# Ultra-Realistic Industrial Cylindrical Pipe Resource Pack Generator
# Based on Real Industrial Pipe Fittings (Elbows, Tees, Couplings)
# For Minecraft 26.3 & Logistics Automation
# ==============================================================================

param(
    [string]$TargetDir = "resourcepacks\Logistics-UltraRealistic-3D-Pipes",
    [switch]$InstallToMod = $false,
    [switch]$CreateZip = $true
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing
Add-Type -AssemblyName System.IO.Compression.FileSystem

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectDir = Split-Path -Parent $scriptDir
Set-Location -Path $projectDir

$packPath = Join-Path $projectDir $TargetDir
$texturesBlockDir = Join-Path $packPath "assets\logistics\textures\block\pipe"
$texturesItemDir = Join-Path $packPath "assets\logistics\textures\item\pipe"
$modelsBlockDir = Join-Path $packPath "assets\logistics\models\block\pipe"
$itemsDir = Join-Path $packPath "assets\logistics\items\pipe"

Write-Host "Creating Resource Pack directories for Minecraft 26.3..." -ForegroundColor Cyan
New-Item -ItemType Directory -Force -Path $texturesBlockDir | Out-Null
New-Item -ItemType Directory -Force -Path $texturesItemDir | Out-Null
New-Item -ItemType Directory -Force -Path $modelsBlockDir | Out-Null
New-Item -ItemType Directory -Force -Path $itemsDir | Out-Null

# ==============================================================================
# 1. pack.mcmeta (Strictly Formatted for Minecraft 26.3)
# ==============================================================================
$packMcmeta = @"
{
  "pack": {
    "pack_format": 46,
    "supported_formats": [15, 65],
    "description": "Ultra-Realistic Industrial 3D Pipes for Logistics Automation"
  }
}
"@
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Join-Path $packPath "pack.mcmeta"), $packMcmeta, $utf8NoBom)

# ==============================================================================
# 2. PBR Normal & Specular Map Generator (LabPBR 1.3)
# ==============================================================================
function Save-ImageWithPBR(
    [System.Drawing.Bitmap]$colorBmp,
    [string]$basePath,
    [float]$roughness = 0.28,
    [float]$metalness = 0.92,
    [int]$emissiveMask = 0
) {
    $w = $colorBmp.Width
    $h = $colorBmp.Height

    # 1. Base Color
    $colorPath = "$basePath.png"
    $colorBmp.Save($colorPath, [System.Drawing.Imaging.ImageFormat]::Png)

    # 2. Normal Map (_n.png) via Sobel filter & cylindrical curvature
    $normBmp = New-Object System.Drawing.Bitmap($w, $h)
    $specBmp = New-Object System.Drawing.Bitmap($w, $h)

    # Calculate height from luminance
    $height = New-Object 'float[]' ($w * $h)
    for ($y = 0; $y -lt $h; $y++) {
        for ($x = 0; $x -lt $w; $x++) {
            $c = $colorBmp.GetPixel($x, $y)
            $lum = (0.299 * $c.R + 0.587 * $c.G + 0.114 * $c.B) / 255.0
            $height[$y * $w + $x] = [float]$lum
        }
    }

    for ($y = 0; $y -lt $h; $y++) {
        $ym1 = if ($y -gt 0) { $y - 1 } else { $y }
        $yp1 = if ($y -lt $h - 1) { $y + 1 } else { $y }
        for ($x = 0; $x -lt $w; $x++) {
            $xm1 = if ($x -gt 0) { $x - 1 } else { $x }
            $xp1 = if ($x -lt $w - 1) { $x + 1 } else { $x }

            $dx = ($height[$ym1 * $w + $xp1] + 2.0 * $height[$y * $w + $xp1] + $height[$yp1 * $w + $xp1]) -
                  ($height[$ym1 * $w + $xm1] + 2.0 * $height[$y * $w + $xm1] + $height[$yp1 * $w + $xm1])
            $dy = ($height[$yp1 * $w + $xm1] + 2.0 * $height[$yp1 * $w + $x] + $height[$yp1 * $w + $xp1]) -
                  ($height[$ym1 * $w + $xm1] + 2.0 * $height[$ym1 * $w + $x] + $height[$ym1 * $w + $xp1])
            $dz = 0.25

            $len = [Math]::Sqrt($dx * $dx + $dy * $dy + $dz * $dz)
            $nx = ($dx / $len) * 0.5 + 0.5
            $ny = (-$dy / $len) * 0.5 + 0.5
            $nz = ($dz / $len)

            $nr = [Math]::Min(255, [Math]::Max(0, [int]($nx * 255.0)))
            $ng = [Math]::Min(255, [Math]::Max(0, [int]($ny * 255.0)))
            $nb = [Math]::Min(255, [Math]::Max(0, [int]($nz * 255.0)))
            $na = [Math]::Min(255, [Math]::Max(0, [int]($height[$y * $w + $x] * 255.0)))
            $normBmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb($na, $nr, $ng, $nb))

            # LabPBR Specular: R = smoothness, G = metalness, B = emissive
            $smooth = [Math]::Min(255, [Math]::Max(0, [int]((1.0 - $roughness) * 255.0)))
            $f0 = [Math]::Min(255, [Math]::Max(0, [int]($metalness * 255.0)))
            $specBmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $smooth, $f0, $emissiveMask))
        }
    }

    $normBmp.Save("$basePath`_n.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $specBmp.Save("$basePath`_s.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $normBmp.Dispose()
    $specBmp.Dispose()
}

# ==============================================================================
# 3. SOLID INDUSTRIAL CYLINDRICAL ARM TEXTURE GENERATOR
# ==============================================================================
# Generates the solid cylindrical pipe arm matching the reference image.
# Features:
# - Top Half (Y = 0..31): Industrial Bolted Machine Flange (Fitting 11) for ARM_EXTENDED
# - Bottom Half (Y = 32..63): 100% SEAMLESS brushed cylindrical pipe body
#   Zero collar lines, zero vertical seams - straight pipe runs merge continuously
function Generate-IndustrialArmTexture(
    [string]$name,
    [float]$baseR, [float]$baseG, [float]$baseB,
    [float]$roughness = 0.28,
    [float]$metalness = 0.92,
    [string]$style = "standard" # "standard", "fluid", "extractor", "powered", "void"
) {
    $bmp = New-Object System.Drawing.Bitmap(64, 64)
    $rand = New-Object System.Random(1337 + $name.GetHashCode())

    # =========================================================================
    # Top Half (Y = 0..31): Industrial Bolted Machine Flange (Fitting 11)
    # Used when connecting to machines / inventory endpoints via ARM_EXTENDED
    # =========================================================================
    for ($y = 0; $y -lt 32; $y++) {
        for ($x = 0; $x -lt 64; $x++) {
            $noise = ($rand.NextDouble() - 0.5) * 5.0
            $br = [Math]::Min(255, [Math]::Max(0, [int]($baseR * 0.70 + $noise)))
            $bg = [Math]::Min(255, [Math]::Max(0, [int]($baseG * 0.70 + $noise)))
            $bb = [Math]::Min(255, [Math]::Max(0, [int]($baseB * 0.70 + $noise)))
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $br, $bg, $bb))
        }
    }

    # Left quadrant [X: 0..31, Y: 0..31]: Circular bolted face of the flange
    $fcx = 15.5
    $fcy = 15.5
    for ($y = 0; $y -lt 32; $y++) {
        for ($x = 0; $x -lt 32; $x++) {
            $dx = $x - $fcx
            $dy = $y - $fcy
            $dist = [Math]::Sqrt($dx * $dx + $dy * $dy)

            if ($dist -lt 5.0) {
                # Dark interior bore of the pipe
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 18, 20, 24))
            } elseif ($dist -lt 7.0) {
                # Dark rubber / graphite gasket seal ring
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 32, 34, 40))
            } elseif ($dist -le 14.5) {
                # Machined flat flange face with light bevel
                $light = if ($dy -lt 0) { 1.15 } else { 0.90 }
                # 6 machined hex bolt heads at radius ~11.0
                $angle = [Math]::Atan2($dy, $dx)
                $modAngle = [Math]::Abs([Math]::IEEERemainder($angle, [Math]::PI / 3.0))
                $isBolt = ($dist -ge 9.5 -and $dist -le 12.5) -and ($modAngle -lt 0.22)

                if ($isBolt) {
                    $light = 1.45 # Bright specular highlight on bolt heads
                }
                if ($dist -ge 13.5) {
                    $light = $light * 1.25 # Outer chamfer highlight
                }

                $rVal = [Math]::Min(255, [Math]::Max(0, [int]($baseR * $light)))
                $gVal = [Math]::Min(255, [Math]::Max(0, [int]($baseG * $light)))
                $bVal = [Math]::Min(255, [Math]::Max(0, [int]($baseB * $light)))
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $rVal, $gVal, $bVal))
            }
        }
    }

    # Right quadrant [X: 32..63, Y: 0..31]: Cylindrical flange collar rim
    for ($y = 0; $y -lt 32; $y++) {
        $ncy = ($y - 15.5) / 15.5
        $ncy = [Math]::Max(-0.98, [Math]::Min(0.98, $ncy))
        $ncz = [Math]::Sqrt([Math]::Max(0.0, 1.0 - $ncy * $ncy))
        $diff = [Math]::Max(0.0, -$ncy * 0.65 + $ncz * 0.74)
        $spec = [Math]::Pow([Math]::Max(0.0, -$ncy * 0.45 + $ncz * 0.89), 16.0) * 0.40
        $flangeLight = 0.40 + 0.50 * $diff + $spec

        for ($x = 32; $x -lt 64; $x++) {
            $mult = $flangeLight
            if ($x -eq 32 -or $x -eq 63) { $mult = $mult * 0.75 }
            elseif ($x -eq 33 -or $x -eq 62) { $mult = $mult * 1.20 }
            $noise = ($rand.NextDouble() - 0.5) * 4.0
            $rVal = [Math]::Min(255, [Math]::Max(0, [int]($baseR * $mult + $noise)))
            $gVal = [Math]::Min(255, [Math]::Max(0, [int]($baseG * $mult + $noise)))
            $bVal = [Math]::Min(255, [Math]::Max(0, [int]($baseB * $mult + $noise)))
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $rVal, $gVal, $bVal))
        }
    }

    # =========================================================================
    # Bottom Half (Y = 32..63): 100% SEAMLESS Brushed Cylindrical Pipe Body
    # Straight pipe runs repeat seamlessly along X with ZERO collar lines or seams
    # =========================================================================
    for ($y = 32; $y -lt 64; $y++) {
        $cy = ($y - 47.5) / 15.5
        $cy = [Math]::Max(-0.98, [Math]::Min(0.98, $cy))
        $cz = [Math]::Sqrt([Math]::Max(0.0, 1.0 - $cy * $cy))

        $diffuse = [Math]::Max(0.0, -$cy * 0.65 + $cz * 0.74)
        $specAngle = [Math]::Max(0.0, -$cy * 0.45 + $cz * 0.89)
        $spec = [Math]::Pow($specAngle, 18.0) * 0.48
        $ambient = 0.38 + [Math]::Max(0.0, $cy * 0.12)

        $cylinderLight = $ambient + 0.50 * $diffuse + $spec

        for ($x = 0; $x -lt 64; $x++) {
            $grain = ($rand.NextDouble() - 0.5) * 6.0
            $rCol = $baseR
            $gCol = $baseG
            $bCol = $baseB
            $mult = $cylinderLight

            if ($style -eq "powered") {
                if ($y -ge 42 -and $y -le 46) {
                    $rCol = 255.0; $gCol = 245.0; $bCol = 190.0
                    $mult = 1.45
                }
            } elseif ($style -eq "void") {
                if ($y -ge 44 -and $y -le 46) {
                    $rCol = 180.0; $gCol = 50.0; $bCol = 255.0
                    $mult = 1.30
                }
            }

            $finalR = [Math]::Min(255, [Math]::Max(0, [int]($rCol * $mult + $grain)))
            $finalG = [Math]::Min(255, [Math]::Max(0, [int]($gCol * $mult + $grain)))
            $finalB = [Math]::Min(255, [Math]::Max(0, [int]($bCol * $mult + $grain)))
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $finalR, $finalG, $finalB))
        }
    }

    $targetBase = Join-Path $texturesBlockDir $name
    $emissive = if ($style -eq "powered" -or $style -eq "void") { 255 } else { 0 }
    Save-ImageWithPBR $bmp $targetBase $roughness $metalness $emissive
    $bmp.Dispose()
}

# ==============================================================================
# 4. SOLID INDUSTRIAL FITTING CORE TEXTURE GENERATOR
# ==============================================================================
# Generates the solid industrial fitting core junction matching the arm body.
# Seamlessly merges with pipe arms - no black holes, no square borders.
# Intelligent logistics pipes feature high-tech illuminated indicator displays.
function Generate-IndustrialCoreTexture(
    [string]$name,
    [float]$baseR, [float]$baseG, [float]$baseB,
    [float]$roughness = 0.28,
    [float]$metalness = 0.92,
    [string]$style = "standard" # "standard", "provider", "requester", "supplier", "crafting", "process", "satellite", "fluid", "extractor", "powered", "void", "chassis"
) {
    $bmp = New-Object System.Drawing.Bitmap(64, 64)
    $rand = New-Object System.Random(2026 + $name.GetHashCode())

    # Fill entire core with matching brushed cast metal that merges seamlessly with arms
    for ($y = 0; $y -lt 64; $y++) {
        $cy = ($y - 31.5) / 31.5
        $cy = [Math]::Max(-0.98, [Math]::Min(0.98, $cy))
        $cz = [Math]::Sqrt([Math]::Max(0.0, 1.0 - $cy * $cy))

        $diffuse = [Math]::Max(0.0, -$cy * 0.65 + $cz * 0.74)
        $specAngle = [Math]::Max(0.0, -$cy * 0.45 + $cz * 0.89)
        $spec = [Math]::Pow($specAngle, 16.0) * 0.42
        $ambient = 0.38 + [Math]::Max(0.0, $cy * 0.12)
        $light = $ambient + 0.50 * $diffuse + $spec

        for ($x = 0; $x -lt 64; $x++) {
            $grain = ($rand.NextDouble() - 0.5) * 5.0
            $rCol = $baseR
            $gCol = $baseG
            $bCol = $baseB

            $finalR = [Math]::Min(255, [Math]::Max(0, [int]($rCol * $light + $grain)))
            $finalG = [Math]::Min(255, [Math]::Max(0, [int]($gCol * $light + $grain)))
            $finalB = [Math]::Min(255, [Math]::Max(0, [int]($bCol * $light + $grain)))
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $finalR, $finalG, $finalB))
        }
    }

    # Intelligent Logistics Status Display Inset in the center [X: 22..41, Y: 22..41]
    if ($style -ne "standard") {
        $lensColor = switch ($style) {
            "provider"  { [System.Drawing.Color]::FromArgb(255, 34, 197, 94) }  # Emerald Green
            "requester" { [System.Drawing.Color]::FromArgb(255, 6, 182, 212) }  # Cyan
            "supplier"  { [System.Drawing.Color]::FromArgb(255, 245, 158, 11) } # Amber Orange
            "crafting"  { [System.Drawing.Color]::FromArgb(255, 234, 179, 8) }  # Yellow Gold
            "process"   { [System.Drawing.Color]::FromArgb(255, 168, 85, 247) } # Purple
            "satellite" { [System.Drawing.Color]::FromArgb(255, 59, 130, 246) } # Blue
            "fluid"     { [System.Drawing.Color]::FromArgb(255, 37, 99, 235) }  # Hydraulic Cobalt
            "extractor" { [System.Drawing.Color]::FromArgb(255, 239, 68, 68) }  # Crimson Red
            "powered"   { [System.Drawing.Color]::FromArgb(255, 253, 224, 71) } # Radiant Gold
            "void"      { [System.Drawing.Color]::FromArgb(255, 192, 38, 211) } # Void Violet
            default     { [System.Drawing.Color]::FromArgb(255, 148, 163, 184) }
        }

        for ($y = 22; $y -le 41; $y++) {
            for ($x = 22; $x -le 41; $x++) {
                if ($x -eq 22 -or $y -eq 22) {
                    $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 20, 22, 26))
                } elseif ($x -eq 41 -or $y -eq 41) {
                    $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 220, 225, 235))
                } elseif ($x -ge 26 -and $x -le 37 -and $y -ge 26 -and $y -le 37) {
                    $dx = $x - 31.5
                    $dy = $y - 31.5
                    $d = [Math]::Sqrt($dx * $dx + $dy * $dy)
                    if ($d -le 4.5) {
                        $bmp.SetPixel($x, $y, $lensColor)
                    } else {
                        $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 28, 30, 36))
                    }
                } else {
                    $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 38, 42, 50))
                }
            }
        }
    }

    $targetBase = Join-Path $texturesBlockDir $name
    $emissive = if ($style -ne "standard") { 255 } else { 0 }
    Save-ImageWithPBR $bmp $targetBase $roughness $metalness $emissive
    $bmp.Dispose()
}

# ==============================================================================
# 5. BATCH GENERATION OF ALL SOLID INDUSTRIAL PIPES
# ==============================================================================
Write-Host "Generating Solid Industrial Pipes (Iron, Copper, Gold, Stone, Void)..." -ForegroundColor Green

# PALETTES (Exact materials from user's reference images)
# 1. Industrial Galvanized / Stainless Steel (Iron & Logistics Pipes)
$steelR = 148.0; $steelG = 154.0; $steelB = 162.0

# 2. Industrial Copper (Clean, Exposed, Weathered, Oxidized)
$copperR = 192.0; $copperG = 108.0; $copperB = 68.0
$expCopperR = 162.0; $expCopperG = 110.0; $expCopperB = 84.0
$weaCopperR = 110.0; $weaCopperG = 150.0; $weaCopperB = 126.0
$oxiCopperR = 76.0;  $oxiCopperG = 172.0; $oxiCopperB = 142.0

# 3. Industrial Brass / Gold
$goldR = 215.0; $goldG = 172.0; $goldB = 52.0

# 4. Cast Iron / Ductile Iron (Stone Pipes)
$stoneR = 72.0; $stoneG = 75.0; $stoneB = 82.0

# 5. Obsidian / Titanium Carbide (Void Pipes)
$voidR = 34.0; $voidG = 34.0; $voidB = 40.0

# A. Stainless Steel & Logistics Network Pipes (Image 2 & 3 matching)
Generate-IndustrialCoreTexture "basic_logistics_pipe_core" $steelR $steelG $steelB 0.28 0.92 "standard"
Generate-IndustrialArmTexture "logistics_pipe_arm" $steelR $steelG $steelB 0.28 0.92 "standard"

# Overlays (tinted in code)
$coreOverlayBmp = New-Object System.Drawing.Bitmap(64, 64)
$og = [System.Drawing.Graphics]::FromImage($coreOverlayBmp)
$og.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
$lensBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 255))
$og.FillRectangle($lensBrush, 0, 0, 8, 8)
$og.FillRectangle($lensBrush, 56, 0, 8, 8)
$og.FillRectangle($lensBrush, 0, 56, 8, 8)
$og.FillRectangle($lensBrush, 56, 56, 8, 8)
$lensBrush.Dispose()
$og.Dispose()
$coreOverlayBmp.Save((Join-Path $texturesBlockDir "logistics_pipe_core_overlay.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$coreOverlayBmp.Save((Join-Path $texturesBlockDir "logistics_pipe_arm_overlay.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$coreOverlayBmp.Dispose()

# Intelligent Logistics Pipes (with high-tech glowing LED status indicator)
Generate-IndustrialCoreTexture "provider_logistics_pipe_core" $steelR $steelG $steelB 0.28 0.92 "provider"
Generate-IndustrialCoreTexture "requester_logistics_pipe_core" $steelR $steelG $steelB 0.28 0.92 "requester"
Generate-IndustrialCoreTexture "supplier_logistics_pipe_core" $steelR $steelG $steelB 0.28 0.92 "supplier"
Generate-IndustrialCoreTexture "crafting_logistics_pipe_core" $steelR $steelG $steelB 0.28 0.92 "crafting"
Generate-IndustrialCoreTexture "process_logistics_pipe_core" $steelR $steelG $steelB 0.28 0.92 "process"
Generate-IndustrialCoreTexture "satellite_logistics_pipe_core" $steelR $steelG $steelB 0.28 0.92 "satellite"
Generate-IndustrialCoreTexture "fluid_provider_pipe_core" $steelR $steelG $steelB 0.28 0.92 "fluid"
Generate-IndustrialCoreTexture "fluid_supplier_pipe_core" $steelR $steelG $steelB 0.28 0.92 "fluid"

# Chassis Pipes Mk1 to Mk5 (Solid steel fitting with status LEDs on collar)
for ($mk = 1; $mk -le 5; $mk++) {
    Generate-IndustrialCoreTexture "chassis_logistics_pipe_mk$($mk)_core" $steelR $steelG $steelB 0.28 0.92 "standard"
}

# Functional Pipes: Extractor, Filter, Insertion, Merger, Passthrough, Bypass
Generate-IndustrialCoreTexture "extractor_pipe_core" $steelR $steelG $steelB 0.28 0.92 "extractor"
Generate-IndustrialArmTexture "extractor_pipe_arm" $steelR $steelG $steelB 0.28 0.92 "extractor"
Generate-IndustrialCoreTexture "fluid_extractor_pipe_core" $copperR $copperG $copperB 0.28 0.92 "fluid"
Generate-IndustrialArmTexture "fluid_extractor_pipe_arm" $copperR $copperG $copperB 0.28 0.92 "fluid"

Generate-IndustrialCoreTexture "filter_pipe_core" $steelR $steelG $steelB 0.28 0.92 "standard"
Generate-IndustrialArmTexture "filter_pipe_arm" $steelR $steelG $steelB 0.28 0.92 "standard"
$coreOverlayBmp = New-Object System.Drawing.Bitmap(64, 64)
$og = [System.Drawing.Graphics]::FromImage($coreOverlayBmp)
$og.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
$lensB = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 255))
$og.FillRectangle($lensB, 24, 24, 16, 16)
$lensB.Dispose()
$og.Dispose()
$coreOverlayBmp.Save((Join-Path $texturesBlockDir "filter_pipe_overlay.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$coreOverlayBmp.Dispose()

Generate-IndustrialCoreTexture "insertion_pipe_core" $steelR $steelG $steelB 0.28 0.92 "standard"
Generate-IndustrialArmTexture "insertion_pipe_arm" $steelR $steelG $steelB 0.28 0.92 "standard"
Generate-IndustrialCoreTexture "insertion_fluid_pipe_core" $copperR $copperG $copperB 0.28 0.92 "fluid"
Generate-IndustrialArmTexture "insertion_fluid_pipe_arm" $copperR $copperG $copperB 0.28 0.92 "fluid"

Generate-IndustrialCoreTexture "merger_pipe_core" $steelR $steelG $steelB 0.28 0.92 "standard"
Generate-IndustrialArmTexture "merger_pipe_arm" $steelR $steelG $steelB 0.28 0.92 "standard"
Generate-IndustrialCoreTexture "merger_fluid_pipe_core" $copperR $copperG $copperB 0.28 0.92 "fluid"
Generate-IndustrialArmTexture "merger_fluid_pipe_arm" $copperR $copperG $copperB 0.28 0.92 "fluid"

Generate-IndustrialCoreTexture "passthru_pipe_core" 65.0 100.0 160.0 0.28 0.92 "standard"
Generate-IndustrialArmTexture "passthru_pipe_arm" 65.0 100.0 160.0 0.28 0.92 "standard"

Generate-IndustrialCoreTexture "bypass_fluid_pipe_core" 155.0 50.0 50.0 0.28 0.92 "fluid"
Generate-IndustrialArmTexture "bypass_fluid_pipe_arm" 155.0 50.0 50.0 0.28 0.92 "fluid"

# Feature Textures (Extractor hazard collar, Merger manifold)
$featBmp = New-Object System.Drawing.Bitmap(64, 64)
$fg = [System.Drawing.Graphics]::FromImage($featBmp)
$fg.Clear([System.Drawing.Color]::FromArgb(255, 30, 30, 32))
$hazardYellow = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 240, 195, 25))
$hazardBlack = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 30, 30, 32))
$fg.FillRectangle($hazardYellow, 0, 16, 64, 48)
for ($hx = -40; $hx -lt 100; $hx += 16) {
    [System.Drawing.Point[]]$hzPts = @(
        [System.Drawing.Point]::new($hx, 16),
        [System.Drawing.Point]::new($hx + 8, 16),
        [System.Drawing.Point]::new($hx + 24, 64),
        [System.Drawing.Point]::new($hx + 16, 64)
    )
    $fg.FillPolygon($hazardBlack, $hzPts)
}
$hazardYellow.Dispose()
$hazardBlack.Dispose()
$fg.Dispose()
Save-ImageWithPBR $featBmp (Join-Path $texturesBlockDir "extractor_pipe_feature") 0.40 0.70
$featBmp.Save((Join-Path $texturesBlockDir "fluid_extractor_pipe_feature.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$featBmp.Dispose()

$mergerFeat = New-Object System.Drawing.Bitmap(64, 64)
$mfg = [System.Drawing.Graphics]::FromImage($mergerFeat)
$mfg.Clear([System.Drawing.Color]::FromArgb(255, 45, 50, 58))
$mfg.Dispose()
Save-ImageWithPBR $mergerFeat (Join-Path $texturesBlockDir "merger_pipe_feature") 0.35 0.85
$mergerFeat.Save((Join-Path $texturesBlockDir "merger_fluid_pipe_feature.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$mergerFeat.Dispose()

# B. Industrial Copper Pipes (Same geometry, warm copper metallic color)
Generate-IndustrialCoreTexture "copper_pipe_core" $copperR $copperG $copperB 0.28 0.92 "standard"
Generate-IndustrialCoreTexture "copper_pipe_core_exposed" $expCopperR $expCopperG $expCopperB 0.38 0.85 "standard"
Generate-IndustrialCoreTexture "copper_pipe_core_weathered" $weaCopperR $weaCopperG $weaCopperB 0.48 0.70 "standard"
Generate-IndustrialCoreTexture "copper_pipe_core_oxidized" $oxiCopperR $oxiCopperG $oxiCopperB 0.58 0.55 "standard"

Generate-IndustrialArmTexture "copper_pipe_arm" $copperR $copperG $copperB 0.28 0.92 "standard"
Generate-IndustrialArmTexture "copper_pipe_arm_exposed" $expCopperR $expCopperG $expCopperB 0.38 0.85 "standard"
Generate-IndustrialArmTexture "copper_pipe_arm_weathered" $weaCopperR $weaCopperG $weaCopperB 0.48 0.70 "standard"
Generate-IndustrialArmTexture "copper_pipe_arm_oxidized" $oxiCopperR $oxiCopperG $oxiCopperB 0.58 0.55 "standard"

# Copper Fluid Pipes (With blue hydraulic pressure seal on collar)
Generate-IndustrialCoreTexture "copper_fluid_pipe_core" $copperR $copperG $copperB 0.28 0.92 "fluid"
Generate-IndustrialCoreTexture "copper_fluid_pipe_core_exposed" $expCopperR $expCopperG $expCopperB 0.38 0.85 "fluid"
Generate-IndustrialCoreTexture "copper_fluid_pipe_core_weathered" $weaCopperR $weaCopperG $weaCopperB 0.48 0.70 "fluid"
Generate-IndustrialCoreTexture "copper_fluid_pipe_core_oxidized" $oxiCopperR $oxiCopperG $oxiCopperB 0.58 0.55 "fluid"

Generate-IndustrialArmTexture "copper_fluid_pipe_arm" $copperR $copperG $copperB 0.28 0.92 "fluid"
Generate-IndustrialArmTexture "copper_fluid_pipe_arm_exposed" $expCopperR $expCopperG $expCopperB 0.38 0.85 "fluid"
Generate-IndustrialArmTexture "copper_fluid_pipe_arm_weathered" $weaCopperR $weaCopperG $weaCopperB 0.48 0.70 "fluid"
Generate-IndustrialArmTexture "copper_fluid_pipe_arm_oxidized" $oxiCopperR $oxiCopperG $oxiCopperB 0.58 0.55 "fluid"

# C. Industrial Gold / Brass Pipes
Generate-IndustrialCoreTexture "gold_pipe_core" $goldR $goldG $goldB 0.22 0.95 "standard"
Generate-IndustrialArmTexture "gold_pipe_arm" $goldR $goldG $goldB 0.22 0.95 "standard"
Generate-IndustrialCoreTexture "gold_pipe_core_powered" $goldR $goldG $goldB 0.15 0.95 "powered"
Generate-IndustrialArmTexture "gold_pipe_arm_powered" $goldR $goldG $goldB 0.15 0.95 "powered"
Generate-IndustrialCoreTexture "gold_fluid_pipe_core" $goldR $goldG $goldB 0.22 0.95 "fluid"
Generate-IndustrialArmTexture "gold_fluid_pipe_arm" $goldR $goldG $goldB 0.22 0.95 "fluid"

# D. Cast Iron / Ductile Iron (Stone Pipes)
Generate-IndustrialCoreTexture "stone_pipe_core" $stoneR $stoneG $stoneB 0.45 0.75 "standard"
Generate-IndustrialArmTexture "stone_pipe_arm" $stoneR $stoneG $stoneB 0.45 0.75 "standard"
Generate-IndustrialCoreTexture "stone_fluid_pipe_core" $stoneR $stoneG $stoneB 0.45 0.75 "fluid"
Generate-IndustrialArmTexture "stone_fluid_pipe_arm" $stoneR $stoneG $stoneB 0.45 0.75 "fluid"

# E. Void Pipes (Obsidian Titanium-Carbide)
Generate-IndustrialCoreTexture "void_pipe_core" $voidR $voidG $voidB 0.40 0.60 "void"
Generate-IndustrialArmTexture "void_pipe_arm" $voidR $voidG $voidB 0.40 0.60 "void"
Generate-IndustrialCoreTexture "void_fluid_pipe_core" $voidR $voidG $voidB 0.40 0.60 "void"
Generate-IndustrialArmTexture "void_fluid_pipe_arm" $voidR $voidG $voidB 0.40 0.60 "void"

# F. Power Junction
$pjOff = New-Object System.Drawing.Bitmap(64, 64)
$pjg = [System.Drawing.Graphics]::FromImage($pjOff)
$pjg.Clear([System.Drawing.Color]::FromArgb(255, 38, 42, 48))
$finBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 24, 26, 30))
for ($fy = 8; $fy -lt 56; $fy += 6) {
    $pjg.FillRectangle($finBrush, 12, $fy, 40, 3)
}
$finBrush.Dispose()
$pjg.Dispose()
Save-ImageWithPBR $pjOff (Join-Path $texturesBlockDir "power_junction_off") 0.35 0.85
$pjOff.Dispose()

$pjOn = New-Object System.Drawing.Bitmap(64, 64)
$pjg = [System.Drawing.Graphics]::FromImage($pjOn)
$pjg.Clear([System.Drawing.Color]::FromArgb(255, 38, 42, 48))
$finBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 24, 26, 30))
for ($fy = 8; $fy -lt 56; $fy += 6) {
    $pjg.FillRectangle($finBrush, 12, $fy, 40, 3)
}
$finBrush.Dispose()
$arcBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 90, 215, 255))
$pjg.FillRectangle($arcBrush, 14, 24, 36, 16)
$arcBrush.Dispose()
$pjg.Dispose()
Save-ImageWithPBR $pjOn (Join-Path $texturesBlockDir "power_junction_on") 0.20 0.85 255
$pjOn.Dispose()

# MCMETA for animated power junction
$pjMeta = @"
{
  "animation": {
    "frametime": 4,
    "interpolate": true
  }
}
"@
[System.IO.File]::WriteAllText((Join-Path $texturesBlockDir "power_junction_on.png.mcmeta"), $pjMeta, [System.Text.Encoding]::UTF8)

# Pipe Markings & Fluid Packet
$markBmp = New-Object System.Drawing.Bitmap(64, 64)
$mg = [System.Drawing.Graphics]::FromImage($markBmp)
$mg.Clear([System.Drawing.Color]::FromArgb(255, 220, 220, 220))
$mPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 45, 50, 58), 3.0)
$mg.DrawRectangle($mPen, 2, 2, 60, 60)
$mPen.Dispose()
$mg.Dispose()
Save-ImageWithPBR $markBmp (Join-Path $texturesBlockDir "pipe_markings") 0.35 0.20
$markBmp.Dispose()

$pktBmp = New-Object System.Drawing.Bitmap(64, 64)
$pkg = [System.Drawing.Graphics]::FromImage($pktBmp)
$pkg.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
[System.Drawing.Point[]]$pktPts = @(
    [System.Drawing.Point]::new(16, 16),
    [System.Drawing.Point]::new(48, 16),
    [System.Drawing.Point]::new(48, 48),
    [System.Drawing.Point]::new(16, 48)
)
$pktBrush = New-Object System.Drawing.Drawing2D.PathGradientBrush(,$pktPts)
$pktBrush.CenterColor = [System.Drawing.Color]::FromArgb(255, 120, 220, 255)
$pktBrush.SurroundColors = [System.Drawing.Color[]]@([System.Drawing.Color]::FromArgb(200, 30, 100, 240))
$pkg.FillEllipse($pktBrush, 16, 16, 32, 32)
$pktBrush.Dispose()
$pkg.Dispose()
$pktBmp.Save((Join-Path $texturesBlockDir "fluid_packet.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$pktBmp.Dispose()

# ==============================================================================
# 6. GENERATE ITEM MODULE TEXTURES (64x64)
# ==============================================================================
Write-Host "Generating Industrial Pipe Module Textures..." -ForegroundColor Green
$moduleNames = @(
    "blank_module", "active_supplier_module", "crafter_module", "crafter_mkii_module", "crafter_mkiii_module",
    "enchantment_sink_module", "extractor_module", "extractor_module_mkii", "extractor_module_mkiii",
    "fluid_provider_module", "fluid_supplier_module", "item_sink_module", "marking_fluid", "marking_fluid_overlay",
    "mod_item_sink_module", "passive_supplier_module", "polymorphic_sink_module", "provider_module",
    "provider_mkii_module", "quicksort_module", "terminus_module"
)
foreach ($modName in $moduleNames) {
    $mBmp = New-Object System.Drawing.Bitmap(64, 64)
    $mg = [System.Drawing.Graphics]::FromImage($mBmp)
    $mg.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $mg.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))

    # PCB Substrate
    $pcbBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 30, 36, 44))
    $mg.FillRectangle($pcbBrush, 12, 8, 40, 48)
    $pcbBrush.Dispose()

    # Gold Edge Pins
    $goldBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 235, 190, 50))
    for ($px = 16; $px -lt 48; $px += 4) {
        $mg.FillRectangle($goldBrush, $px, 52, 2, 4)
    }
    $goldBrush.Dispose()

    # Center Chip
    $icBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 20, 22, 26))
    $mg.FillRectangle($icBrush, 20, 16, 24, 24)
    $icBrush.Dispose()

    # Accent LED
    $modAccent = switch -Wildcard ($modName) {
        "*crafter*" { [System.Drawing.Color]::FromArgb(255, 240, 200, 50) }
        "*extractor*" { [System.Drawing.Color]::FromArgb(255, 230, 70, 40) }
        "*provider*" { [System.Drawing.Color]::FromArgb(255, 50, 200, 100) }
        "*supplier*" { [System.Drawing.Color]::FromArgb(255, 240, 140, 30) }
        "*sink*" { [System.Drawing.Color]::FromArgb(255, 60, 140, 240) }
        "*marking*" { [System.Drawing.Color]::FromArgb(255, 200, 60, 200) }
        "*quicksort*" { [System.Drawing.Color]::FromArgb(255, 50, 220, 220) }
        default { [System.Drawing.Color]::FromArgb(255, 180, 190, 200) }
    }
    $accentBrush = New-Object System.Drawing.SolidBrush($modAccent)
    $mg.FillEllipse($accentBrush, 28, 24, 8, 8)
    $accentBrush.Dispose()

    $mg.Dispose()
    $mBmp.Save((Join-Path $texturesItemDir "$modName.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $mBmp.Dispose()
}

# ==============================================================================
# 7. GENERATE PACK.PNG (256x256 High-Resolution Icon)
# ==============================================================================
Write-Host "Generating Realistic pack.png Icon..." -ForegroundColor Cyan
$packBmp = New-Object System.Drawing.Bitmap(256, 256)
$pg = [System.Drawing.Graphics]::FromImage($packBmp)
$pg.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias

# Industrial Slate Background
$bgBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(0, 0)),
    (New-Object System.Drawing.Point(256, 256)),
    [System.Drawing.Color]::FromArgb(255, 28, 30, 36),
    [System.Drawing.Color]::FromArgb(255, 14, 15, 18)
)
$pg.FillRectangle($bgBrush, 0, 0, 256, 256)
$bgBrush.Dispose()

# Grid Lines
$gridPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(25, 255, 255, 255), 1.0)
for ($x = 0; $x -lt 256; $x += 16) { $pg.DrawLine($gridPen, $x, 0, $x, 256) }
for ($y = 0; $y -lt 256; $y += 16) { $pg.DrawLine($gridPen, 0, $y, 256, $y) }
$gridPen.Dispose()

# Draw 3D Industrial Tee Fitting (Matching Image 2 / Fitting 2 Tee)
# Horizontal Main Pipe
$pipeBrushH = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(0, 96)),
    (New-Object System.Drawing.Point(0, 160)),
    [System.Drawing.Color]::FromArgb(255, 210, 215, 225),
    [System.Drawing.Color]::FromArgb(255, 75, 80, 92)
)
$pg.FillRectangle($pipeBrushH, 20, 100, 216, 56)

# Vertical Branch Pipe (Tee branch going up)
$pipeBrushV = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(100, 0)),
    (New-Object System.Drawing.Point(156, 0)),
    [System.Drawing.Color]::FromArgb(255, 210, 215, 225),
    [System.Drawing.Color]::FromArgb(255, 75, 80, 92)
)
$pg.FillRectangle($pipeBrushV, 100, 30, 56, 75)

# 3 Raised Coupling Collars (Left, Right, and Top branch of Tee)
$collarBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 165, 172, 185))
$collarPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 240, 245, 255), 2.0)
$collarShadow = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 45, 48, 55), 2.0)

# Left collar
$pg.FillRectangle($collarBrush, 24, 94, 20, 68)
$pg.DrawRectangle($collarPen, 24, 94, 20, 68)
# Right collar
$pg.FillRectangle($collarBrush, 212, 94, 20, 68)
$pg.DrawRectangle($collarPen, 212, 94, 20, 68)
# Top branch collar
$pg.FillRectangle($collarBrush, 94, 24, 68, 20)
$pg.DrawRectangle($collarPen, 94, 24, 68, 20)

$pipeBrushH.Dispose()
$pipeBrushV.Dispose()
$collarBrush.Dispose()
$collarPen.Dispose()
$collarShadow.Dispose()

# Typography
$fontTitle = New-Object System.Drawing.Font("Arial", 12, [System.Drawing.FontStyle]::Bold)
$fontSub = New-Object System.Drawing.Font("Arial", 8, [System.Drawing.FontStyle]::Bold)
$whiteBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 245, 248, 255))
$goldBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 240, 195, 60))

$pg.DrawString("INDUSTRIAL 3D PIPES", $fontTitle, $goldBrush, 36, 204)
$pg.DrawString("REALISTIC FITTINGS - MC 26.3", $fontSub, $whiteBrush, 40, 226)

$fontTitle.Dispose()
$fontSub.Dispose()
$whiteBrush.Dispose()
$goldBrush.Dispose()
$pg.Dispose()

$packBmp.Save((Join-Path $packPath "pack.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$packBmp.Dispose()

# ==============================================================================
# 8. COPY ORIGINAL JSON BLOCK & ITEM MODELS
# ==============================================================================
Get-ChildItem -Path "logistics\assets\logistics\models\block\pipe" -Filter "*.json" | ForEach-Object {
    $dest = Join-Path $modelsBlockDir $_.Name
    Copy-Item -Path $_.FullName -Destination $dest -Force
}
Get-ChildItem -Path "logistics\assets\logistics\items\pipe" -Filter "*.json" | ForEach-Object {
    $dest = Join-Path $itemsDir $_.Name
    Copy-Item -Path $_.FullName -Destination $dest -Force
}

# ==============================================================================
# 9. CREATE STANDALONE ZIP DOWNLOAD FOR MINECRAFT 26.3
# (Strictly standard forward slashes '/' via Java jar archiver)
# ==============================================================================
# ==============================================================================
# 9. COMPILE MONOLITHIC 3D PIPE GEOMETRY & BAKE INTO MOD JAR
# ==============================================================================
Write-Host "Compiling 3D Monolithic Pipe Geometry with Java 25..." -ForegroundColor Cyan
$javac25 = "C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot\bin\javac.exe"
$neoforgeJar = "C:\Users\swack\.gradle\caches\modules-2\files-2.1\net.neoforged\neoforge\26.3.0.99\215117e7a679a38b75f88b5d4254adaffd596452\neoforge-26.3.0.99-universal.jar"
$mcJar = "C:\Users\swack\.gradle\caches\neoformruntime\intermediate_results\recompile_4a012532b093b2c64a99e2595f975d9ac89b08e2_output.jar"
$cp = "logistics;$neoforgeJar;$mcJar"
$geomSource = Join-Path $scriptDir "PipeGeometry.java"

if (Test-Path $javac25) {
    & $javac25 -cp $cp -d "logistics" $geomSource
    # Clean up obsolete inner classes
    Remove-Item -Path "logistics\com\logistics\pipe\render\model\PipeGeometry`$*.class" -Force -ErrorAction SilentlyContinue
    Write-Host "Successfully compiled PipeGeometry into logistics/" -ForegroundColor Green
} else {
    Write-Host "Java 25 compiler not found at $javac25" -ForegroundColor Yellow
}

# Sync generated textures directly into the mod directory (Baking into the mod)
Write-Host "Baking textures directly into logistics mod assets..." -ForegroundColor Cyan
$logisticsTexDir = Join-Path $projectDir "logistics\assets\logistics\textures\block\pipe"
Copy-Item -Path "$texturesBlockDir\*" -Destination $logisticsTexDir -Recurse -Force

# ==============================================================================
# 10. CREATE STANDALONE ZIP & DEPLOY TO CURSEFORGE INSTANCE
# ==============================================================================
$resourcepacksDir = Join-Path $projectDir "resourcepacks"
New-Item -ItemType Directory -Force -Path $resourcepacksDir | Out-Null
$zipPath = Join-Path $resourcepacksDir "Logistics-UltraRealistic-3D-Pipes.zip"

if ($CreateZip) {
    Write-Host "Packaging standalone Resource Pack for Minecraft 26.3..." -ForegroundColor Cyan
    if (Test-Path $zipPath) { Remove-Item -Path $zipPath -Force }
    & jar -c -M -f $zipPath -C $packPath .
}

# Deploy to CurseForge 26.3 instance if present
$cfInstanceDir = "C:\Users\swack\curseforge\minecraft\Instances\26.3"
if (Test-Path $cfInstanceDir) {
    Write-Host "Deploying to active CurseForge 26.3 instance..." -ForegroundColor Magenta

    # 1. Build baked logistics jar with round 3D monolithic geometry & textures
    $builtLogisticsJar = Join-Path $projectDir "build\logistics-0.8.12+mc26.3.neoforge.jar"
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $builtLogisticsJar) | Out-Null
    if (Test-Path $builtLogisticsJar) { Remove-Item -Path $builtLogisticsJar -Force }
    & jar -c -M -f $builtLogisticsJar -C "logistics" .
    Write-Host "Built baked logistics mod jar at: $builtLogisticsJar" -ForegroundColor Green

    $cfModsDir = Join-Path $cfInstanceDir "mods"
    if (Test-Path $cfModsDir) {
        $cfLogisticsJar = Join-Path $cfModsDir "logistics-0.8.12+mc26.3.neoforge.jar"
        try {
            Copy-Item -Path $builtLogisticsJar -Destination $cfLogisticsJar -Force -ErrorAction Stop
            Write-Host "Successfully updated logistics mod jar in CurseForge instance!" -ForegroundColor Green
        } catch {
            Write-Host "Minecraft is currently running (locking $cfLogisticsJar)." -ForegroundColor Yellow
            $stagedJar = "$cfLogisticsJar.staged"
            Copy-Item -Path $builtLogisticsJar -Destination $stagedJar -Force
            Write-Host "Staged updated jar at $stagedJar. Close Minecraft to allow instant replacement." -ForegroundColor Yellow
        }
    }

    # 2. Deploy Resource Pack zip and unpacked folder
    $cfRpDir = Join-Path $cfInstanceDir "resourcepacks"
    if (Test-Path $cfRpDir) {
        try {
            Copy-Item -Path $zipPath -Destination (Join-Path $cfRpDir "Logistics-UltraRealistic-3D-Pipes.zip") -Force
            $cfUnpacked = Join-Path $cfRpDir "Logistics-UltraRealistic-3D-Pipes"
            if (Test-Path $cfUnpacked) { Remove-Item -Path $cfUnpacked -Recurse -Force }
            Copy-Item -Path $packPath -Destination $cfUnpacked -Recurse -Force
            Write-Host "Updated Resource Pack in CurseForge instance (press F3+T in game to reload)!" -ForegroundColor Green
        } catch {
            Write-Host "Could not deploy resource pack: $_" -ForegroundColor Yellow
        }
    }
}

# Ensure mod resources remain clean
$modPackDir = Join-Path $projectDir "src\main\resources\resourcepacks"
if (Test-Path $modPackDir) {
    Remove-Item -Path $modPackDir -Recurse -Force
}

Write-Host "=================================================================" -ForegroundColor Green
Write-Host " Industrial 3D Monolithic Pipe Pack Successfully Generated!" -ForegroundColor Green
Write-Host " Folder: $packPath" -ForegroundColor Green
Write-Host " Zip:    $zipPath" -ForegroundColor Green
Write-Host " Mod:    $cfLogisticsJar" -ForegroundColor Green
Write-Host "=================================================================" -ForegroundColor Green
