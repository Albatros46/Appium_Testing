# Downloads Sauce Labs' My Demo App and refuses the file if its SHA-256 is not the published one.
$ErrorActionPreference = 'Stop'
$version = '2.3.0'
$file = 'mda-2.3.0-27.apk'
$sha256 = 'BD3A3D6F2D50E076B2701ED2CC6A0CA82157B40A7033CF5BE9F5D084B6591EC8'
$target = Join-Path (Join-Path $PSScriptRoot '..\apps') $file
New-Item -ItemType Directory -Force (Split-Path $target) | Out-Null
if (-not (Test-Path $target)) {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    $url = "https://github.com/saucelabs/my-demo-app-android/releases/download/$version/$file"
    Invoke-WebRequest -Uri $url -OutFile $target -UseBasicParsing
}
if ((Get-FileHash $target -Algorithm SHA256).Hash -ne $sha256) {
    Remove-Item $target
    throw "SHA-256 of $file does not match the published value"
}
"OK: $target"
