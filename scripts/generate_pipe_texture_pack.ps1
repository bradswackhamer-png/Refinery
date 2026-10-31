# ==============================================================================
# Ultra-Realistic 3D Monolithic Pipe Resource Pack Generator
# For Logistics Automation (logistics mod)
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

Write-Host "Creating Resource Pack directories at: $packPath" -ForegroundColor Cyan
New-Item -ItemType Directory -Force -Path $texturesBlockDir | Out-Null
New-Item -ItemType Directory -Force -Path $texturesItemDir | Out-Null
New-Item -ItemType Directory -Force -Path $modelsBlockDir | Out-Null
New-Item -ItemType Directory -Force -Path $itemsDir | Out-Null

# ==============================================================================
# 1. pack.mcmeta & pack.png
# ==============================================================================
$packMcmeta = @"
{
  "pack": {
    "pack_format": 46,
    "supported_formats": [15, 65],
    "description": "Ultra-Realistic 3D Monolithic Pipes for Logistics Automation"
  }
}
"@
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Join-Path $packPath "pack.mcmeta"), $packMcmeta, $utf8NoBom)

# ==============================================================================
# Helper Drawing & PBR Functions
# ==============================================================================

function Save-ImageWithPBR(
    [System.Drawing.Bitmap]$colorBmp,
    [string]$basePath,
    [float]$roughness = 0.35,
    [float]$metalness = 0.9,
    [float[,]]$customHeight = $null,
    [int]$emissiveMask = 0
) {
    $w = $colorBmp.Width
    $h = $colorBmp.Height

    # 1. Save Base Color
    $colorPath = "$basePath.png"
    $colorBmp.Save($colorPath, [System.Drawing.Imaging.ImageFormat]::Png)

    # 2. Generate Height if not provided
    $height = if ($customHeight -ne $null) { $customHeight } else {
        $arr = New-Object 'float[]' ($w * $h)
        for ($y = 0; $y -lt $h; $y++) {
            for ($x = 0; $x -lt $w; $x++) {
                $c = $colorBmp.GetPixel($x, $y)
                $lum = (0.299 * $c.R + 0.587 * $c.G + 0.114 * $c.B) / 255.0
                $arr[$y * $w + $x] = [float]($lum * ($c.A / 255.0))
            }
        }
        $arr
    }

    # 3. Normal Map (_n.png) via Sobel filter
    $normBmp = New-Object System.Drawing.Bitmap($w, $h)
    $specBmp = New-Object System.Drawing.Bitmap($w, $h)

    for ($y = 0; $y -lt $h; $y++) {
        $ym1 = if ($y -gt 0) { $y - 1 } else { $y }
        $yp1 = if ($y -lt $h - 1) { $y + 1 } else { $y }
        for ($x = 0; $x -lt $w; $x++) {
            $xm1 = if ($x -gt 0) { $x - 1 } else { $x }
            $xp1 = if ($x -lt $w - 1) { $x + 1 } else { $x }

            $h_xp1_ym1 = $height[$ym1 * $w + $xp1]
            $h_xp1_y   = $height[$y * $w + $xp1]
            $h_xp1_yp1 = $height[$yp1 * $w + $xp1]

            $h_xm1_ym1 = $height[$ym1 * $w + $xm1]
            $h_xm1_y   = $height[$y * $w + $xm1]
            $h_xm1_yp1 = $height[$yp1 * $w + $xm1]

            $h_x_ym1   = $height[$ym1 * $w + $x]
            $h_x_yp1   = $height[$yp1 * $w + $x]

            $dx = ($h_xp1_ym1 + 2.0 * $h_xp1_y + $h_xp1_yp1) -
                  ($h_xm1_ym1 + 2.0 * $h_xm1_y + $h_xm1_yp1)
            $dy = ($h_xm1_yp1 + 2.0 * $h_x_yp1 + $h_xp1_yp1) -
                  ($h_xm1_ym1 + 2.0 * $h_x_ym1 + $h_xp1_ym1)
            $dz = 0.333333

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
            $origC = $colorBmp.GetPixel($x, $y)
            $pixRough = $roughness
            $pixMetal = $metalness
            $emissiveVal = 0

            # If pixel is transparent/glass
            if ($origC.A -eq 0) {
                $smooth = 0
                $f0 = 0
            } elseif ($origC.A -lt 100) {
                # Glass
                $smooth = 250
                $f0 = 50 # Dielectric glass
            } else {
                $smooth = [Math]::Min(255, [Math]::Max(0, [int]((1.0 - $pixRough) * 255.0)))
                $f0 = [Math]::Min(255, [Math]::Max(0, [int]($pixMetal * 255.0)))
            }

            if ($emissiveMask -gt 0) {
                $emissiveVal = $emissiveMask
            }

            $specBmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $smooth, $f0, $emissiveVal))
        }
    }

    $normBmp.Save("$basePath`_n.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $specBmp.Save("$basePath`_s.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $normBmp.Dispose()
    $specBmp.Dispose()
}

# Draw Hex Bolt / Socket Head Rivet
function Draw-Bolt([System.Drawing.Graphics]$g, [int]$cx, [int]$cy, [int]$radius, [System.Drawing.Color]$headColor, [System.Drawing.Color]$socketColor) {
    $rimPenLight = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(200, 255, 255, 255), 1.0)
    $rimPenDark = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(180, 0, 0, 0), 1.0)
    $headBrush = New-Object System.Drawing.SolidBrush($headColor)
    $socketBrush = New-Object System.Drawing.SolidBrush($socketColor)

    # Cast drop shadow
    $shadowBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(100, 0, 0, 0))
    $g.FillEllipse($shadowBrush, $cx - $radius, $cy - $radius + 1, $radius * 2, $radius * 2)

    # Bolt outer head
    $g.FillEllipse($headBrush, $cx - $radius, $cy - $radius, $radius * 2, $radius * 2)
    # Highlights & shadows
    $g.DrawArc($rimPenLight, $cx - $radius, $cy - $radius, $radius * 2, $radius * 2, 180, 180)
    $g.DrawArc($rimPenDark, $cx - $radius, $cy - $radius, $radius * 2, $radius * 2, 0, 180)

    # Recessed Allen hex socket in center
    $innerR = [Math]::Max(1, [int]($radius * 0.45))
    $g.FillRectangle($socketBrush, $cx - $innerR, $cy - $innerR, $innerR * 2, $innerR * 2)

    $rimPenLight.Dispose()
    $rimPenDark.Dispose()
    $headBrush.Dispose()
    $socketBrush.Dispose()
    $shadowBrush.Dispose()
}

# ==============================================================================
# 2. GENERATE CORE TEXTURES (64x64)
# ==============================================================================
Write-Host "Generating Ultra-Realistic 64x64 Monolithic Pipe Textures..." -ForegroundColor Green

function Generate-CoreTexture(
    [string]$name,
    [System.Drawing.Color]$baseCol,
    [System.Drawing.Color]$accentCol,
    [float]$metalness = 0.9,
    [float]$roughness = 0.3,
    [string]$style = "standard", # "standard", "fluid", "logistics", "solid", "void", "gold_powered"
    [string]$subText = ""
) {
    $bmp = New-Object System.Drawing.Bitmap(64, 64)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))

    $fw = 8 # Heavy 8-pixel monolithic frame border

    # 1. Frame Fill
    $baseBrush = New-Object System.Drawing.SolidBrush($baseCol)
    # Top bar
    $g.FillRectangle($baseBrush, 0, 0, 64, $fw)
    # Bottom bar
    $g.FillRectangle($baseBrush, 0, 64 - $fw, 64, $fw)
    # Left bar
    $g.FillRectangle($baseBrush, 0, $fw, $fw, 64 - $fw * 2)
    # Right bar
    $g.FillRectangle($baseBrush, 64 - $fw, $fw, $fw, 64 - $fw * 2)

    # 2. Brushed Metal Texture / Grain
    $rand = New-Object System.Random(1337 + $name.GetHashCode())
    for ($i = 0; $i -lt 120; $i++) {
        $gx = $rand.Next(0, 64)
        $gy = $rand.Next(0, 64)
        $inFrame = ($gy -lt $fw) -or ($gy -ge (64 - $fw)) -or ($gx -lt $fw) -or ($gx -ge (64 - $fw))
        if ($inFrame) {
            $delta = $rand.Next(-22, 22)
            $r = [Math]::Min(255, [Math]::Max(0, $baseCol.R + $delta))
            $gCol = [Math]::Min(255, [Math]::Max(0, $baseCol.G + $delta))
            $b = [Math]::Min(255, [Math]::Max(0, $baseCol.B + $delta))
            $brushNoise = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(160, $r, $gCol, $b))
            $gw = $rand.Next(1, 4)
            $g.FillRectangle($brushNoise, $gx, $gy, $gw, 1)
            $brushNoise.Dispose()
        }
    }

    # 3. 3D Bevel Highlights & Ambient Occlusion Shadows
    $penLight = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(180, 255, 255, 255), 1.0)
    $penDark = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(180, 0, 0, 0), 1.0)
    $penInnerLight = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(90, 255, 255, 255), 1.0)
    $penInnerDark = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(140, 0, 0, 0), 1.0)

    # Outer Bevel
    $g.DrawLine($penLight, 0, 0, 63, 0) # Top outer
    $g.DrawLine($penLight, 0, 0, 0, 63) # Left outer
    $g.DrawLine($penDark, 0, 63, 63, 63) # Bottom outer
    $g.DrawLine($penDark, 63, 0, 63, 63) # Right outer

    # Secondary Outer Chamfer
    $g.DrawLine($penLight, 1, 1, 62, 1)
    $g.DrawLine($penLight, 1, 1, 1, 62)
    $g.DrawLine($penDark, 1, 62, 62, 62)
    $g.DrawLine($penDark, 62, 1, 62, 62)

    # Inner Frame Rim Bevel (Border of viewport)
    $g.DrawLine($penInnerDark, $fw, $fw, 64 - $fw, $fw) # Top inner shadow
    $g.DrawLine($penInnerDark, $fw, $fw, $fw, 64 - $fw) # Left inner shadow
    $g.DrawLine($penInnerLight, $fw, 64 - $fw, 64 - $fw, 64 - $fw) # Bottom inner highlight
    $g.DrawLine($penInnerLight, 64 - $fw, $fw, 64 - $fw, 64 - $fw) # Right inner highlight

    # 4. Corner Reinforcement Gussets
    $gussetBrush = New-Object System.Drawing.SolidBrush($accentCol)
    # Triangular 45-degree corner fillets
    [System.Drawing.Point[]]$gpts1 = @(
        [System.Drawing.Point]::new($fw, $fw),
        [System.Drawing.Point]::new($fw + 5, $fw),
        [System.Drawing.Point]::new($fw, $fw + 5)
    )
    $g.FillPolygon($gussetBrush, $gpts1)

    [System.Drawing.Point[]]$gpts2 = @(
        [System.Drawing.Point]::new(64 - $fw, $fw),
        [System.Drawing.Point]::new(64 - $fw - 5, $fw),
        [System.Drawing.Point]::new(64 - $fw, $fw + 5)
    )
    $g.FillPolygon($gussetBrush, $gpts2)

    [System.Drawing.Point[]]$gpts3 = @(
        [System.Drawing.Point]::new($fw, 64 - $fw),
        [System.Drawing.Point]::new($fw + 5, 64 - $fw),
        [System.Drawing.Point]::new($fw, 64 - $fw - 5)
    )
    $g.FillPolygon($gussetBrush, $gpts3)

    [System.Drawing.Point[]]$gpts4 = @(
        [System.Drawing.Point]::new(64 - $fw, 64 - $fw),
        [System.Drawing.Point]::new(64 - $fw - 5, 64 - $fw),
        [System.Drawing.Point]::new(64 - $fw, 64 - $fw - 5)
    )
    $g.FillPolygon($gussetBrush, $gpts4)

    # 5. Machined Industrial Hex Bolts in 4 corners
    $boltColor = [System.Drawing.Color]::FromArgb(255, [Math]::Min(255, $baseCol.R + 40), [Math]::Min(255, $baseCol.G + 40), [Math]::Min(255, $baseCol.B + 40))
    $socketColor = [System.Drawing.Color]::FromArgb(255, [Math]::Max(0, $baseCol.R - 50), [Math]::Max(0, $baseCol.G - 50), [Math]::Max(0, $baseCol.B - 50))
    Draw-Bolt $g 4 4 2 $boltColor $socketColor
    Draw-Bolt $g 59 4 2 $boltColor $socketColor
    Draw-Bolt $g 4 59 2 $boltColor $socketColor
    Draw-Bolt $g 59 59 2 $boltColor $socketColor

    # 6. Center Viewport Styling
    if ($style -eq "standard" -or $style -eq "fluid") {
        # Transparent crystalline sight glass with subtle glare lines and blue refraction
        $glassGlare = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(35, 210, 240, 255), 2.0)
        $g.DrawLine($glassGlare, 12, 14, 28, 50)
        $g.DrawLine($glassGlare, 16, 12, 32, 48)
        $glassGlare.Dispose()

        if ($style -eq "fluid") {
            # Fluid pipe has reinforced circumferential pressure rings and fluid flow markings
            $ringPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(220, $accentCol.R, $accentCol.G, $accentCol.B), 2.0)
            $g.DrawRectangle($ringPen, $fw + 2, $fw + 2, 64 - $fw * 2 - 4, 64 - $fw * 2 - 4)
            $ringPen.Dispose()
        }
    } elseif ($style -eq "gold_powered") {
        # Glowing energetic plasma conduits
        $plasmaBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
            (New-Object System.Drawing.Point(32, 8)),
            (New-Object System.Drawing.Point(32, 56)),
            [System.Drawing.Color]::FromArgb(255, 255, 245, 170),
            [System.Drawing.Color]::FromArgb(255, 255, 140, 20)
        )
        $g.FillEllipse($plasmaBrush, 18, 18, 28, 28)
        $plasmaBrush.Dispose()
        $coreGlow = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 240))
        $g.FillEllipse($coreGlow, 26, 26, 12, 12)
        $coreGlow.Dispose()
    } elseif ($style -eq "void") {
        # Swirling miniature cosmic singularity
        [System.Drawing.Point[]]$voidPts = @(
            [System.Drawing.Point]::new(12, 12),
            [System.Drawing.Point]::new(52, 12),
            [System.Drawing.Point]::new(52, 52),
            [System.Drawing.Point]::new(12, 52)
        )
        $voidBrush = New-Object System.Drawing.Drawing2D.PathGradientBrush(,$voidPts)
        $voidBrush.CenterColor = [System.Drawing.Color]::FromArgb(255, 10, 2, 20)
        $voidBrush.SurroundColors = [System.Drawing.Color[]]@([System.Drawing.Color]::FromArgb(220, 150, 40, 240))
        $g.FillEllipse($voidBrush, 12, 12, 40, 40)
        $voidBrush.Dispose()
        $singularity = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 0, 0, 0))
        $g.FillEllipse($singularity, 24, 24, 16, 16)
        $singularity.Dispose()
    } elseif ($style -eq "logistics") {
        # Monolithic cybernetic node interface
        $nodePlate = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 30, 32, 38))
        $g.FillRectangle($nodePlate, $fw + 2, $fw + 2, 64 - $fw * 2 - 4, 64 - $fw * 2 - 4)
        $nodePlate.Dispose()

        # Circuit traces
        $tracePen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 60, 170, 255), 1.5)
        $g.DrawLine($tracePen, 14, 32, 26, 32)
        $g.DrawLine($tracePen, 26, 32, 32, 26)
        $g.DrawLine($tracePen, 32, 26, 50, 26)
        $g.DrawLine($tracePen, 26, 32, 32, 38)
        $g.DrawLine($tracePen, 32, 38, 50, 38)
        $tracePen.Dispose()

        # Central illuminated optic core
        $opticBrush = New-Object System.Drawing.SolidBrush($accentCol)
        $g.FillEllipse($opticBrush, 27, 27, 10, 10)
        $opticBrush.Dispose()
        $opticCenter = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 255))
        $g.FillEllipse($opticCenter, 30, 30, 4, 4)
        $opticCenter.Dispose()
    } elseif ($style -eq "solid") {
        # Heavy armor plate
        $plateBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, [Math]::Max(0, $baseCol.R - 20), [Math]::Max(0, $baseCol.G - 20), [Math]::Max(0, $baseCol.B - 20)))
        $g.FillRectangle($plateBrush, $fw, $fw, 64 - $fw * 2, 64 - $fw * 2)
        $plateBrush.Dispose()

        # Center embossed emblem / detail
        $g.DrawRectangle($penLight, 20, 20, 24, 24)
        $g.DrawRectangle($penDark, 21, 21, 22, 22)
    }

    # Clean up GDI objects
    $baseBrush.Dispose()
    $gussetBrush.Dispose()
    $penLight.Dispose()
    $penDark.Dispose()
    $penInnerLight.Dispose()
    $penInnerDark.Dispose()
    $g.Dispose()

    # Save with LabPBR
    $targetBase = Join-Path $texturesBlockDir $name
    $emissive = if ($style -eq "gold_powered" -or $style -eq "void" -or $style -eq "logistics") { 255 } else { 0 }
    Save-ImageWithPBR $bmp $targetBase $roughness $metalness $null $emissive
    $bmp.Dispose()
}

# ==============================================================================
# 3. GENERATE ARM TEXTURES (64x64)
# ==============================================================================
function Generate-ArmTexture(
    [string]$name,
    [System.Drawing.Color]$baseCol,
    [System.Drawing.Color]$accentCol,
    [float]$metalness = 0.9,
    [float]$roughness = 0.3,
    [string]$style = "standard"
) {
    $bmp = New-Object System.Drawing.Bitmap(64, 64)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))

    # Rows 0..15: Connection Flange at [0..15, 0..15]
    $flangeBrush = New-Object System.Drawing.SolidBrush($accentCol)
    $g.FillRectangle($flangeBrush, 0, 0, 16, 16)
    $flangeBrush.Dispose()

    # Flange Bevels & 4 Mounting Bolt Holes
    $penL = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(200, 255, 255, 255), 1.0)
    $penD = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(200, 0, 0, 0), 1.0)
    $g.DrawRectangle($penL, 0, 0, 15, 15)
    $g.DrawRectangle($penD, 1, 1, 13, 13)

    # 4 micro bolt holes
    $holeBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 15, 15, 15))
    $g.FillRectangle($holeBrush, 2, 2, 2, 2)
    $g.FillRectangle($holeBrush, 12, 2, 2, 2)
    $g.FillRectangle($holeBrush, 2, 12, 2, 2)
    $g.FillRectangle($holeBrush, 12, 12, 2, 2)
    $holeBrush.Dispose()

    # Rows 16..63: Pipe Conduit Body
    $baseBrush = New-Object System.Drawing.SolidBrush($baseCol)

    # Monolithic Collar Band at joint (y = 16..26)
    $g.FillRectangle($baseBrush, 0, 16, 64, 10)
    # Knurled grip grooves on collar
    for ($gx = 2; $gx -lt 64; $gx += 4) {
        $g.DrawLine($penD, $gx, 17, $gx, 25)
        $g.DrawLine($penL, $gx + 1, 17, $gx + 1, 25)
    }

    # Pipe Wall Rails: Left (x=0..7) and Right (x=56..63) for y=27..63
    $g.FillRectangle($baseBrush, 0, 27, 8, 37)
    $g.FillRectangle($baseBrush, 56, 27, 8, 37)

    # Chamfers on the rails
    $g.DrawLine($penL, 0, 16, 0, 63)
    $g.DrawLine($penL, 1, 16, 1, 63)
    $g.DrawLine($penD, 63, 16, 63, 63)
    $g.DrawLine($penD, 62, 16, 62, 63)

    # Inner rail bevels
    $g.DrawLine($penD, 7, 27, 7, 63)
    $g.DrawLine($penL, 8, 27, 8, 63)
    $g.DrawLine($penD, 55, 27, 55, 63)
    $g.DrawLine($penL, 56, 27, 56, 63)

    # Intermediate reinforced collar ring at y = 46..50
    $g.FillRectangle($baseBrush, 0, 46, 64, 5)
    $g.DrawLine($penL, 0, 46, 63, 46)
    $g.DrawLine($penD, 0, 50, 63, 50)

    # Center Viewport Glass Glare (x=8..55, y=27..45 and 51..63)
    if ($style -eq "standard" -or $style -eq "fluid") {
        $glassGlare = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(35, 210, 240, 255), 2.0)
        $g.DrawLine($glassGlare, 16, 28, 48, 44)
        $g.DrawLine($glassGlare, 16, 52, 48, 62)
        $glassGlare.Dispose()
    } elseif ($style -eq "gold_powered") {
        # Plasma flow strip in center
        $flowBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
            (New-Object System.Drawing.Point(32, 27)),
            (New-Object System.Drawing.Point(32, 63)),
            [System.Drawing.Color]::FromArgb(255, 255, 230, 120),
            [System.Drawing.Color]::FromArgb(255, 255, 120, 20)
        )
        $g.FillRectangle($flowBrush, 22, 27, 20, 37)
        $flowBrush.Dispose()
    }

    $baseBrush.Dispose()
    $penL.Dispose()
    $penD.Dispose()
    $g.Dispose()

    $targetBase = Join-Path $texturesBlockDir $name
    $emissive = if ($style -eq "gold_powered") { 255 } else { 0 }
    Save-ImageWithPBR $bmp $targetBase $roughness $metalness $null $emissive
    $bmp.Dispose()
}

# ==============================================================================
# 4. BATCH GENERATION OF ALL PIPE TEXTURES
# ==============================================================================

# Palette definitions
$colCopper = [System.Drawing.Color]::FromArgb(255, 212, 118, 76)
$colCopperExposed = [System.Drawing.Color]::FromArgb(255, 168, 116, 88)
$colCopperWeathered = [System.Drawing.Color]::FromArgb(255, 108, 154, 124)
$colCopperOxidized = [System.Drawing.Color]::FromArgb(255, 78, 172, 142)
$colBrassGusset = [System.Drawing.Color]::FromArgb(255, 185, 140, 55)

$colStone = [System.Drawing.Color]::FromArgb(255, 72, 74, 78)
$colStoneAccent = [System.Drawing.Color]::FromArgb(255, 42, 44, 48)

$colGold = [System.Drawing.Color]::FromArgb(255, 245, 198, 62)
$colGoldAccent = [System.Drawing.Color]::FromArgb(255, 200, 145, 30)

$colLogistics = [System.Drawing.Color]::FromArgb(255, 52, 56, 64)
$colLogisticsBlue = [System.Drawing.Color]::FromArgb(255, 50, 170, 255)

$colVoid = [System.Drawing.Color]::FromArgb(255, 28, 26, 32)
$colVoidPurple = [System.Drawing.Color]::FromArgb(255, 160, 40, 240)

# A. Copper Transport Pipes
Generate-CoreTexture "copper_pipe_core" $colCopper $colBrassGusset 0.92 0.28 "standard"
Generate-CoreTexture "copper_pipe_core_exposed" $colCopperExposed $colBrassGusset 0.85 0.38 "standard"
Generate-CoreTexture "copper_pipe_core_weathered" $colCopperWeathered $colBrassGusset 0.70 0.48 "standard"
Generate-CoreTexture "copper_pipe_core_oxidized" $colCopperOxidized $colBrassGusset 0.55 0.58 "standard"

Generate-ArmTexture "copper_pipe_arm" $colCopper $colBrassGusset 0.92 0.28 "standard"
Generate-ArmTexture "copper_pipe_arm_exposed" $colCopperExposed $colBrassGusset 0.85 0.38 "standard"
Generate-ArmTexture "copper_pipe_arm_weathered" $colCopperWeathered $colBrassGusset 0.70 0.48 "standard"
Generate-ArmTexture "copper_pipe_arm_oxidized" $colCopperOxidized $colBrassGusset 0.55 0.58 "standard"

# B. Copper Fluid Pipes
Generate-CoreTexture "copper_fluid_pipe_core" $colCopper $colBrassGusset 0.92 0.28 "fluid"
Generate-CoreTexture "copper_fluid_pipe_core_exposed" $colCopperExposed $colBrassGusset 0.85 0.38 "fluid"
Generate-CoreTexture "copper_fluid_pipe_core_weathered" $colCopperWeathered $colBrassGusset 0.70 0.48 "fluid"
Generate-CoreTexture "copper_fluid_pipe_core_oxidized" $colCopperOxidized $colBrassGusset 0.55 0.58 "fluid"

Generate-ArmTexture "copper_fluid_pipe_arm" $colCopper $colBrassGusset 0.92 0.28 "fluid"
Generate-ArmTexture "copper_fluid_pipe_arm_exposed" $colCopperExposed $colBrassGusset 0.85 0.38 "fluid"
Generate-ArmTexture "copper_fluid_pipe_arm_weathered" $colCopperWeathered $colBrassGusset 0.70 0.48 "fluid"
Generate-ArmTexture "copper_fluid_pipe_arm_oxidized" $colCopperOxidized $colBrassGusset 0.55 0.58 "fluid"

# Specific Pipe Accents
$colStoneBlue   = [System.Drawing.Color]::FromArgb(255, 30, 80, 140)
$colGoldPowered = [System.Drawing.Color]::FromArgb(255, 255, 255, 200)
$colGoldFluid   = [System.Drawing.Color]::FromArgb(255, 20, 140, 220)
$colLogisticsEmerald = [System.Drawing.Color]::FromArgb(255, 60, 220, 120)
$colLogisticsCobalt  = [System.Drawing.Color]::FromArgb(255, 40, 180, 240)
$colLogisticsAmber   = [System.Drawing.Color]::FromArgb(255, 240, 160, 40)
$colLogisticsGold    = [System.Drawing.Color]::FromArgb(255, 255, 215, 0)
$colLogisticsOrange  = [System.Drawing.Color]::FromArgb(255, 255, 100, 50)
$colLogisticsViolet  = [System.Drawing.Color]::FromArgb(255, 180, 70, 240)
$colExtractorOrange  = [System.Drawing.Color]::FromArgb(255, 230, 80, 40)
$colFilterAmber      = [System.Drawing.Color]::FromArgb(255, 200, 150, 40)
$colInsertionGreen   = [System.Drawing.Color]::FromArgb(255, 40, 200, 80)
$colMergerBlue       = [System.Drawing.Color]::FromArgb(255, 60, 140, 240)
$colPassthruBlue     = [System.Drawing.Color]::FromArgb(255, 40, 85, 150)
$colPassthruLight    = [System.Drawing.Color]::FromArgb(255, 80, 150, 240)
$colBypassRed        = [System.Drawing.Color]::FromArgb(255, 140, 40, 40)
$colBypassAmber      = [System.Drawing.Color]::FromArgb(255, 230, 180, 40)

# C. Stone Pipes
Generate-CoreTexture "stone_pipe_core" $colStone $colStoneAccent 0.30 0.65 "standard"
Generate-ArmTexture "stone_pipe_arm" $colStone $colStoneAccent 0.30 0.65 "standard"
Generate-CoreTexture "stone_fluid_pipe_core" $colStone $colStoneBlue 0.30 0.65 "fluid"
Generate-ArmTexture "stone_fluid_pipe_arm" $colStone $colStoneBlue 0.30 0.65 "fluid"

# D. Gold Pipes
Generate-CoreTexture "gold_pipe_core" $colGold $colGoldAccent 0.95 0.20 "standard"
Generate-ArmTexture "gold_pipe_arm" $colGold $colGoldAccent 0.95 0.20 "standard"
Generate-CoreTexture "gold_pipe_core_powered" $colGold $colGoldPowered 0.95 0.15 "gold_powered"
Generate-ArmTexture "gold_pipe_arm_powered" $colGold $colGoldPowered 0.95 0.15 "gold_powered"
Generate-CoreTexture "gold_fluid_pipe_core" $colGold $colGoldFluid 0.95 0.20 "fluid"
Generate-ArmTexture "gold_fluid_pipe_arm" $colGold $colGoldFluid 0.95 0.20 "fluid"

# E. Void Pipes
Generate-CoreTexture "void_pipe_core" $colVoid $colVoidPurple 0.40 0.50 "void"
Generate-ArmTexture "void_pipe_arm" $colVoid $colVoidPurple 0.40 0.50 "void"
Generate-CoreTexture "void_fluid_pipe_core" $colVoid $colVoidPurple 0.40 0.50 "void"
Generate-ArmTexture "void_fluid_pipe_arm" $colVoid $colVoidPurple 0.40 0.50 "void"

# F. Logistics Automation Network Pipes
Generate-CoreTexture "basic_logistics_pipe_core" $colLogistics $colLogisticsBlue 0.88 0.32 "logistics"
Generate-ArmTexture "logistics_pipe_arm" $colLogistics $colLogisticsBlue 0.88 0.32 "standard"

# Overlays (tinted in code)
$coreOverlayBmp = New-Object System.Drawing.Bitmap(64, 64)
$og = [System.Drawing.Graphics]::FromImage($coreOverlayBmp)
$og.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
$lensBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 255))
# 4 corner lenses
$og.FillRectangle($lensBrush, 0, 0, 8, 8)
$og.FillRectangle($lensBrush, 56, 0, 8, 8)
$og.FillRectangle($lensBrush, 0, 56, 8, 8)
$og.FillRectangle($lensBrush, 56, 56, 8, 8)
$lensBrush.Dispose()
$og.Dispose()
$coreOverlayBmp.Save((Join-Path $texturesBlockDir "logistics_pipe_core_overlay.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$coreOverlayBmp.Save((Join-Path $texturesBlockDir "logistics_pipe_arm_overlay.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$coreOverlayBmp.Dispose()

# G. Intelligent Logistics Pipes
Generate-CoreTexture "provider_logistics_pipe_core" $colLogistics $colLogisticsEmerald 0.88 0.32 "logistics"
Generate-CoreTexture "fluid_provider_pipe_core" $colLogistics $colLogisticsEmerald 0.88 0.32 "fluid"
Generate-CoreTexture "requester_logistics_pipe_core" $colLogistics $colLogisticsCobalt 0.88 0.32 "logistics"
Generate-CoreTexture "supplier_logistics_pipe_core" $colLogistics $colLogisticsAmber 0.88 0.32 "logistics"
Generate-CoreTexture "fluid_supplier_pipe_core" $colLogistics $colLogisticsAmber 0.88 0.32 "fluid"
Generate-CoreTexture "crafting_logistics_pipe_core" $colLogistics $colLogisticsGold 0.88 0.32 "logistics"
Generate-CoreTexture "process_logistics_pipe_core" $colLogistics $colLogisticsOrange 0.88 0.32 "logistics"
Generate-CoreTexture "satellite_logistics_pipe_core" $colLogistics $colLogisticsViolet 0.88 0.32 "logistics"

# H. Modular Chassis Pipes Mk1 to Mk5
$chassisCol = [System.Drawing.Color]::FromArgb(255, 42, 45, 52)
for ($mk = 1; $mk -le 5; $mk++) {
    $chassisBmp = New-Object System.Drawing.Bitmap(64, 64)
    $cg = [System.Drawing.Graphics]::FromImage($chassisBmp)
    $cg.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $cg.Clear([System.Drawing.Color]::FromArgb(255, $chassisCol.R, $chassisCol.G, $chassisCol.B))

    # Heavy outer chamfer & rivets
    $penL = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(200, 255, 255, 255), 1.0)
    $penD = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(200, 0, 0, 0), 1.0)
    $cg.DrawRectangle($penL, 0, 0, 63, 63)
    $cg.DrawRectangle($penD, 1, 1, 61, 61)
    $colSilver = [System.Drawing.Color]::Silver
    $colDarkGray = [System.Drawing.Color]::DarkGray
    Draw-Bolt $cg 5 5 2 $colSilver $colDarkGray
    Draw-Bolt $cg 58 5 2 $colSilver $colDarkGray
    Draw-Bolt $cg 5 58 2 $colSilver $colDarkGray
    Draw-Bolt $cg 58 58 2 $colSilver $colDarkGray

    # Modular Expansion Slots (number of slots = $mk)
    $slotBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 18, 20, 24))
    $goldPin = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 240, 190, 50))
    $ledColor = switch ($mk) {
        1 { [System.Drawing.Color]::FromArgb(255, 60, 180, 255) }
        2 { [System.Drawing.Color]::FromArgb(255, 60, 220, 100) }
        3 { [System.Drawing.Color]::FromArgb(255, 255, 200, 40) }
        4 { [System.Drawing.Color]::FromArgb(255, 255, 100, 40) }
        5 { [System.Drawing.Color]::FromArgb(255, 200, 60, 255) }
    }
    $ledBrush = New-Object System.Drawing.SolidBrush($ledColor)

    $spacing = [int](36.0 / ($mk + 1))
    for ($s = 1; $s -le $mk; $s++) {
        $sy = 12 + $s * $spacing
        $cg.FillRectangle($slotBrush, 14, $sy, 36, 6)
        $cg.DrawRectangle($penD, 14, $sy, 36, 6)
        # Gold bus pins
        for ($p = 18; $p -lt 46; $p += 4) {
            $cg.FillRectangle($goldPin, $p, $sy + 2, 2, 2)
        }
        # Status LED
        $cg.FillEllipse($ledBrush, 44, $sy + 1, 4, 4)
    }

    $slotBrush.Dispose()
    $goldPin.Dispose()
    $ledBrush.Dispose()
    $penL.Dispose()
    $penD.Dispose()
    $cg.Dispose()

    Save-ImageWithPBR $chassisBmp (Join-Path $texturesBlockDir "chassis_logistics_pipe_mk$($mk)_core") 0.32 0.85 $null 255
    $chassisBmp.Dispose()
}

# I. Functional Pipes: Extractor, Filter, Insertion, Merger, Passthrough, Bypass
# Extractor
Generate-CoreTexture "extractor_pipe_core" $colLogistics $colExtractorOrange 0.88 0.32 "standard"
Generate-ArmTexture "extractor_pipe_arm" $colLogistics $colExtractorOrange 0.88 0.32 "standard"
# Extractor Feature (Intake nozzle & Hazard stripes)
$featBmp = New-Object System.Drawing.Bitmap(64, 64)
$fg = [System.Drawing.Graphics]::FromImage($featBmp)
$fg.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
$hazardYellow = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 240, 200, 30))
$hazardBlack = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 30, 30, 30))
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

# Fluid Extractor
Generate-CoreTexture "fluid_extractor_pipe_core" $colCopper $colExtractorOrange 0.90 0.30 "fluid"
Generate-ArmTexture "fluid_extractor_pipe_arm" $colCopper $colExtractorOrange 0.90 0.30 "fluid"

# Filter Pipe
Generate-CoreTexture "filter_pipe_core" $colLogistics $colFilterAmber 0.88 0.32 "standard"
Generate-ArmTexture "filter_pipe_arm" $colLogistics $colFilterAmber 0.88 0.32 "standard"
$coreOverlayBmp = New-Object System.Drawing.Bitmap(64, 64)
$og = [System.Drawing.Graphics]::FromImage($coreOverlayBmp)
$og.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
$lensB = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 255))
$og.FillRectangle($lensB, 24, 24, 16, 16)
$lensB.Dispose()
$og.Dispose()
$coreOverlayBmp.Save((Join-Path $texturesBlockDir "filter_pipe_overlay.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$coreOverlayBmp.Dispose()

# Insertion Pipe
Generate-CoreTexture "insertion_pipe_core" $colLogistics $colInsertionGreen 0.88 0.32 "standard"
Generate-ArmTexture "insertion_pipe_arm" $colLogistics $colInsertionGreen 0.88 0.32 "standard"
Generate-CoreTexture "insertion_fluid_pipe_core" $colCopper $colInsertionGreen 0.90 0.30 "fluid"
Generate-ArmTexture "insertion_fluid_pipe_arm" $colCopper $colInsertionGreen 0.90 0.30 "fluid"

# Merger Pipe
Generate-CoreTexture "merger_pipe_core" $colLogistics $colMergerBlue 0.88 0.32 "standard"
Generate-ArmTexture "merger_pipe_arm" $colLogistics $colMergerBlue 0.88 0.32 "standard"
Generate-CoreTexture "merger_fluid_pipe_core" $colCopper $colMergerBlue 0.90 0.30 "fluid"
Generate-ArmTexture "merger_fluid_pipe_arm" $colCopper $colMergerBlue 0.90 0.30 "fluid"
# Merger feature
$mergerFeat = New-Object System.Drawing.Bitmap(64, 64)
$mfg = [System.Drawing.Graphics]::FromImage($mergerFeat)
$mfg.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
$mBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 45, 50, 58))
[System.Drawing.Point[]]$mfPts = @([System.Drawing.Point]::new(0, 16), [System.Drawing.Point]::new(64, 16), [System.Drawing.Point]::new(32, 48))
$mfg.FillPolygon($mBrush, $mfPts)
$mBrush.Dispose()
$mfg.Dispose()
Save-ImageWithPBR $mergerFeat (Join-Path $texturesBlockDir "merger_pipe_feature") 0.35 0.85
$mergerFeat.Save((Join-Path $texturesBlockDir "merger_fluid_pipe_feature.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$mergerFeat.Dispose()

# Passthrough Pipe
Generate-CoreTexture "passthru_pipe_core" $colPassthruBlue $colPassthruLight 0.85 0.30 "standard"
Generate-ArmTexture "passthru_pipe_arm" $colPassthruBlue $colPassthruLight 0.85 0.30 "standard"

# Bypass Pipe
Generate-CoreTexture "bypass_fluid_pipe_core" $colBypassRed $colBypassAmber 0.85 0.30 "fluid"
Generate-ArmTexture "bypass_fluid_pipe_arm" $colBypassRed $colBypassAmber 0.85 0.30 "fluid"

# J. Power Junction
$pjOff = New-Object System.Drawing.Bitmap(64, 64)
$pjg = [System.Drawing.Graphics]::FromImage($pjOff)
$pjg.Clear([System.Drawing.Color]::FromArgb(255, 35, 38, 44))
$finBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 22, 24, 28))
for ($fy = 8; $fy -lt 56; $fy += 6) {
    $pjg.FillRectangle($finBrush, 12, $fy, 40, 3)
}
$finBrush.Dispose()
$pjg.Dispose()
Save-ImageWithPBR $pjOff (Join-Path $texturesBlockDir "power_junction_off") 0.40 0.80
$pjOff.Dispose()

# Power junction on
$pjOn = New-Object System.Drawing.Bitmap(64, 64)
$pjg = [System.Drawing.Graphics]::FromImage($pjOn)
$pjg.Clear([System.Drawing.Color]::FromArgb(255, 35, 38, 44))
$finBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 22, 24, 28))
for ($fy = 8; $fy -lt 56; $fy += 6) {
    $pjg.FillRectangle($finBrush, 12, $fy, 40, 3)
}
$finBrush.Dispose()
# Glowing electric arcs
$arcBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 80, 210, 255))
$pjg.FillRectangle($arcBrush, 14, 24, 36, 16)
$arcBrush.Dispose()
$pjg.Dispose()
Save-ImageWithPBR $pjOn (Join-Path $texturesBlockDir "power_junction_on") 0.20 0.80 $null 255
$pjOn.Dispose()

# Copy animation mcmeta
$pjMeta = @"
{
  "animation": {
    "frametime": 4,
    "interpolate": true
  }
}
"@
[System.IO.File]::WriteAllText((Join-Path $texturesBlockDir "power_junction_on.png.mcmeta"), $pjMeta, [System.Text.Encoding]::UTF8)

# K. Pipe Markings
$markBmp = New-Object System.Drawing.Bitmap(64, 64)
$mg = [System.Drawing.Graphics]::FromImage($markBmp)
$mg.Clear([System.Drawing.Color]::FromArgb(255, 220, 220, 220))
$mPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 40, 40, 40), 2.0)
$mg.DrawRectangle($mPen, 2, 2, 60, 60)
$mPen.Dispose()
$mg.Dispose()
Save-ImageWithPBR $markBmp (Join-Path $texturesBlockDir "pipe_markings") 0.45 0.10
$markBmp.Dispose()

# Fluid packet
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
# 5. GENERATE ITEM MODULE TEXTURES (64x64)
# ==============================================================================
Write-Host "Generating Ultra-Realistic 64x64 Pipe Module Textures..." -ForegroundColor Green

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

    # Circuit Board Substrate
    $pcbBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 25, 45, 35))
    $mg.FillRectangle($pcbBrush, 12, 8, 40, 48)
    $pcbBrush.Dispose()

    # Gold Edge Connectors at bottom
    $goldBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 235, 190, 50))
    for ($px = 16; $px -lt 48; $px += 4) {
        $mg.FillRectangle($goldBrush, $px, 52, 2, 4)
    }
    $goldBrush.Dispose()

    # Integrated Circuit / Processor in center
    $icBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 30, 32, 36))
    $mg.FillRectangle($icBrush, 20, 16, 24, 24)
    $icBrush.Dispose()

    # Module Identity Accent Icon / LED
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

    # PCB Traces
    $tracePen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(180, 50, 180, 100), 1.0)
    $mg.DrawLine($tracePen, 16, 12, 24, 12)
    $mg.DrawLine($tracePen, 40, 12, 48, 12)
    $mg.DrawLine($tracePen, 16, 44, 24, 44)
    $mg.DrawLine($tracePen, 40, 44, 48, 44)
    $tracePen.Dispose()

    $mg.Dispose()
    $mBmp.Save((Join-Path $texturesItemDir "$modName.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $mBmp.Dispose()
}

# ==============================================================================
# 6. GENERATE PACK.PNG (256x256 High-Resolution Icon)
# ==============================================================================
Write-Host "Generating Ultra-Realistic 256x256 pack.png Icon..." -ForegroundColor Cyan
$packBmp = New-Object System.Drawing.Bitmap(256, 256)
$pg = [System.Drawing.Graphics]::FromImage($packBmp)
$pg.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias

# Dark Industrial Slate Background with Carbon Weave
$bgBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(0, 0)),
    (New-Object System.Drawing.Point(256, 256)),
    [System.Drawing.Color]::FromArgb(255, 24, 26, 32),
    [System.Drawing.Color]::FromArgb(255, 12, 14, 18)
)
$pg.FillRectangle($bgBrush, 0, 0, 256, 256)
$bgBrush.Dispose()

# Subtle Grid Overlay
$gridPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(20, 255, 255, 255), 1.0)
for ($x = 0; $x -lt 256; $x += 16) { $pg.DrawLine($gridPen, $x, 0, $x, 256) }
for ($y = 0; $y -lt 256; $y += 16) { $pg.DrawLine($gridPen, 0, $y, 256, $y) }
$gridPen.Dispose()

# Draw Isometric Monolithic 3D Pipe Junction
# Central Core Flange Box
$copperBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(88, 70)),
    (New-Object System.Drawing.Point(168, 150)),
    [System.Drawing.Color]::FromArgb(255, 235, 140, 90),
    [System.Drawing.Color]::FromArgb(255, 150, 75, 45)
)
# Pipe Arms extending in 4 directions
$pg.FillRectangle($copperBrush, 28, 96, 60, 64)   # West arm
$pg.FillRectangle($copperBrush, 168, 96, 60, 64)  # East arm
$pg.FillRectangle($copperBrush, 96, 28, 64, 60)   # North arm
$pg.FillRectangle($copperBrush, 96, 168, 64, 60)  # South arm

# Heavy Monolithic Center Manifold Hub
$titaniumBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(80, 80)),
    (New-Object System.Drawing.Point(176, 176)),
    [System.Drawing.Color]::FromArgb(255, 68, 74, 86),
    [System.Drawing.Color]::FromArgb(255, 34, 38, 46)
)
$pg.FillRectangle($titaniumBrush, 80, 80, 96, 96)

# Outer Bevel and Bolt Details on Hub
$hubPenL = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(220, 255, 255, 255), 2.0)
$hubPenD = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(220, 0, 0, 0), 2.0)
$pg.DrawRectangle($hubPenL, 80, 80, 95, 95)
$pg.DrawRectangle($hubPenD, 82, 82, 91, 91)
$colGoldBolt = [System.Drawing.Color]::Gold
$colDarkGold = [System.Drawing.Color]::DarkGoldenrod
Draw-Bolt $pg 90 90 4 $colGoldBolt $colDarkGold
Draw-Bolt $pg 166 90 4 $colGoldBolt $colDarkGold
Draw-Bolt $pg 90 166 4 $colGoldBolt $colDarkGold
Draw-Bolt $pg 166 166 4 $colGoldBolt $colDarkGold

# Glowing Holographic Center Optic Core
[System.Drawing.Point[]]$glowPts = @(
    [System.Drawing.Point]::new(100, 100),
    [System.Drawing.Point]::new(156, 100),
    [System.Drawing.Point]::new(156, 156),
    [System.Drawing.Point]::new(100, 156)
)
$glowBrush = New-Object System.Drawing.Drawing2D.PathGradientBrush(,$glowPts)
$glowBrush.CenterColor = [System.Drawing.Color]::FromArgb(255, 255, 255, 255)
$glowBrush.SurroundColors = [System.Drawing.Color[]]@([System.Drawing.Color]::FromArgb(240, 50, 180, 255))
$pg.FillEllipse($glowBrush, 104, 104, 48, 48)
$glowBrush.Dispose()

# Typography Banner: "LOGISTICS PIPES" & "3D MONOLITHIC"
$fontTitle = New-Object System.Drawing.Font("Arial", 13, [System.Drawing.FontStyle]::Bold)
$fontSub = New-Object System.Drawing.Font("Arial", 8, [System.Drawing.FontStyle]::Bold)
$textBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 255))
$goldText = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 245, 195, 60))

$pg.DrawString("LOGISTICS PIPES 3D", $fontTitle, $goldText, 32, 206)
$pg.DrawString("ULTRA-REALISTIC MONOLITHIC EDITION", $fontSub, $textBrush, 22, 228)

$fontTitle.Dispose()
$fontSub.Dispose()
$textBrush.Dispose()
$goldText.Dispose()
$copperBrush.Dispose()
$titaniumBrush.Dispose()
$hubPenL.Dispose()
$hubPenD.Dispose()
$pg.Dispose()

$packBmp.Save((Join-Path $packPath "pack.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$packBmp.Dispose()

# ==============================================================================
# 7. GENERATE 3D MONOLITHIC BLOCK MODELS (Enhanced JSON Models)
# ==============================================================================
Write-Host "Generating Ultra-Detailed 3D Monolithic Block Models..." -ForegroundColor Green

# 3D Monolithic Pipe Core Base Model
$modelPipeCore = @"
{
  "parent": "block/block",
  "display": {
    "gui": {
      "rotation": [30, -50, 0],
      "scale": [0.8, 0.8, 0.8]
    },
    "fixed": {
      "rotation": [90, 0, 0],
      "translation": [0, -2.5, 0]
    },
    "ground": {
      "scale": [0.3, 0.3, 0.3]
    }
  },
  "elements": [
    {
      "name": "monolithic_core_body",
      "from": [4.0, 4.0, 4.0],
      "to": [12.0, 12.0, 12.0],
      "faces": {
        "north": { "uv": [0, 0, 16, 16], "texture": "#texture" },
        "east":  { "uv": [0, 0, 16, 16], "texture": "#texture" },
        "south": { "uv": [0, 0, 16, 16], "texture": "#texture" },
        "west":  { "uv": [0, 0, 16, 16], "texture": "#texture" },
        "up":    { "uv": [0, 0, 16, 16], "texture": "#texture" },
        "down":  { "uv": [0, 0, 16, 16], "texture": "#texture" }
      }
    },
    {
      "name": "reinforced_collar_north",
      "from": [3.8, 3.8, 3.8],
      "to": [12.2, 12.2, 4.5],
      "faces": {
        "north": { "uv": [0, 0, 16, 16], "texture": "#texture" },
        "east":  { "uv": [0, 0, 2, 16], "texture": "#texture" },
        "west":  { "uv": [14, 0, 16, 16], "texture": "#texture" },
        "up":    { "uv": [0, 0, 16, 2], "texture": "#texture" },
        "down":  { "uv": [0, 14, 16, 16], "texture": "#texture" }
      }
    },
    {
      "name": "reinforced_collar_south",
      "from": [3.8, 3.8, 11.5],
      "to": [12.2, 12.2, 12.2],
      "faces": {
        "south": { "uv": [0, 0, 16, 16], "texture": "#texture" },
        "east":  { "uv": [14, 0, 16, 16], "texture": "#texture" },
        "west":  { "uv": [0, 0, 2, 16], "texture": "#texture" },
        "up":    { "uv": [0, 14, 16, 16], "texture": "#texture" },
        "down":  { "uv": [0, 0, 16, 2], "texture": "#texture" }
      }
    }
  ]
}
"@
[System.IO.File]::WriteAllText((Join-Path $modelsBlockDir "pipe_core.json"), $modelPipeCore, [System.Text.Encoding]::UTF8)

# 3D Monolithic Pipe Arm Base Model (with physical flange & extruded collar)
$modelPipeArm = @"
{
  "parent": "block/block",
  "display": {
    "gui": {
      "rotation": [30, -50, 0],
      "scale": [0.8, 0.8, 0.8]
    },
    "fixed": {
      "rotation": [90, 0, 0],
      "translation": [0, -2.5, 0]
    },
    "ground": {
      "scale": [0.3, 0.3, 0.3]
    }
  },
  "elements": [
    {
      "name": "arm_tube",
      "from": [4.0, 4.0, 0.0],
      "to": [12.0, 12.0, 4.0],
      "faces": {
        "north": { "uv": [0, 0, 2, 2], "texture": "#texture" },
        "east":  { "uv": [0, 8, 16, 16], "rotation": 90, "texture": "#texture" },
        "south": { "uv": [0, 0, 2, 2], "texture": "#texture" },
        "west":  { "uv": [0, 8, 16, 16], "rotation": 270, "texture": "#texture" },
        "up":    { "uv": [0, 8, 16, 16], "texture": "#texture" },
        "down":  { "uv": [0, 8, 16, 16], "rotation": 180, "texture": "#texture" }
      }
    },
    {
      "name": "connection_flange",
      "from": [3.5, 3.5, 0.0],
      "to": [12.5, 12.5, 1.2],
      "faces": {
        "north": { "uv": [0, 0, 2, 2], "texture": "#texture" },
        "east":  { "uv": [0, 8, 4, 16], "rotation": 90, "texture": "#texture" },
        "south": { "uv": [0, 0, 2, 2], "texture": "#texture" },
        "west":  { "uv": [0, 8, 4, 16], "rotation": 270, "texture": "#texture" },
        "up":    { "uv": [0, 8, 16, 12], "texture": "#texture" },
        "down":  { "uv": [0, 8, 16, 12], "rotation": 180, "texture": "#texture" }
      }
    },
    {
      "name": "reinforcement_collar",
      "from": [3.8, 3.8, 2.2],
      "to": [12.2, 12.2, 3.2],
      "faces": {
        "east":  { "uv": [8, 8, 12, 16], "rotation": 90, "texture": "#texture" },
        "west":  { "uv": [8, 8, 12, 16], "rotation": 270, "texture": "#texture" },
        "up":    { "uv": [0, 10, 16, 14], "texture": "#texture" },
        "down":  { "uv": [0, 10, 16, 14], "rotation": 180, "texture": "#texture" }
      }
    }
  ]
}
"@
[System.IO.File]::WriteAllText((Join-Path $modelsBlockDir "pipe_arm.json"), $modelPipeArm, [System.Text.Encoding]::UTF8)

# Copy all original pipe JSON model references to the pack
Get-ChildItem -Path "logistics\assets\logistics\models\block\pipe" -Filter "*.json" | ForEach-Object {
    $dest = Join-Path $modelsBlockDir $_.Name
    if (-not (Test-Path $dest)) {
        Copy-Item -Path $_.FullName -Destination $dest -Force
    }
}

# Copy all item model definitions to the pack
Get-ChildItem -Path "logistics\assets\logistics\items\pipe" -Filter "*.json" | ForEach-Object {
    $dest = Join-Path $itemsDir $_.Name
    Copy-Item -Path $_.FullName -Destination $dest -Force
}

# ==============================================================================
# 8. CREATE ZIP ARCHIVES FOR INSTANT RESOURCE PACK INSTALLATION
# (Using Java jar CLI to ensure strictly standard forward slashes '/')
# ==============================================================================
if ($CreateZip) {
    Write-Host "Packaging standalone Resource Packs with standard forward slashes..." -ForegroundColor Cyan
    $resourcepacksDir = Join-Path $projectDir "resourcepacks"

    # Universal / MC 1.21.4+ (pack_format: 46, supported_formats: [15, 65])
    $zipUniversal = Join-Path $resourcepacksDir "Logistics-UltraRealistic-3D-Pipes.zip"
    if (Test-Path $zipUniversal) { Remove-Item -Path $zipUniversal -Force }
    & jar -c -M -f $zipUniversal -C $packPath .

    # MC 1.21 - 1.21.1 Native (pack_format: 34, supported_formats: [15, 65])
    $mcmeta121 = @"
{
  "pack": {
    "pack_format": 34,
    "supported_formats": [15, 65],
    "description": "Ultra-Realistic 3D Monolithic Pipes for Logistics Automation"
  }
}
"@
    [System.IO.File]::WriteAllText((Join-Path $packPath "pack.mcmeta"), $mcmeta121, $utf8NoBom)
    $zip121 = Join-Path $resourcepacksDir "Logistics-UltraRealistic-3D-Pipes-MC1.21.1.zip"
    if (Test-Path $zip121) { Remove-Item -Path $zip121 -Force }
    & jar -c -M -f $zip121 -C $packPath .

    # MC 1.20 - 1.20.1 Native (pack_format: 15)
    $mcmeta120 = @"
{
  "pack": {
    "pack_format": 15,
    "description": "Ultra-Realistic 3D Monolithic Pipes for Logistics Automation"
  }
}
"@
    [System.IO.File]::WriteAllText((Join-Path $packPath "pack.mcmeta"), $mcmeta120, $utf8NoBom)
    $zip120 = Join-Path $resourcepacksDir "Logistics-UltraRealistic-3D-Pipes-MC1.20.1.zip"
    if (Test-Path $zip120) { Remove-Item -Path $zip120 -Force }
    & jar -c -M -f $zip120 -C $packPath .

    # Restore Universal pack.mcmeta for the directory pack
    [System.IO.File]::WriteAllText((Join-Path $packPath "pack.mcmeta"), $packMcmeta, $utf8NoBom)
}

# ==============================================================================
# 9. STANDALONE DOWNLOAD ONLY: DO NOT BAKE INTO MOD
# ==============================================================================
# Clean any old baked resource packs from mod resources if they exist
$modPackDir = Join-Path $projectDir "src\main\resources\resourcepacks"
if (Test-Path $modPackDir) {
    Write-Host "Ensuring mod resources are clean (not baking into mod)..." -ForegroundColor Yellow
    Remove-Item -Path $modPackDir -Recurse -Force
}

# Update extracted logistics mod reference assets for dev/workspace preview if requested
if ($InstallToMod) {
    Write-Host "Updating local reference mod assets..." -ForegroundColor Cyan
    $logisticsAssets = Join-Path $projectDir "logistics\assets\logistics"
    Copy-Item -Path "$packPath\assets\logistics\*" -Destination $logisticsAssets -Recurse -Force
}

Write-Host "=================================================================" -ForegroundColor Green
Write-Host " Ultra-Realistic 3D Monolithic Pipe Pack Successfully Generated!" -ForegroundColor Green
Write-Host " Pack Folder: $packPath" -ForegroundColor Green
Write-Host " Universal Zip: $zipUniversal" -ForegroundColor Green
Write-Host " MC 1.21.1 Zip: $zip121" -ForegroundColor Green
Write-Host " MC 1.20.1 Zip: $zip120" -ForegroundColor Green
Write-Host "=================================================================" -ForegroundColor Green
